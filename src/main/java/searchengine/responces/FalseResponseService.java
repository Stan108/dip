package searchengine.responces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FalseResponseService implements ResponseService {
    private final String error;
    @Override
    public boolean getResult() {
        return false;
    }

}
