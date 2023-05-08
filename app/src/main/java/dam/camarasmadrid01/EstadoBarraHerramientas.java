package dam.camarasmadrid01;

import android.util.Log;

import androidx.lifecycle.ViewModel;

public class EstadoBarraHerramientas extends ViewModel {
    boolean ubicacion;
    String muestras;
    double latitud_actual;
    double longitud_actual;

    public String getMuestras() {
        return muestras;
    }

    public boolean getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(boolean ubicacion){
       this.ubicacion=ubicacion;

    }
    public void setMuestras(String muestras){
        this.muestras=muestras;

    }

}
