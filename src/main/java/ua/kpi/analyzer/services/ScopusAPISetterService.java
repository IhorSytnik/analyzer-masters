package ua.kpi.analyzer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.analyzer.requests.HttpRequestBrowser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ihor Sytnik
 */
@Service
public class ScopusAPISetterService {

    @Autowired
    private HttpRequestBrowser scopusAPIRequestBrowser;

    public void setApiCredentials(String apikey, String insttoken) {
        Map<String, String> headers = new HashMap<>();
        if (apikey != null && !apikey.isBlank()) {
            headers.put("X-ELS-APIKey", apikey);
        }
        if (insttoken != null && !insttoken.isBlank()) {
            headers.put("X-ELS-Insttoken", insttoken);
        }
        scopusAPIRequestBrowser.setAll(headers);
    }
}
