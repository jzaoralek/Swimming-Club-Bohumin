package bank.fioclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import webtools.rest.logging.LoggingHandler;
import bank.fioclient.dto.AuthToken;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class FioLoggingHandler implements LoggingHandler {

    private final static Logger logger = LoggerFactory.getLogger(FioLoggingHandler.class);

    private AuthToken authToken;

    public FioLoggingHandler(AuthToken authToken) {
        this.authToken = authToken;
    }

    @Override
    public void logRequest(HttpRequest request, String url, String body) throws IOException {
        logger.info("REST request: " + url.replace(authToken.getValue(), hideToken(authToken)));
    }

    @Override
    public void logResponse(ClientHttpResponse response, String body) throws IOException {
        logger.info("REST response: " + response.getRawStatusCode() + "   " + body);
    }

    private String hideToken(AuthToken token) {
        String authToken = token.getValue();
        return authToken.subSequence(0, 3) + "**********" + authToken.subSequence(authToken.length() - 3, authToken.length());
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

}
