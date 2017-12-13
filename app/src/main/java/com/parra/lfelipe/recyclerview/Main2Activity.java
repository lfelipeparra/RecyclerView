package com.parra.lfelipe.recyclerview;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    ArrayList<Lugar> lugares;
    ArrayList<String> ids;
    CheckinsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    InterfaceSite searchSite;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle extras = getIntent().getExtras();
        ids = extras.getStringArrayList("data");
        //conseguirLugares("f");
        for(String id: ids){
            conseguirLugares(id);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvCheckins);
        recyclerView.setHasFixedSize(true);
        adapter = new CheckinsAdapter(Main2Activity.this,lugares);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    void conseguirLugares(String id){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("lugares");
        lugares = new ArrayList<Lugar>();
        myRef.orderByChild("Id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        Lugar l = data.getValue(Lugar.class);
                        float puntaje = l.getPuntaje();
                        int reseñas = l.getReseñas();
                        l.setPuntaje(puntaje/(float)reseñas);
                        lugares.add(l);

                    }
                    Log.e("Tamaño"," "+lugares.size());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
