package webtools.rest.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Project: jira-client
 *
 * Created: 17. 10. 2016
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private LoggingHandler loggingHandler;

    /**
     * 
     * @param loggingHandler
     */
    public LoggingInterceptor(LoggingHandler loggingHandler) {
        this.loggingHandler = loggingHandler;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(body, request);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    /**
     * 
     * @param body
     * @param request
     * @throws IOException
     */
    private void logRequest(byte[] body, HttpRequest request) throws IOException {
        if (loggingHandler == null) {
            return;
        }

        String reqBody = "";
        if (body != null) {
            reqBody = new String(body, "UTF-8");
        }

        String url = request.getURI().getScheme() + "://" + request.getURI().getHost() + request.getURI().getPath();
        if (request.getURI().getQuery() != null) {
            url += request.getURI().getQuery();
        }

        loggingHandler.logRequest(request, url, reqBody);
    }

    /**
     * 
     * @param response
     * @throws IOException
     */
    private void logResponse(ClientHttpResponse response) throws IOException {
        if (loggingHandler == null) {
            return;
        }

        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }

        loggingHandler.logResponse(response, inputStringBuilder.toString());
    }

}
