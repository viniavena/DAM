package dam.camarasmadrid01.modelo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dam.camarasmadrid01.ListadoCamarasFragment;


public class HiloTrabajo implements Runnable {
    private final ListadoCamarasFragment instanciaFragmentoListado;
    private String urlXML;

    public HiloTrabajo(String urlXML, ListadoCamarasFragment instanciaFragmentoListado) {
        this.urlXML = urlXML;
        this.instanciaFragmentoListado = instanciaFragmentoListado;
    }

    @Override
    public void run() {
        ArrayList<Camara> resultado;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            // download del fichero KML
            URL url = new URL(urlXML);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            int responseCode = urlConnection.getResponseCode();
            while (responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                url = new URL(urlConnection.getHeaderField("Location"));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setInstanceFollowRedirects(true);
                responseCode = urlConnection.getResponseCode();
            }
            Log.d("TAG", String.valueOf(responseCode));
            InputStream inputStream = urlConnection.getInputStream();

            // análise del XML
            SAXParser analizadorSAX = factory.newSAXParser();
            ManejadorXML manejadorXML = new ManejadorXML(instanciaFragmentoListado);
            analizadorSAX.parse(new InputSource(inputStream), manejadorXML);
            resultado = manejadorXML.getResultado();

            instanciaFragmentoListado.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instanciaFragmentoListado.actualizaListaCamaras(resultado);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

