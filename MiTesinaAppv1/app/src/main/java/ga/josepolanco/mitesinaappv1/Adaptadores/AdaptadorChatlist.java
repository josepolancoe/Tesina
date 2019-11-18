package ga.josepolanco.mitesinaappv1.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import ga.josepolanco.mitesinaappv1.ChatActivity;
import ga.josepolanco.mitesinaappv1.Modelos.ModelChatlist;
import ga.josepolanco.mitesinaappv1.Modelos.ModeloUsuario;
import ga.josepolanco.mitesinaappv1.R;

public class AdaptadorChatlist extends RecyclerView.Adapter<AdaptadorChatlist.MyHolder>{

    Context context;
    List<ModeloUsuario> listaUsuario;

    private HashMap<String, String> mapUltimoMensaje;

    public AdaptadorChatlist(Context context, List<ModeloUsuario> listaUsuario) {
        this.context = context;
        this.listaUsuario = listaUsuario;
        mapUltimoMensaje = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String id = listaUsuario.get(position).getUid();
        String imagen = listaUsuario.get(position).getImagen();
        String nombre = listaUsuario.get(position).getNombres();
        String ultimoMensaje = mapUltimoMensaje.get(id);

        //seteamos
        holder.chatlist_nombre.setText(nombre);
        if (ultimoMensaje==null || ultimoMensaje.equals("default")){
            holder.chatlist_ultimo_mensaje.setVisibility(View.GONE);
        }else{
            holder.chatlist_ultimo_mensaje.setVisibility(View.VISIBLE);
            holder.chatlist_ultimo_mensaje.setText(ultimoMensaje);
        }

        try{
            Picasso.get().load(imagen).into(holder.chatlist_foto);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_user).into(holder.chatlist_foto);
        }

        if (listaUsuario.get(position).getEstado_en_linea().equalsIgnoreCase("En Linea")){
            //Picasso.get().load(R.drawable.circle_online).into(holder.chatlist_foto);
            holder.chatlist_estado.setImageResource(R.drawable.circle_online);
        }else{
            //Picasso.get().load(R.drawable.circle_offline).into(holder.chatlist_foto);
            holder.chatlist_estado.setImageResource(R.drawable.circle_offline);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("anfitrion_uid",id);
                context.startActivity(intent);
            }
        });
    }

    public void modificarUltimoMensaje(String userId, String ultimoMensaje){
        mapUltimoMensaje.put(userId, ultimoMensaje);
    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView chatlist_foto, chatlist_estado;
        TextView chatlist_nombre, chatlist_ultimo_mensaje;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            chatlist_foto = itemView.findViewById(R.id.chatlist_foto);
            chatlist_estado = itemView.findViewById(R.id.chatlist_estado);
            chatlist_nombre = itemView.findViewById(R.id.chatlist_nombre);
            chatlist_ultimo_mensaje = itemView.findViewById(R.id.chatlist_ultimo_mensaje);
        }
    }
}
