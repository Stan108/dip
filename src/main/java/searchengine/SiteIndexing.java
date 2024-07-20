package searchengine;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.lucene.morphology.analyzer.MorphologyAnalyzer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import searchengine.model.IndexingStatus;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.services.IndexRepositoryService;
import searchengine.services.LemmaRepositoryService;
import searchengine.services.PageRepositoryService;
import searchengine.services.SiteRepositoryService;
import searchengine.sitemap.SiteMapBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SiteIndexing extends Thread {

    private final Site site;
    private final SearchSettings searchSettings;
    private final SiteRepositoryService siteRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;
    private final IndexRepositoryService indexRepositoryService;
    private final boolean allSite;
    private String pageUrl;
    Log log = LogFactory.getLog(Index.class);

    public SiteIndexing(Site site, SearchSettings searchSettings, SiteRepositoryService siteRepositoryService,
                        PageRepositoryService pageRepositoryService, LemmaRepositoryService lemmaRepositoryService,
                        IndexRepositoryService indexRepositoryService, boolean allSite, String pageUrl) {
        this.site = site;
        this.searchSettings = searchSettings;
        this.siteRepositoryService = siteRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
        this.indexRepositoryService = indexRepositoryService;
        this.allSite = allSite;
        this.pageUrl = pageUrl;
    }

    public void run() {
        try {
            if (allSite) {
                runAllIndexing();
            } else {
                runOneSiteIndexing(pageUrl);
            }
        } catch (Exception e) {
            log.error("Ошибка индексации сайта", e);
        }
    }

    private void runAllIndexing() {
        log.info("запуск индексации всех сайтов");
        site.setStatus(IndexingStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepositoryService.save(site);
        SiteMapBuilder builder = new SiteMapBuilder(site.getUrl(), this.isInterrupted());
        builder.buildSiteMap();
        List<String> allSiteUrls = builder.getSiteMap();
        allSiteUrls.forEach(this::runOneSiteIndexing);
    }

    private void runOneSiteIndexing(String pageUrl) {
        log.info("запуск метода индексации одной страницы");
        site.setStatus(IndexingStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepositoryService.save(site);
        try {
            // Проверяем, существует ли уже страница в базе данных
            Page page = pageRepositoryService.getPage(pageUrl.replace(site.getUrl(), ""));
            if (page != null) {
                prepareDbToIndexing(page);
            }
            // Получаем HTML-код страницы и создаем объект Page
            page = getSearchPage(pageUrl, site.getUrl(), site.getId());
            pageRepositoryService.save(page);

            // Обрабатываем страницу
            processPage(page);

            // Привязываем страницу к сайту
            page.setSite(site);
            page.setPath(pageUrl.replace(site.getUrl(), ""));
            pageRepositoryService.save(page);
        } catch (IOException e) {
            site.setLastError("Ошибка чтения страницы: " + pageUrl + "\n" + e.getMessage());
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

    private void processPage(Page page) throws IOException {
        String content = page.getContent();
        LemmaFinder analyzer = LemmaFinder.getInstance();
        Map<String, Integer> lemmas = analyzer.collectLemmas(content);

        // Сохранение лемм и обновление частоты
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            String lemmaName = entry.getKey();
            List<Lemma> existingLemmas = lemmaRepositoryService.getLemma(lemmaName);
            Lemma lemma;
            if (existingLemmas.isEmpty()) {
                lemma = new Lemma(lemmaName, entry.getValue(), page.getSiteId());
                lemmaRepositoryService.save(lemma);
            } else {
                lemma = existingLemmas.get(0);
                lemma.setFrequency(lemma.getFrequency() + entry.getValue());
                lemmaRepositoryService.save(lemma);
            }

            // Сохранение индексов
            searchengine.model.Index existingIndex = indexRepositoryService.getIndexing(lemma.getId(), page.getId());
            if (existingIndex == null) {
                searchengine.model.Index newIndex = new searchengine.model.Index();
                newIndex.setPage(page);
                newIndex.setLemma(lemma);
                newIndex.setRank((float) entry.getValue());
                indexRepositoryService.save(newIndex);
            } else {
                existingIndex.setRank(existingIndex.getRank() + entry.getValue());
                indexRepositoryService.save(existingIndex);
            }
        }
    }

    private void prepareDbToIndexing(Page page) {
        List<searchengine.model.Index> indexingList = indexRepositoryService.getAllIndexingByPageId(page.getId());
        indexRepositoryService.deleteAllIndexing(indexingList); // Сначала удаляем индексы
        List<Lemma> allLemmasIdByPage = lemmaRepositoryService.findLemmasByIndexing(indexingList);
        for (Lemma lemma : allLemmasIdByPage) {
            if (lemma.getFrequency() <= 0) {
                lemmaRepositoryService.deleteLemma(lemma);
            }
        }
        pageRepositoryService.deletePage(page); // Удаляем страницу
    }
}

