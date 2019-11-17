package ga.josepolanco.mitesinaappv1.Modelos;

public class ModeloChat {
    String mensaje, receptor, emisor, timestamp;
    boolean fueVisto;

    public ModeloChat(){}

    public ModeloChat(String mensaje, String receptor, String emisor, String timestamp, boolean fueVisto) {
        this.mensaje = mensaje;
        this.receptor = receptor;
        this.emisor = emisor;
        this.timestamp = timestamp;
        this.fueVisto = fueVisto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFueVisto() {
        return fueVisto;
    }

    public void setFueVisto(boolean fueVisto) {
        this.fueVisto = fueVisto;
    }
}
