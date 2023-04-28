package dam.camarasmadrid01;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View contenedorDetalle = findViewById(R.id.contenedor_detalle_camara);
        contenedorDetalle.setVisibility(View.GONE);

        Toolbar barraHerramientas = findViewById(R.id.toolbar);
        setSupportActionBar(barraHerramientas);
        ActionBar actionBar = getSupportActionBar() ;
        actionBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barraherramientas, menu);
// En el caso de que una opción fuera seleccionable, se
// podría dar un valor inicial
        menu.findItem(R.id.op1).setChecked(true);
        return true;
    }


}