package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ga.josepolanco.mitesinaappv1.Fragments.ChatListFragment;
import ga.josepolanco.mitesinaappv1.Fragments.MapaFragment;
import ga.josepolanco.mitesinaappv1.Fragments.MensajesFragment;
import ga.josepolanco.mitesinaappv1.Fragments.PerfilFragment;
import ga.josepolanco.mitesinaappv1.Fragments.ReservaFragment;

public class PerfilActivity extends AppCompatActivity{

    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTheme(R.style.AppTheme2);
        setContentView(R.layout.activity_perfil);

        //barra de acción y titulo
        //actionBar = getSupportActionBar();
        //actionBar.setTitle("Perfil");

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.menu_navegacion);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //Fragment Inicio Por defecto al iniciar
        //actionBar.setTitle("Reserva");
        ReservaFragment reservaFragment = new ReservaFragment();
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.contenido_perfil, reservaFragment,"");
        fragmentTransaction1.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            //accion al clicar
            switch (menuItem.getItemId()){
                case R.id.nav_reserva:
                    //Fragment Inicio transaccion
                    //actionBar.setTitle("Reserva");
                    ReservaFragment reservaFragment = new ReservaFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.contenido_perfil, reservaFragment,"");
                    fragmentTransaction1.commit();
                    return true;

                case R.id.nav_mapa:
                    //Fragment Perfil transaccion
                    //actionBar.setTitle("Mapa");
                    MapaFragment mapaFragment = new MapaFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.contenido_perfil, mapaFragment,"");
                    fragmentTransaction2.commit();
                    return true;

                /*case R.id.nav_mensajes:
                    //Fragment Mis Amigos transaccion
                    //actionBar.setTitle("Mensajes");
                    MensajesFragment mensajesFragment = new MensajesFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.contenido_perfil, mensajesFragment,"");
                    fragmentTransaction3.commit();
                    return true;*/

                case R.id.nav_mensajes:
                    //Fragment Mis Amigos transaccion
                    //actionBar.setTitle("Mensajes");
                    ChatListFragment chatListFragment = new ChatListFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.contenido_perfil, chatListFragment,"");
                    fragmentTransaction3.commit();
                    return true;

                case R.id.nav_perfil:
                    //Fragment Mis Amigos transaccion
                    //actionBar.setTitle("Perfil");
                    PerfilFragment perfilFragment = new PerfilFragment();
                    FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.contenido_perfil, perfilFragment,"");
                    fragmentTransaction4.commit();
                    return true;

            }
            return false;
        }
    };

    private void validadEstadoUsuario(){
        //obtener usuario actual
        FirebaseUser usuario = firebaseAuth.getCurrentUser();
        if (usuario != null){

        }else{
            //el usuario no ha iniciado sesión, regresa al activity principal
            startActivity(new Intent(PerfilActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        validadEstadoUsuario();
        super.onStart();
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.cerrar_sesion){
            firebaseAuth.signOut();
            validadEstadoUsuario();
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


}
