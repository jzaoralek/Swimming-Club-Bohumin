package bank.fioclient.model;

import java.util.List;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class TransactionList {

    private List<TransactionRaw> transaction;

    public List<TransactionRaw> getTransaction() {
        return transaction;
    }

    public void setTransaction(List<TransactionRaw> transaction) {
        this.transaction = transaction;
    }

}
