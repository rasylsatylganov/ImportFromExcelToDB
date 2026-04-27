package kg.home.demo.service;


import kg.home.demo.configs.ExcelExportProperties;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final JdbcTemplate jdbcTemplate;
    private final ExcelExportProperties props;

    public Map<String, Object> exportExcel() throws Exception {

        String sql = "SELECT "
                + props.getDbColumnId() + ", "
                + props.getDbColumnCode() + ", "
                + props.getDbColumnName()
                + " FROM " + props.getTableName();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("data");

        int rowNum = 0;

        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue(props.getDbColumnId());
        header.createCell(1).setCellValue(props.getDbColumnCode());
        header.createCell(2).setCellValue(props.getDbColumnName());

        for (Map<String, Object> dbRow : rows) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    String.valueOf(dbRow.get(props.getDbColumnId()))
            );

            row.createCell(1).setCellValue(
                    String.valueOf(dbRow.get(props.getDbColumnCode()))
            );

            Object name = dbRow.get(props.getDbColumnName());
            row.createCell(2).setCellValue(
                    name == null ? "" : name.toString()
            );
        }

        Path exportDir = Path.of(props.getExportDir());
        if (!Files.exists(exportDir)) {
            Files.createDirectories(exportDir);
        }
        Path filePath = exportDir.resolve(props.getFileName());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            workbook.write(fos);
        }

        workbook.close();

        return Map.of(
                "records", rows.size(),
                "file", filePath.toAbsolutePath().toString()
        );
    }
}