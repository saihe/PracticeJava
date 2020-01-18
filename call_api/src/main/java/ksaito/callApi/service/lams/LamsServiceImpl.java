package ksaito.callApi.service.lams;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import static ksaito.callApi.Util.print;

@Service
@ConfigurationProperties(prefix = "lams")
@Setter
public class LamsServiceImpl implements LamsService {
    private String baseUrl;

    @Override
    public void call() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(baseUrl)
                .build();
        restTemplate.setMessageConverters(
                Arrays.asList(
                        new MappingJackson2HttpMessageConverter(
                                Jackson2ObjectMapperBuilder
                                        .json()
                                        .indentOutput(true)
                                        .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                                        .build()
                        )
                )
        );
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/lams_api_dev/v1/user/get");
        final RequestEntity.BodyBuilder requestBuilder = RequestEntity.method(HttpMethod.GET, builder.build().toUri());
        requestBuilder.header("token", "abc");
        requestBuilder.header(HttpHeaders.ACCEPT, "application/json");
        requestBuilder.header(HttpHeaders.CONTENT_TYPE, "application/json");
        RequestEntity<Object> requestEntity = requestBuilder.body(null);
        print(baseUrl);
        print(requestEntity.getHeaders());

        ResponseEntity<GetUserResponse> responseEntity = restTemplate.exchange(requestEntity, GetUserResponse.class);
        HttpStatus statusCode = responseEntity.getStatusCode();
        HttpHeaders responseHeaders = responseEntity.getHeaders();

        print(statusCode);
        print(responseHeaders);
        print(responseEntity.getBody());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetUserResponse {
        private User user;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class User {
            private String userName;
            private String userCode;
            private String mailAddress;
        }

        public String toString() {
            return "user_name：" + this.user.getUserName() + ", user_code：" + this.user.getUserCode() + ", mail_address：" + this.user.getMailAddress();
        }
    }
}
