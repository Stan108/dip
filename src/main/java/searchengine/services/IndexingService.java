package searchengine.services;

import searchengine.responces.ResponseService;

public interface IndexingService {
    ResponseService startIndexingAll();
    ResponseService stopIndexingAll();
    ResponseService startIndexingOne(String url);
}
