package mak.bf.myapplication.raaga.autre;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ActiviteDetailStore;


/**
 * Created by Mohamine on 11/30/2016.
 */

public class StoreViewHolderVertical extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nom;
    private TextView type;
    private TextView description;
    private TextView distance;
    private Button follow;
    private Button share;
    private String imageUri;

    private String posteId;

    private ImageView imagePoste;
    private Context context;
    private Store store;

    public StoreViewHolderVertical(View itemView , Context context ) {
        super(itemView );
        initialiser(itemView , context);
    }

    private void initialiser(View Pview, Context Pcontext){

        context = Pcontext;
        type = (TextView) Pview.findViewById(R.id.tv_type_store);
        distance = (TextView) Pview.findViewById(R.id.tv_distance_store);
        nom = (TextView) Pview.findViewById(R.id.tv_nom_store);
        description = (TextView) Pview.findViewById(R.id.tv_description_store);
        follow = (Button) Pview.findViewById(R.id.btn_suivre_store); follow.setOnClickListener(this);
        share = (Button) Pview.findViewById(R.id.btn_partage_store);  share.setOnClickListener(this);
        imagePoste = (ImageView) Pview.findViewById(R.id.iv_image_store);
        imageUri = "";
        store = null;

        Pview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transite.store = store;
                context.startActivity(new Intent(context, ActiviteDetailStore.class));
            }
        });
    }

    public void setNom(String nom) {
        this.nom.setText(nom);
    }

    public TextView getNom() {
        return nom;
    }

    public void setImagePoste(String url , Context context) {
        imageUri = url;
        this.imagePoste = imagePoste;
        // Load the image using Glide
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        Glide.with( context)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(this.imagePoste);
    }

    public void setId( String Pid) {
        this.posteId = Pid;
    }

    public void setDescription(String description){
        this.description.setText(description);
    }

    public void setType(String type) {
        this.type.setText(type);
    }

    public void setDistance(String distance) {
        this.distance.setText(distance);
    }

    public void setStore(Store store){
        this.store = store;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_partage_store:

                break;
            case R.id.btn_suivre_store:

                Static.getRootReference().child("User").child(Static.currentUser()
                        .getUid()).child("listeFollowing").child(posteId).setValue(posteId);
                follow.setClickable(false);

                break;
        }
    }

}
