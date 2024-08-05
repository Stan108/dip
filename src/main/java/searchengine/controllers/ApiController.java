package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.Request;
import searchengine.responces.ResponseService;
import searchengine.services.IndexingService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final IndexingService index;
    private final StatisticsService statisticsService;
    private final SearchService searchService;
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

    @GetMapping("/search")
    ResponseEntity<ResponseService> getSearch(@RequestParam(name = "query", required = false,defaultValue = "") String query,
                                              @RequestParam(name = "site", required = false, defaultValue = "") String site,
                                              @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                              @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) throws IOException {
        ResponseService response = searchService.getResponse(new Request(query), site, offset, limit);
        return ResponseEntity.ok(response);
    }
}
