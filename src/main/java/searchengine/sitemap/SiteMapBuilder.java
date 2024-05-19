package searchengine.sitemap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class SiteMapBuilder {
    private final String url;
    private final boolean isInterrupted;
    private List<String> siteMap;

    public void buildSiteMap() {
        String text = new ForkJoinPool().invoke(new ParseUrl(url, isInterrupted));
        siteMap = stringToList(text);
    }

    private List<String> stringToList(String text) {
        return Arrays.stream(text.split("\n")).collect(Collectors.toList());
    }
}
