package mak.bf.myapplication.raaga.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gregacucnik.EditTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ActivityCreationPoste;
import mak.bf.myapplication.raaga.autre.Adresse;
import mak.bf.myapplication.raaga.autre.Menu;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Store;
import mak.bf.myapplication.raaga.autre.Transite;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Subscription;
import rx.functions.Action1;

import static android.content.Context.LOCATION_SERVICE;

public class FragmentMyStore extends Fragment implements View.OnClickListener {

    private ImageView imageStore;

    private ImageButton btnSetNom;
    private ImageButton btnSetDescription;
    private ImageButton btnSetSiteWeb;
    private ImageButton btnSetImageStore;
    private ImageButton btnSetType;
    private ImageButton btnSetNumero;

    private Button btnSetAdresse;

    private TextView tvNom;
    private TextView tvDescription;
    private TextView tvSiteWeb;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAdresse;
    private TextView tvType;
    private TextView tvNumero;
    private EditTextView etNumero;
    private MaterialDialog dialogAdresse;

    private Context context;

    private Store myStore;

    private void initialisation(View view) {

        this.myStore = Transite.myStore;
        this.context = getContext();

        imageStore = (ImageView) view.findViewById(R.id.iv_image_store);

        btnSetNom = (ImageButton) view.findViewById(R.id.btn_set_nom) ; btnSetNom.setOnClickListener(this);
        btnSetDescription = (ImageButton) view.findViewById(R.id.btn_set_description); btnSetDescription.setOnClickListener(this);
        btnSetSiteWeb = (ImageButton) view.findViewById(R.id.btn_set_siteWeb); btnSetSiteWeb.setOnClickListener(this);
        btnSetImageStore = (ImageButton) view.findViewById(R.id.btn_set_image); btnSetImageStore.setOnClickListener(this);
        btnSetAdresse = (Button) view.findViewById(R.id.btn_set_adresse); btnSetAdresse.setOnClickListener(this);
        btnSetType = (ImageButton) view.findViewById(R.id.btn_set_type); btnSetType.setOnClickListener(this);
        btnSetNumero = (ImageButton) view.findViewById(R.id.btn_set_numero); btnSetNumero.setOnClickListener(this);

        tvNom = (TextView) view.findViewById(R.id.tv_nom_store) ;
        tvDescription = (TextView) view.findViewById(R.id.tv_description_store) ;
        tvSiteWeb = (TextView) view.findViewById(R.id.tv_siteweb_store) ;
        tvLongitude = (TextView) view.findViewById(R.id.tv_longitude_store) ;
        tvLatitude = (TextView) view.findViewById(R.id.tv_latitude_store) ;
        tvAdresse = (TextView) view.findViewById(R.id.tv_adresse_store) ;
        tvType = (TextView) view.findViewById(R.id.tv_type_store);
        tvNumero = (TextView) view.findViewById(R.id.tv_numero_store);

        /* ------------------------------------------------------------------------------- */

        tvNom.setText(myStore.getNomStore());
        tvDescription.setText(myStore.getDescriptionStore());
        tvSiteWeb.setText(myStore.getSiteWeb());
        tvType.setText(myStore.getTypeStore());
        tvNumero.setText(myStore.getNumero());
        tvAdresse.setText(myStore.getAdress());


        GeoFire geoFire = new GeoFire(Static.getRootReference().child("GPS").child("Store"));
        geoFire.getLocation(myStore.getIdStore(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation locationGeoFire) {
//                tvLongitude.setText((int) locationGeoFire.longitude);
//                tvLatitude.setText((int) locationGeoFire.latitude);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });

        // Load the image using Glide
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(myStore.getListeImage().get(0));
        Glide.with( this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(this.imageStore);

        gestionAdresse();

        {//floating boutton
            final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) view.findViewById(R.id.menu);

            final FloatingActionButton btnTypeView = (FloatingActionButton) menuMultipleActions.findViewById(R.id.menu_item_action_type_affichage);
            //boutton ajouter produit

            //boutton ajouter un filtre a une rechercherpartexte

            //boutton rechercher un Poste
            FloatingActionButton btnRecherche = (FloatingActionButton) view.findViewById(R.id.menu_item_action_produit);
            btnRecherche.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(getActivity(),ActivityCreationPoste.class));

                    menuMultipleActions.close(true);
                }
            });
        }
    }

    public static Fragment newInstance() {
        return new FragmentMyStore();
    }

    private void gestionTypeStore() {
        final List<String> listeTyepStore = new ArrayList<>();
        Static.getRootReference().child("Menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Menu menu = postSnapshot.getValue(Menu.class);
                    listeTyepStore.add(menu.getNom());
                }
                new MaterialDialog.Builder(context)
                        .title("Type Store")
                        .theme(Theme.LIGHT)
                        .items(listeTyepStore)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                tvType.setText(text);
                                // modification du type de store
                                setStringValue("typeStore", String.valueOf(text));
                                return true;
                            }
                        })
                        .positiveText(R.string.choose)
                        .show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                listeTyepStore.add("yes");
                new MaterialDialog.Builder(context)
                        .title("Type Store")
                        .items(listeTyepStore)
                        .theme(Theme.LIGHT)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                tvType.setText(text);
                                return true;
                            }
                        })
                        .positiveText(R.string.choose)
                        .show();
            }
        });

    }

    private void gestionAdresse() {
        //gerstion de la liste des caracteristique
        dialogAdresse = new MaterialDialog.Builder(context)
                .title("Adresse")
                .customView(R.layout.dialog_adresse, true)
                .theme(Theme.LIGHT)
                .negativeText("Annuler")
                .build();
        View root = dialogAdresse.getView();

        final EditTextView etAdresse = (EditTextView) root.findViewById(R.id.etAdresse);
        etAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAdresse.dismiss();
            }
        });

        Button etVerification = (Button) root.findViewById(R.id.btn_verification);
        etVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationFromAdresse(etAdresse.getText());
                dialogAdresse.dismiss();
            }
        });

        Button btnGeolocaliser = (Button) root.findViewById(R.id.btn_geolocaliser);
        btnGeolocaliser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationUpdate();
                dialogAdresse.dismiss();
            }
        });
    }

    private void gestionStringValue(final String champs){
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Modifier")
                .customView(R.layout.add_value, true)
                .theme(Theme.LIGHT)
                .positiveText("Ajouter")
                .negativeText("Annuler").build();
        View root = dialog.getView();
        final EditText valeur = (EditText) root.findViewById(R.id.etValeur);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                switch (champs){
                    case "siteWeb":
                        setStringValue(champs , valeur.getText().toString());
                        tvSiteWeb.setText(valeur.getText().toString());
                        break;
                    case "nomStore":
                        setStringValue(champs , valeur.getText().toString());
                        tvNom.setText(valeur.getText().toString());
                        break;
                    case "descriptionStore":
                        setStringValue(champs , valeur.getText().toString());
                        tvDescription.setText(valeur.getText().toString());
                        break;
                    case "numero":
                        setStringValue(champs , valeur.getText().toString());
                        tvNumero.setText(valeur.getText().toString());
                        break;
                }
            }
        });

        dialog.show();


    }

    private void setStringValue(String champs , String value){
        Static.getRootReference().child("Store").child(myStore.getIdStore()).child(champs).setValue(value);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_set_nom:
                gestionStringValue("nomStore");
                break;
            case R.id.btn_set_description:
                gestionStringValue("descriptionStore");
                break;
            case R.id.btn_set_siteWeb:
                gestionStringValue("siteWeb");
                break;
            case R.id.btn_set_image:
                break;
            case R.id.btn_set_adresse:
                dialogAdresse.show();
                break;
            case R.id.btn_set_type:
                gestionTypeStore();
                break;
            case R.id.btn_set_numero:
                gestionStringValue("numero");
                break;
        }
    }

    private void getAdresseFromLocation(Location location){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        String texte = "";
        texte +=  address + " ";
        if (city != null && (!"null".equals(city)))
            texte += city + " ";
        if (!"null".equals(state))
            texte += state + " " ;
        if (!"null".equals(country))
            texte += country + " " ;
        if (!"null".equals(postalCode))
            texte += postalCode  ;


        tvAdresse.setText(texte);
//        if (this.adresse == null)
//            this.adresse = new Adresse();
//        this.adresse.setAdresse(texte);
//        this.adresse.setLatitude(String.valueOf(location.getLatitude()));
//        this.adresse.setLongitude(String.valueOf(location.getLongitude()));
    }

    private void getLocationFromAdresse(String adress){
        Geocoder coder = new Geocoder(getContext());
        List<Address> listeAdress = null;


        try {
            listeAdress = coder.getFromLocationName(adress,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listeAdress.size() == 0) {
            Toast.makeText(context,"mauvaise adresse",Toast.LENGTH_SHORT).show();
            return ;
        }
        if (adress.isEmpty())
            return;
        Address location = listeAdress.get(0);
        location.getLatitude();
        location.getLongitude();

        tvLatitude.setText(" Lat : "+ location.getLatitude());
        tvLongitude.setText(" Long : "+ location.getLongitude());
        tvAdresse.setText(adress);

//        if (this.adresse == null)
//            this.adresse = new Adresse();
//        this.adresse.setAdresse(adress);
//        this.adresse.setLatitude(String.valueOf(location.getLatitude()));
//        this.adresse.setLongitude(String.valueOf(location.getLongitude()));

    }

    // fonction pour activer la MAJ des cordonnee automation
    private void locationUpdate() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setInterval(100);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);

        Subscription subscription = locationProvider.getUpdatedLocation(request)
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {

                        tvLatitude.setText("Lat : "+location.getLatitude());
                        tvLongitude.setText("Long : "+location.getLongitude());
                        getAdresseFromLocation(location);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vue =   inflater.inflate(R.layout.fragment_my_store, container, false);
        initialisation(vue);
        return vue;
    }
}
