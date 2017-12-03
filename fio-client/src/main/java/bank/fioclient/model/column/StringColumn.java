package bank.fioclient.model.column;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class StringColumn extends Column {

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
