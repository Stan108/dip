package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.repo.LemmaRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class LemmaRepositoryServiceImpl implements LemmaRepositoryService{
    private final LemmaRepository lemmaRepository;

    @Override
    public List<Lemma> getLemma(String lemmaName) {
        List<Lemma> lemmas = null;
        try {
            lemmas = lemmaRepository.findByLemma(lemmaName);
        } catch (Exception e) {
            System.out.println(lemmaName);
            e.printStackTrace();
        }
        return lemmas;
    }

    @Override
    public void save(Lemma lemma) {
        lemmaRepository.save(lemma);
    }

    @Override
    public long lemmaCount() {
        return lemmaRepository.count();
    }

    @Override
    public long lemmaCount(long siteId) {
        return lemmaRepository.countBySiteId(siteId);
    }

    @Override
    public void deleteAllLemmas(List<Lemma> lemmaList) {
        lemmaRepository.deleteAll();
    }

    @Override
    public List<Lemma> findLemmasByIndexing(List<Index> indexingList) {
        int[] lemmaIdList = new int[indexingList.size()];
        for (int i = 0; i < indexingList.size(); i++) {
            lemmaIdList[i] = indexingList.get(i).getId();
        }

        return lemmaRepository.findByIdIn(lemmaIdList);
    }

    @Override
    public void deleteAll() {
        lemmaRepository.deleteAll();
    }
}
//    private final LemmaRepository lemmaRepository;
//    @Override
//    public List<Lemma> getLemma(String lemmaName) {
//        List<Lemma> lemmas = null;
//        lemmas = lemmaRepository.findByLemma(lemmaName);
//        System.out.println(lemmaName);
//        return lemmas;
//    }
//
//    @Override
//    public void save(Lemma lemma) {
//        lemmaRepository.save(lemma);
//    }
//
//    @Override
//    public long lemmaCount() {
//        return lemmaRepository.count();
//    }
//
//    @Override
//    public long lemmaCount(long siteId) {
//        return lemmaRepository.count(siteId);
//    }
//
//    @Override
//    public void deleteAllLemmas(List<Lemma> lemmaList) {
//        lemmaRepository.deleteAll();
//    }
//
//    @Override
//    public List<Lemma> findLemmasByIndexing(List<Index> indexingList) {
//        int[] lemmaIdList = indexingList.stream().mapToInt(Index::getLemmaId).toArray();
//        return lemmaRepository.findById(lemmaIdList);
//    }

//private final LemmaRepository lemmaRepository;
//
//@Override
//public List<Lemma> getLemma(String lemmaName) {
//    List<Lemma> lemmas = lemmaRepository.findByLemma(lemmaName);
//    System.out.println(lemmaName);
//    return lemmas;
//}
//
//@Override
//public void save(Lemma lemma) {
//    lemmaRepository.save(lemma);
//}
//
//@Override
//public long lemmaCount() {
//    return lemmaRepository.count();
//}
//
//@Override
//public long lemmaCount(long siteId) {
//    return lemmaRepository.countBySiteId(siteId);
//}
//
//@Override
//public void deleteAllLemmas(List<Lemma> lemmaList) {
//    lemmaRepository.deleteAll(lemmaList);
//}
//
//@Override
//public List<Lemma> findLemmasByIndexing(List<Index> indexingList) {
//    int[] lemmaIdList = indexingList.stream().mapToInt(index -> index.getLemma().getId()).toArray();
//    return lemmaRepository.findByIdIn(lemmaIdList);
//}