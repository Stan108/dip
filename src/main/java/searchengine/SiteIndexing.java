package searchengine;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import searchengine.model.IndexingStatus;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.services.PageRepositoryService;
import searchengine.services.SiteRepositoryService;
import searchengine.sitemap.SiteMapBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SiteIndexing extends Thread{

    private final Site site;
    private final SearchSettings searchSettings;
    private final SiteRepositoryService siteRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final boolean allSite;
    Log log = LogFactory.getLog(Index.class);

    public void run() {
        System.out.println("right now we are in parsing method");
       try {
           if (allSite) {
               runAllIndexing();
           } else {
               runOneSiteIndexing(site.getUrl());
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    private void runAllIndexing() {
        System.out.println("запуск индексации всех сайтов");
        log.info("запуск индексации всех сайтов");
//        site.setStatus(IndexingStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepositoryService.save(site);
        SiteMapBuilder builder = new SiteMapBuilder(site.getUrl(), this.isInterrupted());
        builder.buildSiteMap();
        List<String> allSiteUrls = builder.getSiteMap();
        allSiteUrls.forEach(this::runOneSiteIndexing);
    }


    private void runOneSiteIndexing(String searchUrl) {
        System.out.println("запуск метода индексации одного сайта");
        log.info("запуск метода индексации одного сайта");
        site.setStatus(IndexingStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepositoryService.save(site);
        try {
            Page page = getSearchPage(searchUrl, site.getUrl(), site.getId());
            pageRepositoryService.save(page);
        } catch (IOException e) {
            site.setLastError("Ошибка чтения странцы: " + searchUrl + "\n" + e.getMessage());
            site.setStatus(IndexingStatus.FAILED);
        } finally {
            siteRepositoryService.save(site);
        }
        site.setStatus(IndexingStatus.INDEXED);
        siteRepositoryService.save(site);
    }

    private Page getSearchPage(String url, String baseUrl, int siteId) throws IOException {
        Page page = new Page();
        Connection.Response response = Jsoup.connect(url)
                .userAgent(searchSettings.getAgent())
                .referrer("http://www.google.com")
                .execute();
        String content = response.body();
        String path = url.replaceAll(baseUrl, "");
        int code = response.statusCode();
            page.setCode(code);
            page.setPath(path);
            page.setContent(content);
            page.setSiteId(siteId);
        return page;
    }
}
