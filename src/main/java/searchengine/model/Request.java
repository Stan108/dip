package searchengine.model;


import searchengine.LemmaFinder;

import java.util.ArrayList;
import java.util.List;

public class Request {

    private String req;
    private List<String> reqLemmas;

    public List<String> getReqLemmas() {
        return reqLemmas;
    }

    public String getReq() {
        return req;
    }

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
