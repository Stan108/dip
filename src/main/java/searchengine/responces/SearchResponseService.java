package searchengine.responces;


import lombok.Getter;
import lombok.Setter;
import searchengine.services.SearchData;

@Setter
public class SearchResponseService implements ResponseService {
    private boolean result;
    @Getter
    private int count;
    @Getter
    private SearchData[] data;

    public SearchResponseService() {
    }

    public SearchResponseService(boolean result) {
        this.result = result;
    }

    public SearchResponseService(boolean result, int count, SearchData[] data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }

    public boolean getResult() {
        return result;
    }

}
