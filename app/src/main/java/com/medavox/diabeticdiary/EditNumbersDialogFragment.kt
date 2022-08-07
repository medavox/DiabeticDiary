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
import com.medavox.diabeticdiary.databinding.EditNumbersDialogBinding

/**
 * @author Adam Howard
 * @since 24/06/2017
 */
class EditNumbersDialogFragment : DialogFragment() {
    private val TAG = "EditNumbersDialog"

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,
                             savedInstanceState:Bundle?):View? {
        val binding = EditNumbersDialogBinding.inflate(layoutInflater)
        val view = binding.root
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
        sharedPrefsNumbers?.let { numbersAdapter.addAll(it) }
        Log.i(TAG, "numbersAdapter length:" + numbersAdapter.getCount() +
                "; contents (including loaded SharedPrefs data):" + stringsOf(numbersAdapter))

        binding.numbersList.adapter = numbersAdapter

        binding.numbersList.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, _, pos, _ ->
            val a = (binding.numbersList.adapter as ArrayAdapter<String>)
            a.remove(a.getItem(pos))
            true
        }

        binding.addNumberButton.setOnClickListener {
            val editBoxContents = binding.numberEditBox.text.toString()
            if (!isValidPhoneNumber(editBoxContents)) {
                Toast.makeText(context, "Entered text is not a valid British mobile phone number.",
                        Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val adapter = binding.numbersList.adapter as ArrayAdapter<String>
            adapter.add(editBoxContents)
            Log.i(TAG, "add pressed; adapter length:" + adapter.getCount() +
                    "; contents: " + stringsOf(adapter))
            //numbersAdapter.notifyDataSetChanged()
        }

        binding.confirmNumbers.setOnClickListener {
                val editor = sp?.edit()
                //bizarrely, there doesn't seem to be a method in ArrayAdapters,
                // to get all the data out in one go. eg ArrayAdapter.getArray()
                val adapter = binding.numbersList.adapter as ArrayAdapter<String>
                val newData = HashSet<String>(adapter.count)
                val numbersAsString = StringBuilder()
                for (i in 0 until adapter.count) {
                    adapter.getItem(i)?.let {
                        if (isValidPhoneNumber(it)) {
                            newData.add(it)
                            numbersAsString.append(adapter.getItem(i) + "; ")
                        }
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
