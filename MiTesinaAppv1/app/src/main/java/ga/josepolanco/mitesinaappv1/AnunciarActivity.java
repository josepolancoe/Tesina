package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class AnunciarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    ImageView anunciar_foto_perfil, anunciar_foto;
    TextView anunciar_nombre_anfitrion, anunciar_telefono_anfitrion;
    EditText anunciar_titulo;
    private RadioGroup rb_grupo1, rb_grupo2;
    private RadioButton rb_anunciar_departamento,rb_anunciar_casa,rb_anunciar_hotel, rb_anunciar_alojamiento_entero, rb_anunciar_habitacion_privada;
    public String rbTipo1="", rbTipo2="";
    Button btn_anunciar_foto, btn_anunciar_publicar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;

    String storagePath = "Usuarios_Perfil_Cover_Imags/";

    ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    //url of picked image
    Uri image_uri;

    //for checking profile or cover photo
    String profileOrCoverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anunciar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Usuarios");
        storageReference = getInstance().getReference(); //firebase storage reference

        anunciar_foto_perfil = findViewById(R.id.anunciar_foto_perfil);
        anunciar_foto = findViewById(R.id.anunciar_foto);
        anunciar_nombre_anfitrion = findViewById(R.id.anunciar_nombre_anfitrion);
        anunciar_telefono_anfitrion = findViewById(R.id.anunciar_telefono_anfitrion);
        rb_grupo1 = findViewById(R.id.rb_grupo1);
        rb_grupo2 = findViewById(R.id.rb_grupo2);
        rb_anunciar_departamento = findViewById(R.id.rb_anunciar_departamento);
        rb_anunciar_casa = findViewById(R.id.rb_anunciar_casa);
        rb_anunciar_hotel = findViewById(R.id.rb_anunciar_hotel);
        rb_anunciar_alojamiento_entero = findViewById(R.id.rb_anunciar_alojamiento_entero);
        rb_anunciar_habitacion_privada = findViewById(R.id.rb_anunciar_habitacion_privada);
        btn_anunciar_foto = findViewById(R.id.btn_anunciar_foto);
        btn_anunciar_publicar = findViewById(R.id.btn_anunciar_publicar);
        anunciar_titulo = findViewById(R.id.anunciar_titulo);


        //inicializar Arreglo de Permisos
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Query query = databaseReference.orderByChild("correo").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String nombre = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String celular = ""+snapshot.child("telefono").getValue();
                    String imagen = ""+snapshot.child("imagen").getValue();

                    anunciar_nombre_anfitrion.setText(nombre);
                    anunciar_telefono_anfitrion.setText(celular);
                    try {
                        Picasso.get().load(imagen).into(anunciar_foto_perfil);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_user).into(anunciar_foto_perfil);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        validarRb_grupo1();
        validarRb_grupo2();

        publicarAnuncio();

    }

    private void publicarAnuncio() {
        btn_anunciar_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre_anfitrion = anunciar_nombre_anfitrion.getText().toString();
                String telefono_anfitrion = anunciar_telefono_anfitrion.getText().toString();
                String tipo_alojamiento = rbTipo1;
                String tipo_alojamiento_descripcion = rbTipo2;
                String titulo_alojamiento = anunciar_titulo.getText().toString();
                final Map<String, Object> datosDenuncia = new HashMap<>();
                datosDenuncia.put("nombre_anfitrion",nombre_anfitrion);
                datosDenuncia.put("telefono_anfitrion",telefono_anfitrion);
                datosDenuncia.put("tipo_alojamiento",tipo_alojamiento);
                datosDenuncia.put("tipo_alojamiento_descripcion",tipo_alojamiento_descripcion);
                datosDenuncia.put("titulo_alojamiento",titulo_alojamiento);
                databaseReference.getRoot().child("Alojamientos").push().setValue(datosDenuncia);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void validarRb_grupo1(){
        rb_grupo1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_anunciar_departamento){
                    rbTipo1 = rb_anunciar_departamento.getText().toString();
                } else  if (checkedId == R.id.rb_anunciar_casa) {
                    rbTipo1 = rb_anunciar_casa.getText().toString();
                } else  if (checkedId == R.id.rb_anunciar_hotel) {
                    rbTipo1 = rb_anunciar_hotel.getText().toString();
                }
            }
        });
    }

    private void validarRb_grupo2() {
        rb_grupo2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_anunciar_alojamiento_entero){
                    rbTipo2 = rb_anunciar_alojamiento_entero.getText().toString();
                } else  if (checkedId == R.id.rb_anunciar_habitacion_privada) {
                    rbTipo2 = rb_anunciar_habitacion_privada.getText().toString();
                }
            }
        });
    }
}
