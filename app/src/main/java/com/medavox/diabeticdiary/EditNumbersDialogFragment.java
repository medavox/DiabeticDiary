package com.medavox.diabeticdiary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Adam Howard
 * @date 24/06/2017
 */
public class EditNumbersDialogFragment extends DialogFragment {
    private static final String TAG = "EditNumbersDialog";

    public static String stringsOf(Adapter adapter) {
        String s = "[ ";
        for(int i = 0; i < adapter.getCount(); i++) {
            s += adapter.getItem(i).toString()+"; ";
        }
        return s+" ]";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_numbers_dialog, container);
        final SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.SP_KEY, Context.MODE_PRIVATE);
        Set<String> emptyNumbers = new HashSet<>(0);
        Set<String> sharedPrefsNumbers = sp.getStringSet(MainActivity.SMS_RECIPIENTS_KEY, emptyNumbers);
        List<String> numbersListCollection = new ArrayList<>();

        ArrayAdapter<String> numbersAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.edit_numbers_list_item, numbersListCollection);
        numbersAdapter.addAll(sharedPrefsNumbers);
        Log.i(TAG, "numbersAdapter length:" + numbersAdapter.getCount() +
                "; contents (including loaded SharedPrefs data):" + stringsOf(numbersAdapter));

        final ListView listView = (ListView) view.findViewById(R.id.numbers_list);
        Button addButton = (Button) view.findViewById(R.id.add_number_button);
        Button saveButton = (Button) view.findViewById(R.id.confirm_numbers);
        final EditText editBox = (EditText) view.findViewById(R.id.number_edit_box);

        listView.setAdapter(numbersAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowId) {

                ArrayAdapter<String> a = ((ArrayAdapter<String>)listView.getAdapter());
                a.remove(a.getItem(pos));
                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                //this is terrible, todo: fix it
                String editBoxContents = editBox.getText().toString();
                if (!MainActivity.isValidPhoneNumber(editBoxContents)) {
                    Toast.makeText(getActivity(), "Entered text is not a valid British mobile phone number.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                adapter.add(editBoxContents);
                Log.i(TAG, "add pressed; new adapter length:" + adapter.getCount() +
                        "; contents: " + stringsOf(adapter));
                //numbersAdapter.notifyDataSetChanged();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                //bizarrely, there doesn't seem to be a method in ArrayAdapters,
                // to get all the data out in one go. eg ArrayAdapter.getArray()
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                Set<String> newData = new HashSet<String>(adapter.getCount());
                String numbersAsString = "";
                for (int i = 0; i < adapter.getCount(); i++) {
                    String s = adapter.getItem(i);
                    if (MainActivity.isValidPhoneNumber(s)) {
                        newData.add(s);
                        numbersAsString += adapter.getItem(i) + "; ";
                    }
                }
                editor.putStringSet(MainActivity.SMS_RECIPIENTS_KEY, newData);
                editor.apply();
                Toast.makeText(getActivity(), "Numbers saved.", Toast.LENGTH_SHORT).show();
                dismiss();

                Log.i(TAG, "Saved the following numbers to SharedPreferences:" + numbersAsString);
            }
        });

        return view;
    }
}
