package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ga.josepolanco.mitesinaappv1.Adaptadores.AdaptadorChat;
import ga.josepolanco.mitesinaappv1.Modelos.ModeloChat;

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

    //para revisar si se ha visto el mensaje o no
    ValueEventListener vistoListener;
    DatabaseReference vistoReference;

    List<ModeloChat> listaChat;
    AdaptadorChat adaptadorChat;

    String miId, anfitrion_uid, anfitrion_imagen;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //Propiedades del Recycler
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

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
                    anfitrion_imagen = ""+snapshot.child("imagen").getValue();


                    //seteamos los datos
                    chat_anfitrion_nombre.setText(nombre);
                    try{
                        Picasso.get().load(anfitrion_imagen).into(chat_foto_perfil);
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

        leerMensaje();
        verMensaje();
    }

    private void verMensaje() {
        vistoReference = FirebaseDatabase.getInstance().getReference("Chats");
        vistoListener = vistoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModeloChat chat = snapshot.getValue(ModeloChat.class);
                    if (chat.getReceptor().equals(miId) && chat.getEmisor().equals(anfitrion_uid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("fueVisto",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void leerMensaje() {
        listaChat = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModeloChat modeloChat = snapshot.getValue(ModeloChat.class);
                    if (modeloChat.getReceptor().equals(miId)&&modeloChat.getEmisor().equals(anfitrion_uid) ||
                            modeloChat.getReceptor().equals(anfitrion_uid)&&modeloChat.getEmisor().equals(miId)){
                        listaChat.add(modeloChat);
                    }

                    //Adaptador
                    adaptadorChat = new AdaptadorChat(ChatActivity.this, listaChat, anfitrion_imagen);
                    adaptadorChat.notifyDataSetChanged();

                    //Recycler
                    recyclerView.setAdapter(adaptadorChat);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void enviarMensaje(String mensaje) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor",miId);
        hashMap.put("receptor",anfitrion_uid);
        hashMap.put("mensaje",mensaje);
        hashMap.put("timestamp",timestamp);
        hashMap.put("fueVisto",false);
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

    @Override
    protected void onPause() {
        super.onPause();
        vistoReference.removeEventListener(vistoListener);
    }
}
