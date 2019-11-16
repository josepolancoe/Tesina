package ga.josepolanco.mitesinaappv1.Adaptadores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdaptadorUsuarios {


    class MyHolder extends RecyclerView.ViewHolder{
        ImageView chat_foto_perfil;

        public MyHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
