package ga.josepolanco.mitesinaappv1.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ga.josepolanco.mitesinaappv1.Clases.ModeloReserva;
import ga.josepolanco.mitesinaappv1.MainActivity;
import ga.josepolanco.mitesinaappv1.PerfilActivity;
import ga.josepolanco.mitesinaappv1.R;
import ga.josepolanco.mitesinaappv1.Reservas.AdaptadorAnuncios;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservaFragment extends Fragment {
    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<ModeloReserva> modeloReservaList;
    AdaptadorAnuncios adaptadorAnuncios;
    DatabaseReference databaseReference;



    public ReservaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.reserva_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        modeloReservaList = new ArrayList<>();

        cargarAnuncios();
        
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);  //para ver las opciones del menu en el fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_principal, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cerrar_sesion){
            firebaseAuth.signOut();
            validadEstadoUsuario();
        }
        return super.onOptionsItemSelected(item);
    }

    private void validadEstadoUsuario(){
        //obtener usuario actual
        FirebaseUser usuario = firebaseAuth.getCurrentUser();
        if (usuario != null){

        }else{
            //el usuario no ha iniciado sesión, regresa al activity principal
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    private void cargarAnuncios() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Anuncios");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modeloReservaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ModeloReserva modeloReserva = ds.getValue(ModeloReserva.class);

                    modeloReservaList.add(modeloReserva);

                    //adaptador
                    adaptadorAnuncios = new AdaptadorAnuncios(getActivity(),modeloReservaList);
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adaptadorAnuncios);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void buscarAnuncios(String buscar){

    }



}
