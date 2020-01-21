package ksaito.callApi.service.visa;

import ksaito.callApi.Util;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ksaito.callApi.Util.print;

@Service
@ConfigurationProperties(prefix = "visa")
@Setter
public class VisaServiceImpl implements VisaService {
    private String baseUrl;
    private String keyStorePath;
    private String keyStorePassword;
    private String privateKeyPassword;
    private String trustStorePath;
    private String trustStorePassword;
    private String userId;
    private String password;

    @Override
    public void call() {
//        helloWorld();
//        transactionQuery();
        resolve();
        fundsTransferInquiry();
    }

    private void helloWorld () {
        Optional.ofNullable(createClient()).ifPresent(client -> {
            try {
                HttpGet request = new HttpGet(
                        new  URIBuilder(baseUrl)
                                .setPath("/vdp/helloworld")
                                .build()
                );
                print("======リクエスト======");
                print(request.getURI());
                printResponse(client.execute(request));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void transactionQuery() {
        Optional.ofNullable(createClient()).ifPresent(client -> {
            try {
                HttpGet request = new HttpGet(
                        new URIBuilder(baseUrl)
                                .setPath("/visadirect/v1/transactionquery")
                                .setParameter("acquiringBIN", "408999")
                                .setParameter("transactionIdentifier", "587010322176103")
                                .build()
            );
                print("======リクエスト======");
                print(request.getURI());
                printResponse(client.execute(request));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resolve() {
        Optional.ofNullable(createClient()).ifPresent(client -> {
            try {
                HttpPost request = new HttpPost(
                        new URIBuilder(baseUrl)
                                .setPath("/visaaliasdirectory/v1/resolve")
                                .build()
                );
                List<NameValuePair> requestParameter = new ArrayList<>();
                Collections.addAll(
                        requestParameter,
                        new BasicNameValuePair("alias","254711333888"),
                        new BasicNameValuePair("businessApplicationId","PP")
                );
                request.setHeaders(
                        new Header[]{
                                new BasicHeader(
                                        HttpHeaders.AUTHORIZATION,
                                        "Basic " + HttpHeaders.encodeBasicAuth(userId, password, StandardCharsets.UTF_8)
                                ),
                                new BasicHeader(
                                        HttpHeaders.ACCEPT,
                                        "application/json,application/octet-stream"
                                ),
                                new BasicHeader(
                                        HttpHeaders.CONTENT_TYPE,
                                        "application/json"
                                )
                        }
                );
//                request.setEntity(
//                        EntityBuilder
//                                .create()
//                                .setParameters(requestParameter)
//                                .setContentEncoding(StandardCharsets.UTF_8.name())
//                                .setContentType(ContentType.APPLICATION_JSON)
//                                .build()
//                );
                request.setEntity(
                        new StringEntity(
                                "{\"alias\": \"74958889999\",\"businessApplicationId\": \"PP\"}",
                                ContentType.APPLICATION_JSON
                        )
                );
                print("======リクエスト======");
                print(request.getURI());
                Arrays.stream(request.getAllHeaders()).forEach(Util::print);
                print("======リクエストパラメーター======");
                print(EntityUtils.toString(request.getEntity()));
                printResponse(client.execute(request));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fundsTransferInquiry() {
        Optional.ofNullable(createClient()).ifPresent(client -> {
            try {
                List<NameValuePair> requestParameter = new ArrayList<>();
                Collections.addAll(
                        requestParameter,
                        new BasicNameValuePair("systemsTraceAuditNumber","350420"),
                        new BasicNameValuePair("retrievalReferenceNumber","401010350420"),
                        new BasicNameValuePair("primaryAccountNumber","4895142232120006")
                );
                HttpPost request = new HttpPost(
                        new URIBuilder(baseUrl)
                                .setPath("/paai/fundstransferattinq/v5/cardattributes/fundstransferinquiry")
                                .build()
                );
                request.setHeaders(
                        new Header[]{
                                new BasicHeader(
                                        HttpHeaders.AUTHORIZATION,
                                        "Basic " + HttpHeaders.encodeBasicAuth(userId, password, StandardCharsets.UTF_8)
                                ),
                                new BasicHeader(
                                        HttpHeaders.ACCEPT,
                                        "application/json,application/octet-stream"
                                ),
                                new BasicHeader(
                                        HttpHeaders.CONTENT_TYPE,
                                        "application/json"
                                )
                        }
                );
//                request.setEntity(new UrlEncodedFormEntity(requestParameter));
//                request.setEntity(
//                        EntityBuilder
//                                .create()
//                                .setParameters(requestParameter)
//                                .setContentEncoding(StandardCharsets.UTF_8.name())
//                                .setContentType(ContentType.APPLICATION_JSON)
//                                .build()
//                );
                request.setEntity(
                        new StringEntity(
                                "{\"systemsTraceAuditNumber\": \"350420\",\"retrievalReferenceNumber\": \"401010350420\",\"primaryAccountNumber\": \"4895142232120006\"}",
                                ContentType.APPLICATION_JSON
                        )
                );
                print("======リクエスト======");
                print(request.getURI());
                Arrays.stream(request.getAllHeaders()).forEach(Util::print);
                print("======リクエストパラメーター======");
                print(EntityUtils.toString(request.getEntity()));
                request.setConfig(
                        RequestConfig
                                .custom()
                                .build()
                );
                printResponse(client.execute(request));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private CloseableHttpClient createClient() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(Files.newInputStream(Paths.get(keyStorePath)), keyStorePassword.toCharArray());
            KeyStore trustKeyStore = KeyStore.getInstance("JKS");
            trustKeyStore.load(Files.newInputStream(Paths.get(trustStorePath)), trustStorePassword.toCharArray());
            // Load client certificate into key store
            SSLContext sslcontext = null;
            sslcontext = SSLContexts.custom()
//                    .loadKeyMaterial(
//                            new File(keyStorePath),
//                            keyStorePassword.toCharArray(),
//                            privateKeyPassword.toCharArray()
//                    )
                    .loadKeyMaterial(
                            keyStore,
                            keyStorePassword.toCharArray()
                    )
//                    .loadTrustMaterial(Paths.get(trustStorePath).toFile(), trustStorePassword.toCharArray())
//                    .loadTrustMaterial(
//                            trustKeyStore,
//                            null
//                    )
                    .build();
            // Allow TLSv1.2 protocol only
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1.2"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier()
            );

            HttpHost httpHost = new HttpHost("sandbox.api.visa.com", 443, "https");
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(httpHost.getHostName(), httpHost.getPort()),
                    new UsernamePasswordCredentials(userId, password));
//            List<Header> headers = new ArrayList<>();
//            headers.add(
//                    new BasicHeader(
//                            HttpHeaders.AUTHORIZATION,
//                            "Basic " + HttpHeaders.encodeBasicAuth(userId, password, Charset.defaultCharset())
//                    )
//            );
//            headers.add(
//                    new BasicHeader(
//                            HttpHeaders.ACCEPT,
//                            "application/json,application/octet-stream"
//                    )
//            );
//            headers.add(
//                    new BasicHeader(
//                            HttpHeaders.CONTENT_TYPE,
//                            "application/json"
//                    )
//            );
            return HttpClients.custom()
                    .setSSLSocketFactory(sslSocketFactory)
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build();

        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException | CertificateException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void printResponse(CloseableHttpResponse httpResponse) throws IOException {
        if (200 != httpResponse.getStatusLine().getStatusCode()) {
            print("======ステータス======");
            print(httpResponse.getStatusLine());
            print("======ヘッダー======");
            Arrays.stream(httpResponse.getAllHeaders()).forEach(Util::print);
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        String entityString = EntityUtils.toString(httpEntity);
        print(entityString);
        Optional.ofNullable(httpResponse.getEntity()).map(entity -> {
            try {
                print("======ボディ======");
                return EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
                return e;
            } finally {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).ifPresent(Util::print);
    }
}
