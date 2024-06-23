package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.repo.IndexRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class IndexRepositoryServiceImpl implements IndexRepositoryService{
    private final IndexRepository indexRepository;
    @Override
    public List<Index> getAllIndexingByLemmaId(int lemmaId) {
        return indexRepository.findByLemmaId(lemmaId);
    }

    @Override
    public List<Index> getAllIndexingByPageId(int pageId) {
        return indexRepository.findByPageId(pageId);
    }

    @Override
    public void deleteAllIndexing(List<Index> indexingList) {
        indexRepository.deleteAll(indexingList);
    }

    @Override
    public Index getIndexing(int lemmaId, int pageId) {
        Index indexing = null;
        try {
            indexing = indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
        } catch (Exception e) {
            System.out.println("lemmaId " + lemmaId + " + pageId: " + pageId + " not unique");
            e.printStackTrace();
        }
        return indexing;
    }
    @Override
    public void save(Index indexing) {
        indexRepository.save(indexing);
    }
}
