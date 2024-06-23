package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.responces.ResponseService;
import searchengine.services.IndexingService;
import searchengine.services.StatisticsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final IndexingService index;
    private final StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    ResponseEntity<ResponseService> startIndexing() {
        ResponseService response = index.startIndexingAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stopIndexing")
    ResponseEntity<ResponseService> stopIndexing() {
        ResponseService response = index.stopIndexingAll();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/indexPage")
    ResponseEntity<ResponseService> startIndexingOne(@RequestParam(name = "url",
    required = false, defaultValue = " ") String url) {
        ResponseService response = index.startIndexingOne(url);
        return ResponseEntity.ok(response);
    }
}
