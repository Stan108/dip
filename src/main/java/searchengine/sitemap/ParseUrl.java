package searchengine.sitemap;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveTask;

@RequiredArgsConstructor
public class ParseUrl extends RecursiveTask<String> {

    public final static List<String> urlList = new Vector<>();
    private final static Log log = LogFactory.getLog(ParseUrl.class);
    private final String url;
    private final boolean isInterrupted;

    @Override
    protected String compute() {
        if(isInterrupted) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(url);

        try {
            Thread.sleep(200);
            Document doc = getDocumentByurl(url);
            Elements rootElements = doc.select("a");
            List<ParseUrl> linkParsers = new ArrayList<>();
            rootElements.forEach(element -> {
                String link = element.attr("abs:href");
                if (link.startsWith(element.baseUri())
                    && !link.equals(element.baseUri())
                    && !link.contains("#")
                    && !link.contains("pdf")
                    && !urlList.contains(link)) {
                    urlList.add(link);
                    ParseUrl linkParse = new ParseUrl(link, false);
                    linkParse.fork();
                    linkParsers.add(linkParse);
                }
            });

            for (ParseUrl lg : linkParsers) {
                String text = lg.join();
                if (!text.isEmpty()) {
                    result.append("\n");
                    result.append(text);
                }
            }


        } catch (InterruptedException | IOException e) {
            log.warn("Ошибка парсинга сайта: " + url);
        }


        return result.toString();
    }

    private Document getDocumentByurl(String url) throws InterruptedException, IOException {
        Thread.sleep(200);
        return Jsoup.connect(url)
                .maxBodySize(0)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .get();
    }
}
