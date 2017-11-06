package com.parra.lfelipe.recyclerview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoSiteFragment extends Fragment {


    public InfoSiteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String id = getArguments().getString("id");
        View view = inflater.inflate(R.layout.fragment_info_site, container, false);
        TextView textView = view.findViewById(R.id.prueba);
        textView.setText(id);
        return view;
    }
}
