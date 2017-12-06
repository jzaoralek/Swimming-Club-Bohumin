package bank.fioclient.model;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class Response {

    private AccountStatementRaw accountStatement;

    public AccountStatementRaw getAccountStatement() {
        return accountStatement;
    }

    public void setAccountStatement(AccountStatementRaw accountStatement) {
        this.accountStatement = accountStatement;
    }
}
