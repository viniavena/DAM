package dam.camarasmadrid01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImagenPantalla extends AppCompatActivity {

    private ImageView ivCamara, btnCompartir;
    private Handler handler;
    private Runnable runnable;
    private Bitmap bitmap;
    private String urlString;
    String respuesta=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_pantalla);

        ivCamara = findViewById(R.id.iv_camara);
        btnCompartir = findViewById(R.id.btn_compartir);

        // Recebe a url da activity anterior
        Intent intent = getIntent();
        urlString = intent.getStringExtra("urlString");

        // Configura o handler para atualizar a imagem a cada 30 segundos
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                new DownloadImageTask().execute(urlString);
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(runnable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Para o handler quando a atividade é destruída
        handler.removeCallbacks(runnable);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlString = urls[0];
            Bitmap bitmap = null;
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();

                Log.d("TAG", "url"+urlConnection);

                if ( urlConnection.getResponseCode() ==HttpURLConnection.HTTP_MOVED_PERM || urlConnection.getResponseCode() ==HttpURLConnection.HTTP_SEE_OTHER
                        || urlConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP){
                    String connection=urlConnection.getHeaderField("Location");
                    url = new URL(connection);
                    urlConnection = (HttpURLConnection) url.openConnection();
                }
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK  ) {
                    InputStream is = urlConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } else {
                    respuesta="Ha fallado la conexión a"+urls [0];
                    Log.d("TAG",respuesta);
                }
            } catch (IOException e) {
                Log.d("TAG", "Se ha producido esta excepción: " + e.toString());
                throw new RuntimeException(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                ivCamara.setImageBitmap(bitmap);
            }

            btnCompartir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Crie o Intent para compartilhar o link
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, urlString);

                    // Abra o modal de compartilhamento
                    Intent chooser = Intent.createChooser(shareIntent, "Desea compartir el enlace de la imagem?");
                    startActivity(chooser);
                }
            });
        }
    }
}