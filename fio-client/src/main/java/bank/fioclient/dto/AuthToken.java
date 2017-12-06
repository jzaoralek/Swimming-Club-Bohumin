package bank.fioclient.dto;

/**
 * Project: fio-client
 *
 * Created: 14. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class AuthToken {

    private String value;

    public AuthToken() {
    }

    public AuthToken(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "********************";
    }
}
