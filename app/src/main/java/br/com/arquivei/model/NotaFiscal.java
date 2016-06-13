package br.com.arquivei.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by henrique on 4/6/2016.
 */
public class NotaFiscal implements Parcelable {

    // Status Types
    public static final String STATUS_OK = "enviada";
    public static final String STATUS_PENDING = "pendente";

    // Abreviação Meses
    public static final String[] MESES = {"Jan", "Fev", "Mar", "Abril", "Maio", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    private String data;
    private String valor;
    private String cnpj;
    private String QRcode;
    private String status;

    /* Construtor */
    public NotaFiscal(String valorQR) {
        this.QRcode = valorQR;
        this.data = "";
        this.valor = "";
        this.cnpj = "";
        this.status = STATUS_PENDING;
        filtrarNota();
    }

    public String getData() {
        return this.data;
    }

    public String getValor() {
        return this.valor;
    }

    public String getCnpj() { return this.cnpj; }

    public String getStatus() { return status; }

    public String getDia() { return ("" + data.charAt(0) + data.charAt(1));}

    public String getMes() { return MESES[Integer.valueOf( ("" + data.charAt(3) + data.charAt(4)) ) - 1];}


    /* Extrai os campos CNPJ, data e Valor da nota fiscal a partir do QR Code*/
    public void filtrarNota() {

        // QR Code: ID|TimeStamp|Valor||...
        // ID: CNJP: xxxxxx 99.999.999/9999-99 xxxx xxxx xxxx xxxx xxxx xxxx

        int i;
        String id = new String();
        String aux = new String();

        // Remove o CFe do ínicio, se tiver
        if (QRcode.charAt(0) == 'C')
            QRcode = QRcode.substring(3);

        //Filtrar ID
        for (i = 0 ; this.QRcode.charAt(i) != '|'; i++) {
            id = id + this.QRcode.charAt(i);
        }

        Log.i("nota", id);

        //A partir ID, filtrar CNPJ
        for (int j = 6; j < 20; j++) {
            this.cnpj = this.cnpj + id.charAt(j);
        }

        cnpj = String.format("%s.%s.%s/%s-%s", cnpj.substring(0, 2), cnpj.substring(2,5), cnpj.substring(5,8), cnpj.substring(8, 12), cnpj.substring(12));

        //Pegando data
        for (i++ ; this.QRcode.charAt(i) != '|'; i++) {
            aux = aux + this.QRcode.charAt(i);
        }

        // aux aaaaMMddhhmmss
        this.data = this.data + aux.charAt(6) + aux.charAt(7) + '/' + aux.charAt(4) + aux.charAt(5) + '/' + aux.charAt(0) + aux.charAt(1) + aux.charAt(2) + aux.charAt(3);

        //Pegando valor
        for (i++ ; this.QRcode.charAt(i) != '|'; ++i) {
            this.valor = this.valor + this.QRcode.charAt(i);
        }
    }

    /* Métodos do Parcelable para enviar o objeto NotaFiscal entre duas activitys*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getData());
        dest.writeString(getCnpj());
        dest.writeString(getValor());
    }

    public static final Parcelable.Creator<NotaFiscal> CREATOR
            = new Parcelable.Creator<NotaFiscal>() {
        public NotaFiscal createFromParcel(Parcel in) {
            return new NotaFiscal(in);
        }

        public NotaFiscal[] newArray(int size) {
            return new NotaFiscal[size];
        }
    };

    private NotaFiscal(Parcel in) {
        data = in.readString();
        cnpj = in.readString();
        valor = in.readString();
    }
}