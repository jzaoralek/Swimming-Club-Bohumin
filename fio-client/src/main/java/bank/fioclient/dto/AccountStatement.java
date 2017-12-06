package bank.fioclient.dto;

import java.util.List;

import bank.fioclient.model.Info;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class AccountStatement {

    private Info info;
    private List<Transaction> transactions;

    public AccountStatement() {
    }

    public AccountStatement(Info info, List<Transaction> transactions) {
        this.info = info;
        this.transactions = transactions;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}
