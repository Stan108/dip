package searchengine.services;

import searchengine.model.Index;
import searchengine.model.Lemma;

import java.util.List;

public interface LemmaRepositoryService {


    List<Lemma> getLemma(String lemmaName);
    void save(Lemma lemma);
    long lemmaCount();
    long lemmaCount(long siteId);
    void deleteAllLemmas(List<Lemma> lemmaList);
    List<Lemma> findLemmasByIndexing(List<Index> indexingList);
}

//    List<Lemma> getLemma (String lemmaName);
//    void save(Lemma lemma);
//    long lemmaCount();
//    long lemmaCount(long siteId);
//    void deleteAllLemmas(List<Lemma> lemmaList);
//    List<Lemma> findLemmasByIndexing(List<Index> indexingList);
