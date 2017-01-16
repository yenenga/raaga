package mak.bf.myapplication.raaga.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gregacucnik.EditTextView;
import com.jaredrummler.materialspinner.MaterialSpinner;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ActiviteDetailPoste;
import mak.bf.myapplication.raaga.activite.ActiviteDetailStore;
import mak.bf.myapplication.raaga.activite.FullScannerActivity;
import mak.bf.myapplication.raaga.adaptateur.AdapteurPosteWithQuery;
import mak.bf.myapplication.raaga.adaptateur.AdapteurStoreWithQuery;
import mak.bf.myapplication.raaga.autre.Poste;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Store;
import mak.bf.myapplication.raaga.autre.Transite;

import static android.app.Activity.RESULT_OK;
import static mak.bf.myapplication.raaga.autre.Static.TYPE_POSTE_RECHERCHER;
import static mak.bf.myapplication.raaga.autre.Static.TYPE_STORE_RECHERCHER;

public class FragmentListeStore extends Fragment  {

    private MaterialDialog filtreDialog;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private MaterialDialog RechercheDialog;
    private MaterialDialog rechercherParTexte;
    private DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vue =   inflater.inflate(R.layout.fragment_liste_store, container, false);

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
        DatabaseReference posteRef = Static.getRootReference().child("Store");
        Query query = posteRef.getRef();
        mRecyclerView.setAdapter(getAdapter(query));

        return vue;
    }

    // fonction de confuguration de l'adapter
    private AdapteurStoreWithQuery getAdapter(Query requete) {

        AdapteurStoreWithQuery recyclerAdapter = new AdapteurStoreWithQuery(requete,Store.class,getContext(),null,null);



        return recyclerAdapter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialisation du filtre dans la fonction gestionFiltre()
        gestionFiltre();
        //initialisation de la rechercherpartexte dans gestionRecherche
        gestionRechercher();
        //initialisation de la rechercherpartexte dans gestionRecherche
        gestionRechercherParTexte();
        //initialisation de la rechercherpartexte dans gestionRecherche

        // estion du boutton floatting
        {//floating boutton
            final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) view.findViewById(R.id.menu);

            //boutton ajouter un filtre a une rechercherpartexte
            FloatingActionButton btnFiltre = (FloatingActionButton) view.findViewById(R.id.menu_item_action_filtre);
            btnFiltre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // afficher la fenetre du filtrage
                    filtreDialog.show();
                    //fermer le menu flotant
                    menuMultipleActions.close(true);
                }
            });

            //boutton rechercher un Poste
            FloatingActionButton btnRecherche = (FloatingActionButton) view.findViewById(R.id.menu_item_action_rechercher);
            btnRecherche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //verification de la presance un compte user ou pas
                    RechercheDialog.show();
                    menuMultipleActions.close(true);
                }
            });
        }
    }

    public static Fragment newInstance() {
        return new FragmentListeStore();
    }

    public static Fragment newInstance(DatabaseReference reference) {
        FragmentListeStore fragment = new FragmentListeStore();
        fragment.reference = reference;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void gestionFiltre() {
        //refferance du filtre
        //gerstion de la liste des caracteristique
        filtreDialog = new MaterialDialog.Builder(getContext())
                .title("Filtre de rechercherpartexte")
                .customView(R.layout.dialog_add_filtre, true)
                .theme(Theme.LIGHT)
                .positiveText("Filtre")
                .negativeText("Annuler")
                .neutralText("Effacer").build();
        View root = filtreDialog.getView();

        final Query requete = Static.getRootReference().child("Poste");

        //trier par categorie
        final MaterialSpinner typeType = (MaterialSpinner) root.findViewById(R.id.spinnerTypeType);
        typeType.setItems("Aucun","Toutes categories", "Autres", "Consoles & jeux video",
                "electronique", "Electromenager", "Emploi", "Immobilier", "Meubles, deco et jardin",
                "Vetement, mode et accessoires", "Voitures et motos");
        typeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (typeType.getText().toString().equals("Aucun"))
                    return;

                requete.equalTo(typeType.getText().toString(),"categorie");
            }
        });


        //listener sur la boutton Rechercher
        filtreDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRecyclerView.setAdapter(getAdapter(reference));

                // furmeture du dialog
                filtreDialog.dismiss();
            }
        });

        //listener sur la boutton effacher
        filtreDialog.getActionButton(DialogAction.NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRecyclerView.setAdapter(getAdapter(requete));

                // furmeture du dialog
                filtreDialog.dismiss();
            }
        });

    }

    private void gestionRechercher() {

        //gerstion de la liste des caracteristique
        RechercheDialog = new MaterialDialog.Builder(getContext())
                .title("Type de recherche")
                .customView(R.layout.choix_type_recherche, true)
                .theme(Theme.LIGHT).build();
        View root = RechercheDialog.getView();

        Button btnParTexte = (Button) root.findViewById(R.id.btn_rechrcher_par_texte);
        btnParTexte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechercherParTexte.show();
                RechercheDialog.dismiss();
            }
        });

        Button btnParScannage = (Button) root.findViewById(R.id.btn_rechercher_par_scannage);
        btnParScannage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), FullScannerActivity.class),Static.FULL_SCANNER_ACTIVITY);
                RechercheDialog.dismiss();
            }
        });
    }

    private void gestionRechercherParTexte(){
        //refferance du filtre
        final DatabaseReference reference = Static.getRootReference().child("Poste");
        //gerstion de la liste des caracteristique
        rechercherParTexte = new MaterialDialog.Builder(getContext())
                .title("Recherche par texte")
                .customView(R.layout.rechercherpartexte, true)
                .theme(Theme.LIGHT)
                .positiveText("Rechercher")
                .negativeText("Annuler").build();
        View root = rechercherParTexte.getView();

        final EditTextView editTextView = (EditTextView)root.findViewById(R.id.etDescription);

        //listener sur la boutton Rechercher
        rechercherParTexte.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextView.getText().equals(""))
                    Toast.makeText(getContext(),"......",Toast.LENGTH_SHORT).show();
                else {
                    Query requete = Static.getRootReference().child("Poste").orderByChild("nom").equalTo(editTextView.getText());
                    mRecyclerView.setAdapter(getAdapter(reference));
                }

                // furmeture du dialog
                rechercherParTexte.dismiss();
            }
        });

        //listener sur la boutton annuler
        rechercherParTexte.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // furmeture du dialog
                rechercherParTexte.dismiss();
            }
        });

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

                    Static.getRootReference().child("Poste").child(idRechrcher).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        Static.getRootReference().child("Store").child(idRechrcher).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Toast.makeText(getContext(),"Valeur = " + data.getExtras().get("valeur")+"\ntypr = " + data.getExtras().get("type"),Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
