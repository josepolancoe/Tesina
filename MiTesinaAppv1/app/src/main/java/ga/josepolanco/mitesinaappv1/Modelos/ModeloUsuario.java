package ga.josepolanco.mitesinaappv1.Modelos;

public class ModeloUsuario {
    String uid, nombres, apellidos, correo, imagen, telefono, sexo, dni;

    public ModeloUsuario(){

    }

    public ModeloUsuario(String uid, String nombres, String apellidos, String correo, String imagen, String telefono, String sexo, String dni) {
        this.uid = uid;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.imagen = imagen;
        this.telefono = telefono;
        this.sexo = sexo;
        this.dni = dni;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
