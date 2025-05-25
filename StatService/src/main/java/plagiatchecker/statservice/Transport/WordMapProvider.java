package plagiatchecker.statservice.Transport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import plagiatchecker.statservice.Domain.Entities.WordMapRequestParams;
import plagiatchecker.statservice.Domain.Interfaces.Transport.WordMapProviderI;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WordMapProvider implements WordMapProviderI {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://quickchart.io")
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build())
            .build();

    @Override
    public byte[] getWordMap(String words) {

        Map<String, Object> params = new HashMap<>();
        params.put("text", words);
        params.put("width", 1500);
        params.put("height", 1000);
        params.put("removeStopwords", true);
        params.put("format", "png");
        params.put("fontScale", 25);

        Mono<byte[]> response = webClient
                .post()
                .uri("/wordcloud")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.IMAGE_PNG)
                .bodyValue(params)
                .retrieve()
                .bodyToMono(byte[].class);

        return response.block();
    }
}
