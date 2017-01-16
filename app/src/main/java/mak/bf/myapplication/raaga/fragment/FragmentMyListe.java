package mak.bf.myapplication.raaga.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.Firebase;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import fr.arnaudguyon.perm.PermResult;
import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ActiviteDetailPoste;
import mak.bf.myapplication.raaga.activite.ActiviteDetailStore;
import mak.bf.myapplication.raaga.activite.FullScannerActivity;
import mak.bf.myapplication.raaga.adaptateur.AdapteurMyListe;
import mak.bf.myapplication.raaga.autre.Poste;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Store;
import mak.bf.myapplication.raaga.autre.Transite;

import static android.app.Activity.RESULT_OK;
import static mak.bf.myapplication.raaga.autre.Static.PERMISSION_CAMERA_REQUEST;
import static mak.bf.myapplication.raaga.autre.Static.TYPE_POSTE_RECHERCHER;
import static mak.bf.myapplication.raaga.autre.Static.TYPE_STORE_RECHERCHER;
import static mak.bf.myapplication.raaga.autre.Static.getRootReference;

public class FragmentMyListe extends Fragment  {

    private MaterialDialog filtreDialog;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private MaterialDialog RechercheDialog;
    private MaterialDialog rechercherParTexte;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vue =   inflater.inflate(R.layout.fragment_my_liste_poste, container, false);

        mRecyclerView = (RecyclerView) vue.findViewById(R.id.recyclerView);
        {// creation et paramettrage de recycler view

            linearLayoutManager = new LinearLayoutManager(getActivity());
            gridLayoutManager = new GridLayoutManager(getContext(),2);


            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            //Use this now
            mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        }

//        mRecyclerView.setAdapter(mAdapter);
        DatabaseReference posteRef = getRootReference().child("Poste");
        Query query = posteRef.getRef();
        mRecyclerView.setAdapter(getAdapter(query));

        return vue;
    }

    // fonction de confuguration de l'adapter
    private AdapteurMyListe getAdapter(Query requete) {
        Query ref = Static.getRootReference().child("Poste").orderByChild("idStore").equalTo(Static.currentUser().getUid());
        Query mQuery = getRootReference().child("Poste").getRef();
        mQuery.equalTo(Static.currentUser().getUid(),"idStore");
        AdapteurMyListe recyclerAdapter = new AdapteurMyListe(ref,Poste.class,getContext(),null,null);



        return recyclerAdapter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public static Fragment newInstance() {
        return new FragmentMyListe();
    }

    public static Fragment newInstance(DatabaseReference reference) {
        FragmentMyListe fragment = new FragmentMyListe();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            PermResult cameraPermissionResult = new PermResult(permissions, grantResults);
            if (cameraPermissionResult.isGranted()) {
                startActivityForResult(new Intent(getContext(), FullScannerActivity.class),Static.FULL_SCANNER_ACTIVITY);
            } else if (cameraPermissionResult.isDenied()) {
                Toast.makeText(getContext(), "Camera Permission refuser", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Camera Permission annuler", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode){
            case Static.FULL_SCANNER_ACTIVITY:

                String typeRecherche = data.getExtras().get("type").toString();
                String idRechrcher = data.getExtras().get("valeur").toString();

                if (typeRecherche.equals(TYPE_POSTE_RECHERCHER)) {

                    getRootReference().child("Poste").child(idRechrcher).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Transite.poste = dataSnapshot.getValue(Poste.class);
                            startActivity(new Intent(getActivity(), ActiviteDetailPoste.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), ".....", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                        getRootReference().child("Store").child(idRechrcher).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Transite.store = dataSnapshot.getValue(Store.class);
                                startActivity(new Intent(getActivity(), ActiviteDetailStore.class));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(),".....",Toast.LENGTH_SHORT).show();
                            }
                        });

                }
                Toast.makeText(getContext(),"Valeur = " + data.getExtras().get("valeur"),Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
