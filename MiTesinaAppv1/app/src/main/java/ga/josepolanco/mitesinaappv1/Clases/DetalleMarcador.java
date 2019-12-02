package ga.josepolanco.mitesinaappv1.Clases;

public class DetalleMarcador {
    double latitud, longitud;
    String anuncio_id, titulo, tipo_alojamiento, precio;

    public DetalleMarcador(){

    }

    public DetalleMarcador(double latitud, double longitud, String anuncio_id, String titulo, String tipo_alojamiento, String precio) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.anuncio_id = anuncio_id;
        this.titulo = titulo;
        this.tipo_alojamiento = tipo_alojamiento;
        this.precio = precio;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getAnuncio_id() {
        return anuncio_id;
    }

    public void setAnuncio_id(String anuncio_id) {
        this.anuncio_id = anuncio_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo_alojamiento() {
        return tipo_alojamiento;
    }

    public void setTipo_alojamiento(String tipo_alojamiento) {
        this.tipo_alojamiento = tipo_alojamiento;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
