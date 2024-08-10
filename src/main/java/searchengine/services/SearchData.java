package searchengine.services;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SearchData {
    String site;
    String siteName;
    String uri;
    String title;
    String snippet;
    double relevance;

    public SearchData() {
    }

    @Override
    public String toString() {
        return "SearchData{" +
                "uri='" + uri + '\'' +
                ", title='" + title + '\'' +
                ", snippet='" + snippet + '\'' +
                ", relevance=" + relevance +
                '}';
    }
}

