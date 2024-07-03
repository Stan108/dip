package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Service;
import searchengine.Index;
import searchengine.responces.FalseResponseService;
import searchengine.responces.ResponseService;
import searchengine.responces.TrueResponseService;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService{
    private final Index index;
    private final Log log = LogFactory.getLog(IndexingServiceImpl.class);




    @Override
    public ResponseService startIndexingAll() {
        log.info("Попытка запуска индексации всех сайтов");
        ResponseService response = null;
        boolean indexing = true;
        try {
            indexing = index.allSiteIndexing();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (indexing) {
            response = new TrueResponseService();
            log.info("Индексация всех сайтов запущена");
        }
        else {
            response = new FalseResponseService("Индексация уже запущена");
            log.warn("Индексация всех сайтов не запущена. Т.к. процесс индексации был запущен ранее.");
        }
        return response;
    }

    @Override
    public ResponseService stopIndexingAll() {
        boolean indexing = index.stopSiteIndexing();
        log.info("попытка остановки индексации");
        ResponseService response;
        if (indexing) {
            response = new TrueResponseService();
            log.info("индексация остановлена");
        } else {
            response = new FalseResponseService("индексация не запущена");
            log.warn("Остановка индексаии не может быть выполнена, так как индексация не запущена");
        }

        return null;
    }

    @Override
    public ResponseService startIndexingOne(String url) {
        ResponseService resp;
        String response;
        response = index.checkedSiteIndexing(url);
        resp = switch (response) {
            case "not found" -> new FalseResponseService("Страница находится за пределами сайтов,\" +\n" +
                    "                    \" указанных в конфигурационном файле");
            case "false" -> new FalseResponseService("Индексация страницы уже запущена");
            default -> new TrueResponseService();
        };
        return resp;
    }
}
