package dam.camarasmadrid01;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClaseAsyncTask_Imagen extends AsyncTask<String, Integer, Bitmap> {


    private DetalleCamaraFragment detalleCamaraFragment;
    long startTime;
    String respuesta=null;


    public ClaseAsyncTask_Imagen(DetalleCamaraFragment detalleCamaraFragment) {

        this.detalleCamaraFragment=detalleCamaraFragment;
    }





    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

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
            respuesta = "Se ha producido esta excepción: "+ e.toString();
            Log.d("TAG",respuesta);
            throw new RuntimeException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }

    protected void onPreExecute() {

        detalleCamaraFragment.progressBar.setVisibility(View.VISIBLE);


    }





    protected void onProgressUpdate(Integer... progreso) {

    }


    protected void onPostExecute(Bitmap bitmap) {

        detalleCamaraFragment.ivDetallesCamara.setImageBitmap(bitmap);
        detalleCamaraFragment.progressBar.setVisibility(View.GONE);

        detalleCamaraFragment.ivDetallesCamara.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(detalleCamaraFragment.getContext(), ImagenPantalla.class);
                intent.putExtra("urlString", urlParsed);
                detalleCamaraFragment.startActivity(intent);
                return true;
            }
        });
    }
}