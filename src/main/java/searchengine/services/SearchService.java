package searchengine.services;

import searchengine.model.Request;
import searchengine.responces.ResponseService;

import java.io.IOException;

public interface SearchService {
    ResponseService getResponse (Request request, String url, int offset, int limit) throws IOException;
}
