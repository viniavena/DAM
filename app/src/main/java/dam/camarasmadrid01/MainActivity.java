package dam.camarasmadrid01;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import dam.camarasmadrid01.modelo.ListadoCamaras;


public class MainActivity extends AppCompatActivity {
    public Menu menuOpciones;
    EstadoBarraHerramientas estadoBarraHerramientas;
    private FusedLocationProviderClient clienteLocalizacion;
    private Location currentLocation;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View contenedorDetalle = findViewById(R.id.contenedor_detalle_camara);
        contenedorDetalle.setVisibility(View.GONE);
        //barra de herramientas
        Toolbar barraHerramientas = findViewById(R.id.toolbar);
        setSupportActionBar(barraHerramientas);
        ActionBar actionBar = getSupportActionBar() ;
        actionBar.show();
        //ubicacion actual
        //ubicaccion
        clienteLocalizacion = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, PERMISSION_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> ultimaLocalizacion = clienteLocalizacion.getLastLocation();
            ultimaLocalizacion.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location localizacion) {
                    if (localizacion != null) {
                        // Localización encontrada
                        estadoBarraHerramientas.latitud_actual=localizacion.getLatitude();
                        estadoBarraHerramientas.longitud_actual=localizacion.getLongitude();

                        Log.d("TAG", "si pillo localizacion");
                    } else {
                        Log.d("TAG", "no pillo localizacion");
                    }
                }
            });
        }

        estadoBarraHerramientas = new ViewModelProvider(this).get(EstadoBarraHerramientas.class);
        estadoBarraHerramientas.setMuestras("Ninguna");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barraherramientas, menu);
// En el caso de que una opción fuera seleccionable, se
// podría dar un valor inicial
        menuOpciones = menu; // menuOpciones es un atributo Menu
        menu.findItem(R.id.op1).setChecked(true);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.op1:

                return true;
            case R.id.op_ubicacion:
                String mensaje = "Se seleccionó la segunda opción";
                if (item.isChecked()) { // Si el checkbox ya estaba activado
                    item.setChecked(false); // Se desactiva
                    mensaje += " y se desactivó";

                    estadoBarraHerramientas.setUbicacion(false);
                } else {
                    item.setChecked(true); // Si no estaba activado se activa
                    mensaje += " y se activó";
                    estadoBarraHerramientas.setUbicacion(true);
                    verificarOsolicitarPermisos();
                }

                return true;


            case R.id.op_una:
                item.setChecked(true);
                estadoBarraHerramientas.setMuestras("una");
                return true;
            case R.id.op_todas:
                item.setChecked(true);
                estadoBarraHerramientas.setMuestras("todas");
                return true;
            case R.id.op_agrupacion:
                item.setChecked(true);
                estadoBarraHerramientas.setMuestras("agrupacion");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Permisos
    private void verificarOsolicitarPermisos() {
        // Verificar si ya se tiene concedido el permiso de otra petición anterior
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si tiene el permiso concedido, invocar al método que realiza la operación
            operacionConPermisos();
        } else {
// No tiene el permiso, entonces solicitarlo
// Antes de solicitarlo, verificar si ya se solicitó anteriormente
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {*/
                solicitarPermisoDeNuevo();
            /*} else {
                // Si ha sido la primera vez que se va a solicitar el permiso, hacer la solicitud
                solicitarPermiso(this);
            }*/
        }
    }
    private void solicitarPermiso(Activity actividad) {
        lanzadorPeticionPermiso.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
    }
    private void solicitarPermisoDeNuevo(){
        final Activity actividad=this;
        // Mostrar un cuadro de diálogo explicando el motivo de solicitar el permiso
        new AlertDialog.Builder(actividad)
                .setTitle("Solicitud de permiso")
                .setMessage("Justificación: este permiso se le solicita por ....")
                . setNegativeButton("No acepto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // El usuario sigue sin aceptar el permiso
                        menuOpciones.getItem(1).setChecked(false);
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        solicitarPermiso(actividad);
                    }
                })
                .show(); // Mostrar el cuadro de diálogo
    }
    private void operacionConPermisos(){
       //permiso concedido
    }
    // Definición de un atributo con la referencia del código a ejecutar cuando se solicita el permiso
    private ActivityResultLauncher<String> lanzadorPeticionPermiso =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), // Clase que muestra el cuadro de diálogo para pedir permiso
                    esConcendido -> { // Función a ejecutar cuando se retorna del cuadro de diálogo devolviendo el resultado
                        if (esConcendido) {
// Permiso concedido, se puede ejecutar el código que lo necesita
                            operacionConPermisos();
                        } else {
// Permiso denegado, indicarlo al usuario
                            menuOpciones.getItem(1).setChecked(false);
                        }
                    });




}