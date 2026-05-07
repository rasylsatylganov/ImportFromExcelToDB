package kg.home.demo.controller;

import kg.home.demo.service.ExcelImportService;
import kg.home.demo.service.ExcelImportStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImportController {

    private final ExcelImportService service;
    private final ExcelImportStreamService streamService;

    @PostMapping("/importExcel")
    public Map<String, Object> importExcel() throws Exception {

        long start = System.currentTimeMillis();

//        int count = service.importExcel();
        int count = streamService.importExcel();

        long duration = System.currentTimeMillis() - start;

        return Map.of(
                "status", "completed",
                "timeMs", duration,
                "records", count
        );
    }
}