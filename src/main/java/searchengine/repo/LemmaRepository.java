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
    int countBySiteId(int siteId);
    List<Lemma> findByIdIn(int[] ids);
    @Modifying
    @Transactional
    @Query("DELETE FROM Lemma l WHERE l.id IN :lemmaIds")
    void deleteByLemmaIds(@Param("lemmaIds") List<Integer> lemmaIds);
}
