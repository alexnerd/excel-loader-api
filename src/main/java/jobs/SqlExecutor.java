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
