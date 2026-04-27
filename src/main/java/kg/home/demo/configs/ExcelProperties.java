package kg.home.demo.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "excel")
public class ExcelProperties {

    private String importDir;
    private String archiveDir;
    private String fileName;
    private String tableName;
    private String dbColumnId;
    private String dbColumnCode;
    private String dbColumnName;
    private int columnCode;
    private int columnName;

}