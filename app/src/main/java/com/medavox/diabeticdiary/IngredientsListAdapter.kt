package com.medavox.diabeticdiary

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import java.io.Closeable
import java.io.IOException
import kotlinx.android.synthetic.main.ingredient_list_item.view.*

/**
 * @author Adam Howard
 * @since 02/09/2017
 */

class IngredientsListAdapter(private val activity :Activity) : BaseAdapter(), Closeable {
    private val items:MutableList<CarbIngredient> = mutableListOf()
    private val TAG = "IngredientsAdapter"

    fun add(ci:CarbIngredient) {
        items.add(ci)
        notifyDataSetChanged()
    }

    fun remove(i:Int) {
        items.removeAt(i)
        notifyDataSetChanged()
    }

    override fun getCount():Int {
        //Log.i(TAG, "getCount() called, returning "+items.size())
        return items.size
    }

    override fun getItem(i:Int):Any {
        //Log.i(TAG, "getItem("+i+") called, returning "+items.get(i)+")")
        return items.get(i)
    }

    override fun getItemId(i:Int):Long {
        //Log.i(TAG, "getItemId("+i+") called, returning "+items.size()+")")
        return items.size.toLong()//I don't want to bother with this, but I'm also not sure if android requires
        //different return values for different values of i. So we just return i.
    }

    override fun getView(i:Int, convertView:View?, parent:ViewGroup ):View? {
        //Log.i(TAG, "getView("+i+", "+convertView+", "+parent+") called")
        if(i <  0 || i >= items.size) {
            return null
        }
        val view:View =
        if(convertView == null || !convertViewIsUsable(convertView)) {
            activity.layoutInflater.inflate(R.layout.ingredient_list_item, null)
        }
        else {
            convertView
        }

        val ci:CarbIngredient = items.get(i)
        //carb_ing
        view.carb_ingredient_item_text_view.text = ci.grams.toString()+"g of food at "+ci.percentCarb+
                "% carb = "+(ci.CPx1000.toFloat()/1000F).toString()+" CP"
        //removeButton.setOnClickListener()
        view.remove_carb_ingredient_button.setOnClickListener(View.OnClickListener() {
            @Override fun onClick(view:View) {
                remove(i);//can't believe how easy it was getting the index of the view that had its remove button clicked
                //we just use the parameter of this enclosing method! brilliant!
            }})
        return view
    }

    @Throws(IOException::class)
    override fun close()  {
        items.clear()
        //items = null
        notifyDataSetInvalidated()
    }
    /**checks whether the resuable view we get in getView,
     * actually has the appropriate subviews to be reusable*/
    private fun convertViewIsUsable(convertView:View):Boolean {
        return convertView.remove_carb_ingredient_button != null &&
                convertView.carb_ingredient_item_text_view != null
    }
}
