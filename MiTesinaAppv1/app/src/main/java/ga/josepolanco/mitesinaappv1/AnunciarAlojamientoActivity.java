package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AnunciarAlojamientoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    ImageView iv_anunciar_foto;
    TextView anunciar_titulo2, anunciar_precio, lat, lon;
    Button btn_anunciar_publicar2;


    private RadioGroup rb_grupo1, rb_grupo2;
    private RadioButton rb_anunciar_departamento, rb_anunciar_casa, rb_anunciar_hotel, rb_anunciar_alojamiento_entero, rb_anunciar_habitacion_privada;
    public String rbTipo1 = "", rbTipo2 = "";
    double latitud = 0, longitud = 0;
    String alojamiento_precio="", anuncio_estado="Disponible";
    String anuncio_precio_formato;
    String txt_spinner1="", txt_spiner2="";
    String txt_checkbox1="",txt_checkbox2="",txt_checkbox3="",txt_checkbox4="",txt_checkbox5="",txt_checkbox6="",txt_checkbox7="",txt_checkbox8="";
    String alojamiento_ubicacion="";

    ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri = null;

    //user info
    String nombre, correo, uid, imagen_perfil;

    private FusedLocationProviderClient mFusedLocationClient;

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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    nombre = "" + snapshot.child("nombres").getValue();
                    correo = "" + snapshot.child("correo").getValue();
                    imagen_perfil = "" + snapshot.child("imagen").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        lat =findViewById(R.id.tv_latitude);
        lon=findViewById(R.id.tv_longitude);
        obtenerLatLong();

        iv_anunciar_foto = findViewById(R.id.iv_anunciar_foto);
        anunciar_titulo2 = findViewById(R.id.anunciar_titulo2);
        anunciar_precio = findViewById(R.id.anunciar_precio);
        rb_grupo1 = findViewById(R.id.rb_grupo1);
        rb_grupo2 = findViewById(R.id.rb_grupo2);
        rb_anunciar_departamento = findViewById(R.id.rb_anunciar_departamento);
        rb_anunciar_casa = findViewById(R.id.rb_anunciar_casa);
        rb_anunciar_hotel = findViewById(R.id.rb_anunciar_hotel);
        rb_anunciar_alojamiento_entero = findViewById(R.id.rb_anunciar_alojamiento_entero);
        rb_anunciar_habitacion_privada = findViewById(R.id.rb_anunciar_habitacion_privada);
        btn_anunciar_publicar2 = findViewById(R.id.btn_anunciar_publicar2);

        final CheckBox cb1 = findViewById(R.id.cb_servicios_basicos);
        final CheckBox cb2 = findViewById(R.id.cb_wifi);
        final CheckBox cb3 = findViewById(R.id.cb_ordenador);
        final CheckBox cb4 = findViewById(R.id.cb_tv);
        final CheckBox cb5 = findViewById(R.id.cb_lavadora);
        final CheckBox cb6 = findViewById(R.id.cb_cocina);
        final CheckBox cb7 = findViewById(R.id.cb_almuerzo);
        final CheckBox cb8 = findViewById(R.id.cb_desayuno);

               Spinner spinner1 = findViewById(R.id.spinner_1);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.spinner_tipo_alojamiento, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);

        Spinner spinner2 = findViewById(R.id.spinner_2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.spinner_tipo_detalle, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Hola");
        adapter.add("Cuando");
        adapter.add("Hint to be displayed");

        spinner1.setAdapter(adapter);
        spinner1.setSelection(adapter.getCount()); //display hint
        spinner1.setOnItemClickListener((AdapterView.OnItemClickListener) this);*/

        iv_anunciar_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        validarRb_grupo1();
        validarRb_grupo2();

        onCheckboxClicked(cb1);

        btn_anunciar_publicar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarRb_grupo1();
                validarRb_grupo2();
                if (rb_grupo1.getCheckedRadioButtonId() == -1){
                    Toast.makeText(AnunciarAlojamientoActivity.this, "seleccione un tipo de alojamiento", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rb_grupo2.getCheckedRadioButtonId() == -1)                {
                    Toast.makeText(AnunciarAlojamientoActivity.this, "seleccione grupo2", Toast.LENGTH_SHORT).show();
                    return;
                }
                String titulo=anunciar_titulo2.getText().toString().trim();
                if (TextUtils.isEmpty(titulo)){
                    Toast.makeText(AnunciarAlojamientoActivity.this, "Ingrese el titutlo", Toast.LENGTH_SHORT).show();
                    return;
                }
                alojamiento_precio = anunciar_precio.getText().toString().trim();
                //anuncio_precio_formato = Double.parseDouble(anunciar_precio.getText().toString());
                anuncio_precio_formato = anunciar_precio.getText().toString().trim();

                if (TextUtils.isEmpty(alojamiento_precio)){
                    Toast.makeText(AnunciarAlojamientoActivity.this, "Ingrese el precio", Toast.LENGTH_SHORT).show();
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

    private void obtenerLatLong() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this,new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    Log.e(" Latitud alojamiento: ",+location.getLatitude()+" Longitud: "+location.getLongitude());
                    lat.setText(String.valueOf(location.getLatitude()));
                    lon.setText(String.valueOf(location.getLongitude()));
                    latitud=location.getLatitude();
                    longitud=location.getLongitude();

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(latitud, longitud, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    alojamiento_ubicacion = addresses.get(0).getAddressLine(0);
                }
            }
        });
    }

    private void uploadData(final String titulo, String uri) {
        progressDialog.setMessage("Publicando Anuncio...");
        progressDialog.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Anuncios/" + "anuncio_" + timeStamp;

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
                                HashMap<Object, Object> hashMap = new HashMap<>();
                                hashMap.put("anfitrion_uid",uid);
                                hashMap.put("anfitrion_nombre",nombre);
                                hashMap.put("anfitrion_correo",correo);
                                hashMap.put("anfitrion_imagen",imagen_perfil);
                                hashMap.put("anuncio_id", timeStamp);
                                hashMap.put("anuncio_titulo",titulo);
                                hashMap.put("anuncio_imagen_alojamiento",downloadUri);
                                hashMap.put("anuncio_fecha",timeStamp);
                                hashMap.put("tipo_alojamiento",rbTipo1);
                                hashMap.put("detalle_tipo_alojamiento",rbTipo2);
                                hashMap.put("latitud",latitud);
                                hashMap.put("longitud",longitud);
                                hashMap.put("servicio_1",txt_checkbox1);
                                hashMap.put("servicio_2",txt_checkbox2);
                                hashMap.put("servicio_3",txt_checkbox3);
                                hashMap.put("servicio_4",txt_checkbox4);
                                hashMap.put("servicio_5",txt_checkbox5);
                                hashMap.put("servicio_6",txt_checkbox6);
                                hashMap.put("servicio_7",txt_checkbox7);
                                hashMap.put("servicio_8",txt_checkbox8);
                                hashMap.put("anuncio_precio","S/. "+alojamiento_precio+" por noche");
                                hashMap.put("anuncio_precio_formato", anuncio_precio_formato);
                                hashMap.put("anuncio_ubicacion",alojamiento_ubicacion);
                                hashMap.put("anuncio_estado",anuncio_estado);

                                //publicamos archivo
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Anuncios");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //añadido a la base de datos
                                                progressDialog.dismiss();
                                                Toast.makeText(AnunciarAlojamientoActivity.this, "Anuncio publicado", Toast.LENGTH_SHORT).show();
                                                //reset View
                                                anunciar_titulo2.setText("");
                                                anunciar_precio.setText("");
                                                iv_anunciar_foto.setImageURI(null);
                                                image_uri=null;
                                                rb_grupo1.clearCheck();
                                                rb_grupo2.clearCheck();
                                                startActivity(new Intent(AnunciarAlojamientoActivity.this, PerfilActivity.class));
                                                finish();
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
            Toast.makeText(this, "El anuncio debe contener una imagen del alojamiento", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
/*            HashMap<Object, Object> hashMap = new HashMap<>();
            hashMap.put("anfitrion_uid",uid);
            hashMap.put("anfitrion_nombre",nombre);
            hashMap.put("anfitrion_correo",correo);
            hashMap.put("anfitrion_imagen",imagen_perfil);
            hashMap.put("anuncio_id", timeStamp);
            hashMap.put("anuncio_titulo",titulo);
            hashMap.put("anuncio_imagen_alojamiento","noImagen");
            hashMap.put("anuncio_fecha",timeStamp);
            hashMap.put("tipo_alojamiento",rbTipo1);
            hashMap.put("detalle_tipo_alojamiento",rbTipo2);
            hashMap.put("latitud",latitud);
            hashMap.put("longitud",longitud);

            //publicamos archivo
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Anuncios");
            ref.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //añadido a la base de datos
                            progressDialog.dismiss();
                            Toast.makeText(AnunciarAlojamientoActivity.this, "Anuncio publicado", Toast.LENGTH_SHORT).show();
                            //reset View
                            anunciar_titulo2.setText("");
                            anunciar_precio.setText("");
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
            });*/
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
                //iv_anunciar_foto.setImageURI(image_uri);
                Picasso.get().load(image_uri).resize(500,500).centerInside().into(iv_anunciar_foto);

                //Picasso.get().load(image_uri).into(iv_anunciar_foto);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is shot from camera
                //iv_anunciar_foto.setImageURI(image_uri);
                Picasso.get().load(image_uri).resize(500,500).centerInside().into(iv_anunciar_foto);


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSpinner = parent.getId();

        switch(idSpinner) {
            case R.id.spinner_1:
                txt_spinner1 = parent.getItemAtPosition(position).toString();
                Toast.makeText(this, ""+txt_spinner1, Toast.LENGTH_SHORT).show();
                break;
            case R.id.spinner_2:
                txt_spiner2 = parent.getItemAtPosition(position).toString();
                Toast.makeText(this, ""+txt_spiner2, Toast.LENGTH_SHORT).show();
                break;

            }
        }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Seleccione una opcion", Toast.LENGTH_SHORT).show();
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.cb_servicios_basicos:
                if (checked){
                    txt_checkbox1 = "Servicios Básicos";
                    Toast.makeText(this, ""+txt_checkbox1, Toast.LENGTH_SHORT).show();
                }
            else
                txt_checkbox1="";
                break;
            case R.id.cb_wifi:
                if (checked){
                    txt_checkbox2 = "Wi-fi";
                    Toast.makeText(this, ""+txt_checkbox2, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox2="";
                break;
            case R.id.cb_ordenador:
                if (checked){
                    txt_checkbox3 = "Ordenador";
                    Toast.makeText(this, ""+txt_checkbox3, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox3="";
                break;
            case R.id.cb_tv:
                if (checked){
                    txt_checkbox4 = "TV";
                    Toast.makeText(this, ""+txt_checkbox4, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox4="";
                break;
            case R.id.cb_lavadora:
                if (checked){
                    txt_checkbox5 = "Lavadora";
                    Toast.makeText(this, ""+txt_checkbox5, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox5="";
                break;
            case R.id.cb_cocina:
                if (checked){
                    txt_checkbox6 = "Cocina";
                    Toast.makeText(this, ""+txt_checkbox6, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox6="";
                break;
            case R.id.cb_almuerzo:
                if (checked){
                    txt_checkbox7 = "Almuerzo";
                    Toast.makeText(this, ""+txt_checkbox7, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox7="";
                break;
            case R.id.cb_desayuno:
                if (checked){
                    txt_checkbox8 = "Desayuno";
                    Toast.makeText(this, ""+txt_checkbox8, Toast.LENGTH_SHORT).show();
                }
                else
                    txt_checkbox8="";
                break;
            // TODO: Veggie sandwich
        }
    }

}
