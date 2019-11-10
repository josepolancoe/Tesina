package ga.josepolanco.mitesinaappv1.Clases;

public class ModeloReserva {
    String anfitrion_nombre, anfitrion_imagen, anuncio_titulo, anuncio_imagen_alojamiento, tipo_alojamiento;

    public ModeloReserva(){

    }

    public ModeloReserva(String anfitrion_nombre, String anfitrion_imagen, String anuncio_titulo, String anuncio_imagen_alojamiento, String tipo_alojamiento) {
        this.anfitrion_nombre = anfitrion_nombre;
        this.anfitrion_imagen = anfitrion_imagen;
        this.anuncio_titulo = anuncio_titulo;
        this.anuncio_imagen_alojamiento = anuncio_imagen_alojamiento;
        this.tipo_alojamiento = tipo_alojamiento;
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
}
