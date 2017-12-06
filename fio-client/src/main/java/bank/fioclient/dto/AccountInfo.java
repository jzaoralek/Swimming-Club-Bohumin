package bank.fioclient.dto;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class AccountInfo {

    private String cisloUctu;
    private String kodBanky;
    private String nazevBanky;
    private String nazevUctu;

    public AccountInfo() {
    }

    public String getNazevUctu() {
        return nazevUctu;
    }

    public void setNazevUctu(String nazevUctu) {
        this.nazevUctu = nazevUctu;
    }

    public String getCisloUctu() {
        return cisloUctu;
    }

    public void setCisloUctu(String cisloUctu) {
        this.cisloUctu = cisloUctu;
    }

    public String getKodBanky() {
        return kodBanky;
    }

    public void setKodBanky(String kodBanky) {
        this.kodBanky = kodBanky;
    }

    public String getNazevBanky() {
        return nazevBanky;
    }

    public void setNazevBanky(String nazevBanky) {
        this.nazevBanky = nazevBanky;
    }

}
