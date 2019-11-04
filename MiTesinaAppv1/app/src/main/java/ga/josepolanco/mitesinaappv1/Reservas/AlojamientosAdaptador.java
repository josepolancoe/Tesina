package ga.josepolanco.mitesinaappv1.Reservas;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ga.josepolanco.mitesinaappv1.Clases.Alojamiento;
import ga.josepolanco.mitesinaappv1.R;

public class AlojamientosAdaptador extends RecyclerView.Adapter<AlojamientosAdaptador.AlojamientosViewHolder>{
    List<Alojamiento> alojamientoList;

    public AlojamientosAdaptador(List<Alojamiento> alojamientoList) {
        this.alojamientoList = alojamientoList;
    }

    public static class AlojamientosViewHolder extends RecyclerView.ViewHolder{
        TextView alojamiento_tipo, alojamiento_titulo, alojamiento_precio;
        ImageView alojamiento_imagen, alojamiento_foto_perfil;

        public AlojamientosViewHolder(View itemView) {
            super(itemView);
            alojamiento_tipo = itemView.findViewById(R.id.alojamiento_tipo);
            alojamiento_titulo = itemView.findViewById(R.id.alojamiento_titulo);
            alojamiento_precio = itemView.findViewById(R.id.alojamiento_precio);
            alojamiento_imagen = itemView.findViewById(R.id.alojamiento_imagen);
            alojamiento_foto_perfil = itemView.findViewById(R.id.alojamiento_foto_perfil);
        }
    }

    @NonNull
    @Override
    public AlojamientosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alojamientos, viewGroup, false);
        AlojamientosViewHolder holder = new AlojamientosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AlojamientosViewHolder denunciasViewHolder, int i) {
        Alojamiento alojamiento = alojamientoList.get(i);
        denunciasViewHolder.alojamiento_tipo.setText(alojamiento.getTipo());
        denunciasViewHolder.alojamiento_titulo.setText(alojamiento.getTitulo());
        denunciasViewHolder.alojamiento_precio.setText(alojamiento.getPrecio());
        Picasso.get().load(alojamiento.getImagen_alojamiento()).resize(500,700).centerInside().into(denunciasViewHolder.alojamiento_imagen);
        Picasso.get().load(alojamiento.getImagen_perfil()).resize(100,100).into(denunciasViewHolder.alojamiento_foto_perfil);
//        if (Denuncias.getUrlImagen()!=null){
//            Picasso.get().load(Denuncias.getUrlImagen()).into(denunciasViewHolder.noticia_denuncias_imagen);
//        }else{
//            //Mustra imagen por defecto.
//        }
    }

    @Override
    public int getItemCount() {
        return alojamientoList.size();
    }
}
