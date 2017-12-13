package com.parra.lfelipe.recyclerview;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mango on 7/12/2017.
 */

public class CheckinsAdapter  extends RecyclerView.Adapter<CheckinsAdapter.MyViewHolder>{
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Lugar> lugares;

    public CheckinsAdapter(Context context, ArrayList<Lugar> lugares) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.lugares = lugares;
    }

    @Override
    public CheckinsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("prueba","Entro");
        View view = inflater.inflate(R.layout.item_checkins,parent,false);
        CheckinsAdapter.MyViewHolder holder = new CheckinsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Toast.makeText(context,lugares.get(position).getId(),Toast.LENGTH_SHORT).show();
        if(!lugares.isEmpty()){
            holder.tvTitle.setText(lugares.get(position).getNombre());
            holder.tvResenas.setText(Integer.toString(lugares.get(position).getReseñas()));
            Glide.with(context).load(lugares.get(position).getImagen()).into(holder.imagePlace);
            holder.ratingBar.setRating(lugares.get(position).getPuntaje());
            holder.tvCategoria.setText(lugares.get(position).getCategoria());
            holder.tvDireccion.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return lugares.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvResenas, tvDireccion,tvCategoria, tvTitle;
        ImageView imagePlace;
        RatingBar ratingBar;
        Button addCheck;
        public MyViewHolder(final View itemView) {
            super(itemView);
            tvResenas = (TextView) itemView.findViewById(R.id.tvReseñas);
            tvCategoria = (TextView) itemView.findViewById(R.id.tvCategorias);
            tvDireccion = (TextView) itemView.findViewById(R.id.tvDireccion);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            imagePlace = (ImageView) itemView.findViewById(R.id.imagePlace);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingview);
            addCheck = (Button) itemView.findViewById(R.id.bAddCheck);
            addCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String sid = lugares.get(getAdapterPosition()).getId();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference myRef = database.getReference();
                    myRef.child("checkin").orderByChild("sid").equalTo(sid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                boolean verificar = false;
                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    if(data.child("uid").getValue(String.class).equals("Felipe")){
                                        verificar=true;
                                        Toast.makeText(context,"Ya realizó checkin",Toast.LENGTH_SHORT).show();
                                    }
                                    if(!verificar){
                                        Checkin checkin = new Checkin("Felipe",sid);
                                        myRef.child("checkin").push().setValue(checkin);
                                        Toast.makeText(context,"Checkin creado",Toast.LENGTH_SHORT).show();
                                        myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                                    if(data.child("sid").getValue(String.class).equals(sid)){
                                                        myRef.child("ubicacion").child(data.getKey()).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }else{
                                Checkin checkin = new Checkin("Felipe",sid);
                                myRef.child("checkin").push().setValue(checkin);
                                Toast.makeText(context,"Checkin creado",Toast.LENGTH_SHORT).show();
                                myRef.child("ubicacion").orderByChild("uid").equalTo("Felipe").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                            if(data.child("sid").getValue(String.class).equals(sid)){
                                                myRef.child("ubicacion").child(data.getKey()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }
}
