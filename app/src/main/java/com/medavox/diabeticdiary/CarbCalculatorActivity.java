package com.medavox.diabeticdiary;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.medavox.util.validate.Validator;

import static com.medavox.diabeticdiary.EditNumbersDialogFragment.stringsOf;

public class CarbCalculatorActivity extends AppCompatActivity {
    private static final String TAG = "IngredientsCarbCalc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carb_calculator);

        final IngredientsListAdapter ingredientsAdapter = new IngredientsListAdapter(this);
        ListView listView = (ListView) findViewById(R.id.ingredients_list);
        Button addButton = (Button) findViewById(R.id.add_ingredient_button);

        listView.setAdapter(ingredientsAdapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                Log.i(TAG, "item at position "+position+" clicked; view:"+stringOf(view));
            }

        });*/

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
                Log.i(TAG, "item at position "+pos+" clicked; view:"+stringOf(view));
                //ArrayAdapter<String> a = ((ArrayAdapter<String>)listView.getAdapter());
                //a.remove(a.getItem(pos));
                return true;
            }
        });

        EditText gramsBox = (EditText) findViewById(R.id.ingredient_grams_edit_box);
        EditText carbPercentBox = (EditText) findViewById(R.id.ingredient_carb_percent_edit_box);
        final TextView carbReading = (TextView)findViewById(R.id.total_carb_reading);

        ingredientsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override public void onChanged() {
                super.onChanged();
                int sum = 0;
                for(int i = 0; i <  ingredientsAdapter.getCount(); i++) {
                    if(ingredientsAdapter.getItem(i) instanceof CarbIngredient) {
                        sum += ((CarbIngredient)ingredientsAdapter.getItem(i)).getCPx1000();
                    }
                    else {
                        Log.e(TAG, "item in adapter isn't the right class!" +
                                "\nExpected class: CarbIngredient; Actual Class: "
                                +ingredientsAdapter.getItem(i).getClass().getSimpleName());
                    }
                }
                float CPs = (float)sum / 1000.0f;
                carbReading.setText("Total: "+CPs+" CP");
            }
        });
        AddListener addListener = new AddListener(gramsBox, carbPercentBox, ingredientsAdapter);
        carbPercentBox.setOnEditorActionListener(addListener);
        addButton.setOnClickListener(addListener);

    }

    private class AddListener implements View.OnClickListener, TextView.OnEditorActionListener {
        private EditText gramsBox;
        private EditText carbPercentBox;
        private IngredientsListAdapter ingredientsAdapter;

        public AddListener(EditText gramsBox, EditText carbPercentBox,
                           IngredientsListAdapter ingredientsAdapter)  {
            this.gramsBox = gramsBox;
            this.carbPercentBox = carbPercentBox;
            this.ingredientsAdapter = ingredientsAdapter;
        }
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            Log.i(TAG, "editor action on "+textView+": "+actionId+" keyEvent: "+keyEvent);
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                onClick(textView);
                return true;
            }
            return false;
        }

        @Override public void onClick(View view) {
            String gramsString = gramsBox.getText().toString();
            String carbPercentString = carbPercentBox.getText().toString();
            int grams;
            int percent;
            try {
                grams = Integer.parseInt(gramsString);
                percent = Integer.parseInt(carbPercentString);

                Validator.check(percent >= 0, "Carb percentage must be above zero!");
                Validator.check(percent <= 100, "Carb percentage must be less than 100%!");
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
                Toast.makeText(CarbCalculatorActivity.this, errorMessage, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            gramsBox.setText("");
            carbPercentBox.setText("");
            ingredientsAdapter.add(new CarbIngredient(grams, percent));
            //listy.invalidate();
            gramsBox.requestFocus();
            Log.i(TAG, "add pressed; new adapter length:" + ingredientsAdapter.getCount() +
                    "; contents: " + stringsOf(ingredientsAdapter));
        }
    }

    private String stringOf(View view) {
        return ""+view.getClass().getSimpleName();
    }
}
