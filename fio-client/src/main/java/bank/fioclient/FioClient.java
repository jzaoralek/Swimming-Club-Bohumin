package bank.fioclient;

import java.util.Calendar;

import org.springframework.http.HttpMethod;

import webtools.rest.RestExecutor;
import webtools.rest.exception.RestException;
import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.AuthToken;
import bank.fioclient.exception.AuthTokenException;
import bank.fioclient.exception.EarlyAccessException;
import bank.fioclient.exception.FioClientException;
import bank.fioclient.model.Response;
import bank.fioclient.utils.DateUtils;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class FioClient {

    private String domain;
    private RestExecutor restExecutor;
    private FioLoggingHandler fioLoggingHandler;
    private ResponseMapper mapper;

    public FioClient(String domain) {
        this.domain = domain;
        fioLoggingHandler = new FioLoggingHandler(null);
        restExecutor = new RestExecutor(domain, fioLoggingHandler);
        mapper = new ResponseMapper();
    }

    /**
     * Pohyby na účtu za určené období
     * 
     * @param token autorizační token
     * @param datumOd
     * @param datumDo
     * @return
     * @throws EarlyAccessException 
     * @throws FioClientException 
     * @throws AuthTokenException
     */
    public AccountStatement transactions(AuthToken token, Calendar datumOd, Calendar datumDo) throws EarlyAccessException, AuthTokenException,
            FioClientException {
        try {
            fioLoggingHandler.setAuthToken(token);
            Response response = restExecutor.execute("/ib_api/rest/periods/{token}/{datum_od}/{datum_do}/transactions.json",
                    HttpMethod.GET, null, Response.class, token.getValue(), DateUtils.toString(datumOd), DateUtils.toString(datumDo));
            return mapper.map(response);
        } catch (RestException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * Pohyby na účtu od posledního stažení
     * <br/><br/>
     * 
     * Při každém dotazu bankovní systém automaticky zapíše novou zarážku posledního IDpohybu 
     * nebo data jestliže v odpovědi jsou pohyby na účtu. Pokud odpověď je prázdná, tak zarážka 
     * zůstává na serveru stejná a odpověď obsahuje pouze základní informace o účtu (hlavička).
     * 
     * @param token
     * @return
     * @throws EarlyAccessException
     * @throws AuthTokenException
     * @throws FioClientException
     */
    public AccountStatement lastTransactions(AuthToken token) throws EarlyAccessException, AuthTokenException, FioClientException {
        try {
            fioLoggingHandler.setAuthToken(token);
            Response response = restExecutor.execute("/ib_api/rest/last/{token}/transactions.json", HttpMethod.GET, null, Response.class, token.getValue());
            return mapper.map(response);
        } catch (RestException e) {
            handleException(e);
        }
        return null;
    }

    /**
     * 
     * @param e
     * @throws EarlyAccessException
     * @throws FioClientException
     * @throws AuthTokenException
     */
    private void handleException(RestException e) throws EarlyAccessException, FioClientException, AuthTokenException {
        if (e.isStatus() && e.getHttpStatus().intValue() == 409) {
            throw new EarlyAccessException(e.getHttpStatus());
        }
        if (e.isStatus() && e.getHttpStatus().intValue() == 500) {
            throw new AuthTokenException(e.getHttpStatus(), e);
        }
        throw new FioClientException(e.getHttpStatus(), e);
    }

    /**
     * 
     * @return
     */
    public String getDomain() {
        return domain;
    }
}
