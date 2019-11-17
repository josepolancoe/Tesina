package ga.josepolanco.mitesinaappv1.Adaptadores;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import java.util.List;
import java.util.Locale;

import ga.josepolanco.mitesinaappv1.Modelos.ModeloChat;
import ga.josepolanco.mitesinaappv1.R;

public class AdaptadorChat extends RecyclerView.Adapter<AdaptadorChat.MyHolder> {
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModeloChat> listaChat;
    String imageUrl;

    FirebaseUser firebaseUser;

    public AdaptadorChat(Context context, List<ModeloChat> listaChat, String imageUrl) {
        this.context = context;
        this.listaChat = listaChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
            return new MyHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String mensaje = listaChat.get(position).getMensaje();
        String timeStamp = listaChat.get(position).getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dataTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

        holder.chat_mensaje_contenido.setText(mensaje);
        holder.chat_mensaje_tiempo.setText(dataTime);
        try {
            Picasso.get().load(imageUrl).into(holder.chat_foto_perfil);
        }catch (Exception e){
            Picasso.get().load(R.drawable.img_user).into(holder.chat_foto_perfil);
        }

        if (position == listaChat.size()-1){
            if (listaChat.get(position).isFueVisto()){
                holder.chat_mensaje_estado.setText("Visto");
            }else{
                holder.chat_mensaje_estado.setText("Entregado");
            }
        }else{
            holder.chat_mensaje_estado.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        //obtenemos la sesion actual
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (listaChat.get(position).getEmisor().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }


    class MyHolder extends RecyclerView.ViewHolder{

        ImageView chat_foto_perfil;
        TextView chat_mensaje_contenido,chat_mensaje_tiempo,chat_mensaje_estado;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            chat_foto_perfil = itemView.findViewById(R.id.chat_foto_perfil);
            chat_mensaje_contenido = itemView.findViewById(R.id.chat_mensaje_contenido);
            chat_mensaje_tiempo = itemView.findViewById(R.id.chat_mensaje_tiempo);
            chat_mensaje_estado = itemView.findViewById(R.id.chat_mensaje_estado);

        }
    }
}
