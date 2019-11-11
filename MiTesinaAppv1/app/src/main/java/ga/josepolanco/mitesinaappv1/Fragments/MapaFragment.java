package ga.josepolanco.mitesinaappv1.Fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;

import ga.josepolanco.mitesinaappv1.PerfilActivity;
import ga.josepolanco.mitesinaappv1.R;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment{
    private FusedLocationProviderClient mFusedLocationClient;
    String anuncio_titulo, anuncio_tipo,detalle_tipo_alojamiento;

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;

    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }

                mMap.setMyLocationEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                mMap.clear(); //clear old markers

                database.getReference().child("Anuncios").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            //d_latitud = snapshot.child("latitud").getValue(Double.class);
                            //d_longitud = snapshot.child("longitud").getValue(Double.class);
                            anuncio_titulo = snapshot.child("anuncio_titulo").getValue(String.class);
                            anuncio_tipo = snapshot.child("tipo_alojamiento").getValue(String.class);
                            detalle_tipo_alojamiento = snapshot.child("detalle_tipo_alojamiento").getValue(String.class);
                            LatLng latLng = new LatLng(-8.0994363,-79.0232988);
                            String info_marker= anuncio_titulo;
                            if (anuncio_tipo.equalsIgnoreCase("Departamento")){
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(info_marker)
                                        .snippet(detalle_tipo_alojamiento)
                                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_departamento",100,100))));

                            }if (anuncio_tipo.equalsIgnoreCase("Casa")){
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(info_marker)
                                        .snippet(detalle_tipo_alojamiento)
                                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("marker_casa",100,100))));

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

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
                                            //plaza armas trujillo
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
        });

        return view;
    }


    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}
