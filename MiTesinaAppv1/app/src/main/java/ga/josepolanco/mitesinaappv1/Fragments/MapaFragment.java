package ga.josepolanco.mitesinaappv1.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;

import ga.josepolanco.mitesinaappv1.Clases.MapsLanLog;
import ga.josepolanco.mitesinaappv1.PerfilActivity;
import ga.josepolanco.mitesinaappv1.R;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback{
    private FusedLocationProviderClient mFusedLocationClient;
    String anuncio_titulo, anuncio_tipo,detalle_tipo_alojamiento;
    double d_latitud, d_longitud;

    private static final int LOCATION_REQUEST_CODE = 1;
    //
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private ArrayList<Marker> tempMarkerList = new ArrayList<>();
    private ArrayList<Marker> realMarkerList = new ArrayList<>();

    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mDatabase.child("Anuncios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Marker marker : realMarkerList){
                    marker.remove();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MapsLanLog mapsLanLog = snapshot.getValue(MapsLanLog.class);
                    Double latitud = mapsLanLog.getLatitud();
                    Double longitud = mapsLanLog.getLongitud();
                    String anuncio_titulo = snapshot.child("anuncio_titulo").getValue(String.class);
                    String tipo_alojamiento = snapshot.child("tipo_alojamiento").getValue(String.class);
                    String detalle_tipo_alojamiento = snapshot.child("detalle_tipo_alojamiento").getValue(String.class);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitud,longitud));
                    markerOptions.title(anuncio_titulo);
                    markerOptions.snippet(detalle_tipo_alojamiento);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_casa));
                    tempMarkerList.add(mMap.addMarker(markerOptions));
                }
                realMarkerList.clear();
                realMarkerList.addAll(tempMarkerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //mMap.clear(); //clear old markers

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.e(" Latitud: ",+location.getLatitude()+" Longitud: "+location.getLongitude());
                            double latitud = location.getLatitude();
                            double longitud = location.getLongitude();
                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(new LatLng(latitud,longitud))
                                    .zoom(15) //tamaño de la vista
                                    .bearing(0) //direccion de la camara
                                    .tilt(45) //angulo de la camara
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null); //distancia de acercamiento

                        }else{
                            CameraPosition googlePlex = CameraPosition.builder()
                                    .target(new LatLng(-8.1117789,-79.0286686))
                                    .zoom(15) //tamaño de la vista
                                    .bearing(0) //direccion de la camara
                                    .tilt(45) //angulo de la camara
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null); //distancia de acercamiento

                        }
                    }
                });
    }

}
