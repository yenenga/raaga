package mak.bf.myapplication.raaga.activite;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fr.arnaudguyon.perm.Perm;
import fr.arnaudguyon.perm.PermResult;
import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.autre.CategoriePublication;
import mak.bf.myapplication.raaga.autre.Poste;
import mak.bf.myapplication.raaga.autre.Static;
import mak.bf.myapplication.raaga.autre.Store;
import me.gujun.android.taggroup.TagGroup;
import com.gregacucnik.EditTextView;

import static android.R.attr.id;
import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;
import static java.security.AccessController.getContext;
import static mak.bf.myapplication.raaga.autre.Static.PERMISSION_CAMERA_REQUEST;
import static mak.bf.myapplication.raaga.autre.Static.READ_EXTERNAL_STORAGE;

public class ActivityCreationPoste extends AppCompatActivity implements View.OnClickListener {

    private EditTextView etDescription;
    private EditTextView etNom;
    private EditTextView etPrix;
    private MaterialSpinner spinerTypeePublication;
    private Button btnPublier;
    private Button btnAnnuler;
    private Button btnChoose;
    private Button btnTake;
    private Button btnAjouterCaracteristique;
    private LinearLayout ll_caracteristique;
    private ImageView ivMedia;
    private TagGroup hashTagGroup;
    private static final int IMAGE_CHOOSE = 2;
    private static final int IMAGE_CAPTURE = 3;
    private Context context;
    private Map<String,String> mapListeCaracteristique;
    //    FirebaseUser user;
    private DatabaseReference rootDatabaseReference;
    private StorageReference rootStorageReference;
    DatabaseReference userRef;
    private ProgressDialog progressDialog;
    private Uri mediaUri;
    private FirebaseUser user;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_poste);
        initialiser();
    }

    private void initialiser(){
        context = this;

        mapListeCaracteristique = new HashMap<>();
        btnChoose = (Button) findViewById(R.id.btnchoose); btnChoose.setOnClickListener(this);
        btnTake = (Button) findViewById(R.id.btntake); btnTake.setOnClickListener(this);
        btnPublier = (Button) findViewById(R.id.btnPublier); btnPublier.setOnClickListener(this);
        btnAnnuler = (Button) findViewById(R.id.btnAnnuler); btnAnnuler.setOnClickListener(this);
        btnAjouterCaracteristique = (Button) findViewById(R.id.btn_ajouter_caracteristique);btnAjouterCaracteristique.setOnClickListener(this);
        ll_caracteristique = (LinearLayout) findViewById(R.id.ll_liste_caracteristique);
        hashTagGroup = (TagGroup)  findViewById(R.id.taggroupe_hashtag);

        ivMedia = (ImageView) findViewById(R.id.ivMediaPoste);
        etDescription =(EditTextView) findViewById(R.id.etDescription);
        etNom = (EditTextView)findViewById(R.id.etNom);
        etPrix = (EditTextView)findViewById(R.id.etPrix);
        {//initialisation des spinner
            spinerTypeePublication = (MaterialSpinner) findViewById(R.id.spinnerTypePublication);
            spinerTypeePublication.setItems("Toutes categories", "Autres", "Consoles & jeux video",
                    "electronique", "Electromenager", "Emploi", "Immobilier", "Meubles, deco et jardin",
                    "Vetement, mode et accessoires", "Voitures et motos");

            spinerTypeePublication.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                }
            });
        }

        gestionCaracteristique();

        progressDialog = new ProgressDialog(this);
        rootStorageReference = FirebaseStorage.getInstance().getReference();
        rootDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
    }

    private void gestionCaracteristique() {
        //gerstion de la liste des caracteristique
        dialog = new MaterialDialog.Builder(context)
                .title("Caracteristique")
                .customView(R.layout.add_caracteristique, true)
                .theme(Theme.LIGHT)
                .positiveText("Ajouter")
                .negativeText("Annuler").build();
        View root = dialog.getView();
        final EditTextView cle = (EditTextView) root.findViewById(R.id.etCle);
        final EditTextView valeur = (EditTextView) root.findViewById(R.id.etValeur);

        //listener sur la boutton annuler
        dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// furmeture du dialog
                dialog.dismiss();
            }
        });

        //listener sur la boutton valider
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = cle.getText() + " : " + valeur.getText();

                //arrete si la cle ou la valeur est vide
                if (cle.getText().equals("") || valeur.getText().equals("")){
                    dialog.dismiss();
                    return;
                }

                final TextView caracter = new TextView(context);

                {// supprime la vue si elle est clicke
                    caracter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ll_caracteristique.removeView(caracter);
                        }
                    });
                }
//                    caracter.setBackgroundResource(android.R.mak.bf.myapplication.raaga.activite.drawable.dialog_holo_light_frame);
                caracter.setTextSize(17);
                caracter.setSingleLine(true);
                caracter.setGravity(17);
                caracter.setText(text);
                ll_caracteristique.addView(caracter, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mapListeCaracteristique.put(cle.getText(),valeur.getText());

                valeur.setText("");
                cle.setText("");

                // furmeture du dialog
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnchoose:

                Perm lectureStokagePermission = new Perm(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (lectureStokagePermission.isGranted()) {
                    Intent takeIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    takeIntent.setType("image/*");
                    startActivityForResult(takeIntent, IMAGE_CHOOSE);
                } else if (lectureStokagePermission.isDenied()) {
                    Toast.makeText(context, "Camera Permission already denied", Toast.LENGTH_LONG).show();
                    lectureStokagePermission.askPermission(READ_EXTERNAL_STORAGE);
                } else {
                    lectureStokagePermission.askPermission(READ_EXTERNAL_STORAGE);
                }
                break;

            case R.id.btntake:

                Perm cameraPermission = new Perm(this, android.Manifest.permission.CAMERA);
                if (cameraPermission.isGranted()) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
                    }
                } else if (cameraPermission.isDenied()) {
                    Toast.makeText(context, "Camera Permission already denied", Toast.LENGTH_LONG).show();
                    cameraPermission.askPermission(Static.PERMISSION_CAMERA_REQUEST);
                } else {
                    cameraPermission.askPermission(PERMISSION_CAMERA_REQUEST);
                }
                break;

            case R.id.btnPublier:
                saveMedia(mediaUri);
                break;

            case R.id.btnAnnuler:
                finish();
                break;

            case R.id.btn_ajouter_caracteristique:
                dialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode != RESULT_OK)
//            return;

        switch (requestCode){
            case IMAGE_CHOOSE:
                beginCrop(data.getData());
                break;

            case IMAGE_CAPTURE:
                beginCrop(data.getData());
                break;

            case  UCrop.REQUEST_CROP:
                mediaUri = UCrop.getOutput(data);
                ivMedia.setImageURI(mediaUri);
                break;
        }
    }

    private void saveMedia(Uri data) {
        if (mediaUri == null){
            Toast.makeText(context,"Publier quoi...",Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setMessage("Chargerment");
            progressDialog.show();

            StorageReference reference = FirebaseStorage.getInstance().getReference().
                    child("image/[ " + Static.dateActuel() + " ] user id = "+ Static.currentUser().getUid());
            reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    saveData(String.valueOf(taskSnapshot.getDownloadUrl()));
                }
            });

        }
    }

    private void saveData(String Purl) {
        if (Purl == null)
            return;


        String nom = etNom.getText().toString();
        String description = etDescription.getText().toString();
        String type = spinerTypeePublication.getText().toString();
        String userId = Static.currentUser().getUid();
        String userName = Static.currentUser().getDisplayName();
        String prix = etPrix.getText().toString();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://raaga-73a17.firebaseio.com/");
        DatabaseReference refPoste = rootRef.child("Poste").getRef();
        DatabaseReference refStore = rootRef.child("Store").getRef();
        String key =  refPoste.push().getKey();

        Poste poste = new Poste(key,userId);//creation du Poste

        //ajout des tags s'il existe
        if (hashTagGroup.getTags().length > 0){
            for (String tag :
                    hashTagGroup.getTags()) {
                poste.addHashTag(tag);
            }
        }


        // affectation des valeur au Poste
        poste.setNom(nom);
        poste.setType(type);
        poste.setDescription(description);
        poste.addImage(Purl);
        poste.setPrix(Integer.parseInt(prix));
        poste.setUserName(userName);
        poste.addCaracteristique(mapListeCaracteristique);
        poste.setIdStore(Static.currentUser().getUid());




        refPoste.child(key).setValue(poste);
        Map<String,Object> value = new HashMap<>();
        value.put(key,Static.dateActuel());

        progressDialog.dismiss();

        startActivity(new Intent(this,Main2Activity.class));
        finish();
    }

    // fonction pour lancer la redimenssion de la photo choisi
    private void beginCrop(Uri data) {
        // Uri pour la photo redimenssionner stock dand le dossier temporaire et a le meme nom
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), Static.dateActuel()));
        // lancement de l'activity de redimention
        UCrop.of(data, destinationUri)
                .withAspectRatio(16, 16)
                .withMaxResultSize(maxWidth, maxHeight)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case READ_EXTERNAL_STORAGE :

                PermResult lecturePermissionResult = new PermResult(permissions, grantResults);
                if (lecturePermissionResult.isGranted()) {
                    Intent takeIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    takeIntent.setType("image/*");
                    startActivityForResult(takeIntent, IMAGE_CHOOSE);
                } else if (lecturePermissionResult.isDenied()) {
                    Toast.makeText(context, "lecture Permission refuser", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "lecture Permission annuler", Toast.LENGTH_LONG).show();
                }

                break;
            case PERMISSION_CAMERA_REQUEST:

                PermResult cameraPermissionResult = new PermResult(permissions, grantResults);
                if (cameraPermissionResult.isGranted()) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
                    }
                } else if (cameraPermissionResult.isDenied()) {
                    Toast.makeText(context, "lecture Permission refuser", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "lecture Permission annuler", Toast.LENGTH_LONG).show();
                }

                break;
        }

    }

}
