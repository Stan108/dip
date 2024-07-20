package searchengine;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Component;
import searchengine.model.IndexingStatus;
import searchengine.model.Site;
import searchengine.repo.SiteRepository;
import searchengine.responces.FalseResponseService;
import searchengine.responces.ResponseService;
import searchengine.responces.TrueResponseService;
import searchengine.services.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static searchengine.model.IndexingStatus.INDEXED;

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
            // Создаем новую запись для сайта
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

        // Шаг 3: Запуск индексации страницы
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

    // Метод для получения имени сайта из конфигурации
    private String getSiteNameFromConfig(String baseUrl) {
        for (HashMap<String, String> siteMap : searchSettings.getSites()) {
            if (siteMap.get("url").equals(baseUrl) || siteMap.get("url").equals(baseUrl.replace("www.", ""))) {
                return siteMap.get("name");
            }
        }
        return null;
    }
}






//        List<Site> sitesInRepository = siteRepositoryService.getAllSites();
//        String baseUrl = "";
//        for (Site site : sitesInConfig) {
//            String siteUrl = site.getUrl();
//            if (url.startsWith(siteUrl) || url.startsWith(siteUrl.replace("www.", ""))) {
//                baseUrl = siteUrl;
//                break;
//            }
//        }
//        if (baseUrl.isEmpty()) {
//            return "not found";
//        } else {
//            Site site = siteRepositoryService.getSite(baseUrl);
//            if (site == null) {
//                return "site not found";
//            }
//                        siteRepositoryService.save(site);
//                        site.setStatus(IndexingStatus.INDEXING);
//                        site.setStatusTime(LocalDateTime.now());
//                        SiteIndexing indexing = new SiteIndexing(
//                                site,
//                                searchSettings,
//                                siteRepositoryService,
//                                pageRepositoryService,
//                                lemmaRepositoryService,
//                                indexRepositoryService,
//                                false);
//                        executor.execute(indexing);
//                        siteRepositoryService.save(site);
//                        return "true";
//                    }
//        }


//        List<Site> siteList = siteRepositoryService.getAllSites();
//        String baseUrl = "";
//        for (Site site : siteList) {
//            String siteUrl = site.getUrl();
//            if (url.startsWith(siteUrl) || url.startsWith(siteUrl.replace("www.", ""))) {
//                baseUrl = siteUrl;
//                break;
//            }
//        }
//        if (baseUrl.isEmpty()) {
//            return "not found";
//        } else {
//            Site site = siteRepositoryService.getSite(baseUrl);
//            if (site == null) {
//                return "site not found";
//            }
//            site.setStatus(IndexingStatus.INDEXING);
//            site.setStatusTime(LocalDateTime.now());
//            SiteIndexing indexing = new SiteIndexing(
//                    site,
//                    searchSettings,
//                    siteRepositoryService,
//                    pageRepositoryService,
//                    lemmaRepositoryService,
//                    indexRepositoryService,
//                    false);
//            executor.execute(indexing);
//            siteRepositoryService.save(site);
//            return "true";
//        }
//    }





