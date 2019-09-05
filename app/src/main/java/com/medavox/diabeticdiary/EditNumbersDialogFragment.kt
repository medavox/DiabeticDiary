package com.medavox.diabeticdiary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.edit_numbers_dialog.*

/**
 * @author Adam Howard
 * @since 24/06/2017
 */
class EditNumbersDialogFragment : DialogFragment() {
    private val TAG = "EditNumbersDialog"

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,
                             savedInstanceState:Bundle?):View? {
        val view = inflater.inflate(R.layout.edit_numbers_dialog, container)
        val context:Context =  if(activity  == null) {
            Log.e(TAG, "activity was null. Can't do anything!")
            return null
        } else {
            activity as Context
        }
        val sp = context.getSharedPreferences(MainActivity.SP_KEY, Context.MODE_PRIVATE)
        val emptyNumbers:MutableSet<String> = mutableSetOf()
        val sharedPrefsNumbers = sp?.getStringSet(MainActivity.SMS_RECIPIENTS_KEY, emptyNumbers)
        val numbersListCollection:MutableList<String> = mutableListOf()

        val numbersAdapter:ArrayAdapter<String> = ArrayAdapter(context,
                R.layout.edit_numbers_list_item, numbersListCollection)
        numbersAdapter.addAll(sharedPrefsNumbers)
        Log.i(TAG, "numbersAdapter length:" + numbersAdapter.getCount() +
                "; contents (including loaded SharedPrefs data):" + stringsOf(numbersAdapter))

        numbers_list.adapter = numbersAdapter

        numbers_list.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, _, pos, _ ->
            val a = (numbers_list.adapter as ArrayAdapter<String>)
            a.remove(a.getItem(pos))
            true
        }

        add_number_button.setOnClickListener {
            val editBoxContents = number_edit_box.text.toString()
            if (!isValidPhoneNumber(editBoxContents)) {
                Toast.makeText(context, "Entered text is not a valid British mobile phone number.",
                        Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val adapter = numbers_list.adapter as ArrayAdapter<String>
            adapter.add(editBoxContents)
            Log.i(TAG, "add pressed; adapter length:" + adapter.getCount() +
                    "; contents: " + stringsOf(adapter))
            //numbersAdapter.notifyDataSetChanged()
        }

        confirm_numbers.setOnClickListener {
                val editor = sp?.edit()
                //bizarrely, there doesn't seem to be a method in ArrayAdapters,
                // to get all the data out in one go. eg ArrayAdapter.getArray()
                val adapter = numbers_list.adapter as ArrayAdapter<String>
                val newData = HashSet<String>(adapter.count)
                val numbersAsString = StringBuilder()
                for (i in 0 until adapter.count) {
                    val s = adapter.getItem(i)
                    if (isValidPhoneNumber(s)) {
                        newData.add(s)
                        numbersAsString.append( adapter.getItem(i) + "; ")
                    }
                }
                editor?.putStringSet(MainActivity.SMS_RECIPIENTS_KEY, newData)
                editor?.apply()
                Toast.makeText(context, "Numbers saved.", Toast.LENGTH_SHORT).show()
                dismiss()

                Log.i(TAG, "Saved the following numbers to SharedPreferences:$numbersAsString")
        }

        return view
    }
}
