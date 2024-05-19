package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CleanUpServiceImpl implements CleanupService{
    private final SiteRepositoryService siteRepositoryService;
    private final PageRepositoryService pageRepositoryService;

    private static final Log log = LogFactory.getLog(CleanUpServiceImpl.class);
    @Override
    public void clearAllData() {
        log.info("Очистка всех данных");
        pageRepositoryService.deleteAll();
        siteRepositoryService.deleteAll();
    }
}
