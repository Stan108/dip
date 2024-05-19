package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.repo.PageRepository;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PageRepositoryServiceImpl implements PageRepositoryService{

    private final PageRepository pageRepository;
    @Override
    public Page getPage(String pagePath) {
        return pageRepository.findByPath(pagePath);
    }

    @Override
    public void save(Page page) {
        pageRepository.save(page);
    }

    @Override
    public Optional<Page> findPageById(int id) {
        return pageRepository.findById(id);
    }

    @Override
    public Optional<Page> findPageByPageIdAndSiteId(int pageId, int siteId) {
        return findPageByPageIdAndSiteId(pageId, siteId);
    }

    @Override
    public long pageCount() {
        return pageRepository.count();
    }

    @Override
    public long pageCount(long siteId) {
        return pageRepository.count(siteId);
    }

    @Override
    public void deletePage(Page page) {
        pageRepository.delete(page);
    }

    @Override
    public int deleteAllBySiteId(int siteId) {
        return pageRepository.deleteAllBySiteId(siteId);
    }

    @Override
    public void deleteAll() {
        pageRepository.deleteAll();
    }
}
