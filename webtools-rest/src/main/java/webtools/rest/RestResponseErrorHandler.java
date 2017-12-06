package webtools.rest;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Project: webtools-rest
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class RestResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse paramClientHttpResponse) throws IOException {

    }

    @Override
    public boolean hasError(ClientHttpResponse paramClientHttpResponse) throws IOException {
        return false;
    }

}
