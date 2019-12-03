package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AnuncioDetalleActivity extends AppCompatActivity {
    Context context;

    //obtenemos los detalles del usuario y el anuncio
    String anfitrion_uid, anfitrion_nombre, anfitrion_correo, anfitrion_imagen,
            anuncio_id, anuncio_titulo, anuncio_imagen_alojamiento, anuncio_fecha, detalle_tipo_alojamiento, tipo_alojamiento;
    String anuncio_precio_formato;

    ImageView anuncio_detalle_foto_perfil,anuncio_detalle_imagen_alojamiento;
    ImageButton alojamiento_opciones;
    TextView anuncio_detalle_tipo_alojamiento_descripcion,anuncio_detalle_titulo_alojamiento,anuncio_detalle_anfitrion_nombre,anuncio_detalle_precio,anuncio_detalle_tipo_alojamiento,txt_ubicacion_alojamiento,anuncio_detalle_servicios_comentarios;

    TextView txt_servicios_basicos, txt_servicio_wifi, txt_servicio_cocina,txt_servicio_ordenador,txt_servicio_almuerzo,
            txt_servicio_tv, txt_servicio_desayuno, txt_servicio_lavadora, txt_tipo_alojamiento;

    Button btn_contactar_anuncio_detalle, anuncio_detalle_btnReservar;
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
        txt_ubicacion_alojamiento=findViewById(R.id.txt_ubicacion_alojamiento);
        btn_contactar_anuncio_detalle = findViewById(R.id.btn_contactar_anuncio_detalle);

        //TextView Servicios Alojamiento
        txt_servicios_basicos = findViewById(R.id.txt_servicios_basicos);
        txt_servicio_wifi = findViewById(R.id.txt_servicio_wifi);
        txt_servicio_ordenador = findViewById(R.id.txt_servicio_ordenador);
        txt_servicio_almuerzo = findViewById(R.id.txt_servicio_almuerzo);
        txt_servicio_cocina = findViewById(R.id.txt_servicio_cocina);
        txt_servicio_desayuno = findViewById(R.id.txt_servicio_desayuno);
        txt_servicio_lavadora = findViewById(R.id.txt_servicio_lavadora);
        txt_servicio_tv = findViewById(R.id.txt_servicio_tv);

        txt_tipo_alojamiento = findViewById(R.id.txt_tipo_alojamiento);

        anuncio_detalle_btnReservar = findViewById(R.id.anuncio_detalle_btnReservar);

        cargarAnuncioDetalle();

        btn_contactar_anuncio_detalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("anfitrion_uid",anfitrion_uid);
                getApplicationContext().startActivity(intent);
            }
        });

        anuncio_detalle_btnReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("anuncio_id", anuncio_id);
                intent.putExtra("anfitrion_uid",anfitrion_uid);
                intent.putExtra("anfitrion_nombre",anfitrion_nombre);
                intent.putExtra("anuncio_precio_formato",anuncio_precio_formato);
                getApplicationContext().startActivity(intent);
            }
        });

    }

    private void cargarAnuncioDetalle() {
        //obteniendo el anuncio del id especifico
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anuncios");
        Query query = reference.orderByChild("anuncio_id").equalTo(anuncio_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String AdTitulo = "" + snapshot.child("anuncio_titulo").getValue();
                    String AdTipoDetalle = "" + snapshot.child("detalle_tipo_alojamiento").getValue();
                    String AdTipoAlojamiento = "" + snapshot.child("tipo_alojamiento").getValue();
                    String AdImagenAlojamiento = "" + snapshot.child("anuncio_imagen_alojamiento").getValue();
                    String AdImagenAnfitrion = "" + snapshot.child("anfitrion_imagen").getValue();
                    anfitrion_uid = "" + snapshot.child("anfitrion_uid").getValue();
                    //String AdAnfitrionNombre = ""+snapshot.child("anfitrion_nombre");
                    anfitrion_nombre = "" + snapshot.child("anfitrion_nombre").getValue();
                    //Anfitrion


                    //Anuncio
                    String Aprecio = "" + snapshot.child("anuncio_precio").getValue();
                    anuncio_precio_formato = ""+snapshot.child("anuncio_precio_formato").getValue();
                    String Aubicacion = ""+snapshot.child("anuncio_ubicacion").getValue();


                    //Servicios Alojamiento
                    String SaBasico = "" + snapshot.child("servicio_1").getValue();
                    String SaWifi = "" + snapshot.child("servicio_2").getValue();
                    String SaOrdenador = "" + snapshot.child("servicio_3").getValue();
                    String SaTv = "" + snapshot.child("servicio_4").getValue();
                    String SaLavadora = "" + snapshot.child("servicio_5").getValue();
                    String SaCocina = "" + snapshot.child("servicio_6").getValue();
                    String SaAlmuerzo = "" + snapshot.child("servicio_7").getValue();
                    String SaDesayuno = "" + snapshot.child("servicio_8").getValue();

                    //Set Anuncio
                    anuncio_detalle_titulo_alojamiento.setText(AdTitulo);
                    anuncio_detalle_tipo_alojamiento_descripcion.setText(AdTipoDetalle);
                    anuncio_detalle_anfitrion_nombre.setText(anfitrion_nombre);
                    anuncio_detalle_precio.setText(Aprecio);
                    txt_tipo_alojamiento.setText(AdTipoAlojamiento);
                    txt_ubicacion_alojamiento.setText(Aubicacion);

                    //Set Servicios
                    /*txt_servicios_basicos.setText(SaBasico);
                    txt_servicio_wifi.setText(SaWifi);
                    txt_servicio_ordenador.setText(SaOrdenador);
                    txt_servicio_tv.setText(SaTv);
                    txt_servicio_lavadora.setText(SaLavadora);
                    txt_servicio_cocina.setText(SaCocina);
                    txt_servicio_almuerzo.setText(SaAlmuerzo);
                    txt_servicio_desayuno.setText(SaDesayuno);*/


                            if (!SaBasico.equals("")){
                                txt_servicios_basicos.setText(SaBasico);
                            }
                            else
                                txt_servicios_basicos.setVisibility(View.GONE);
                            if (!SaWifi.equals("")){
                                txt_servicio_wifi.setText(SaWifi);
                            }
                            else
                                txt_servicio_wifi.setVisibility(View.GONE);
                            if (!SaOrdenador.equals("")){
                                txt_servicio_ordenador.setText(SaOrdenador);
                            }
                            else
                                txt_servicio_ordenador.setVisibility(View.GONE);
                            if (!SaTv.equals("")){
                                txt_servicio_tv.setText(SaTv);
                            }
                            else
                                txt_servicio_tv.setVisibility(View.GONE);
                            if (!SaLavadora.equals("")){
                                txt_servicio_lavadora.setText(SaLavadora);
                            }
                            else
                                txt_servicio_lavadora.setVisibility(View.GONE);
                            if (!SaCocina.equals("")){
                                txt_servicio_cocina.setText(SaCocina);
                            }
                            else
                                txt_servicio_cocina.setVisibility(View.GONE);
                            if (!SaAlmuerzo.equals("")){
                                txt_servicio_almuerzo.setText(SaAlmuerzo);
                            }
                            else
                                txt_servicio_almuerzo.setVisibility(View.GONE);
                            if (!SaDesayuno.equals("")){
                                txt_servicio_desayuno.setText(SaDesayuno);
                            }
                            else
                                txt_servicio_desayuno.setVisibility(View.GONE);


                    anuncio_detalle_anfitrion_nombre.setVisibility(View.VISIBLE);
                    Picasso.get().load(AdImagenAnfitrion).into(anuncio_detalle_foto_perfil);

                    if (AdImagenAlojamiento.equals("noImagen")){
                        anuncio_detalle_imagen_alojamiento.setVisibility(View.GONE);
                    }else{
                        anuncio_detalle_imagen_alojamiento.setVisibility(View.VISIBLE);
                        try{
                            Picasso.get().load(AdImagenAlojamiento).resize(700,700).centerInside().into(anuncio_detalle_imagen_alojamiento);
                        }catch (Exception e){
                            Toast.makeText(AnuncioDetalleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
