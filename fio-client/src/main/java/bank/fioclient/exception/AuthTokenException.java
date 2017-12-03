package bank.fioclient.exception;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class AuthTokenException extends FioClientException {

    /**
     * 
     */
    private static final long serialVersionUID = -5897636060869807857L;

    public AuthTokenException(Integer httpStatus, Throwable t) {
        super(httpStatus, "Neexistující nebo neaktivní token. Zkontrolujte si platnost a správnost tokenu v internetovém bankovnictví.", t);
    }
}
