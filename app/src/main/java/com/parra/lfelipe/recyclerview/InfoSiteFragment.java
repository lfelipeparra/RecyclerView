package com.parra.lfelipe.recyclerview;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoSiteFragment extends Fragment implements View.OnClickListener{

    View view;
    InterfaceSite agregarReseña;
    String id;
    private LocationManager locationManager;
    private Location location;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            agregarReseña = (InterfaceSite) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement communicator");
        }
    }

    public InfoSiteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        id = getArguments().getString("id");
        view = inflater.inflate(R.layout.fragment_info_site, container, false);
        Button bAgregarReseña,bAgregarCheckin;
        bAgregarReseña = (Button)view.findViewById(R.id.bAgregarReseña);
        bAgregarCheckin = (Button)view.findViewById(R.id.bAgregarCheckin);
        bAgregarCheckin.setOnClickListener(this);
        bAgregarReseña.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.bAgregarCheckin:
                final String sid;
                sid = this.id; //cuidado dentro de if(!verificar)
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                Query query = myRef.child("checkin");
                query.addListenerForSingleValueEvent(new ValueEventListener() { //Arreglar
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Checkin ch;
                            boolean verificar=false;
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                ch = data.getValue(Checkin.class);
                                if(ch.getSid().equals(sid)&&ch.getUid().equals("Felipe")){
                                    Toast.makeText(getContext(),"Ya realizó checkin",Toast.LENGTH_SHORT).show();
                                    verificar=true;
                                    break;
                                }
                            }
                            if(!verificar){//igual al no existe. Posible función
                                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                                location = locationManager.getLastKnownLocation("gps");
                                Log.e("prueba",location.getLatitude()+","+location.getLongitude());
                                myRef.child("lugares").orderByChild("Id").equalTo(sid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot data: dataSnapshot.getChildren()){
                                            double latitud = data.child("Latitud").getValue(double.class);
                                            double longitud = data.child("Longitud").getValue(double.class);


                                            Location loc1 = new Location("");
                                            loc1.setLatitude(latitud);
                                            loc1.setLongitude(longitud);

                                            Location loc2 = new Location("");
                                            loc2.setLatitude(location.getLatitude());
                                            loc2.setLongitude(location.getLongitude());

                                            float distanceInMeters = loc1.distanceTo(loc2);
                                            Log.e("Prueba","Metros: "+distanceInMeters);

                                            if(distanceInMeters <= 50){
                                                Checkin checkin = new Checkin("Felipe",sid);
                                                myRef.child("checkin").push().setValue(checkin);
                                                Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();
                                                myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){
                                                            Ubicacion ubicacion;
                                                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                                                ubicacion = data.getValue(Ubicacion.class);
                                                                if(ubicacion.getSid().equals(sid)){
                                                                    myRef.child("ubicacion").child(data.getKey()).removeValue();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }else{
                                                myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){
                                                            Ubicacion ubicacion;
                                                            boolean ver=false;
                                                            for(DataSnapshot data : dataSnapshot.getChildren()){
                                                                ubicacion = data.getValue(Ubicacion.class);
                                                                if(ubicacion.getSid().equals(sid)){
                                                                    ver = true;
                                                                    Checkin checkin = new Checkin("Felipe",sid);
                                                                    myRef.child("checkin").push().setValue(checkin);
                                                                    Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();
                                                                    myRef.child("ubicacion").child(data.getKey()).removeValue();
                                                                }
                                                            }
                                                            if(!ver){
                                                                Toast.makeText(getContext(),"No has estado cerca del sitio",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else {
                                                            Toast.makeText(getContext(),"No has estado cerca del sitio",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                /*Checkin checkin = new Checkin("Felipe",sid);
                                myRef.child("checkin").push().setValue(checkin);
                                Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();*/

                            }

                        }else{
                            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                            location = locationManager.getLastKnownLocation("gps");
                            myRef.child("lugares").orderByChild("Id").equalTo(sid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        double latitud = data.child("Latitud").getValue(double.class);
                                        double longitud = data.child("Longitud").getValue(double.class);


                                        Location loc1 = new Location("");
                                        loc1.setLatitude(latitud);
                                        loc1.setLongitude(longitud);

                                        Location loc2 = new Location("");
                                        loc2.setLatitude(location.getLatitude());
                                        loc2.setLongitude(location.getLongitude());

                                        float distanceInMeters = loc1.distanceTo(loc2);
                                        Log.e("Prueba","Metros: "+distanceInMeters);

                                        if(distanceInMeters <= 50){
                                            Checkin checkin = new Checkin("Felipe",sid);
                                            myRef.child("checkin").push().setValue(checkin);
                                            Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();
                                            myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        Ubicacion ubicacion;
                                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                                            ubicacion = data.getValue(Ubicacion.class);
                                                            if(ubicacion.getSid().equals(sid)){
                                                                myRef.child("ubicacion").child(data.getKey()).removeValue();
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }else{
                                            myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        Ubicacion ubicacion;
                                                        boolean ver=false;
                                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                                            ubicacion = data.getValue(Ubicacion.class);
                                                            if(ubicacion.getSid().equals(sid)){
                                                                ver = true;
                                                                Log.e("prueba","HOla");
                                                                Checkin checkin = new Checkin("Felipe",sid);
                                                                myRef.child("checkin").push().setValue(checkin);
                                                                Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();
                                                                myRef.child("ubicacion").child(data.getKey()).removeValue();
                                                            }
                                                        }
                                                        if(!ver){
                                                            Toast.makeText(getContext(),"No has estado cerca del sitio",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        Toast.makeText(getContext(),"No has estado cerca del sitio",Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                                /*Checkin checkin = new Checkin("Felipe",sid);
                                myRef.child("checkin").push().setValue(checkin);
                                Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();*/
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;
            case R.id.bAgregarReseña:
                agregarReseña.aregarReseña(this.id);
                break;
        }
    }
}
