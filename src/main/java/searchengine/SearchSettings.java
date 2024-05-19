package searchengine;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
@Getter
@Setter
@Component
@ConfigurationProperties("indexing-settings")
public class SearchSettings {
    private List<HashMap<String, String>> sites;
    private String agent;
}
