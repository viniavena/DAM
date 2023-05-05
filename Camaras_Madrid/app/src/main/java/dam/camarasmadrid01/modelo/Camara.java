package dam.camarasmadrid01.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Camara implements Parcelable {
    private String nombre;
    private String url;
    private String coordenadas;

    public Camara(String nombre, String url, String coordenadas) {
        this.nombre = nombre;
        this.url = url;
        this.coordenadas = coordenadas;
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
    }

    public static final Parcelable.Creator<Camara> CREATOR = new Parcelable.Creator<Camara>() {
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
    }
}