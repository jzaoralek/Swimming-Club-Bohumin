package bank.fioclient.exception;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class FioClientException extends Exception {

    private Integer httpStatus;

    /**
     * 
     */
    private static final long serialVersionUID = -2957722231990352330L;

    public FioClientException(Throwable t) {
        super(t);
    }

    public FioClientException(Integer httpStatus, Throwable t) {
        super(t);
        this.httpStatus = httpStatus;
    }

    public FioClientException(Integer httpStatus, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public FioClientException(Integer httpStatus, String msg, Throwable t) {
        super(msg, t);
        this.httpStatus = httpStatus;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

}
