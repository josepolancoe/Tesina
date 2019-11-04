package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    EditText txt_correo, txt_clave;
    Button btn_login;
    TextView tv_olvide_clave;
    SignInButton mGoogleLoginBtn;

    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    private static final String TAG = "FacebookLogin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //barra de accion y titulo
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Login");


        //Activar boton de retroceso
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        txt_correo = findViewById(R.id.l_correo);
        txt_clave = findViewById(R.id.l_clave);
        btn_login = findViewById(R.id.l_btn_iniciar_sesion);
        tv_olvide_clave = findViewById(R.id.l_clave_olvidada);
        mGoogleLoginBtn = findViewById(R.id.googleLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando Sesión");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = txt_correo.getText().toString();
                String clave = txt_clave.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                     //Correo invalido
                    txt_correo.setError("Correo Invalido");
                    txt_correo.setFocusable(true);
                }else{
                    iniciarSesion(correo,clave);
                }
            }
        });

        tv_olvide_clave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperarClave();
            }
        });

        //Boton Google Iniciar Sesion
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_facebook);
        loginButton.setReadPermissions("email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                // ...
            }
        });

    }

    private void recuperarClave() {
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Clave");

        //Modificar LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);

        //view to set in dialog
        final EditText correo_recuperacion = new EditText(this);
        correo_recuperacion.setHint("Correo");
        correo_recuperacion.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        correo_recuperacion.setMinEms(16);

        linearLayout.addView(correo_recuperacion);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        //Botones recover
        builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String correo = correo_recuperacion.getText().toString().trim();
                realizarRecuperacion(correo);
            }
        });

        //Boton cancelar
        builder.setNegativeButton("Cancear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cerrar dialogo
                dialogInterface.dismiss();
            }
        });

        //ver dialogo
        builder.create().show();
    }

    private void realizarRecuperacion(String correo) {
        progressDialog.setMessage("Enviando Correo...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Correo Enviado.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Falló al Enviar.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                //obtiene y muestra un mensaje de error
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void iniciarSesion(String correo, String clave){
        progressDialog.setMessage("Iniciando Sesión...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(correo, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, PerfilActivity.class));
                            Toast.makeText(LoginActivity.this, "Sesión Exitosa.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Falló Autenticación.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Algo salió mal.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //ir a la actividad anterior
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in filed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                //obtener correo de usuario y llave
                                String email = user.getEmail();
                                String uid = user.getUid();

                                //cuando el usuario está registrado, almacena la información del usuario en la base de datos en tiempo real de Firebase también usando HashMap
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //subimos informacion en hasmap
                                hashMap.put("correo",email);
                                hashMap.put("uid",uid);
                                hashMap.put("nombres","");
                                hashMap.put("apellidos","");
                                hashMap.put("sexo","");
                                hashMap.put("fecha-de-nacimiento","");
                                hashMap.put("telefono","");
                                hashMap.put("contacto-de-emergencia","");
                                hashMap.put("imagen","");

                                //intanciamos la base de dato
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //ruta para almacenar datos de usuario denominados usuarios
                                DatabaseReference reference = database.getReference("Usuarios");
                                //poner datos dentro de hashmap en la base de datos
                                reference.child(uid).setValue(hashMap);
                            }

                            Toast.makeText(LoginActivity.this, "Correo "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, PerfilActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Inicio Fallido...", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "ERROR DESCONOCIDO "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        Toast.makeText(LoginActivity.this, ""+token, Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //obtener correo de usuario y llave
                            String email = user.getEmail();
                            Log.d(TAG,  email);
                            Toast.makeText(LoginActivity.this, email, Toast.LENGTH_SHORT).show();
                            String uid = user.getUid();

                            //cuando el usuario está registrado, almacena la información del usuario en la base de datos en tiempo real de Firebase también usando HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //subimos informacion en hasmap
                            hashMap.put("correo",email);
                            hashMap.put("uid",uid);
                            hashMap.put("nombres","");
                            hashMap.put("apellidos","");
                            hashMap.put("sexo","");
                            hashMap.put("fecha-de-nacimiento","");
                            hashMap.put("telefono","");
                            hashMap.put("contacto-de-emergencia","");
                            hashMap.put("imagen","");

                            //intanciamos la base de dato
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //ruta para almacenar datos de usuario denominados usuarios
                            DatabaseReference reference = database.getReference("Usuarios");
                            //poner datos dentro de hashmap en la base de datos
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(LoginActivity.this, "Correo "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, PerfilActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
