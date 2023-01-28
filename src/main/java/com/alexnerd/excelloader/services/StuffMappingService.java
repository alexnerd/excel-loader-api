/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alexnerd.excelloader.services;

import com.alexnerd.excelloader.dto.ExcelRowMapper;
import com.alexnerd.excelloader.dto.LoadResponseDto;
import com.alexnerd.excelloader.dto.StuffDto;
import com.alexnerd.excelloader.dto.StuffPageableDto;
import com.alexnerd.excelloader.jobs.SqlExecutor;
import com.alexnerd.excelloader.repository.StuffRepository;
import com.alexnerd.excelloader.repository.StuffSpecification;
import com.alexnerd.excelloader.repository.dao.Stuff;
import com.github.pjfanning.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StuffMappingService {

    @Autowired
    private StuffRepository stuffRepository;

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private SqlExecutor sqlExecutor;

    @Autowired
    private ThreadPoolTaskExecutor taskThreadPoolExecutor;

    @Value("${app.batch-size}")
    private int batchSize;

    @Value("${app.retry-num}")
    private int retryNum;

    private final Semaphore semaphore;

    public StuffMappingService(@Value("${app.workers-number}") int defaultWorkersNumber) {
        this.semaphore = new Semaphore(defaultWorkersNumber);
    }

    public LoadResponseDto upload(FileItemIterator itemIterator) throws IOException {
        int result = 0;
        while (itemIterator.hasNext()) {
            FileItemStream stream = itemIterator.next();
            if (!stream.isFormField()) {
                String dbName = this.createTable();
                try (InputStream is = stream.openStream();
                     Workbook workbook = StreamingReader.builder()
                             .rowCacheSize(1000)
                             .bufferSize(4096)
                             .open(is)) {
                    Sheet sheet = workbook.getSheetAt(0);

                    result = this.processOneSheet(sheet, dbName).stream()
                            .filter(Objects::nonNull)
                            .map(f -> {
                                try {
                                    return f.get();
                                } catch (InterruptedException | ExecutionException ex) {
                                    log.error("Can't complete saving process to DB", ex);
                                }
                                return 0;
                            }).reduce(Integer::sum).orElse(0);
                } finally {
                    this.copyAndDeleteTable(dbName);
                }
            }
        }
        return new LoadResponseDto(result);
    }



    public StuffPageableDto getStuff(int page, int size, String sortColumn, String like) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Specification<Stuff> specification = getSpecification(sortColumn, like);
        Page<Stuff> stuff = stuffRepository.findAll(specification, pageable);
        return new StuffPageableDto(stuff.stream()
                .map(this::stuffToDto)
                .collect(Collectors.toList()),
                stuff.getTotalElements());
    }

    private StuffDto stuffToDto(Stuff stuff) {
        StuffDto stuffDto = new StuffDto();
        stuffDto.setName(stuff.getName());
        stuffDto.setDescription(stuff.getDescription());
        return stuffDto;
    }

    private Specification<Stuff> getSpecification(String sort, String like) {
        return StuffSpecification.specBuilder(StuffSpecification.sortSpec(sort))
                .and(StuffSpecification.likeSpec(like))
                .build();
    }

    private List<Future<Integer>> processOneSheet(Sheet sheet, String dbName) {
        List<String> content = new ArrayList<>();
        List<Future<Integer>> resultList = new ArrayList<>();
        for (Row r : sheet) {
            for (Cell c : r) {
                if (c.getColumnIndex() != 0) break;
                if (content.size() == batchSize) {
                    final ExcelRowMapper mapper = new ExcelRowMapper(dbName, content);
                    resultList.add(this.execute(mapper));
                    content = new ArrayList<>();
                }
                String stringCellValue = c.getStringCellValue();
                if (!stringCellValue.isBlank()) {
                    content.add(stringCellValue
                            .trim()
                            .replace("'", "''")
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\""));
                }
            }
        }
        if (!content.isEmpty()) {
            final ExcelRowMapper mapper = new ExcelRowMapper(dbName, content);
            resultList.add(this.execute(mapper));
        }
        return resultList;
    }

    private Future<Integer> execute(ExcelRowMapper mapper) {
        semaphore.acquireUninterruptibly();
        return taskThreadPoolExecutor.submit(() -> sqlExecutor.executeSql(mapper, semaphore));
    }

    private String getDbName() {
        return "tmp_stuff_" + ThreadLocalRandom.current().nextInt(10000, 99999);
    }

    public String createTable() {
        for (int i = 0; i < retryNum; i++) {
            try {
                String dbName = this.getDbName();
                template.execute("CREATE TABLE " + dbName + " (name varchar NOT NULL)");
                return dbName;
            } catch (BadSqlGrammarException ex) {
                log.warn("Error while creating tmp table for stuff ", ex);
            }
        }
        throw new RuntimeException("Can't create tmp table for stuff");
    }

    public void copyAndDeleteTable(String dbName) {
        template.execute("INSERT INTO stuff (name) " +
                "(SELECT DISTINCT (name) from " + dbName + " EXCEPT SELECT name FROM stuff) ON CONFLICT DO NOTHING");
        template.execute("DROP TABLE IF EXISTS " + dbName);
    }
}
