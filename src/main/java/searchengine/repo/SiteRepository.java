package searchengine.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import java.util.Optional;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {
        Site findByUrl(String url);
        Optional<Site> findByUrlContaining(String url);
}
