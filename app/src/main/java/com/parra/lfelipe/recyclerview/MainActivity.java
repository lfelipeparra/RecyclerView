package com.parra.lfelipe.recyclerview;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements InterfaceSite{

    FragmentManager fm;
    FragmentTransaction ft;
    ListFragment listFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public void listSite(String name) {
        ft=fm.beginTransaction();
        listFragment = ListFragment.newInstance(name);
        listFragment.show(ft,"List");
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
