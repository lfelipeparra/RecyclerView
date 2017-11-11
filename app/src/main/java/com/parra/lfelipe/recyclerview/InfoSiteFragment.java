package com.parra.lfelipe.recyclerview;


import android.content.Context;
import android.os.Bundle;
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
                sid = this.id;
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                Query query = myRef.child("checkin");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            if(!verificar){
                                Checkin checkin = new Checkin("Felipe",sid);
                                myRef.child("checkin").push().setValue(checkin);
                                Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Checkin checkin = new Checkin("Felipe",sid);
                            myRef.child("checkin").push().setValue(checkin);
                            Toast.makeText(getContext(),"Checkin creado",Toast.LENGTH_SHORT).show();
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
