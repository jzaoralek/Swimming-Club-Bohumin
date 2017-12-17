package bank.fioclient.dto;

import java.util.Calendar;

/**
 * Project: fio-client
 *
 * Created: 13. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class Transaction {

    private AccountInfo protiucet;
    private Calendar datumPohybu; // Datum - datum pohybu ve tvaru rrrr-mm-dd+GMT
    private Double objem; // Objem - velikost přijaté (odeslané) částky
    private String konstantniSymbol; // KS
    private String variabilniSymbol; // VS - může začínat nulou (pouze číslice)
    private String uzivatelskaIdentifikace; // Uživatelská identifikace
    private String typ; // Typ - např. "Bezhotovostní platba"
    private String mena; // Měna - např. "CZK"
    private Long idPokynu; // ID pokynu
    private Long idPohybu; // ID pohybu - Všechny pohyby na účtech v bankovním systému jsou evidovány podle jedinečného klíče IDpohyb.
    private String komentar; // Komentář
    private String provedl; // Provedl - např. "Wojnar, Aleš"
    private String zpravaProPrijemnce;

    public Transaction() {
    }

    public String getZpravaProPrijemnce() {
        return zpravaProPrijemnce;
    }

    public void setZpravaProPrijemnce(String zpravaProPrijemnce) {
        this.zpravaProPrijemnce = zpravaProPrijemnce;
    }

    public String getProvedl() {
        return provedl;
    }

    public void setProvedl(String provedl) {
        this.provedl = provedl;
    }

    public AccountInfo getProtiucet() {
        return protiucet;
    }

    public void setProtiucet(AccountInfo protiucet) {
        this.protiucet = protiucet;
    }

    public Calendar getDatumPohybu() {
        return datumPohybu;
    }

    public void setDatumPohybu(Calendar datumPohybu) {
        this.datumPohybu = datumPohybu;
    }

    public Double getObjem() {
        return objem;
    }

    public void setObjem(Double objem) {
        this.objem = objem;
    }

    public String getKonstantniSymbol() {
        return konstantniSymbol;
    }

    public void setKonstantniSymbol(String konstantniSymbol) {
        this.konstantniSymbol = konstantniSymbol;
    }

    public String getVariabilniSymbol() {
        return variabilniSymbol;
    }

    public void setVariabilniSymbol(String variabilniSymbol) {
        this.variabilniSymbol = variabilniSymbol;
    }

    public String getUzivatelskaIdentifikace() {
        return uzivatelskaIdentifikace;
    }

    public void setUzivatelskaIdentifikace(String uzivatelskaIdentifikace) {
        this.uzivatelskaIdentifikace = uzivatelskaIdentifikace;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getMena() {
        return mena;
    }

    public void setMena(String mena) {
        this.mena = mena;
    }

    public Long getIdPokynu() {
        return idPokynu;
    }

    public void setIdPokynu(Long idPokynu) {
        this.idPokynu = idPokynu;
    }

    public Long getIdPohybu() {
        return idPohybu;
    }

    public void setIdPohybu(Long idPohybu) {
        this.idPohybu = idPohybu;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

	@Override
	public String toString() {
		return "Transaction [protiucet=" + protiucet + ", datumPohybu=" + datumPohybu + ", objem=" + objem
				+ ", konstantniSymbol=" + konstantniSymbol + ", variabilniSymbol=" + variabilniSymbol
				+ ", uzivatelskaIdentifikace=" + uzivatelskaIdentifikace + ", typ=" + typ + ", mena=" + mena
				+ ", idPokynu=" + idPokynu + ", idPohybu=" + idPohybu + ", komentar=" + komentar + ", provedl="
				+ provedl + ", zpravaProPrijemnce=" + zpravaProPrijemnce + "]";
	}
}
