package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView chat_foto_perfil;
    TextView chat_anfitrion_nombre,chat_anfitrion_estado;
    EditText chat_entrada;
    ImageButton chat_enviar;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference;

    String miId, anfitrion_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chat_toolbar);
        recyclerView = findViewById(R.id.chat_recyclerView);
        chat_foto_perfil = findViewById(R.id.chat_foto_perfil);
        chat_anfitrion_nombre = findViewById(R.id.chat_anfitrion_nombre);
        chat_anfitrion_estado = findViewById(R.id.chat_anfitrion_estado);
        chat_entrada = findViewById(R.id.chat_entrada);
        chat_enviar = findViewById(R.id.chat_enviar);

        Intent intent = getIntent();
        anfitrion_uid = intent.getStringExtra("anfitrion_uid");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userReference = firebaseDatabase.getReference().child("Usuarios");

        Query query = userReference.orderByChild("uid").equalTo(anfitrion_uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //obtenemos información
                    String nombre = ""+snapshot.child("nombres").getValue();
                    String imagen = ""+snapshot.child("imagen").getValue();


                    //seteamos los datos
                    chat_anfitrion_nombre.setText(nombre);
                    try{
                        Picasso.get().load(imagen).into(chat_foto_perfil);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_user).into(chat_foto_perfil);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chat_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtenemos el texto del mensaje
                String mensaje = chat_entrada.getText().toString().trim();

                //revisamos si es vacio o no
                if (TextUtils.isEmpty(mensaje)){
                    Toast.makeText(ChatActivity.this, "Ingrese algun mensaje!", Toast.LENGTH_SHORT).show();
                }else{
                    enviarMensaje(mensaje);
                }
            }
        });
    }

    private void enviarMensaje(String mensaje) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor",miId);
        hashMap.put("receptor",anfitrion_uid);
        hashMap.put("mensaje",mensaje);
        databaseReference.child("Chats").push().setValue(hashMap);

        //vaciamos la caja
        chat_entrada.setText("");
    }

    private void validadEstadoUsuario(){
        //obtener usuario actual
        FirebaseUser usuario = firebaseAuth.getCurrentUser();
        if (usuario != null){
            miId = usuario.getUid();

        }else{
            //el usuario no ha iniciado sesión, regresa al activity principal
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        //ocultamos el buscador,
        menu.findItem(R.id.menu_buscar).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cerrar_sesion){
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            validadEstadoUsuario();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        validadEstadoUsuario();
        super.onStart();
    }
}
