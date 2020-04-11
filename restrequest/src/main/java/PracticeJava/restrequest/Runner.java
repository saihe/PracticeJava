package PracticeJava.restrequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("checkstyle:Indentation")
@Slf4j
@Component
public class Runner implements CommandLineRunner {
    @Value("${root}")
    private String root;
    @Value("${uri}")
    private String uri;
    @Override
    public void run(String... args) throws Exception {
        CloseableHttpClient httpClient = HttpUtil.createHttpClient(true, proxy);

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return new BufferingClientHttpRequestFactory(requestFactory);
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(root)
                .proxy
                .build();

        ResponseEntity<String> res = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                null,
                String.class
        );

        log.info(res.getBody());
        System.exit(0);
    }
}
