package goncharov.ru.service.implementation;

import goncharov.ru.service.GifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import goncharov.ru.client.GifClient;

import java.util.Map;

/**
 * Service for Giphy.com
 */
@Service
public class GiphyGifService implements GifService {
    private GifClient gifClient;

    @Value("${giphy.api.key}")
    private String apiKey;

    @Autowired
    public GiphyGifService(GifClient gifClient) {
        this.gifClient = gifClient;
    }

    /**
     * Response from Giphy.com just sends to a client as a ResposeEntity
     * with a slight modification - adding compareResult
     * for a visual result check convenience.
     *
     * @param tag
     * @return
     */
    @Override
    public ResponseEntity<Map> getGif(String tag) {
        return null;
    }
}
