package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.model.Site;

import java.util.List;

@Service
public interface SiteRepositoryService {
    Site getSite(String url);
    Site getSite(int siteId);
    void save(Site site);
    long siteCount();
    List<Site> getAllSites();

    void deleteByUrl(String url);
    void deleteAll();

}


