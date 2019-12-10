package ga.josepolanco.mitesinaappv1.Adaptadores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ga.josepolanco.mitesinaappv1.AnuncioDetalleActivity;
import ga.josepolanco.mitesinaappv1.ChatActivity;
import ga.josepolanco.mitesinaappv1.Modelos.ModeloReserva;
import ga.josepolanco.mitesinaappv1.R;

public class AdaptadorAnuncios extends RecyclerView.Adapter<AdaptadorAnuncios.MyHolder>{

    Context context;
    List<ModeloReserva> modeloReservaList;

    String  myUid;

    public AdaptadorAnuncios(Context context, List<ModeloReserva> modeloReservaList) {
        this.context = context;
        this.modeloReservaList = modeloReservaList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alojamientos,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        final String anfitrion_uid = modeloReservaList.get(position).getAnfitrion_uid();
        String anfitrion_nombre = modeloReservaList.get(position).getAnfitrion_nombre();
        String anfitrion_imagen = modeloReservaList.get(position).getAnfitrion_imagen();
        final String anuncio_id = modeloReservaList.get(position).getAnuncio_id();
        String anuncio_titulo = modeloReservaList.get(position).getAnuncio_titulo();
        final String anuncio_imagen_alojamiento =  modeloReservaList.get(position).getAnuncio_imagen_alojamiento();
        String anuncio_fecha = modeloReservaList.get(position).getAnuncio_fecha();
        //Double anuncio_precio = modeloReservaList.get(position).getAnuncio_precio();
        String anuncio_precio = modeloReservaList.get(position).getAnuncio_precio();
        String tipo_alojamiento = modeloReservaList.get(position).getTipo_alojamiento();
        String anuncio_estado = modeloReservaList.get(position).getAnuncio_estado();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(anuncio_fecha));
        String fecha_anuncio = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.alojamiento_titulo.setText(anuncio_titulo);
        holder.alojamiento_tipo.setText(tipo_alojamiento);
        holder.alojamiento_precio.setText(anuncio_precio);
        holder.alojamiento_estado.setText(anuncio_estado);


        try{
            Picasso.get().load(anfitrion_imagen).placeholder(R.drawable.img_user).into(holder.alojamiento_foto_perfil);
        }catch(Exception e){

        }

        if (anuncio_estado.equalsIgnoreCase("Reservado")){
            holder.reserva_alojamiento_card_view.setVisibility(View.GONE);
        }else{
            holder.reserva_alojamiento_card_view.setVisibility(View.VISIBLE );
        }

        if (anuncio_imagen_alojamiento.equals("noImagen")){
            //ocultamos imagevview
            holder.alojamiento_imagen.setVisibility(View.GONE);
        }else{
            holder.alojamiento_imagen.setVisibility(View.VISIBLE);
            try{
                //Muestra tamaño reducido
                Picasso.get().load(anuncio_imagen_alojamiento).resize(700,700).centerCrop().into(holder.alojamiento_imagen);

                //Original
                //Picasso.get().load(anuncio_imagen_alojamiento).into(holder.alojamiento_imagen);
            }catch(Exception e){

            }
        }

        holder.alojamiento_opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpciones(holder.alojamiento_opciones, anfitrion_uid, myUid, anuncio_id, anuncio_imagen_alojamiento);

            }
        });

        holder.reserva_alojamiento_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciamos activity detalle anuncio
                Intent intent = new Intent(context, AnuncioDetalleActivity.class);
                intent.putExtra("anuncio_id",anuncio_id);
                context.startActivity(intent);
            }
        });

        holder.anuncio_acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciamos activity detalle anuncio
                Intent intent = new Intent(context, AnuncioDetalleActivity.class);
                intent.putExtra("anuncio_id",anuncio_id);
                context.startActivity(intent);
            }
        });

        holder.anuncio_enviar_mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciamos activity detalle anuncio
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("anfitrion_uid",anfitrion_uid);
                context.startActivity(intent);
            }
        });
    }

    private void mostrarOpciones(ImageButton alojamiento_opciones, final String anfitrion_uid, String myUid, final String anuncio_id, final String anuncio_imagen_alojamiento) {
        PopupMenu popupMenu = new PopupMenu(context, alojamiento_opciones, Gravity.END);

        if (anfitrion_uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Eliminar");

        }
            popupMenu.getMenu().add(Menu.NONE,1,0,"Ver más");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id==0){
                    //click eliminar
                    eliminarAnuncio(anuncio_id,anuncio_imagen_alojamiento);
                }else if(id==1){
                    //iniciamos activity detalle anuncio
                    Intent intent = new Intent(context, AnuncioDetalleActivity.class);
                    intent.putExtra("anuncio_id",anuncio_id);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void eliminarAnuncio(String anuncio_id, String anuncio_imagen_alojamiento) {
        //eliminar cuando haya imagen o no
        if (anuncio_imagen_alojamiento.equals("noImagen")){
            eliminarAnuncioSinImagen(anuncio_id);
        }else{
            eliminarAnuncioConImagen(anuncio_id,anuncio_imagen_alojamiento);
        }
    }

    private void eliminarAnuncioConImagen(final String anuncio_id, String anuncio_imagen_alojamiento) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Eliminando Anuncio...");

        //eliminar imagen usando url
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(anuncio_imagen_alojamiento);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //imagen eliminada, ahora elimina de la bd
                Query query = FirebaseDatabase.getInstance().getReference("Anuncios").orderByChild("anuncio_id").equalTo(anuncio_id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            snapshot.getRef().removeValue(); //remueve los valores de la bd
                        }
                        //eliminado
                        Toast.makeText(context, "Anuncio eliminado", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void eliminarAnuncioSinImagen(String anuncio_id) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Eliminando Anuncio...");

        //imagen eliminada, ahora elimina de la bd
        Query query = FirebaseDatabase.getInstance().getReference("Anuncios").orderByChild("anuncio_id").equalTo(anuncio_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.getRef().removeValue(); //remueve los valores de la bd
                }
                //eliminado
                Toast.makeText(context, "Anuncio eliminado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modeloReservaList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        ImageView alojamiento_foto_perfil,alojamiento_imagen;
        TextView alojamiento_tipo,alojamiento_titulo,alojamiento_precio, alojamiento_estado;
        ImageButton alojamiento_opciones;

        CardView reserva_alojamiento_card_view;
        Button anuncio_acceder, anuncio_enviar_mensaje;

        public MyHolder(@NonNull View itemView){
            super(itemView);

            alojamiento_foto_perfil = itemView.findViewById(R.id.alojamiento_foto_perfil);
            alojamiento_imagen = itemView.findViewById(R.id.alojamiento_imagen);
            alojamiento_tipo = itemView.findViewById(R.id.alojamiento_tipo);
            alojamiento_titulo = itemView.findViewById(R.id.alojamiento_titulo);
            alojamiento_precio = itemView.findViewById(R.id.alojamiento_precio);

            alojamiento_opciones = itemView.findViewById(R.id.alojamiento_opciones);
            reserva_alojamiento_card_view = itemView.findViewById(R.id.reserva_alojamiento_card_view);

            anuncio_acceder = itemView.findViewById(R.id.anuncio_acceder);
            anuncio_enviar_mensaje = itemView.findViewById(R.id.anuncio_enviar_mensaje);

            alojamiento_estado = itemView.findViewById(R.id.alojamiento_estado);





        }
    }
}
