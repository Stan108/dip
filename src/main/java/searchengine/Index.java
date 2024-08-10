package searchengine;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Component;
import searchengine.model.IndexingStatus;
import searchengine.model.Site;
import searchengine.services.*;
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
    private final LemmaRepositoryService lemmaRepositoryService;
    private final IndexRepositoryService indexRepositoryService;

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
                    siteRepositoryService, pageRepositoryService,
                    lemmaRepositoryService, indexRepositoryService, true);
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
                        lemmaRepositoryService, indexRepositoryService, true);
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
        if (!isThreadAlive) {
            executor.shutdownNow();
        }

        List<Site> siteList = siteRepositoryService.getAllSites();
        siteList.forEach(site -> {
            site.setStatus(IndexingStatus.FAILED);
            siteRepositoryService.save(site);
        });

        return isThreadAlive;
    }

    public String checkedSiteIndexing(String url) {
        String baseUrl = getBaseUrlFromConfig(url);
        if (baseUrl == null) {
            return "not found";
        }

        Site site = siteRepositoryService.findByUrl(baseUrl);
        if (site == null) {
            site = new Site();
            site.setUrl(baseUrl);
            site.setName(getSiteNameFromConfig(baseUrl));
            site.setStatus(IndexingStatus.INDEXING);
            site.setStatusTime(LocalDateTime.now());
            siteRepositoryService.save(site);
        } else if (site.getStatus().equals(IndexingStatus.INDEXING)) {
            return "false";
        } else {
            site.setStatus(IndexingStatus.INDEXING);
            site.setStatusTime(LocalDateTime.now());
            siteRepositoryService.save(site);
        }
        SiteIndexing indexing = new SiteIndexing(
                site,
                searchSettings,
                siteRepositoryService,
                pageRepositoryService,
                lemmaRepositoryService,
                indexRepositoryService,
                false,
                url);
        executor.execute(indexing);
        return "true";
    }

    private String getBaseUrlFromConfig(String url) {
        for (HashMap<String, String> siteMap : searchSettings.getSites()) {
            String siteUrl = siteMap.get("url");
            if (url.startsWith(siteUrl) || url.startsWith(siteUrl.replace("www.", ""))) {
                return siteUrl;
            }
        }
        return null;
    }
    private String getSiteNameFromConfig(String baseUrl) {
        for (HashMap<String, String> siteMap : searchSettings.getSites()) {
            if (siteMap.get("url").equals(baseUrl) || siteMap.get("url").equals(baseUrl.replace("www.", ""))) {
                return siteMap.get("name");
            }
        }
        return null;
    }
}






