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

package jobs;

import com.alexnerd.excelloader.dto.ExcelRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Component
public class SqlExecutor {

    @Autowired
    private JdbcTemplate template;

    public int executeSql(ExcelRowMapper mapper, Semaphore semaphore) {
        try {
            return template.update(this.createSql(mapper));
        } finally {
            semaphore.release();
        }
    }

    private String createSql(ExcelRowMapper mapper) {
            return "INSERT INTO " + mapper.getDbName() + "(name) VALUES" +
                    mapper.getRowValues().stream().map(s -> "('" + s + "')").collect(Collectors.joining(", "));
    }
}
