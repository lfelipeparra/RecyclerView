package com.parra.lfelipe.recyclerview;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                Checkin checkin = new Checkin("Felipe",this.id);
                myRef.child("checkin").push().setValue(checkin);
                break;
            case R.id.bAgregarReseña:
                agregarReseña.aregarReseña(this.id);
                break;
        }
    }
}
