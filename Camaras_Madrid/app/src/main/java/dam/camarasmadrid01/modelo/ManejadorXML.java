package dam.camarasmadrid01.modelo;


import android.os.SystemClock;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import dam.camarasmadrid01.ListadoCamarasFragment;

public class ManejadorXML extends DefaultHandler {
    private String nombre,url,coordenada;
    private StringBuilder contenido;
    private boolean esNombre, esUrl, esCoordenada;
    private StringBuilder resultado;
    private int contadorCamarasActual = 0;
    private ListadoCamarasFragment instanciaFragmentoListado;
    ArrayList<Camara> listaCamaras = new ArrayList<Camara>();

    public ArrayList<Camara> getResultado() {
        return listaCamaras;
    }

    public ManejadorXML(ListadoCamarasFragment instanciaClaseTarea) {
        this.instanciaFragmentoListado = instanciaClaseTarea;
    }
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        contenido = new StringBuilder();
        resultado = new StringBuilder();
        esNombre = false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        contenido.setLength(0);
        switch (localName) {
            case "Data":
                if (attributes.getValue(0).equals("Nombre")) {
                    esNombre = true;
                }
                break;
            case"Value":
                break;
            case "description":
                esUrl = true;
                break;
            case "coordinates":
                esCoordenada = true;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start,length);
        contenido.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (localName) {
            case  "Placemark":
                listaCamaras.add(new Camara(nombre, url,coordenada));
                SystemClock.sleep(10);
                instanciaFragmentoListado.progresoContador.setText(Integer.toString(contadorCamarasActual++));
                break;
            case "Value":
                if (esNombre) {
                    nombre = contenido.toString().trim();
                    esNombre = false;
                }
                break;
            case "description":
                if (esUrl) {
                    url = contenido.toString().trim();
                    esUrl = false;
                }
                break;
            case "coordinates":
                if (esCoordenada) {
                    coordenada = contenido.toString().trim();
                    esCoordenada = false;
                }
                break;
        }
        contenido.setLength(0);
    }

    @Override
    public void endDocument()throws  SAXException{
        super.endDocument();
    }
}
