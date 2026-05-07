package kg.home.demo.service;


import kg.home.demo.configs.ExcelImportProperties;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final ExcelImportProperties props;
    private final JdbcTemplate jdbcTemplate;

    public int importExcel() throws Exception {

        Long existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM " + props.getTableName(),
                Long.class
        );
        if (existing != null && existing > 0) {
            throw new IllegalStateException(
                    "Импорт запрещён: в таблице уже есть " + existing + " записей"
            );
        }

        int count = 0;

        Path inputPath = Path.of(props.getImportDir(), props.getFileName());
        File file = inputPath.toFile();

        List<Object[]> batch = new ArrayList<>();
        int batchSize = 1000;

        IOUtils.setByteArrayMaxOverride(1_000_000_000);

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(file))) {

            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            long id = 1;

            for (Row row : sheet) {

                String code = getCellValue(row, formatter, props.getColumnCode() - 1);
                String name = getCellValue(row, formatter, props.getColumnName() - 1);

                if (code == null || code.isBlank()) {
                    continue;
                }
                count++;
                batch.add(new Object[]{id++, code, name});
                if (batch.size() == batchSize) {
                    executeBatch(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                executeBatch(batch);
            }
        }

        Path outPath = Path.of(props.getArchiveDir(), props.getFileName());
        Files.move(inputPath, outPath, StandardCopyOption.REPLACE_EXISTING);

        return count;
    }

    private String getCellValue(Row row, DataFormatter formatter, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;

        String value = formatter.formatCellValue(cell);
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private Long safeLong(String value) {
        if (value == null || value.isBlank()) return null;
        return Long.parseLong(value.trim());
    }

    private void executeBatch(List<Object[]> batch) {

        String sql =
                "INSERT INTO " + props.getTableName() +
                        " (" + props.getDbColumnId() + ", " +
                        props.getDbColumnCode() + ", " +
                        props.getDbColumnName() + ") " +
                        "VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, batch);
    }
}