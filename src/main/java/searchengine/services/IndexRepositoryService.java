package searchengine.services;

import searchengine.model.Index;
import java.util.List;

public interface IndexRepositoryService {
    List<Index> getAllIndexingByLemmaId(int lemmaId);
    List<Index> getAllIndexingByPageId(int pageId);
    void deleteAllIndexing(List<Index> indexingList);
    Index getIndexing (int lemmaId, int pageId);
    void save(Index indexing);
}
