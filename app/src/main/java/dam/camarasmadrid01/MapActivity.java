package dam.camarasmadrid01;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dam.camarasmadrid01.databinding.ActivityMapBinding;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    private RadioGroup radioGroup;
    private RadioButton radioButtonMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Mapa das camaras");
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
        LatLng coordenadasCamara =getLatLngFromString(cameraLocationString);

        Log.d("TAG", "aq"+String.valueOf(coordenadasCamara));

        String nombreCamera = getIntent().getStringExtra("nombre_camera");

        Log.d("TAG", "nombre"+nombreCamera);

        MarkerOptions markerOptions = new MarkerOptions().position(coordenadasCamara).title(nombreCamera);
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

}