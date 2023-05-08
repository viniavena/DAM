package dam.camarasmadrid01;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import dam.camarasmadrid01.modelo.ListadoCamaras;


public class DetalleCamaraFragment extends Fragment{

    private TextView tvDetallesCamara;
    public ImageView ivDetallesCamara, mapIcon, favoriteIcon;
    public ProgressBar progressBar;
    public EstadoBarraHerramientas estadoBarraHerramientas;
    public ListadoCamaras listadoCamaras;

    public DetalleCamaraFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //barra de Herramientas
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Instanciar el objeto ViewModel. Como ya lo ha instanciado la actividad, y es una clase singleton, recibirá su referencia
        estadoBarraHerramientas = new ViewModelProvider(getActivity()).get(EstadoBarraHerramientas.class);
        listadoCamaras= new ViewModelProvider(getActivity()).get(ListadoCamaras.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Cria a view do fragmento
        View view = inflater.inflate(R.layout.fragment_detalle_camara, container, false);

        // Encontra o TextView no layout e define o texto da URL
        TextView tvDetalles = view.findViewById(R.id.tv_detallesCamara);
        ivDetallesCamara = view.findViewById(R.id.iv_DetallesCamara);
        progressBar= view.findViewById(R.id.pb_Imagen);

        mapIcon = view.findViewById(R.id.mapa_icon);
        favoriteIcon = view.findViewById(R.id.iv_favoriteDetalle);


        progressBar.setVisibility(View.GONE);
        Bundle args = getArguments();
        if (args != null) {
            String Nombre = args.getString("nombre");
            String url_String= args.getString("url");
            String coordenadas = args.getString("coordenadas");
            Boolean isFavorite = args.getBoolean("isFavorite");

            favoriteIcon.setImageResource(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.favorite_border_24);

            if (url_String != null) {

                ClaseAsyncTask_Imagen tarea_datos = new ClaseAsyncTask_Imagen(DetalleCamaraFragment.this);
                tarea_datos.execute(url_String);
                //preguntar esto
                //url_String=url_String
                //tarea_datos.execute("https://informo.madrid.es/cameras/Camara06303.jpg");
                tvDetalles.setText(Nombre);

            }

            if (coordenadas != null){
                mapIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("TAG","coordenadas antes do botao: "+String.valueOf(coordenadas));
                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        intent.putExtra("coordenadas", coordenadas);
                        intent.putExtra("nombre_camera", Nombre);

                        //barra herramientas
                        intent.putExtra("muestras",estadoBarraHerramientas.getMuestras());
                        intent.putExtra("ubicacion",estadoBarraHerramientas.getUbicacion());

                        //longitud y latitud actual
                        intent.putExtra("longitud",estadoBarraHerramientas.longitud_actual);
                        intent.putExtra("latitud",estadoBarraHerramientas.latitud_actual);

                        //listado de las camaras

                        intent.putExtra("listaCamaras",listadoCamaras.getListaCamaras().getValue());
                        startActivity(intent);
                    }
                });
            }

        }
        return view;
    }
}