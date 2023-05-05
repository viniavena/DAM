package dam.camarasmadrid01;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import dam.camarasmadrid01.modelo.Camara;
import dam.camarasmadrid01.modelo.HiloTrabajo;
import dam.camarasmadrid01.modelo.ListadoCamaras;


public class ListadoCamarasFragment extends Fragment {
    public EstadoBarraHerramientas estadoBarraHerramientas;
    public TextView progresoContador;
    public LinearLayout layoutProgresso;
    private ListView listView;
    private LinearLayout contenedorListaCamaras;
    private final String nombreFichero = "CamarasMadrid.kml";
    private MutableLiveData<ArrayList<Camara>> listaCamaras;
    ListadoCamaras viewModel;

    public ListadoCamarasFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Inicializar o ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ListadoCamaras.class);

        // Verificar se o ViewModel já contém a lista de câmeras
        if (viewModel.getListaCamaras() != null) {
            listaCamaras = viewModel.getListaCamaras();
            //actualizaListaCamaras(listaCamaras.getValue());
            Log.d("TAG", "oi: " + String.valueOf(listaCamaras));
        } else {
            // Se a lista ainda não estiver no ViewModel, criar um novo thread para processá-la
            new Thread(new HiloTrabajo(nombreFichero, this)).start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listado_camaras, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar as referências para as vistas
        listView = view.findViewById(R.id.lv_camaras);
        layoutProgresso = view.findViewById(R.id.layout_progresso);
        progresoContador = view.findViewById(R.id.progresoContador);
        contenedorListaCamaras = view.findViewById(R.id.contenedorListaCamaras);

        // Ocultar o contenedor do listado e mostrar o do contador
        contenedorListaCamaras.setVisibility(View.GONE);
        layoutProgresso.setVisibility(View.VISIBLE);

        // Verificar se o ViewModel já contém a lista de câmeras
        if (savedInstanceState != null) {
            listaCamaras = viewModel.getListaCamaras();
            actualizaListaCamaras(listaCamaras.getValue());
            Log.d("TAG", "oi: " + String.valueOf(listaCamaras));
        } else {
            // Se a lista ainda não estiver no ViewModel, criar um novo thread para processá-la
            new Thread(new HiloTrabajo(nombreFichero, this)).start();
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
        Log.d("TAG", "Lista de cámaras guardada en ViewModel: " + listaCamaras.getValue());    }
}