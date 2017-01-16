package mak.bf.myapplication.raaga.autre;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import mak.bf.myapplication.R;


/**
 * Created by Mohamine on 11/30/2016.
 */

public class MyPosteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nom;
    private TextView prix;
    private TextView description;
    private Button supprimer;
    private Button modifier;
    private String imageUri;

    private String posteId;

    private ImageView imagePoste;
    private Context context;

    public MyPosteViewHolder(View itemView , Context context ) {
        super(itemView );
        initialiser(itemView , context);
    }

    private void initialiser(View Pview, Context Pcontext){

        context = Pcontext;
        prix = (TextView) Pview.findViewById(R.id.tv_prix_poste);
        nom = (TextView) Pview.findViewById(R.id.tv_nom_poste);
        description = (TextView) Pview.findViewById(R.id.tv_description_poste);
        supprimer = (Button) Pview.findViewById(R.id.btn_supprime); supprimer.setOnClickListener(this);
        modifier = (Button) Pview.findViewById(R.id.btn_modifier);  modifier.setOnClickListener(this);
        imagePoste = (ImageView) Pview.findViewById(R.id.iv_image_poste);
        imageUri = "";
    }

    public void setNom(String nom) {
        this.nom.setText(nom);
    }

    public void setPrix(String prix) {
        this.prix.setText(prix);
    }

    public TextView getNom() {
        return nom;
    }

    public TextView getPrix() {
        return prix;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_supprime:

            case R.id.btn_modifier:

                break;
        }
    }

}
