package mak.bf.myapplication.raaga.autre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.activite.ActiviteDetailPoste;
import mak.bf.myapplication.raaga.activite.ActiviteDetailStore;
import mak.bf.myapplication.raaga.activite.Main2Activity;


/**
 * Created by Mohamine on 11/30/2016.
 */

public class PosteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView nom;
    private TextView prix;
    private TextView description;
    private Button save;
    private Button share;
    private String imageUrl = "";

    private String posteId;

    private ImageView imagePoste;
    private Context context;
    private Poste poste;

    public PosteViewHolder(View itemView , final Context context ) {
        super(itemView );
        initialiser(itemView , context);
    }

    private void initialiser(View Pview, Context Pcontext){

        context = Pcontext;
        prix = (TextView) Pview.findViewById(R.id.tv_prix_poste);
        nom = (TextView) Pview.findViewById(R.id.tv_nom_poste);
        description = (TextView) Pview.findViewById(R.id.tv_description_poste);
        save = (Button) Pview.findViewById(R.id.btn_save_poste); save.setOnClickListener(this);
        share = (Button) Pview.findViewById(R.id.btn_partage_poste);  share.setOnClickListener(this);
        imagePoste = (ImageView) Pview.findViewById(R.id.iv_image_poste);
        imageUrl = "";

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
        imageUrl = url;
        // Load the image using Glide
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        Glide.with( context)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(this.imagePoste);

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.raaga)
                .thumbnail(1f)
                .into(imagePoste);

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
            case R.id.btn_partage_poste:

//                BitmapDrawable drawable = (BitmapDrawable) imagePoste.getDrawable();
//                Bitmap bitmap = drawable.getBitmap();
//
//                SharePhoto photo = new SharePhoto.Builder()
//                        .setImageUrl(Uri.parse("http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg"))
//                        .build();
//                SharePhotoContent content = new SharePhotoContent.Builder()
//                        .addPhoto(photo)
//                        .setContentUrl(Uri.parse("http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg"))
//                        .build();
//
//                ShareDialog.show((Activity) context,content);
                break;
            case R.id.btn_save_poste:
                Map<String , Object> valeur = new HashMap<>();
                valeur.put(posteId,Static.dateActuel());
                    Static.getRootReference().child("User").child(Static.currentUser()
                            .getUid()).child("listeFavoris").child(posteId).setValue(posteId);
                    save.setClickable(false);

                break;
        }
    }

    public void setPoste(final Poste poste) {
        this.poste = poste;
        imagePoste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transite.poste = poste;
                context.startActivity(new Intent(context, ActiviteDetailPoste.class));
            }
        });
    }
}
