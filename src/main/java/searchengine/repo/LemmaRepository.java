package searchengine.repo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {
    List<Lemma> findByLemma(String lemma);
    long countBySiteId(long siteId);
    List<Lemma> findByIdIn(int[] ids);
    @Modifying
    @Transactional
    @Query("DELETE FROM Lemma l WHERE l.id IN :lemmaIds")
    void deleteByLemmaIds(@Param("lemmaIds") List<Integer> lemmaIds);


}

//    List<Lemma> findByLemma (String lemma);
//
//    @Query(value = "SELECT * from search_lemma WHERE id IN(:id)", nativeQuery = true)
//    List<Lemma> findById (int[] id);
//
//    @Query(value = "SELECT count(*) from Lemma where site_id = :id")
//    long count(@Param("id") long id);