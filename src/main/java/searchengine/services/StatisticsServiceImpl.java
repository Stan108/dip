package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static searchengine.model.IndexingStatus.INDEXED;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private static final Log log = LogFactory.getLog(StatisticsServiceImpl.class);

    private final Random random = new Random();
    private final SitesList sites;
    private final SiteRepositoryService siteRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        total.setSites(sites.getSites().size());
        total.setIndexing(true);

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();

        for(int i = 0; i < sitesList.size(); i++) {
            Site siteFromConfig = sitesList.get(i);
            searchengine.model.Site site = siteRepositoryService.getSite(siteFromConfig.getUrl());

            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(siteFromConfig.getName());
            item.setUrl(siteFromConfig.getUrl());
//            int pages = random.nextInt(1_000);
            int pages = (int) pageRepositoryService.pageCount(site.getId());
//            int lemmas = pages * random.nextInt(1_000);
            int lemmas = lemmaRepositoryService.lemmaCount(site.getId());
            item.setPages(pages);
            item.setLemmas(lemmas);
//            item.setStatus(statuses[i % 3]);
            item.setStatus(site.getStatus().name());
//            item.setError(errors[i % 3]);
            item.setError(site.getLastError());

//            item.setStatusTime(System.currentTimeMillis() -
//                    (random.nextInt(10_000)));
            item.setStatusTime(site.getStatusTime().toString());
            total.setPages((total.getPages() + pages));
            total.setLemmas((total.getLemmas() + lemmas));
            detailed.add(item);
        }

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
    private boolean isSitesIndexing(){
        boolean is = true;
        for(searchengine.model.Site s : siteRepositoryService.getAllSites()){
            if(!s.getStatus().equals(INDEXED)){
                is = false;
                break;
            }
        }
        return is;
    }
}
