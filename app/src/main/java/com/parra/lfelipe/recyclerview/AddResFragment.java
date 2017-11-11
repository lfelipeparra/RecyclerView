package com.parra.lfelipe.recyclerview;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddResFragment extends Fragment implements View.OnClickListener, RatingBar.OnRatingBarChangeListener{

    RatingBar bar;
    TextView tvReseña;
    Button bAgregar,bCancelar;
    float puntaje=5.0f;
    String idSite;

    public AddResFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        idSite = getArguments().getString("idSite");
        View view = inflater.inflate(R.layout.fragment_add_res, container, false);
        bar = (RatingBar)view.findViewById(R.id.rating);
        tvReseña = (TextView)view.findViewById(R.id.tvReseña);
        bAgregar = (Button)view.findViewById(R.id.bAgregar);
        bCancelar = (Button)view.findViewById(R.id.bCancelar);
        bAgregar.setOnClickListener(this);
        bCancelar.setOnClickListener(this);
        bar.setOnRatingBarChangeListener(this);
        bar.setRating(5.0f);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.bAgregar:
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                Query query = myRef.child("checkin");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("prueba",dataSnapshot.toString());
                        if(dataSnapshot.exists()){
                            Checkin ch;
                            boolean verificar=false;
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                ch = data.getValue(Checkin.class);
                                if(ch.getSid().equals(idSite) && ch.getUid().equals("Felipe")){
                                    final String key = data.getKey();
                                    String text = tvReseña.getText().toString();
                                    verificar=true;
                                    if(!text.isEmpty()){
                                        Reseñas reseñas = new Reseñas("Felipe",idSite,text,puntaje);
                                        myRef.child("reseñas").push().setValue(reseñas);
                                        final DatabaseReference refSite = database.getReference("lugares");
                                        refSite.orderByChild("Id").equalTo(idSite).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    myRef.child("checkin").child(key).removeValue();
                                                    for (DataSnapshot data: dataSnapshot.getChildren()){
                                                        Lugar lugar = data.getValue(Lugar.class);
                                                        Map<String, Object> updates = new HashMap<String, Object>();
                                                        updates.put("Reseñas",lugar.getReseñas()+1);
                                                        updates.put("Puntaje",lugar.getPuntaje()+puntaje);
                                                        refSite.child(idSite).updateChildren(updates);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        getActivity().onBackPressed();
                                        break;
                                    }else{
                                        Toast.makeText(getActivity(),"Llene el campo de reseña",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            if(!verificar){
                                Toast.makeText(getActivity(),"Debes realizar checkin",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),"Debes realizar checkin",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;
            case R.id.bCancelar:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        puntaje=v;
    }
}
