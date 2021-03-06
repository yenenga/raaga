package mak.bf.myapplication.raaga.adaptateur;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.Query;

import java.util.ArrayList;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.autre.Poste;
import mak.bf.myapplication.raaga.autre.PosteViewHolder;
import mak.bf.myapplication.raaga.autre.PosteViewHolderHorizontal;

/**
 * Created by Matteo on 24/08/2015.
 */
public class AdapteurPosteHorizontalWithQuery extends FirebaseRecyclerAdapter<PosteViewHolderHorizontal, Poste> {

    private Context context;


    public AdapteurPosteHorizontalWithQuery(Query query, Class<Poste> itemClass, Context context, @Nullable ArrayList<Poste> items,
                                            @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.context = context;
    }

    @Override public PosteViewHolderHorizontal onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poste_view_holder_horizontal, parent, false);

        return new PosteViewHolderHorizontal(view , this.context);
    }




    @Override public void onBindViewHolder(PosteViewHolderHorizontal viewHolder, int position) {
        Poste model = getItem(position);

        viewHolder.setNom(model.getNom());
        viewHolder.setPrix(String.valueOf(model.getPrix()));
        viewHolder.setImagePoste(model.getListeImage().get(0), context);
        viewHolder.setId(model.getId());

    }

    @Override protected void itemAdded(Poste item, String key, int position) {
        Log.d("AdapteurPosteWithQuery", "Added a new item to the adapter.");
    }

    @Override protected void itemChanged(Poste oldItem, Poste newItem, String key, int position) {
        Log.d("AdapteurPosteWithQuery", "Changed an item.");
    }

    @Override protected void itemRemoved(Poste item, String key, int position) {
        Log.d("AdapteurPosteWithQuery", "Removed an item from the adapter.");
    }

    @Override protected void itemMoved(Poste item, String key, int oldPosition, int newPosition) {
        Log.d("AdapteurPosteWithQuery", "Moved an item.");
    }
}