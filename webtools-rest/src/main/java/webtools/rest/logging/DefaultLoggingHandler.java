package webtools.rest.logging;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Project: jira-client
 *
 * Created: 17. 10. 2016
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class DefaultLoggingHandler implements LoggingHandler {

    @Override
    public void logRequest(HttpRequest request, String url, String body) {
        print("REST request: " + url);
    }

    @Override
    public void logResponse(ClientHttpResponse response, String body) throws IOException {
        print("REST response: " + response.getRawStatusCode() + "   " + body);
    }

    public static void print(String message) {
        System.out.println(message);
    }
}
