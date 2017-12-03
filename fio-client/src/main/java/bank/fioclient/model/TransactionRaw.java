package bank.fioclient.model;

import bank.fioclient.model.column.DoubleColumn;
import bank.fioclient.model.column.LongColumn;
import bank.fioclient.model.column.StringColumn;

/**
 * Project: fio-client
 *
 * Created: 12. 1. 2017
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class TransactionRaw {

    private StringColumn column0; // Datum - datum pohybu ve tvaru rrrr-mm-dd+GMT
    private DoubleColumn column1; // Objem - velikost přijaté (odeslané) částky
    private StringColumn column2; // Protiúčet - číslo protiúčtu
    private StringColumn column3; // Kód banky - číslo banky protiúčtu
    private StringColumn column4; // KS
    private StringColumn column5; // VS - může začínat nulou (pouze číslice)

    private StringColumn column7; // Uživatelská identifikace
    private StringColumn column8; // Typ - např. "Bezhotovostní platba"
    private StringColumn column9; // Provedl - např. "Wojnar, Aleš"
    private StringColumn column10; // Název protiúčtu - např. "NESS CZECH S.R.O.

    private StringColumn column12; // Název banky - název banky protiúčtu
    private StringColumn column14; // Měna - např. "CZK"

    private StringColumn column16; // Zpráva pro příjemce
    private LongColumn column17; // ID pokynu
    private LongColumn column22; // ID pohybu - Všechny pohyby na účtech v bankovním systému jsou evidovány podle jedinečného klíče IDpohyb.
    private StringColumn column25; // Komentář

    public StringColumn getColumn16() {
        return column16;
    }

    public void setColumn16(StringColumn column16) {
        this.column16 = column16;
    }

    public StringColumn getColumn10() {
        return column10;
    }

    public void setColumn10(StringColumn column10) {
        this.column10 = column10;
    }

    public StringColumn getColumn9() {
        return column9;
    }

    public void setColumn9(StringColumn column9) {
        this.column9 = column9;
    }

    public StringColumn getColumn0() {
        return column0;
    }

    public void setColumn0(StringColumn column0) {
        this.column0 = column0;
    }

    public DoubleColumn getColumn1() {
        return column1;
    }

    public void setColumn1(DoubleColumn column1) {
        this.column1 = column1;
    }

    public StringColumn getColumn2() {
        return column2;
    }

    public void setColumn2(StringColumn column2) {
        this.column2 = column2;
    }

    public StringColumn getColumn3() {
        return column3;
    }

    public void setColumn3(StringColumn column3) {
        this.column3 = column3;
    }

    public StringColumn getColumn4() {
        return column4;
    }

    public void setColumn4(StringColumn column4) {
        this.column4 = column4;
    }

    public StringColumn getColumn5() {
        return column5;
    }

    public void setColumn5(StringColumn column5) {
        this.column5 = column5;
    }

    public StringColumn getColumn7() {
        return column7;
    }

    public void setColumn7(StringColumn column7) {
        this.column7 = column7;
    }

    public StringColumn getColumn8() {
        return column8;
    }

    public void setColumn8(StringColumn column8) {
        this.column8 = column8;
    }

    public StringColumn getColumn12() {
        return column12;
    }

    public void setColumn12(StringColumn column12) {
        this.column12 = column12;
    }

    public StringColumn getColumn14() {
        return column14;
    }

    public void setColumn14(StringColumn column14) {
        this.column14 = column14;
    }

    public LongColumn getColumn17() {
        return column17;
    }

    public void setColumn17(LongColumn column17) {
        this.column17 = column17;
    }

    public LongColumn getColumn22() {
        return column22;
    }

    public void setColumn22(LongColumn column22) {
        this.column22 = column22;
    }

    public StringColumn getColumn25() {
        return column25;
    }

    public void setColumn25(StringColumn column25) {
        this.column25 = column25;
    }
}
