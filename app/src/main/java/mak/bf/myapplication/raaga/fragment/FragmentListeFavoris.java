package mak.bf.myapplication.raaga.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ActiviteDetailPoste;
import mak.bf.myapplication.raaga.autre.Poste;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Transite;
import mak.bf.myapplication.raaga.autre.User;

public class FragmentListeFavoris extends Fragment  {

    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vue =   inflater.inflate(R.layout.fragment_liste_favoris, container, false);
        listView = (ListView) vue.findViewById(R.id.listeView);

        DatabaseReference mRef = Static.getUserReference().child("listeFavoris");
        FirebaseListAdapter myAdapter = new FirebaseListAdapter<String>(getActivity(),String.class,R.layout.poste_view_holder_favoris,mRef) {
            @Override
            protected void populateView(final View viewHolder, String id, int i) {
                Static.getRootReference().child("Poste").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         final Poste poste = dataSnapshot.getValue(Poste.class);

                        TextView nom = (TextView) viewHolder.findViewById(R.id.tv_nom_poste);
                        nom.setText(poste.getNom());

                        TextView prix = (TextView) viewHolder.findViewById(R.id.tv_prix_poste);
                        prix.setText(String.valueOf(poste.getPrix()));

                        TextView description = (TextView) viewHolder.findViewById(R.id.tv_description_poste);
                        description.setText(poste.getDescription());

                        ImageView image = (ImageView) viewHolder.findViewById(R.id.iv_image_poste);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(poste.getListeImage().get(0));
                        Glide.with( getContext())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(image);

                        Button btnUnfollow = (Button) viewHolder.findViewById(R.id.btn_unfollow);
                        btnUnfollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Static.getUserReference().child("listeFavoris").child(poste.getId()).removeValue();
                            }
                        });

                        viewHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Transite.poste = poste;
                                startActivity(new Intent(getActivity(),ActiviteDetailPoste.class));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        listView.setAdapter(myAdapter);


        return vue;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static Fragment newInstance() {
        return new FragmentListeFavoris();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
