package dam.camarasmadrid01.modelo;

import android.os.Parcel;
import android.os.Parcelable;
public class Camara implements Parcelable {
    private String nombre;
    private String url;
    private String coordenadas;
    private boolean favorita;
    private boolean selecionada;

    public Camara(String nombre, String url, String coordenadas) {
        this.nombre = nombre;
        this.url = url;
        this.coordenadas = coordenadas;
        this.favorita = false;
        this.selecionada = false;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public boolean isFavorita() {
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }

    public boolean isSelecionada() {
        return selecionada;
    }

    public void setSelecionada(boolean selecionada) {
        this.selecionada = selecionada;
    }

    public String getDetalles(){
        return  "Nombre: " + nombre + "\nCoordenadas: " + coordenadas + "\nURL: " + url;
    }

    @Override
    public String toString(){
        return nombre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(url);
        dest.writeString(coordenadas);
        dest.writeByte((byte) (favorita ? 1 : 0));
        dest.writeByte((byte) (selecionada ? 1 : 0));
    }

    public static final Creator<Camara> CREATOR = new Creator<Camara>() {
        public Camara createFromParcel(Parcel in) {
            return new Camara(in);
        }

        public Camara[] newArray(int size) {
            return new Camara[size];
        }
    };

    private Camara(Parcel in) {
        nombre = in.readString();
        url = in.readString();
        coordenadas = in.readString();
        favorita = in.readByte() != 0;
        selecionada = in.readByte() != 0;
    }
}