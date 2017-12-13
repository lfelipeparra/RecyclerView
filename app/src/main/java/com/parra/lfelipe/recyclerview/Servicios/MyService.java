package com.parra.lfelipe.recyclerview.Servicios;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parra.lfelipe.recyclerview.Main2Activity;
import com.parra.lfelipe.recyclerview.MainActivity;
import com.parra.lfelipe.recyclerview.R;
import com.parra.lfelipe.recyclerview.Ubicacion;

import java.util.ArrayList;
import java.util.Calendar;

public class MyService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ArrayList<String> sids;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                //Notificacion
                int hora = Calendar.getInstance().get(Calendar.HOUR);
                int minutos = Calendar.getInstance().get(Calendar.MINUTE);
                Log.e("Hola","Hora "+hora);
                if(hora==5 && (0<=minutos && minutos<59)){
                    sids = new ArrayList<String>();
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference();
                    myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                int cont = 0;
                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    cont++;
                                    sids.add(data.child("sid").getValue(String.class));
                                }

                                if(cont >=1){
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList("data",sids);
                                    Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                                    intent.putExtras(bundle);

                                    PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

                                    Uri notificacion = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    builder.setContentTitle("Nuevas ubicaciones").
                                            setContentText("¿Desea hacer checkin?").
                                            setTicker("Nuevas ubicaciones").
                                            setSmallIcon(R.mipmap.ic_launcher).
                                            setContentIntent(contIntent).
                                            setAutoCancel(true).
                                            setSound(notificacion).
                                            setVibrate(new long[] {1000, 1000}).
                                            setLights(Color.RED,3000,3000);

                                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    nm.notify(1,builder.build());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    //Pedir ubicacion
                }
                myRef.child("lugares").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                double latitud = data.child("Latitud").getValue(double.class);
                                double longitud = data.child("Longitud").getValue(double.class);
                                final String sid = data.child("Id").getValue(String.class);
                                Location loc1 = new Location("");
                                loc1.setLatitude(latitud);
                                loc1.setLongitude(longitud);

                                Location loc2 = new Location("");
                                loc2.setLatitude(location.getLatitude());
                                loc2.setLongitude(location.getLongitude());

                                float distanceInMeters = loc1.distanceTo(loc2);
                                Log.e("Data","Metros: "+distanceInMeters);
                                if(distanceInMeters<=50){
                                    myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String temp = sid;
                                            if(dataSnapshot.exists()){
                                                boolean verificar = false;
                                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                                    if(data.child("sid").getValue(String.class).equals(temp)){
                                                        verificar = true;
                                                        //Toast.makeText(getApplicationContext(),"Ya existe",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                if(!verificar){
                                                    String lid = myRef.child("ubicacion").push().getKey();
                                                    Ubicacion ubicacion = new Ubicacion("Felipe",lid,temp);
                                                    myRef.child("ubicacion").child(lid).setValue(ubicacion);
                                                    //Toast.makeText(getApplicationContext(),"Nueva ubicacion",Toast.LENGTH_SHORT).show();

                                                }
                                            }else {
                                                String lid = myRef.child("ubicacion").push().getKey();
                                                Ubicacion ubicacion = new Ubicacion("Felipe",lid,temp);
                                                myRef.child("ubicacion").child(lid).setValue(ubicacion);
                                                //Toast.makeText(getApplicationContext(),"Nueva ubicacion",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        locationManager.requestLocationUpdates("gps", 10000, 0, locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
