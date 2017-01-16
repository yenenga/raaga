package mak.bf.myapplication.raaga.adaptateur;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.Query;

import java.util.ArrayList;

import mak.bf.myapplication.R;
import mak.bf.myapplication.raaga.autre.Store;
import mak.bf.myapplication.raaga.autre.StoreViewHolderVertical;

/**
 * Created by Matteo on 24/08/2015.
 */
public class AdapteurStoreWithQuery extends FirebaseRecyclerAdapter<StoreViewHolderVertical, Store> {

    private Context context;


    public AdapteurStoreWithQuery(Query query, Class<Store> itemClass, Context context, @Nullable ArrayList<Store> items,
                                  @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.context = context;
    }

    @Override public StoreViewHolderVertical onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_view_holder, parent, false);

        return new StoreViewHolderVertical(view , this.context);
    }

    @Override
    public void onBindViewHolder(StoreViewHolderVertical viewHolder, int position) {
        Store model = getItem(position);

        viewHolder.setNom(model.getNomStore());
        viewHolder.setImagePoste(model.getListeImage().get(0), context);
        viewHolder.setId(model.getIdStore());
        viewHolder.setDescription(model.getDescriptionStore());
        viewHolder.setType(model.getTypeStore());
        viewHolder.setStore(model);

        //gestion du clic du poste
    }

    @Override
    protected void itemAdded(Store item, String key, int position) {

    }

    @Override
    protected void itemChanged(Store oldItem, Store newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Store item, String key, int position) {

    }

    @Override
    protected void itemMoved(Store item, String key, int oldPosition, int newPosition) {

    }

}