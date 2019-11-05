package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AnunciarAlojamientoActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ImageView iv_anunciar_foto;
    TextView anunciar_titulo2;
    Button btn_anunciar_publicar2;


    private RadioGroup rb_grupo1, rb_grupo2;
    private RadioButton rb_anunciar_departamento,rb_anunciar_casa,rb_anunciar_hotel, rb_anunciar_alojamiento_entero, rb_anunciar_habitacion_privada;
    public String rbTipo1="", rbTipo2="";

    ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri=null;

    //user info
    String nombre,correo,uid,dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anunciar_alojamiento);

        //inicializar Arreglo de Permisos
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);

        firebaseAuth = firebaseAuth.getInstance();
        revisarEstadoUsuario();

        //obtengo los datos usuarios de?
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        Query query = databaseReference.orderByChild("correo").equalTo(correo);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                     nombre = ""+snapshot.child("nombres").getValue();
                     correo = ""+snapshot.child("correo").getValue();
                     dp = ""+snapshot.child("imagen").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        iv_anunciar_foto = findViewById(R.id.iv_anunciar_foto);
        anunciar_titulo2 = findViewById(R.id.anunciar_titulo2);
        rb_grupo1 = findViewById(R.id.rb_grupo1);
        rb_grupo2 = findViewById(R.id.rb_grupo2);
        rb_anunciar_departamento = findViewById(R.id.rb_anunciar_departamento);
        rb_anunciar_casa = findViewById(R.id.rb_anunciar_casa);
        rb_anunciar_hotel = findViewById(R.id.rb_anunciar_hotel);
        rb_anunciar_alojamiento_entero = findViewById(R.id.rb_anunciar_alojamiento_entero);
        rb_anunciar_habitacion_privada = findViewById(R.id.rb_anunciar_habitacion_privada);
        btn_anunciar_publicar2 = findViewById(R.id.btn_anunciar_publicar2);

        iv_anunciar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        btn_anunciar_publicar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarRb_grupo1();
                validarRb_grupo2();
                if (rb_grupo1.getCheckedRadioButtonId() == -1){
                    Toast.makeText(AnunciarAlojamientoActivity.this, "seleccione grupo1", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rb_grupo2.getCheckedRadioButtonId() == -1)                {
                    Toast.makeText(AnunciarAlojamientoActivity.this, "seleccione grupo2", Toast.LENGTH_SHORT).show();
                    return;
                }
                String titulo=anunciar_titulo2.getText().toString().trim();
                if (TextUtils.isEmpty(titulo)){
                    Toast.makeText(AnunciarAlojamientoActivity.this, "Ingrese titutlo", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_uri==null){
                    uploadData(titulo,"noImagen");
                }else{
                    uploadData(titulo,String.valueOf(image_uri));
                }
            }
        });
    }

    private void uploadData(final String titulo, String uri) {
        progressDialog.setMessage("Publicando Anuncio...");
        progressDialog.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if (!uri.equals("noImagen")){
            StorageReference reference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            reference.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Imagen subiendo a firebase, ahora obtenemos el url
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()){
                                //url recibida
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid",uid);
                                hashMap.put("uName",nombre);
                                hashMap.put("uCorreo",correo);
                                hashMap.put("uDp",dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitulo",titulo);
                                hashMap.put("pImagen",downloadUri);
                                hashMap.put("pTime",timeStamp);
                                hashMap.put("tipo_alojamiento1",rbTipo1);
                                hashMap.put("tipo_alojamiento2",rbTipo2);

                                //publicamos archivo
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //añadido a la base de datos
                                                progressDialog.dismiss();
                                                Toast.makeText(AnunciarAlojamientoActivity.this, "Alojamiento publicado", Toast.LENGTH_SHORT).show();
                                                //reset View
                                                anunciar_titulo2.setText("");
                                                iv_anunciar_foto.setImageURI(null);
                                                image_uri=null;
                                                rb_grupo1.clearCheck();
                                                rb_grupo2.clearCheck();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AnunciarAlojamientoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });
        }else{
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid",uid);
            hashMap.put("uName",nombre);
            hashMap.put("uCorreo",correo);
            hashMap.put("uDp",dp);
            hashMap.put("pId", timeStamp);
            hashMap.put("pTitulo",titulo);
            hashMap.put("pImagen","noImagen");
            hashMap.put("pTime",timeStamp);
            hashMap.put("tipo_alojamiento1",rbTipo1);
            hashMap.put("tipo_alojamiento2",rbTipo2);

            //publicamos archivo
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //añadido a la base de datos
                            progressDialog.dismiss();
                            Toast.makeText(AnunciarAlojamientoActivity.this, "Alojamiento publicado", Toast.LENGTH_SHORT).show();
                            //reset View
                            anunciar_titulo2.setText("");
                            iv_anunciar_foto.setImageURI(null);
                            image_uri=null;
                            rb_grupo1.clearCheck();
                            rb_grupo2.clearCheck();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AnunciarAlojamientoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void showImagePickDialog() {
        String[] options={"Camara","Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Foto");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }
                }else if (which==1){
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        //permission enabled
                        pickFromCamera();
                    }else{
                        //permission denied
                        Toast.makeText(this, "Porfavor active el permiso de camara y almacenamiento", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permission enabled
                        pickFromGallery();
                    }else{
                        //permission denied
                        Toast.makeText(this, "Porfavor active el permiso almacenamiento", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //Intent of picking image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        //put image uri
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        revisarEstadoUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();
        revisarEstadoUsuario();
    }

    private void revisarEstadoUsuario(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user!=null){
            correo = user.getEmail();
            uid = user.getUid();

        }else{
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get url of image
                image_uri = data.getData();
                iv_anunciar_foto.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is shot from camera
                iv_anunciar_foto.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
