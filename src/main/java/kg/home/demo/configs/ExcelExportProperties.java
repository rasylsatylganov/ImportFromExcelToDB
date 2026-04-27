package kg.home.demo.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "excel-export")
public class ExcelExportProperties {

    private String tableName;

    private String dbColumnId;
    private String dbColumnCode;
    private String dbColumnName;

    private String exportDir;
    private String fileName;
}
