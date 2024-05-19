package searchengine.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import searchengine.model.Page;

import java.util.Optional;

public interface PageRepository extends CrudRepository<Page, Integer> {

    Page findByPath (String path);

    Optional<Page> findByIdAndSiteId (int id, int siteId);

    @Query(value = "SELECT count(*) from Page where site_id = :id")
    long count(@Param("id") long id);

    @Query("DELETE FROM Page p WHERE p.site.id = :siteId")
    int deleteAllBySiteId(@Param(("siteId")) int siteId);
}
