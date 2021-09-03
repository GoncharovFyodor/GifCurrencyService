package goncharov.ru.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for getting random gif from giphy.com
 */
@org.springframework.cloud.openfeign.FeignClient(name = "giphyClient", url = "${giphy.url.general}")
public interface FeignGiphyGifClient extends GifClient {
    @Override
    @GetMapping("/random")
    public ResponseEntity<Map> getRandomGif
            (@RequestParam("api_key") String apiKey,
             @RequestParam("tag") String tag);
}
