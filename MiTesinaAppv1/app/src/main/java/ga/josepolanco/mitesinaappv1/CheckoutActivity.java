package ga.josepolanco.mitesinaappv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    EditText txt_checkoutPrecioSoles, txt_checkoutPrecioDolares;
    Button btn_realizarPago;
    LinearLayout group_payment;

    private static final int REQUEST_CODE = 1234;

    String API_GET_TOKEN = "https://mitesinareservaalojamiento.000webhostapp.com/main.php";
    String API_CHECKOUT = "https://mitesinareservaalojamiento.000webhostapp.com/checkout.php";

    String token, amount;
    String anfitrion_uid, anfitrion_nombre, anuncio_id, anuncio_precio_formato, anuncio_estado ="Reservado";
    double precio_pagar;
    HashMap<String, String> paramsHash;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        txt_checkoutPrecioSoles = findViewById(R.id.txt_checkoutPrecioSoles);
        txt_checkoutPrecioDolares = findViewById(R.id.txt_checkoutPrecioDolares);
        btn_realizarPago = findViewById(R.id.btn_realizarPago);

        group_payment = findViewById(R.id.group_payment);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        Intent intent = getIntent();
        anuncio_id = intent.getStringExtra("anuncio_id");
        anuncio_precio_formato = intent.getStringExtra("anuncio_precio_formato");
        txt_checkoutPrecioSoles.setText(anuncio_precio_formato);
        double convertidorUSD = Double.parseDouble(anuncio_precio_formato) * 0.30;
        anuncio_precio_formato = String.valueOf(convertidorUSD);
        txt_checkoutPrecioDolares.setText(anuncio_precio_formato);
        //Toast.makeText(this, anuncio_id, Toast.LENGTH_SHORT).show();

        new CheckoutActivity.getToken().execute();

        btn_realizarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });

    }

    private void actualizarEstadoAnuncio() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Anuncios").child(anuncio_id);
        databaseReference.child("anuncio_estado").setValue(anuncio_estado);
    }

    private void submitPayment(){
        String payValue = txt_checkoutPrecioDolares.getText().toString();
        if(!payValue.isEmpty())
        {
            DropInRequest dropInRequest=new DropInRequest().clientToken(token);
            startActivityForResult(dropInRequest.getIntent(this),REQUEST_CODE);
        }
        else
            Toast.makeText(this, "Enter a valid amount for payment", Toast.LENGTH_SHORT).show();
    }

    private void sendPayments(){
        RequestQueue queue= Volley.newRequestQueue(CheckoutActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, API_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().contains("Successful")){
                            Toast.makeText(CheckoutActivity.this, "Pago realizado con exito!", Toast.LENGTH_SHORT).show();
                            actualizarEstadoAnuncio();

                        }
                        else {
                            Toast.makeText(CheckoutActivity.this, "Algo Falló, intente nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("Response",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Err",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(paramsHash==null)
                    return null;
                Map<String,String> params=new HashMap<>();
                for(String key:paramsHash.keySet())
                {
                    params.put(key,paramsHash.get(key));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("Content-type","application/x-www-form-urlencoded");
                return params;
            }
        };
        RetryPolicy mRetryPolicy=new DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(mRetryPolicy);
        queue.add(stringRequest);
    }






    private class getToken extends AsyncTask {
        ProgressDialog mDailog;

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client=new HttpClient();
            client.get(API_GET_TOKEN, new HttpResponseCallback() {
                @Override
                public void success(final String responseBody) {
                    mDailog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            group_payment.setVisibility(View.VISIBLE);
                            token=responseBody;
                        }
                    });
                }

                @Override
                public void failure(Exception exception) {
                    mDailog.dismiss();
                    Log.d("Err",exception.toString());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDailog=new ProgressDialog(CheckoutActivity.this,android.R.style.Theme_DeviceDefault_Light_Dialog);
            mDailog.setCancelable(false);
            mDailog.setMessage("Cargando cartera, Espere por favor");
            mDailog.show();
        }

        @Override
        protected void onPostExecute(Object o){
            super.onPostExecute(o);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== REQUEST_CODE){
            if(resultCode==RESULT_OK)
            {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce= result.getPaymentMethodNonce();
                String strNounce=nonce.getNonce();
                if(!txt_checkoutPrecioDolares.getText().toString().isEmpty())
                {
                    amount=txt_checkoutPrecioDolares.getText().toString();
                    paramsHash=new HashMap<>();
                    paramsHash.put("amount",amount);
                    paramsHash.put("nonce",strNounce);

                    sendPayments();
                }
                else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Exception error=(Exception)data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("Err",error.toString());
            }
        }
    }

}
