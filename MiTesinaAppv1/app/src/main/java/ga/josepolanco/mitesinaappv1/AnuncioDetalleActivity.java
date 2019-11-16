package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AnuncioDetalleActivity extends AppCompatActivity {

    //obtenemos los detalles del usuario y el anuncio
    String anfitrion_uid, anfitrion_nombre, anfitrion_correo, anfitrion_imagen,
            anuncio_id, anuncio_titulo, anuncio_imagen_alojamiento, anuncio_fecha, detalle_tipo_alojamiento, tipo_alojamiento;

    ImageView anuncio_detalle_foto_perfil,anuncio_detalle_imagen_alojamiento;
    ImageButton alojamiento_opciones;
    TextView anuncio_detalle_tipo_alojamiento_descripcion,anuncio_detalle_titulo_alojamiento,anuncio_detalle_anfitrion_nombre,anuncio_detalle_precio,anuncio_detalle_tipo_alojamiento,anuncio_detalle_descripcion_alojamiento,anuncio_detalle_servicios_comentarios;

    Button btn_anuncio_enviar_mensaje, btn_anuncio_reseñas;
    LinearLayout anuncioDetalleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio_detalle);

        Intent intent = getIntent();
        anuncio_id = intent.getStringExtra("anuncio_id");

        anuncio_detalle_foto_perfil = findViewById(R.id.anuncio_detalle_foto_perfil);
        anuncio_detalle_imagen_alojamiento = findViewById(R.id.anuncio_detalle_imagen_alojamiento);
        anuncio_detalle_tipo_alojamiento = findViewById(R.id.anuncio_detalle_tipo_alojamiento);
        anuncio_detalle_titulo_alojamiento = findViewById(R.id.anuncio_detalle_titulo_alojamiento);
        anuncio_detalle_precio = findViewById(R.id.anuncio_detalle_precio);
        alojamiento_opciones = findViewById(R.id.alojamiento_opciones);
        anuncio_detalle_tipo_alojamiento_descripcion = findViewById(R.id.anuncio_detalle_tipo_alojamiento_descripcion);
        anuncio_detalle_anfitrion_nombre=findViewById(R.id.anuncio_detalle_anfitrion_nombre);
        anuncio_detalle_descripcion_alojamiento=findViewById(R.id.anuncio_detalle_descripcion_alojamiento);
        anuncio_detalle_servicios_comentarios=findViewById(R.id.anuncio_detalle_servicios_comentarios);

        cargarAnuncioDetalle();


    }

    private void cargarAnuncioDetalle() {
        //obteniendo el anuncio del id especifico
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anuncios");
        Query query = reference.orderByChild("anuncio_id").equalTo(anuncio_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String AdTitulo = ""+snapshot.child("anuncio_titulo").getValue();
                    String AdTipoDetalle = ""+snapshot.child("detalle_tipo_alojamiento").getValue();
                    String AdTipo = ""+snapshot.child("anuncio_titulo").getValue();
                    String AdImagenAlojamiento = ""+snapshot.child("anuncio_imagen_alojamiento").getValue();
                    String AdImagenAnfitrion = ""+snapshot.child("anfitrion_imagen").getValue();
                    String AdAnfitrionId = ""+snapshot.child("anfitrion_uid").getValue();
                    //String AdAnfitrionNombre = ""+snapshot.child("anfitrion_nombre");
                    anfitrion_nombre = ""+snapshot.child("anfitrion_nombre").getValue();

                    anuncio_detalle_titulo_alojamiento.setText(AdTitulo);
                    anuncio_detalle_tipo_alojamiento_descripcion.setText(AdTipoDetalle);
                    anuncio_detalle_anfitrion_nombre.setText(anfitrion_nombre);

                    anuncio_detalle_anfitrion_nombre.setVisibility(View.VISIBLE);
                    Picasso.get().load(AdImagenAnfitrion).into(anuncio_detalle_foto_perfil);

                    if (AdImagenAlojamiento.equals("noImagen")){
                        anuncio_detalle_imagen_alojamiento.setVisibility(View.GONE);
                    }else{
                        anuncio_detalle_imagen_alojamiento.setVisibility(View.VISIBLE);
                        try{
                            Picasso.get().load(AdImagenAlojamiento).resize(700,700).centerInside().into(anuncio_detalle_imagen_alojamiento);
                        }catch (Exception e){

                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
