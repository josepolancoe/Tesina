package ga.josepolanco.mitesinaappv1.Reservas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ga.josepolanco.mitesinaappv1.Clases.ModeloReserva;
import ga.josepolanco.mitesinaappv1.R;

public class AdaptadorAnuncios extends RecyclerView.Adapter<AdaptadorAnuncios.MyHolder>{

    Context context;
    List<ModeloReserva> modeloReservaList;

    public AdaptadorAnuncios(Context context, List<ModeloReserva> modeloReservaList) {
        this.context = context;
        this.modeloReservaList = modeloReservaList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alojamientos,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String anfitrion_nombre = modeloReservaList.get(position).getAnfitrion_nombre();
        String anfitrion_imagen = modeloReservaList.get(position).getAnfitrion_imagen();
        String anuncio_titulo = modeloReservaList.get(position).getAnuncio_titulo();
        String anuncio_imagen_alojamiento =  modeloReservaList.get(position).getAnuncio_imagen_alojamiento();
        String tipo_alojamiento = modeloReservaList.get(position).getTipo_alojamiento();

        holder.alojamiento_titulo.setText(anuncio_titulo);
        holder.alojamiento_tipo.setText(tipo_alojamiento);

        try{
            Picasso.get().load(anfitrion_imagen).placeholder(R.drawable.img_user).into(holder.alojamiento_foto_perfil);
        }catch(Exception e){

        }

        if (anuncio_imagen_alojamiento.equals("noImagen")){
            //ocultamos imagevview
            holder.alojamiento_imagen.setVisibility(View.GONE);
        }else{
            try{
                Picasso.get().load(anuncio_imagen_alojamiento).into(holder.alojamiento_imagen);
            }catch(Exception e){

            }
        }
    }

    @Override
    public int getItemCount() {
        return modeloReservaList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        ImageView alojamiento_foto_perfil,alojamiento_imagen;
        TextView alojamiento_tipo,alojamiento_titulo,alojamiento_precio;

        public MyHolder(@NonNull View itemView){
            super(itemView);

            alojamiento_foto_perfil = itemView.findViewById(R.id.alojamiento_foto_perfil);
            alojamiento_imagen = itemView.findViewById(R.id.alojamiento_imagen);
            alojamiento_tipo = itemView.findViewById(R.id.alojamiento_tipo);
            alojamiento_titulo = itemView.findViewById(R.id.alojamiento_titulo);
            alojamiento_precio = itemView.findViewById(R.id.alojamiento_precio);


        }
    }
}
