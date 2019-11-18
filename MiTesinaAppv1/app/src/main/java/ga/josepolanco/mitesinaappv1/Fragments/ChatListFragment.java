package ga.josepolanco.mitesinaappv1.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ga.josepolanco.mitesinaappv1.Adaptadores.AdaptadorChatlist;
import ga.josepolanco.mitesinaappv1.Modelos.ModelChatlist;
import ga.josepolanco.mitesinaappv1.Modelos.ModeloChat;
import ga.josepolanco.mitesinaappv1.Modelos.ModeloUsuario;
import ga.josepolanco.mitesinaappv1.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;

    List<ModeloUsuario> listUsuario;
    List<ModelChatlist> listaChatlist;
    DatabaseReference chatlistReference;
    DatabaseReference userReference;
    FirebaseUser currentUser;
    AdaptadorChatlist adaptadorChatlist;


    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.chatlist_recyclerview);

        listaChatlist = new ArrayList<>();

        chatlistReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        chatlistReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaChatlist.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelChatlist modelChatlist = snapshot.getValue(ModelChatlist.class);
                    listaChatlist.add(modelChatlist);
                }
                cargarChatlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void cargarChatlist() {
        listUsuario = new ArrayList<>();
        userReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUsuario.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModeloUsuario user = snapshot.getValue(ModeloUsuario.class);
                    for (ModelChatlist chatlist : listaChatlist){
                        if (user.getUid() != null && user.getUid().equals(chatlist.getId())){
                            listUsuario.add(user);
                            break;
                        }
                    }
                }
                //adaptador
                adaptadorChatlist = new AdaptadorChatlist(getContext(),listUsuario);
                adaptadorChatlist.notifyDataSetChanged();
                //setAdaptador
                recyclerView.setAdapter(adaptadorChatlist);
                //set ultimo mensaje
                for (int i=0; i<listUsuario.size();i++){
                    ultimoMensaje(listUsuario.get(i).getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ultimoMensaje(final String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String elUltimoMensaje = "default";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModeloChat chat = snapshot.getValue(ModeloChat.class);
                    if (chat==null){
                        continue;
                    }
                    String emisor = chat.getEmisor();
                    String receptor = chat.getReceptor();
                    if (emisor==null || receptor==null){
                        continue;
                    }
                    if (chat.getReceptor().equals(currentUser.getUid()) &&
                            chat.getEmisor().equals(userId) ||
                                    chat.getReceptor().equals(userId) &&
                                    chat.getEmisor().equals(currentUser.getUid())){
                        elUltimoMensaje = chat.getMensaje();

                    }
                }
                adaptadorChatlist.modificarUltimoMensaje(userId, elUltimoMensaje);
                adaptadorChatlist.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
