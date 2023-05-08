package dam.camarasmadrid01;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dam.camarasmadrid01.modelo.Camara;

public class AdaptadorListadoCamara extends ArrayAdapter<Camara> {

    private List<Camara> listaCamaras;
    private final LayoutInflater inflador;

    public AdaptadorListadoCamara(Context contexto, ArrayList<Camara> camaras){
        super(contexto, 0, camaras);
        this.listaCamaras = camaras;
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int posicion, View vistaReciclada, ViewGroup padre){
        if (vistaReciclada == null){
            vistaReciclada = inflador.inflate(R.layout.lista_camaras_item, padre, false);
        }

        final TextView camaraNombre= vistaReciclada.findViewById(R.id.tv_nombreCamara);
        final ImageView esFavorito = vistaReciclada.findViewById(R.id.iv_EsFavorito);
        final ImageView esEligida = vistaReciclada.findViewById(R.id.iv_EsEligida);

        final Camara camara = getItem(posicion);

        camaraNombre.setText(camara.getNombre());

        if(camara.isFavorita()){
            esFavorito.setVisibility(View.VISIBLE);
        } else{
            esFavorito.setVisibility(View.GONE);
        }


        //if(camara.isSelecionada()){Log.d("TAG","nombre en el adaptador: "+camara.getNombre()+" posicion en el adaptador: "+String.valueOf(posicion));}

        esEligida.setImageResource(camara.isSelecionada() ? R.drawable.baseline_camera_alt_24 : R.drawable.outline_camera_alt_24);

        return vistaReciclada;
    }
}