package webtools.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Project: webtools-rest
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class RestException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5915885797799440100L;

    private Integer httpStatus;

    public RestException(Throwable t) {
        super(t);

        if (t instanceof HttpStatusCodeException) {
            httpStatus = ((HttpStatusCodeException) t).getStatusCode().value();
        }
    }

    public RestException(HttpStatus status) {
        super("Server vratil chybovy http status kod: " + status.value());
        httpStatus = status.value();
    }

    /**
     * 
     * @param status
     * @throws RestException
     */
    public static void throwIfBadStatus(HttpStatus status) throws RestException {
        if (status.is3xxRedirection() || status.is4xxClientError() || status.is5xxServerError()) {
            throw new RestException(status);
        }
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    /**
     * 
     * @return
     */
    public boolean isStatus() {
        if (httpStatus != null && httpStatus > 0) {
            return true;
        }
        return false;
    }
}
