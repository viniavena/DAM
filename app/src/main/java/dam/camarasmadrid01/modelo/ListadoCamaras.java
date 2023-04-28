package dam.camarasmadrid01.modelo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class ListadoCamaras extends ViewModel {

    private MutableLiveData<ArrayList<Camara>> listaCamaras = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Camara>> getListaCamaras() {
        return listaCamaras;
    }

    public void setListaCamaras(ArrayList<Camara> listaCamaras) {
        this.listaCamaras.setValue(listaCamaras);
    }
}