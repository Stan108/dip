package searchengine.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;


import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IndexRepository extends CrudRepository<Index, Integer> {
    List<Index> findByLemmaId (int lemmaId);
    List<Index> findByPageId (int pageId);
    Index findByLemmaIdAndPageId (int lemmaId, int pageId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Index i WHERE i.page.id = :pageId")
    void deleteByPageId(@Param("pageId") int pageId);
}

