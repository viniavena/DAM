package dam.camarasmadrid01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
    int posicaoCamaraSelecionada = -1;
    public AdaptadorListadoCamara adapter;
    ArrayList<Camara> listaCamarasFavoritas;

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

        // fecha ultima decarga
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

            new Thread(new HiloTrabajo(nombreFichero, this)).start();
        }

    }
    public void actualizaListaCamaras (ArrayList<Camara> listadoCamaras) {

        // Ocultar los elementos visibles durante el análisis y hacer visible el contenedor del listado de cámaras
        if (layoutProgresso != null) {
            layoutProgresso.setVisibility(View.GONE);
        }
        if (contenedorListaCamaras != null) {
            contenedorListaCamaras.setVisibility(View.VISIBLE);
        }

        // coge las camaras favoritas
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> favoritas = preferences.getStringSet("favoritas", new HashSet<String>());
        for (Camara c : listadoCamaras) {
            if (favoritas.contains(c.getNombre())) {
                c.setFavorita(true);
            }
        }
        listaCamarasFavoritas = new ArrayList<>();
        ArrayList<Camara> listaCamarasNaoFavoritas = new ArrayList<>();
        for (Camara c : listadoCamaras) {
            if (c.isFavorita()) {
                listaCamarasFavoritas.add(c);
            } else {
                listaCamarasNaoFavoritas.add(c);
            }
        }

        listaCamarasFavoritas.addAll(listaCamarasNaoFavoritas);

        // Mostrar las cámaras en el TextView
        adapter = new AdaptadorListadoCamara(getContext(), listaCamarasFavoritas);
        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Adiciona um listener de clique ao ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtém a câmera selecionada
                Camara camara = (Camara) listView.getItemAtPosition(position);
                //Log.d("CLICK", "lista em click: "+String.valueOf(listaCamarasFavoritas));
                //Log.d("TAG","nombre en la lista: "+camara.getNombre()+" posicion en la lista: "+String.valueOf(position));

                for (int i = 0; i < listaCamarasFavoritas.size(); i++) {
                    if (i != position) {
                        Camara outraCamara = listaCamarasFavoritas.get(i);
                        outraCamara.setSelecionada(false);
                    }
                }
                // Define a câmera selecionada como verdadeira
                camara = listaCamarasFavoritas.get(position);
                camara.setSelecionada(true);

                // Notifica o adaptador de que os dados foram alterados
                adapter.notifyDataSetChanged();

                // Cria um novo fragmento e adiciona a URL da câmera como um argumento
                DetalleCamaraFragment detalleCamaraFragment = new DetalleCamaraFragment();
                Bundle args = new Bundle();
                args.putString("url", camara.getUrl());
                args.putString("nombre", camara.getNombre());
                args.putString("coordenadas", camara.getCoordenadas());
                args.putBoolean("isFavorite", camara.isFavorita(

                detalleCamaraFragment.setArguments(args);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.contenedor_detalle_camara, detalleCamaraFragment);
                ft.addToBackStack(null);
                ft.commit();

                View detalleView = getActivity().findViewById(R.id.contenedor_detalle_camara);
                detalleView.setVisibility(View.VISIBLE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtenha a câmera correspondente ao item clicado
                Camara camara = (Camara) listView.getItemAtPosition(position);
                boolean isFavorita = camara.isFavorita();

                if (isFavorita) {
                    // Remove a câmera da lista de favoritos
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Set<String> favoritas = preferences.getStringSet("favoritas", new HashSet<String>());
                    favoritas.remove(camara.getNombre());
                    preferences.edit().putStringSet("favoritas", favoritas).apply();

                    camara.setFavorita(false);
                } else {
                    // Adiciona a câmera à lista de favoritos
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Set<String> favoritas = preferences.getStringSet("favoritas", new HashSet<String>());
                    favoritas.add(camara.getNombre());
                    preferences.edit().putStringSet("favoritas", favoritas).apply();

                    camara.setFavorita(true);
                }

                // Ordena a lista de câmeras com as favoritas no topo
                ArrayList<Camara> listaCamarasFavoritasTemporal = new ArrayList<>();
                ArrayList<Camara> listaCamarasNaoFavoritas = new ArrayList<>();

                for (Camara c : listadoCamaras) {
                    if (c.isFavorita()) {
                        listaCamarasFavoritasTemporal.add(c);
                    } else {
                        listaCamarasNaoFavoritas.add(c);
                    }
                }

                listaCamarasFavoritasTemporal.addAll(listaCamarasNaoFavoritas);
                listaCamarasFavoritas = listaCamarasFavoritasTemporal;

                // Atualiza o adaptador da lista com a nova lista de câmeras ordenada
                adapter = new AdaptadorListadoCamara(getContext(), listaCamarasFavoritas);
                listView.setAdapter(adapter);

                return true;
            }
        });

        viewModel.setListaCamaras(listadoCamaras);
        listaCamaras = viewModel.getListaCamaras();
    }
}