package dam.camarasmadrid01.modelo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dam.camarasmadrid01.ListadoCamarasFragment;


public class HiloTrabajo implements Runnable {
    private final ListadoCamarasFragment instanciaFragmentoListado;
    private String urlXML;
    String xmlString;
    String xmlString2;
    Boolean descargar;

    public HiloTrabajo(String urlXML, ListadoCamarasFragment instanciaFragmentoListado, Boolean descargar) {
        this.urlXML = urlXML;
        this.instanciaFragmentoListado = instanciaFragmentoListado;
        this.descargar = descargar;
    }

    @Override
    public void run() {
        ArrayList<Camara> resultado;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        if (descargar) {
            // Flujo para cuando no hay descarga
            xmlString = downloadXML(urlXML);
            guardaFicheroKML(xmlString);
            analisarXML(xmlString);
        } else {
            xmlString = recuperarFichero();
            analisarXML(xmlString);
        }
    }

    private String downloadXML(String urlXML) {
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
            Log.d("TAG", String.valueOf("code request: "+responseCode));
            InputStream inputStream = urlConnection.getInputStream();

            // Obten una String con el contenido del XML
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            inputStream.close();

            // Guarda la fecha de la descarga en SharedPreferences
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(instanciaFragmentoListado.getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("fecha_ultima_descarga", System.currentTimeMillis());
            editor.apply();

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void analisarXML(String xmlString) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);

            // análise del XML
            SAXParser analizadorSAX = factory.newSAXParser();
            ManejadorXML manejadorXML = new ManejadorXML(instanciaFragmentoListado);
            analizadorSAX.parse(new InputSource(new StringReader(xmlString)), manejadorXML);
            ArrayList<Camara> resultado = manejadorXML.getResultado();

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

    private void guardaFicheroKML(String xmlString) {
        try {
            Log.d("TAG","String que chega: "+xmlString);
            // Obter o contexto da activity
            Context context = instanciaFragmentoListado.getActivity().getApplicationContext();

            // Abrir o arquivo para escrita
            FileOutputStream fileOutputStream = context.openFileOutput("CamarasMadrid.kml", Context.MODE_PRIVATE);

            // Escrever a string XML no arquivo
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(xmlString);
            bufferedWriter.close();

            // Exibir mensagem de sucesso com o caminho do arquivo
            Log.d("TAG", "Fichero guardado en " + context.getFilesDir() + File.separator + "CamarasMadrid.kml");
        } catch (IOException e) {
            // Exibir mensagem de erro em caso de falha na escrita
            Log.d("TAG", "Error al guardar el fichero " + e.getMessage());
        }
    }

    public String recuperarFichero(){
        StringBuilder resultado=new StringBuilder();
        String linea;
        try {
            // Obter o contexto da activity
            Context context = instanciaFragmentoListado.getActivity().getApplicationContext();

            InputStream inputStream = context.openFileInput("CamarasMadrid.kml");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((linea = bufferedReader.readLine()) != null) {
                resultado.append(linea + "\n");
            }
            inputStream.close();
            Log.d ("TAG","Lido el fichero");

        } catch (FileNotFoundException e) {
            resultado.append("El fichero no existe");
            Log.d ("TAG","El fichero no existe");
        } catch (IOException e) {
            resultado.append("Error al recuperar el fichero "+e.getMessage());
            Log.d ("TAG","Error al recuperar el fichero "+e.getMessage());
        }
        return resultado.toString();
    }
}
