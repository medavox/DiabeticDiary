package com.medavox.diabeticdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.medavox.util.validate.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.medavox.diabeticdiary.EditNumbersDialogFragment.stringsOf;

public class IngredientCarbCalculator extends AppCompatActivity {

    private static final String TAG = "IngredientsCarbCalc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_carb_calculator);

        final ArrayAdapter<CarbIngredient> ingredientsAdapter = new ArrayAdapter<>(this,
                R.layout.ingredient_list_item, new ArrayList<CarbIngredient>());
        Log.i(TAG, "ingredientsAdapter length:" + ingredientsAdapter.getCount() +
                "; contents (including loaded SharedPrefs data):" + stringsOf(ingredientsAdapter));

        final ListView listView = (ListView) findViewById(R.id.ingredients_list);
        Button addButton = (Button) findViewById(R.id.add_ingredient_button);

        listView.setAdapter(ingredientsAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowId) {

                ArrayAdapter<String> a = ((ArrayAdapter<String>)listView.getAdapter());
                a.remove(a.getItem(pos));
                return true;
            }
        });

        final EditText gramsBox = (EditText) findViewById(R.id.ingredient_grams_edit_box);
        final EditText carbPercentBox = (EditText) findViewById(R.id.ingredient_carb_precent_edit_box);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                String gramsString = gramsBox.getText().toString();
                String carbPercentString = carbPercentBox.getText().toString();
                int grams;
                int percent;
                try {
                    grams = Integer.parseInt(gramsString);
                    percent = Integer.parseInt(carbPercentString);

                    Validator.check(percent >= 0, "Carb percentage must be above zero!");
                    Validator.check(grams >= 0, "Ingredient weight must be above zero!");
                }
                catch (Exception nfe) {
                    String errorMessage;
                    if(nfe instanceof NumberFormatException) {
                        errorMessage = "Please enter valid numbers!";
                    }
                    else {
                        errorMessage = nfe.getMessage();
                    }
                    Toast.makeText(IngredientCarbCalculator.this, errorMessage, Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                ingredientsAdapter.add(new CarbIngredient(grams, percent));
                Log.i(TAG, "add pressed; new adapter length:" + ingredientsAdapter.getCount() +
                        "; contents: " + stringsOf(ingredientsAdapter));
                //numbersAdapter.notifyDataSetChanged();
            }
        });
    }
}
