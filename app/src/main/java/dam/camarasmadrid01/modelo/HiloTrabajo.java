package dam.camarasmadrid01.modelo;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dam.camarasmadrid01.ListadoCamarasFragment;


public class HiloTrabajo implements Runnable {

    private final ListadoCamarasFragment instanciaFragmentoListado;
    private String fichero;


    public HiloTrabajo(String fichero, ListadoCamarasFragment instanciaFragmentoListado) {
        this.fichero = fichero;
        this.instanciaFragmentoListado = instanciaFragmentoListado;
    }

    @Override
    public void run() {
        ArrayList<Camara> resultado;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            // Realiza a análise do XML
            InputStream inputStream = instanciaFragmentoListado.getContext().getAssets().open(fichero);
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


