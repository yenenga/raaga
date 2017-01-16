package mak.bf.myapplication.raaga.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ImageFullScreen;
import mak.bf.myapplication.raaga.autre.Adresse;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Store;
import mak.bf.myapplication.raaga.autre.Transite;
import mak.bf.myapplication.raaga.autre.User;

import static android.content.Context.LOCATION_SERVICE;

public class fragmentDetailStore extends Fragment implements View.OnClickListener {
    private SliderLayout sliderLayout; //afficher des images du produit
    private Button btnCarte;
    private Button btnContact;
    private Button btnFollow;
    private Button btnPartager;
    private Store store;
    Context context;

    private TextView nom;
    private TextView description;
    private TextView type;
    private TextView adresse;

    private ImageView ivQrCode;


    public fragmentDetailStore() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static fragmentDetailStore newInstance( ) {
        return  new fragmentDetailStore();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vue = inflater.inflate(R.layout.fragment_detail_store, container, false);
        sliderLayout = (SliderLayout) vue.findViewById(R.id.slider_boutique);

        nom = (TextView) vue.findViewById(R.id.tv_nom_boutique);
        description = (TextView) vue.findViewById(R.id.tv_detail_boutique);
        type = (TextView)vue.findViewById(R.id.tv_type_store);
        adresse = (TextView) vue.findViewById(R.id.tv_adresse_store);

        btnCarte = (Button) vue.findViewById(R.id.btn_carte);
        btnContact = (Button) vue.findViewById(R.id.btn_contacte);
        btnFollow = (Button) vue.findViewById(R.id.btn_follow);
        btnPartager = (Button) vue.findViewById(R.id.btn_partager);

        ivQrCode = (ImageView) vue.findViewById(R.id.iv_qrcode_store);

        store = Transite.store;
        return vue;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.context = getActivity().getBaseContext();
        this.nom.setText(store.getNomStore());
        this.description.setText(store.getDescriptionStore());
        this.type.setText(store.getTypeStore());
        this.adresse.setText(store.getAdress());
        this.btnPartager.setOnClickListener(this);
        this.btnFollow.setOnClickListener(this);
        this.btnContact.setOnClickListener(this);
        this.btnCarte.setOnClickListener(this);
        this.ivQrCode.setImageBitmap(Static.generateQR(getContext(),store.getIdStore(),Static.TYPE_STORE_RECHERCHER));

        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setPresetTransformer(RelativeLayout.ALIGN_BASELINE);
        sliderLayout.setDuration(10000);

        imageLoader();
    }


    private void imageLoader(){

        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setPresetTransformer(RelativeLayout.ALIGN_BASELINE);
        sliderLayout.setDuration(15000);

        DatabaseReference ref = Static.getRootReference().child("Store").child(store.getIdStore());

        ref.child("listeImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> donnee = (ArrayList<String>) dataSnapshot.getValue();

                for (String url :
                        donnee) {
                    TextSliderView textSliderView = new TextSliderView(context);
                    // initialize a SliderLayout
                    textSliderView
                            .image(url)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView baseSliderView) {
                                    Intent intent = new Intent(context,ImageFullScreen.class);
                                    intent.putExtra("liste",donnee);
                                    startActivity(intent);

                                }
                            });



                    sliderLayout.addSlider(textSliderView);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_partager:

                partageStore();

                break;
            case R.id.btn_contacte:

                numeroStore();

                break;
            case R.id.btn_carte:

                afficheCarte();

                break;
            case R.id.btn_follow:

                followStore();

                break;
        }
    }

    private void followStore() {

        Static.getRootReference().child("User").child(Static.currentUser()
                .getUid()).child("listeFollowing").child(store.getIdStore()).setValue(store.getIdStore());
        btnFollow.setClickable(false);
        btnFollow.setBackgroundColor(getResources().getColor(R.color.blue_semi_transparent));
    }

    private void partageStore() {

    }

    private void afficheCarte(){

        GeoFire geoFire = new GeoFire(Static.getRootReference().child("GPS").child("Store"));
        geoFire.getLocation(store.getIdStore(), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation locationGeoFire) {
                if (locationGeoFire != null) {
                    System.out.println(String.format("The location for key %s is [%f,%f]", key, locationGeoFire.latitude, locationGeoFire.longitude));

                    //Obtention de la référence du service
                    LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                    //Si le GPS est disponible, on s'y abonne
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Uri gmmIntentUri;
                        gmmIntentUri = Uri.parse("google.navigation:q=" + locationGeoFire.latitude + "," +locationGeoFire.longitude);

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(mapIntent);
                    }
                    //si le GPS n est pas active on demande l activation du GPS d abord
                    else {
                        Toast.makeText(context, "Veillez active votre GPS pour etre Geolocalise", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                    }


                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });

    }

    private void numeroStore() {

        new MaterialDialog.Builder(getActivity())
                .title("numero")
                .content(store.getNumero())
                .theme(Theme.LIGHT)
                .positiveText("appeler")
                .negativeText("annuler")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent appel = new Intent(Intent.ACTION_DIAL,
                                Uri.parse("tel:" + store.getNumero()));
                        appel.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(appel);
                    }
                })
               .show();
    }
}
