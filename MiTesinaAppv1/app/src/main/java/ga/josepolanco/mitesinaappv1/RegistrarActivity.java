package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrarActivity extends AppCompatActivity {
    EditText r_correo, r_clave;
    Button r_btn_registrar;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Crear Cuenta");

        //Activar boton hacia atras
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        r_correo = findViewById(R.id.r_correo);
        r_clave = findViewById(R.id.r_clave);
        r_btn_registrar = findViewById(R.id.r_registrar);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando Usuario...");

        r_btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = r_correo.getText().toString().trim();
                String clave = r_clave.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    r_correo.setError("Correo Invalido");
                    r_correo.setFocusable(true);
                }else if (clave.length()<6){
                    r_clave.setError("La clave debe ser mayor a 6 digitos");
                }else{
                    registrarUsuario(correo,clave);
                }
            }
        });
    }

    private void registrarUsuario(String correo, String clave) {
        //correo y clave serán validados, mostrará un dialogo y se registrará.
    progressDialog.show();

        mAuth.createUserWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Registro exitoso, desaparece el dialogo y lanza la actividad.
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //obtener correo de usuario y llave
                            String email = user.getEmail();
                            String uid = user.getUid();

                            //cuando el usuario está registrado, almacena la información del usuario en la base de datos en tiempo real de Firebase también usando HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //subimos informacion en hasmap
                            hashMap.put("correo",email);
                            hashMap.put("uid",uid);
                            hashMap.put("nombres","");
                            hashMap.put("apellidos","");
                            hashMap.put("sexo","");
                            hashMap.put("fecha-de-nacimiento","");
                            hashMap.put("telefono","");
                            hashMap.put("contacto-de-emergencia","");
                            hashMap.put("imagen","");

                            //intanciamos la base de dato
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //ruta para almacenar datos de usuario denominados usuarios
                            DatabaseReference reference = database.getReference("Usuarios");
                            //poner datos dentro de hashmap en la base de datos
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegistrarActivity.this, "Usuario Registrado: \n "+user.getEmail().toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrarActivity.this, PerfilActivity.class));
                            finish();
                        } else {
                            //Si el registro fallará, mostará un mensaje al usuario.
                            progressDialog.dismiss();
                            Toast.makeText(RegistrarActivity.this, "Algo falló.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error, desaparece el dialogo y obtiene un mensaje de error
                progressDialog.dismiss();
                Toast.makeText(RegistrarActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //volver a la actividad anterior
        return super.onSupportNavigateUp();
    }
}
