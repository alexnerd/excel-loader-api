package com.alexnerd.excelloader.jobs;


import com.alexnerd.excelloader.dto.ExcelRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Component
public class SqlExecutor {

    @Autowired
    private JdbcTemplate template;

    @Transactional
    public int executeSql(ExcelRowMapper mapper, Semaphore semaphore) {
        try {
            return template.update(this.createSQL(mapper));
        } finally {
            semaphore.release();
        }
    }

    private String createSQL(ExcelRowMapper mapper) {
        return "INSERT INTO " + mapper.getDbName() + "(name) VALUES " +
                mapper.getRowValues().stream().map(s -> "('"
                        + s
                        + "')").collect(Collectors.joining(", "));
    }
}
