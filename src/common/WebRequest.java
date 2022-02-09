package common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebRequest implements Externalizable {
    private LocalDateTime dataOra;
    private String seriale;
    private Category categoria;
    private Type tipologia;
    private int durata;
    private Result esito;

    public WebRequest(String seriale, Category categoria, Type tipologia, Result esito) {
        this.seriale = seriale;
        this.categoria = categoria;
        this.tipologia = tipologia;
        this.esito = esito;
        this.durata = 0;
        this.dataOra = LocalDateTime.now();
    }

    public WebRequest() {
        this(randomSerial(), Category.randomCategory(), Type.randomType(), Result.randomResult());
    }

    public WebRequest(Type type) {
        this(randomSerial(), Category.randomCategory(), type, Result.randomResult());
    }

    private static String randomSerial() {
        String possibleSerialChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int randomCharIndex = (int) (Math.random() * (possibleSerialChars.length() - 1));
            char randomChar = possibleSerialChars.charAt(randomCharIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return String.format("%s | %05d | %s | %s | %s | %s",
                dataOra.format(DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss")),
                durata,
                seriale,
                esito.toString(),
                categoria.toString(),
                tipologia.toString());
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public String getSeriale() {
        return seriale;
    }

    public void setSeriale(String seriale) {
        this.seriale = seriale;
    }

    public Category getCategoria() {
        return categoria;
    }

    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }

    public Type getTipologia() {
        return tipologia;
    }

    public void setTipologia(Type tipologia) {
        this.tipologia = tipologia;
    }

    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        if (durata >= 9999) {
            this.durata = 9999;
        } else {
            this.durata = durata;
        }
    }

    public Result getEsito() {
        return esito;
    }

    public void setEsito(Result esito) {
        this.esito = esito;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(dataOra.toString());
        out.writeUTF(seriale);
        out.writeInt(categoria.i);
        out.writeInt(tipologia.i);
        out.writeInt(durata);
        out.writeInt(esito.i);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        dataOra = LocalDateTime.parse(in.readUTF());
        seriale = in.readUTF();
        categoria = Category.getByValue(in.readInt());
        tipologia = Type.getByValue(in.readInt());
        durata = in.readInt();
        esito = Result.getByValue(in.readInt());
    }

}
