package bank.fioclient.exception;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class EarlyAccessException extends FioClientException {

    /**
     * 
     */
    private static final long serialVersionUID = 1543993974292818239L;

    public EarlyAccessException(Integer httpStatus) {
        super(httpStatus, "Není dodržen minimální interval 30 sekund mezi stažením dat z banky / "
                + "uploadem dat do banky u konkrétního tokenu (bez ohledu na typ formátu). "
                + "Konkrétní token lze použít pouze 1x pro čtení nebo zápis během 30 sekund");
    }
}
