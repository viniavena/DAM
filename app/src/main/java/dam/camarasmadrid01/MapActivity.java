package dam.camarasmadrid01;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import dam.camarasmadrid01.databinding.ActivityMapBinding;
import dam.camarasmadrid01.modelo.Camara;
import dam.camarasmadrid01.modelo.ListadoCamaras;


public class MapActivity extends AppCompatActivity  implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Agrupacion_Cluster>, ClusterManager.OnClusterItemClickListener<Agrupacion_Cluster>{

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    private RadioGroup radioGroup;
    private RadioButton radioButtonMapa;

    ArrayList<Camara> listaCamaras;
    ListadoCamaras viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //barra de herramientas MAP
        Toolbar barraHerramientas = findViewById(R.id.toolbar);
        setSupportActionBar(barraHerramientas);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Mapa de camaras");
        }
        radioButtonMapa = findViewById(R.id.rb_mapa);
        radioButtonMapa.setChecked(true);

        radioGroup = findViewById(R.id.RG);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int seleccionado = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(seleccionado);
                String texto = rb.getText().toString();
                switch (texto) {
                    case "Mapa":
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case "Satelite":
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case "Hibrido":
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case "Topografico":
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
                Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the camera and move the camera
        String cameraLocationString = getIntent().getStringExtra("coordenadas");
        LatLng coordenadasCamara = getLatLngFromString(cameraLocationString);

        Log.d("TAG", "aq" + String.valueOf(coordenadasCamara));

        String nombreCamera = getIntent().getStringExtra("nombre_camera");

        Log.d("TAG", "nombre" + nombreCamera);
        boolean ubicacion = getIntent().getBooleanExtra("ubicacion", false);
        String muestras = getIntent().getStringExtra("muestras");
        //listado de las camaras
        listaCamaras= getIntent().getParcelableArrayListExtra("listaCamaras");
        //mostrar ubicacion (magenta)
        if (ubicacion == true) {

                LatLng latLng = new LatLng(getIntent().getDoubleExtra("latitud",0), getIntent().getDoubleExtra("longitud",0));
                 //circulo mapa
                 CircleOptions circleOptions=new CircleOptions().center(latLng).radius(1000);
                Circle circulo=mMap.addCircle(circleOptions);
                circulo.setVisible(true);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Mi ubicación").
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                mMap.addMarker(markerOptions);
            }
        //mostrar todas las camaras
        if (muestras.equals("todas")) {

            for(int i=0;i<listaCamaras.size() ;i++){

                LatLng latLng =getLatLngFromString(listaCamaras.get(i).getCoordenadas());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(listaCamaras.get(i).getNombre());
                mMap.addMarker(markerOptions);
            }
        } else if (muestras.equals("agrupacion")) {
            ClusterManager<Agrupacion_Cluster> mClusterManager = new ClusterManager<>(this, mMap);


            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mClusterManager.setOnClusterClickListener(this);
            mClusterManager.setOnClusterItemClickListener(this);

            // Agrega tus marcadores al ClusterManager
            for (Camara camara : listaCamaras) {
                Agrupacion_Cluster marker = new Agrupacion_Cluster(getLatLngFromString(camara.getCoordenadas()).latitude, getLatLngFromString(camara.getCoordenadas()).longitude, camara.getNombre(), camara.getNombre());
                mClusterManager.addItem(marker);
            }

            // Mueve la cámara a la posición de los marcadores
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Camara camara : listaCamaras) {
                builder.include(getLatLngFromString(camara.getCoordenadas()));
            }
            LatLngBounds bounds = builder.build();
        }
        //La camara marcada
        MarkerOptions markerOptions = new MarkerOptions().position(coordenadasCamara).title(nombreCamera).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadasCamara, 16));

    }


    // Método para converter uma String de coordenadas em um objeto LatLng
    private LatLng getLatLngFromString(String str) {
        String[] parts = str.split(",");
        double lng = Double.parseDouble(parts[0].trim());
        double lat = Double.parseDouble(parts[1].trim());
        return new LatLng(lat, lng);
    }
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }


    @Override
    public boolean onClusterClick(Cluster<Agrupacion_Cluster> cluster) {
        return false;
    }

    @Override
    public boolean onClusterItemClick(Agrupacion_Cluster item) {
        return false;
    }
}