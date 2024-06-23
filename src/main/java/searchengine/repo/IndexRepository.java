package searchengine.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Index;


import java.util.List;

@Repository
public interface IndexRepository extends CrudRepository<Index, Integer> {
    List<Index> findByLemmaId (int lemmaId);
    List<Index> findByPageId (int pageId);
    Index findByLemmaIdAndPageId (int lemmaId, int pageId);
}

