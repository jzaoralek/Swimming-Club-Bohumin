package bank.fioclient;

import java.util.ArrayList;
import java.util.List;

import bank.fioclient.dto.AccountInfo;
import bank.fioclient.dto.AccountStatement;
import bank.fioclient.dto.Transaction;
import bank.fioclient.model.Response;
import bank.fioclient.model.TransactionRaw;
import bank.fioclient.model.column.Column;
import bank.fioclient.utils.DateUtils;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class ResponseMapper {

    /**
     * 
     * @param res
     * @return
     */
    public AccountStatement map(Response res) {
        AccountStatement as = new AccountStatement();
        as.setInfo(res.getAccountStatement().getInfo());
        as.setTransactions(mapTransactions(res));
        return as;
    }

    /**
     * 
     * @param res
     * @return
     */
    private List<Transaction> mapTransactions(Response res) {
        List<Transaction> result = new ArrayList<Transaction>();

        if (res.getAccountStatement().getTransactionList() == null || res.getAccountStatement().getTransactionList().getTransaction() == null) {
            return result;
        }

        for (TransactionRaw rawTransaction : res.getAccountStatement().getTransactionList().getTransaction()) {
            result.add(mapTransaction(rawTransaction));
        }

        return result;
    }

    /**
     * 
     * @param raw
     * @return
     */
    private Transaction mapTransaction(TransactionRaw raw) {
        Transaction t = new Transaction();

        AccountInfo ai = new AccountInfo();
        ai.setCisloUctu((String) getValue(raw.getColumn2()));
        ai.setKodBanky((String) getValue(raw.getColumn3()));
        ai.setNazevBanky((String) getValue(raw.getColumn12()));
        ai.setNazevUctu((String) getValue(raw.getColumn10()));

        t.setProtiucet(ai);
        t.setDatumPohybu(DateUtils.toCalendar((String) getValue(raw.getColumn0())));
        t.setObjem((Double) getValue(raw.getColumn1()));
        t.setKonstantniSymbol((String) getValue(raw.getColumn4()));
        t.setVariabilniSymbol((String) getValue(raw.getColumn5()));
        t.setUzivatelskaIdentifikace((String) getValue(raw.getColumn7()));
        t.setTyp((String) getValue(raw.getColumn8()));
        t.setMena((String) getValue(raw.getColumn14()));
        t.setIdPokynu((Long) getValue(raw.getColumn17()));
        t.setIdPohybu((Long) getValue(raw.getColumn22()));
        t.setKomentar((String) getValue(raw.getColumn25()));
        t.setProvedl((String) getValue(raw.getColumn9()));
        t.setZpravaProPrijemnce((String) getValue(raw.getColumn16()));
        return t;
    }

    /**
     * 
     * @param c
     * @return
     */
    private Object getValue(Column c) {
        if (c != null) {
            return c.getValue();
        }
        return null;
    }
}
