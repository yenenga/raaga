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
import mak.bf.myapplication.raaga.activite.ActiviteDetailStore;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Store;
import mak.bf.myapplication.raaga.autre.Transite;

public class FragmentListeFollowing extends Fragment  {

    private ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vue =   inflater.inflate(R.layout.fragment_liste_following, container, false);
        listView = (ListView) vue.findViewById(R.id.listeView);

        DatabaseReference mRef = Static.getUserReference().child("listeFollowing");
        FirebaseListAdapter myAdapter = new FirebaseListAdapter<String>(getActivity(),String.class,R.layout.store_view_holder_following,mRef) {
            @Override
            protected void populateView(final View viewHolder, String id, int i) {
                Static.getRootReference().child("Store").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Store store = dataSnapshot.getValue(Store.class);

                        TextView nom = (TextView) viewHolder.findViewById(R.id.tv_nom_store);
                        nom.setText(store.getNomStore());

                        TextView type = (TextView) viewHolder.findViewById(R.id.tv_type_store);
                        type.setText(store.getTypeStore());

                        TextView description = (TextView) viewHolder.findViewById(R.id.tv_description_store);
                        description.setText(store.getDescriptionStore());

                        ImageView image = (ImageView) viewHolder.findViewById(R.id.iv_image_store);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(store.getListeImage().get(0));
                        Glide.with( getContext())
                                .using(new FirebaseImageLoader())
                                .load(storageReference)
                                .into(image);

                        Button btnUnfollow = (Button) viewHolder.findViewById(R.id.btn_unfollow_store);
                        btnUnfollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Static.getUserReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Static.getUserReference().child("listeFollowing").child(store.getIdStore()).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        viewHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Transite.store = store;
                                startActivity(new Intent(getActivity(),ActiviteDetailStore.class));
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
        return new FragmentListeFollowing();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
