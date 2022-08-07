package com.medavox.diabeticdiary.carbcalculator

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.medavox.diabeticdiary.R
import com.medavox.diabeticdiary.databinding.IngredientListItemBinding

import java.io.Closeable
import java.io.IOException

/**
 * @author Adam Howard
 * @since 02/09/2017
 */

class IngredientsListAdapter(private val activity :Activity) : BaseAdapter(), Closeable {
    val items:MutableList<CarbIngredient> = mutableListOf()
    private val TAG = "IngredientsAdapter"

    fun add(ci:CarbIngredient) {
        items.add(ci)
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
        val layoutInflater:LayoutInflater = if(convertView != null) LayoutInflater.from(convertView.context) else activity.layoutInflater
        val view:View = convertView ?: layoutInflater.inflate(R.layout.ingredient_list_item, null)
        val binding = IngredientListItemBinding.inflate(layoutInflater)
        val ci:CarbIngredient = items.get(i)
        //carb_ing
        binding.carbIngredientItemTextView.text = ci.grams.toString()+"g of food at "+ci.percentCarb+
                "% carb = "+(ci.CPx1000.toFloat()/1000F).toString()+" CP"
        //can't believe how easy it was getting the index of the view,
        // that had its remove button clicked
        //we just use the parameter of this enclosing method! brilliant!
        binding.removeCarbIngredientButton.setOnClickListener {
            items.removeAt(i)
            notifyDataSetChanged()
        }
        return view
    }

    @Throws(IOException::class)
    override fun close()  {
        items.clear()
        //items = null
        notifyDataSetInvalidated()
    }
}
