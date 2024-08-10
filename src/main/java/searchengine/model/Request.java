package searchengine.model;


import lombok.Getter;
import searchengine.LemmaFinder;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Request {

    private final String req;
    private final List<String> reqLemmas;

    public Request(String req){
        this.req = req;
        reqLemmas = new ArrayList<>();
        try {
            LemmaFinder analyzer = LemmaFinder.getInstance();
            reqLemmas.addAll(analyzer.getLemmaSet(req));
        }catch (Exception e) {
            System.out.println("ошибка морфологочиского анализа");
        }
    }
}
