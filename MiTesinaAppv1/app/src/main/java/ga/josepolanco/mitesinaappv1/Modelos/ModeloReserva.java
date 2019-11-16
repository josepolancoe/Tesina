package ga.josepolanco.mitesinaappv1.Modelos;

public class ModeloReserva {
    public String anfitrion_uid, anfitrion_nombre, anfitrion_imagen, anuncio_id, anuncio_titulo, anuncio_imagen_alojamiento, anuncio_fecha, tipo_alojamiento;
    public double anuncio_precio;

    public ModeloReserva(){

    }

    public ModeloReserva(String anfitrion_uid, String anfitrion_nombre, String anfitrion_imagen, String anuncio_id, String anuncio_titulo, String anuncio_imagen_alojamiento, String anuncio_fecha, String tipo_alojamiento, double anuncio_precio) {
        this.anfitrion_uid = anfitrion_uid;
        this.anfitrion_nombre = anfitrion_nombre;
        this.anfitrion_imagen = anfitrion_imagen;
        this.anuncio_id = anuncio_id;
        this.anuncio_titulo = anuncio_titulo;
        this.anuncio_imagen_alojamiento = anuncio_imagen_alojamiento;
        this.anuncio_fecha = anuncio_fecha;
        this.tipo_alojamiento = tipo_alojamiento;
        this.anuncio_precio = anuncio_precio;
    }

    public String getAnfitrion_nombre() {
        return anfitrion_nombre;
    }

    public void setAnfitrion_nombre(String anfitrion_nombre) {
        this.anfitrion_nombre = anfitrion_nombre;
    }

    public String getAnfitrion_imagen() {
        return anfitrion_imagen;
    }

    public void setAnfitrion_imagen(String anfitrion_imagen) {
        this.anfitrion_imagen = anfitrion_imagen;
    }

    public String getAnuncio_titulo() {
        return anuncio_titulo;
    }

    public void setAnuncio_titulo(String anuncio_titulo) {
        this.anuncio_titulo = anuncio_titulo;
    }

    public String getAnuncio_imagen_alojamiento() {
        return anuncio_imagen_alojamiento;
    }

    public void setAnuncio_imagen_alojamiento(String anuncio_imagen_alojamiento) {
        this.anuncio_imagen_alojamiento = anuncio_imagen_alojamiento;
    }

    public String getTipo_alojamiento() {
        return tipo_alojamiento;
    }

    public void setTipo_alojamiento(String tipo_alojamiento) {
        this.tipo_alojamiento = tipo_alojamiento;
    }

    public String getAnfitrion_uid() {
        return anfitrion_uid;
    }

    public void setAnfitrion_uid(String anfitrion_uid) {
        this.anfitrion_uid = anfitrion_uid;
    }

    public String getAnuncio_id() {
        return anuncio_id;
    }

    public void setAnuncio_id(String anuncio_id) {
        this.anuncio_id = anuncio_id;
    }

    public String getAnuncio_fecha() {
        return anuncio_fecha;
    }

    public void setAnuncio_fecha(String anuncio_fecha) {
        this.anuncio_fecha = anuncio_fecha;
    }

    public double getAnuncio_precio() {
        return anuncio_precio;
    }

    public void setAnuncio_precio(double anuncio_precio) {
        this.anuncio_precio = anuncio_precio;
    }
}
