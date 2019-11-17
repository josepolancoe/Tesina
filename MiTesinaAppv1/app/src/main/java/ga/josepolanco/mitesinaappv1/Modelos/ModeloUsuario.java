package ga.josepolanco.mitesinaappv1.Modelos;

public class ModeloUsuario {
    String uid, nombres, apellidos, correo, imagen, telefono, sexo, dni, fecha_de_nacimiento, contacto_de_emergencia, estado_en_linea;

    public ModeloUsuario(){

    }

    public ModeloUsuario(String uid, String nombres, String apellidos, String correo, String imagen, String telefono, String sexo, String dni, String fecha_de_nacimiento, String contacto_de_emergencia, String estado_en_linea) {
        this.uid = uid;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.imagen = imagen;
        this.telefono = telefono;
        this.sexo = sexo;
        this.dni = dni;
        this.fecha_de_nacimiento = fecha_de_nacimiento;
        this.contacto_de_emergencia = contacto_de_emergencia;
        this.estado_en_linea = estado_en_linea;
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

    public String getFecha_de_nacimiento() {
        return fecha_de_nacimiento;
    }

    public void setFecha_de_nacimiento(String fecha_de_nacimiento) {
        this.fecha_de_nacimiento = fecha_de_nacimiento;
    }

    public String getContacto_de_emergencia() {
        return contacto_de_emergencia;
    }

    public void setContacto_de_emergencia(String contacto_de_emergencia) {
        this.contacto_de_emergencia = contacto_de_emergencia;
    }

    public String getEstado_en_linea() {
        return estado_en_linea;
    }

    public void setEstado_en_linea(String estado_en_linea) {
        this.estado_en_linea = estado_en_linea;
    }
}
