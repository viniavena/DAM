package dam.camarasmadrid01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import dam.camarasmadrid01.modelo.Camara;
import dam.camarasmadrid01.modelo.HiloTrabajo;
import dam.camarasmadrid01.modelo.ListadoCamaras;


public class ListadoCamarasFragment extends Fragment {

    public TextView progresoContador;
    public LinearLayout layoutProgresso;
    private ListView listView;
    private LinearLayout contenedorListaCamaras;
    private final String urlFichero = "http://informo.madrid.es/informo/tmadrid/CCTV.kml";
    private MutableLiveData<ArrayList<Camara>> listaCamaras;
    ListadoCamaras viewModel;
    private Fragment instanciaFragmentoListado;

    public ListadoCamarasFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar o ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ListadoCamaras.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listado_camaras, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instanciaFragmentoListado = this;

        // Recuperar as referências para as vistas
        listView = view.findViewById(R.id.lv_camaras);
        layoutProgresso = view.findViewById(R.id.layout_progresso);
        progresoContador = view.findViewById(R.id.progresoContador);
        contenedorListaCamaras = view.findViewById(R.id.contenedorListaCamaras);

        // Ocultar o contenedor do listado e mostrar o do contador
        contenedorListaCamaras.setVisibility(View.GONE);
        layoutProgresso.setVisibility(View.VISIBLE);

        //
        SharedPreferences sharedPreferencesMirarFecha = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        long ultimaDescarga = sharedPreferencesMirarFecha.getLong("fecha_ultima_descarga", -1);

        // Verificar se o ViewModel já contém a lista de câmeras
        if (savedInstanceState != null) {
            listaCamaras = viewModel.getListaCamaras();
            actualizaListaCamaras(listaCamaras.getValue());
        } else {
            // Se a lista ainda não estiver no ViewModel, criar um novo thread para processá-la
            if (ultimaDescarga == -1) {
                Log.d("TAG", "Fecha todavia no hay sigo guardada");
                new Thread(new HiloTrabajo(urlFichero, ListadoCamarasFragment.this, Boolean.TRUE)).start();
            } else {
                Log.d("TAG", "Fecha guardada: " + String.valueOf(ultimaDescarga));
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle("¿Quieres actualizar tu lista de cámaras?");

                Date dateToFormat = new Date(ultimaDescarga);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(dateToFormat);
                builder.setMessage("Última descarga en: " + formattedDate);

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new HiloTrabajo(urlFichero, ListadoCamarasFragment.this, Boolean.TRUE)).start();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new HiloTrabajo(urlFichero, ListadoCamarasFragment.this, Boolean.FALSE)).start();
                    }
                });
                builder.create().show();
            }

        }

    }
    public void actualizaListaCamaras (ArrayList<Camara> listadoCamaras) {
        Log.d("TAG", "Actualizando lista de cámaras");


        // Ocultar los elementos visibles durante el análisis y hacer visible el contenedor del listado de cámaras
        if (layoutProgresso != null) {
            layoutProgresso.setVisibility(View.GONE);
        }
        if (contenedorListaCamaras != null) {
            contenedorListaCamaras.setVisibility(View.VISIBLE);
        }

        // Mostrar las cámaras en el TextView
        ArrayAdapter<Camara> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, listadoCamaras);
        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Adiciona um listener de clique ao ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtém a câmera selecionada
                Camara camara = (Camara) listView.getItemAtPosition(position);

                // Cria um novo fragmento e adiciona a URL da câmera como um argumento
                DetalleCamaraFragment detalleCamaraFragment = new DetalleCamaraFragment();
                Bundle args = new Bundle();
                args.putString("url", camara.getUrl());
                args.putString("nombre", camara.getNombre());
                args.putString("coordenadas", camara.getCoordenadas());
                detalleCamaraFragment.setArguments(args);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contenedor_detalle_camara, detalleCamaraFragment);
                ft.addToBackStack(null);
                ft.commit();

                View detalleView = getActivity().findViewById(R.id.contenedor_detalle_camara);
                detalleView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.setListaCamaras(listadoCamaras);
        listaCamaras = viewModel.getListaCamaras();
    }
}