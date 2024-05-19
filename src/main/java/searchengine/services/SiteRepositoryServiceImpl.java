package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.IndexingStatus;
import searchengine.model.Site;
import searchengine.repo.SiteRepository;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SiteRepositoryServiceImpl implements SiteRepositoryService{

    private final SiteRepository siteRepository;
    @Override
    public Site getSite(String url) {
        Site site = siteRepository.findByUrl(url);
        if(site == null){
            site = new Site(url);
            siteRepository.save(site);
        }
        return site;
    }

    @Override
    public Site getSite(int siteId) {
        return siteRepository.findById(siteId).orElse(null);
    }

    @Override
    public void save(Site site) {
        siteRepository.save(site);
    }

    @Override
    public long siteCount() {
        return siteRepository.count();
    }

    @Override
    public List<Site> getAllSites() {
        List<Site> siteList = new ArrayList<>();
        Iterable<Site> siteIterable = siteRepository.findAll();
        siteIterable.forEach(siteList::add);
        return siteList;
    }

    @Override
    public void deleteByUrl(String url) {
        Site site = siteRepository.findByUrl(url);
        siteRepository.delete(site);
    }

    @Override
    public void deleteAll() {
        Iterable<Site> allSites = siteRepository.findAll();
        for (Site s : allSites) {
            siteRepository.delete(s);
        }
    }
}
