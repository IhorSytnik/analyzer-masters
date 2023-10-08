package ua.kpi.analyzer.requests;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Map;

/**
 * A class for different HTTP requests.
 *
 * @author Ihor Sytnik
 */
public class HttpRequestBrowser {
    private final WebClient client;
    private final HttpHeaders commonHeaders;
    private final RateLimiter limiter;

    /**
     *
     * @param baseUrl a URL to be used as a base (e.g. "https://steamcommunity.com"), to be extended later by additional
     *                paths (e.g. "/discussions/").
     * @param followRedirects {@code true} if you want it to follow redirects, {@code false} - otherwise.
     * @param commonHeaders headers that are usually shared between requests on the <b>baseUrl</b> web-site.
     * @param sleepMilliseconds delay between requests.
     */
    public HttpRequestBrowser(String baseUrl, boolean followRedirects,
                              HttpHeaders commonHeaders, long sleepMilliseconds) {
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(followRedirects)
                ))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
        this.commonHeaders = commonHeaders;
        this.limiter = RateLimiter.create(1000d / sleepMilliseconds);
    }

    /**
     * Makes a request to <b>uri</b>, with http method <b>method</b> and headers <b>headers</b>.
     *
     * @param uri a URI the request should be sent to.
     * @param method http request method.
     * @return response.
     */
    public WebClient.ResponseSpec request(String uri, HttpMethod method,
                                          String body) {
        limiter.acquire();
        return client.method(method)
                .uri(uri)
                .body(BodyInserters.fromValue(body))
                .headers(httpHeaders -> {
                    httpHeaders.setAll(this.commonHeaders.toSingleValueMap());
                })
                .retrieve();
    }

    public void addHeaders(String headerName, @Nullable String headerValue) {
        commonHeaders.add(headerName, headerValue);
    }

    public void addHeaders(HttpHeaders headers) {
        commonHeaders.addAll(headers);
    }

    public void setAll(Map<String, String> headers) {
        commonHeaders.setAll(headers);
    }

    public WebClient.ResponseSpec post(String uri,
                                       String body) throws InterruptedException {
        return request(uri, HttpMethod.POST, body);
    }

    /**
     * A shortcut method to make a GET request, with common headers to the given <b>uri</b>.
     *
     * @param uri an uri the request should be sent to.
     * @return response.
     * @throws InterruptedException see {@link #request(String, HttpMethod, String)}.
     */
    public WebClient.ResponseSpec get(String uri) throws InterruptedException {
        return request(uri, HttpMethod.GET, "");
    }
}
