package bank.fioclient.model.column;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class DoubleColumn extends Column {

    private Double value;

    @Override
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
