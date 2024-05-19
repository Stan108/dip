package searchengine;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Component;
import searchengine.model.IndexingStatus;
import searchengine.model.Site;
import searchengine.services.CleanupService;
import searchengine.services.PageRepositoryService;
import searchengine.services.SiteRepositoryService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class Index {
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private final CleanupService cleanupService;
    private final SearchSettings searchSettings;
    private final SiteRepositoryService siteRepositoryService;
    private final PageRepositoryService pageRepositoryService;

    Log log = LogFactory.getLog(Index.class);

    public boolean allSiteIndexing() {
        cleanupService.clearAllData();
        boolean isIndexing;
        List<Site> siteList = getSites();
        for (Site site : siteList) {
            isIndexing = startSiteIndexing(site);
            if (!isIndexing) {
                return false;
            }
        }
        return true;
    }

    private boolean startSiteIndexing(Site site) {
        Site site1 = siteRepositoryService.getSite(site.getUrl());
        if (site1 == null) {
            siteRepositoryService.save(site);
            SiteIndexing indexing = new SiteIndexing(site, searchSettings,
                                    siteRepositoryService, pageRepositoryService, true);
            executor.execute(indexing);
            return true;
        } else {
            if (!site1.getStatus().equals(IndexingStatus.INDEXING)) {
                siteRepositoryService.save(site);
                SiteIndexing indexing = new SiteIndexing(
                        siteRepositoryService.getSite(site.getUrl()),
                        searchSettings,
                        siteRepositoryService,
                        pageRepositoryService,
                        true);
                executor.execute(indexing);
                return true;
            } else {
                return false;
            }
        }
    }

    private List<Site> getSites() {
        List<Site> siteList = new ArrayList<>();
        for (HashMap<String, String> siteMap : searchSettings.getSites()) {
            String url = siteMap.get("url");
            String name = siteMap.get("name");
            Site site = new Site(url, name);
            site.setStatus(IndexingStatus.FAILED);
            site.setStatusTime(LocalDateTime.now());
            siteRepositoryService.save(site);
            siteList.add(site);
        }
        return siteList;
    }

    public boolean stopSiteIndexing() {
        boolean isThreadAlive = false;
        if (executor.getActiveCount() == 0) {
            return false;
        }
        executor.shutdown();
        try {
            isThreadAlive = executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("ошибка остановки потоков " + e);
        }
        if (isThreadAlive) {
            List<Site> siteList = siteRepositoryService.getAllSites();
            siteList.forEach(site -> {
                 site.setStatus(IndexingStatus.FAILED);
                 siteRepositoryService.save(site);
            });
        }
        return isThreadAlive;
    }
}
