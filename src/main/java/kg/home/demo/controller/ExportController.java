package kg.home.demo.controller;

import kg.home.demo.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExportController {

    private final ExcelExportService service;

    @GetMapping("/exportExcel")
    public Map<String, Object> exportExcel() throws Exception {

        long start = System.currentTimeMillis();

        Map<String, Object> result = service.exportExcel();

        long duration = System.currentTimeMillis() - start;

        return Map.of(
                "status", "completed",
                "records", result.get("records"),
                "file", result.get("file"),
                "timeMs", duration
        );
    }
}