package com.parra.lfelipe.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parra.lfelipe.recyclerview.Servicios.MyService;

public class MainActivity extends AppCompatActivity implements InterfaceSite{

    FragmentManager fm;
    FragmentTransaction ft;
    ListFragment listFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this,MyService.class));
        fm = getSupportFragmentManager();
        SitesFragment sitesFragment = new SitesFragment();
        ft = fm.beginTransaction();
        ft.add(R.id.container,sitesFragment).commit();
    }

    @Override
    public void searchSite(String id) {
        Bundle args = new Bundle();
        args.putString("id",id);
        listFragment.dismiss();
        ft = fm.beginTransaction();
        InfoSiteFragment fragment = new InfoSiteFragment();
        fragment.setArguments(args);
        ft.replace(R.id.container,fragment).commit();
        ft.addToBackStack("ListCat");
    }

    @Override
    public void listSite(final String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("lugares");

        myRef.orderByChild("Nombre").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cont = 0;
                String sid = "";
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    cont++;
                    sid = data.child("Id").getValue(String.class);
                }
                if(cont==0){
                    Toast.makeText(getApplicationContext(),"Problemas de conexión",Toast.LENGTH_SHORT).show();
                }else if(cont==1){
                    //ir a info lugar
                    Bundle args = new Bundle();
                    args.putString("id",sid);
                    ft = fm.beginTransaction();
                    InfoSiteFragment fragment = new InfoSiteFragment();
                    fragment.setArguments(args);
                    ft.replace(R.id.container,fragment).commit();
                    ft.addToBackStack("ListCat");
                }else{
                    ft=fm.beginTransaction();
                    listFragment = ListFragment.newInstance(name);
                    listFragment.show(ft,"List");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void aregarReseña(String idSite) {
        Bundle args = new Bundle();
        args.putString("idSite",idSite);
        ft = fm.beginTransaction();
        AddResFragment addResFragment = new AddResFragment();
        addResFragment.setArguments(args);
        ft.replace(R.id.container,addResFragment).commit();
        ft.addToBackStack("InfoSite");
    }
}
