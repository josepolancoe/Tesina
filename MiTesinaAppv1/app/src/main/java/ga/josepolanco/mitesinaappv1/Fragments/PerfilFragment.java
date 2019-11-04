package ga.josepolanco.mitesinaappv1.Fragments;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.security.Key;
import java.util.HashMap;

import ga.josepolanco.mitesinaappv1.AnunciarActivity;
import ga.josepolanco.mitesinaappv1.MainActivity;
import ga.josepolanco.mitesinaappv1.R;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    ImageView perfil_foto;
    TextView perfil_nombre, perfil_celular, perfil_correo;
    CardView card_perfil_anunciar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;
    //path where images or user profile and cover will be stored
    String storagePath = "Usuarios_Perfil_Cover_Imags/";

    FloatingActionButton floatingActionButton;
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

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);


        //Inicializar firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Usuarios");
        storageReference = getInstance().getReference(); //firebase storage reference


        //inicializar Arreglo de Permisos
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //Inicializar vistas
        perfil_nombre = view.findViewById(R.id.perfil_nombre);
        perfil_foto = view.findViewById(R.id.perfil_foto);
        perfil_correo = view.findViewById(R.id.perfil_correo);
        perfil_celular = view.findViewById(R.id.perfil_celular);
        floatingActionButton = view.findViewById(R.id.perfil_flotante);
        card_perfil_anunciar = view.findViewById(R.id.card_perfil_anunciar);
        card_perfil_anunciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AnunciarActivity.class);
                startActivity(intent);
            }
        });
        pd = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("correo").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String nombre = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();
                    String celular = ""+snapshot.child("telefono").getValue();
                    String imagen = ""+snapshot.child("imagen").getValue();

                    perfil_nombre.setText(nombre);
                    perfil_correo.setText(correo);
                    perfil_celular.setText(celular);
                    try {
                        Picasso.get().load(imagen).into(perfil_foto);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.img_user).into(perfil_foto);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEditarPerfil();
            }


        });
        return view;
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        requestPermissions(cameraPermissions, STORAGE_REQUEST_CODE);
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
                        Toast.makeText(getActivity(), "Porfavor active el permiso de camara y almacenamiento", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        //permission enabled
                        pickFromGallery();
                    }else{
                        //permission denied
                        Toast.makeText(getActivity(), "Porfavor active el permiso almacenamiento", Toast.LENGTH_SHORT).show();
                    }
            }

            }
            break;
        }
    }

    private void pickFromCamera() {
        //Intent of picking image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get url of image
                image_uri = data.getData();
                ActualizarPerfilFoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is shot from camera
                ActualizarPerfilFoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ActualizarPerfilFoto(final Uri uri) {
        //show progress
        pd.show();
        //path and name of image to be stored in firebase storage
        String filePathAndName = storagePath+ ""+profileOrCoverPhoto+""+firebaseUser.getUid();
        StorageReference storageReference1 = storageReference.child(filePathAndName);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //check if image is upload or not and uri is received
                if (uriTask.isSuccessful()){
                    //image upload
                    //add update url in users database
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto, downloadUri.toString());
                    databaseReference.child(firebaseUser.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "imagen actualizada", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error al catualizar", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    //error
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Ocurrio algun error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
                pd.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarEditarPerfil() {

        String opciones[]={"Editar Foto","Editar Nombre","Editar Telefono","Cerrar Sesión"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Elegir Opcion");

        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    pd.setMessage("Actualizando foto");
                    profileOrCoverPhoto = "imagen";
                    EditarImagenDialog();
                }else if (which==1){
                    pd.setMessage("Actualizando nombre");
                    editarNombreDialog("nombres");
                }else if (which==2){
                    pd.setMessage("Actualizando telefono");
                    editarNombreDialog("telefono");

                }else if (which==3){
                    pd.setMessage("Cerrando sesión.");
                    firebaseAuth.signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);


                }
            }




        });

        builder.create().show();
    }

    private void editarNombreDialog(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Editar Nombre"+ key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Ingrese "+key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);

        //add button in dialog to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text from edit text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //updated, dismiss progress
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Actualizado.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "Ingrese "+key, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //add button in dialog to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            pd.dismiss();
            }
        });

        //create and show dialog
        builder.create().show();
    }


    private void EditarImagenDialog() {
        String opciones[]={"Tomar Foto","Elegir Foto"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Editar Foto");

        builder.setItems(opciones, new DialogInterface.OnClickListener() {
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

}
