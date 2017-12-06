package com.jzaoralek.scb.dataservice.service.impl;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import bank.fioclient.FioClient;
import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.AuthToken;
import bank.fioclient.exception.AuthTokenException;
import bank.fioclient.exception.EarlyAccessException;
import bank.fioclient.exception.FioClientException;

@Service("fioService")
public class FioService {
	
//	@Autowired
//    private FioAuthTokenService fioAuthTokenService;

    private FioClient restClient;
//    private AccountBalance accountBalance; // cache
//    private int cacheTimeoutSec = 300; // 5 min

    @PostConstruct
    public void init() {
        restClient = new FioClient("https://www.fio.cz");
    }

    /**
     * Pohyby na účtu za určené období
     * 
     * @param token
     * @param datumOd
     * @param datumDo
     * @return
     * @throws EarlyAccessException
     * @throws AuthTokenException
     * @throws FioClientException
     */
    public AccountStatement transactions(AuthToken token, Calendar datumOd, Calendar datumDo) throws EarlyAccessException, AuthTokenException,
            FioClientException {
        return restClient.transactions(token, datumOd, datumDo);
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
        return restClient.lastTransactions(token);
    }

//    /**
//     * Vrátí zůstatek na účtu. Hodnota je v cache.
//     * 
//     * @return
//     * @throws Exception
//     */
//    public AccountBalance accountBalance() throws Exception {
//        Calendar timestamp = GregorianCalendar.getInstance();
//        if (accountBalance != null && isCacheValid(accountBalance)) {
//            return accountBalance;
//        }
//        accountBalance = new AccountBalance();
//        accountBalance.setCreated(timestamp);
//        AuthToken token = getValidToken();
//        if (token == null) {
//            accountBalance.setErrorMessage("Nenalezen platný auth-token");
//            accountBalance.setValid(false);
//            accountBalance.setValidTo(timestamp);
//            return accountBalance;
//        }
//        AccountStatement accountStatement = transactions(token, timestamp, timestamp);
//        accountBalance.setBalance(accountStatement.getInfo().getClosingBalance());
//        accountBalance.setValid(true);
//        accountBalance.setAccountNumber(accountStatement.getInfo().getAccountId() + "/" + accountStatement.getInfo().getBankId());
//        Calendar validTo = GregorianCalendar.getInstance();
//        validTo.add(Calendar.SECOND, cacheTimeoutSec);
//        accountBalance.setValidTo(validTo);
//        return accountBalance;
//    }

//    /**
//     * Vrátí platný autentizační klíč nebo null
//     * 
//     * @return
//     * @throws DatabaseException
//     * @throws CryptoException
//     */
//    private AuthToken getValidToken() throws DatabaseException, CryptoException {
//        FioAuthToken fioAuthToken = fioAuthTokenService.getValidToken(FioAuthTokenTypeEnum.READ);
//        if (fioAuthToken == null) {
//            return null;
//        }
//        return new AuthToken(fioAuthToken.getToken());
//    }

//    /**
//     * 
//     * @param info
//     * @return
//     */
//    private boolean isCacheValid(CacheInfo info) {
//        Calendar timestamp = GregorianCalendar.getInstance();
//        if (info.getValidTo() == null) {
//            return false;
//        }
//        if (info.getValidTo().after(timestamp)) {
//            return true;
//        }
//        return false;
//    }
}
