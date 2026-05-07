package kg.home.demo.service;

import kg.home.demo.configs.ExcelImportProperties;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.*;
        import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelImportStreamService {

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

        Path inputPath = Path.of(props.getImportDir(), props.getFileName());
        File file = inputPath.toFile();

        OPCPackage pkg = OPCPackage.open(file);
        XSSFReader reader = new XSSFReader(pkg);

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);

        XMLReader parser = XMLReaderFactory.createXMLReader();

        SheetHandler handler = new SheetHandler();

        XSSFSheetXMLHandler sheetHandler =
                new XSSFSheetXMLHandler(
                        reader.getStylesTable(),
                        strings,
                        handler,
                        new DataFormatter(),
                        false
                );

        parser.setContentHandler(sheetHandler);

        XSSFReader.SheetIterator sheets =
                (XSSFReader.SheetIterator) reader.getSheetsData();

        while (sheets.hasNext()) {
            try (InputStream sheet = sheets.next()) {
                parser.parse(new InputSource(sheet));
            }
        }

        handler.flush();

        Path outPath = Path.of(props.getArchiveDir(), props.getFileName());
        Files.move(inputPath, outPath, StandardCopyOption.REPLACE_EXISTING);

        return handler.getCount();
    }

    private class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private List<Object[]> batch = new ArrayList<>();
        private int batchSize = 5000;

        private String code;
        private String name;

        private long id = 1;
        private int count = 0;

        @Override
        public void startRow(int rowNum) {

            if (rowNum == 0) {
                return; // skip header
            }

            code = null;
            name = null;
        }

        @Override
        public void endRow(int rowNum) {

            if (code == null || code.isBlank()) {
                return;
            }

            batch.add(new Object[]{id++, code, name});
            count++;

            if (batch.size() >= batchSize) {
                executeBatch(batch);
                batch.clear();
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {

            int col = cellReference.charAt(0) - 'A';

            if (col == props.getColumnCode() - 1) {
                code = formattedValue;
            }

            if (col == props.getColumnName() - 1) {
                name = formattedValue;
            }
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
        }

        public void flush() {

            if (!batch.isEmpty()) {
                executeBatch(batch);
                batch.clear();
            }
        }

        public int getCount() {
            return count;
        }
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