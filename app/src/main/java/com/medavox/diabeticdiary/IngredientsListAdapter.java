package com.medavox.diabeticdiary;

import android.app.Activity;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Adam Howard
 * @date 02/09/2017
 */

public class IngredientsListAdapter extends BaseAdapter implements Closeable {
    private List<CarbIngredient> items;
    private Activity activity;
    private static final String TAG = "IngredientsAdapter";

    public IngredientsListAdapter(Activity activity) {
        items = new LinkedList<>();
        this.activity = activity;
    }

    public void add(CarbIngredient ci) {
        items.add(ci);
        notifyDataSetChanged();
    }

    public void remove(int i) {
        items.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        //Log.i(TAG, "getCount() called, returning "+items.size());
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        //Log.i(TAG, "getItem("+i+") called, returning "+items.get(i)+")");
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        //Log.i(TAG, "getItemId("+i+") called, returning "+items.size()+")");
        return items.size();//I don't want to bother with this, but I'm also not sure if android requires
        //different return values for different values of i. So we just return i.
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        //Log.i(TAG, "getView("+i+", "+convertView+", "+parent+") called");
        if(i <  0 || i >= items.size()) {
            return null;
        }
        View view;
        if(convertView == null || !convertViewIsUsable(convertView)) {
            view = activity.getLayoutInflater().inflate(R.layout.ingredient_list_item, null);
        }
        else {
            view = convertView;
        }
        TextView display = (TextView)view.findViewById(R.id.carb_ingredient_item_text_view);
        Button removeButton = (Button)view.findViewById(R.id.remove_carb_ingredient_button);
        CarbIngredient ci = items.get(i);
        display.setText(ci.getGrams()+"g of food at "+ci.getPercentCarb()+"% carb = "+(((float)ci.getCPx1000())/1000)+" CP");
        //removeButton.setOnClickListener();
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                remove(i);//can't believe how easy it was getting the index of the view that had its remove button clicked
                //we just use the parameter of this enclosing method! brilliant!
            }});
        return view;
    }

    @Override
    public void close() throws IOException {
        items.clear();
        //items = null;
        notifyDataSetInvalidated();
    }
    /**checks whether the resuable view we get in getView,
     * actually has the appropriate subviews to be reusable*/
    private boolean convertViewIsUsable(View convertView) {
        return convertView.findViewById(R.id.remove_carb_ingredient_button) != null &&
                convertView.findViewById(R.id.carb_ingredient_item_text_view) != null;
    }
}
