package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanUpServiceImpl implements CleanupService{
    private final SiteRepositoryService siteRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;
    private final IndexRepositoryService indexRepositoryService;

    private static final Log log = LogFactory.getLog(CleanUpServiceImpl.class);
    @Override
    public void clearAllData() {
        log.info("Очистка всех данных");
        indexRepositoryService.deleteAll();
        lemmaRepositoryService.deleteAll();
        pageRepositoryService.deleteAll();
        siteRepositoryService.deleteAll();
    }

    public void clearIndex(List<Index> indexList){
        indexRepositoryService.deleteAllIndexing(indexList);
    }

    public void clearAllLemmas(List<Lemma> lemmaList){
        lemmaRepositoryService.deleteAllLemmas(lemmaList);
    }

}
