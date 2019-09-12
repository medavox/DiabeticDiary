package com.medavox.diabeticdiary.carbcalculator

import android.database.DataSetObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.medavox.diabeticdiary.R
import com.medavox.diabeticdiary.stringsOf

import com.medavox.util.validate.Validator

import kotlinx.android.synthetic.main.activity_carb_calculator.*
//TODO:
// focus the first text input box in onResume()
// expand the text input fields to fill the rest of the row not occupied by the text
// support optional decimal percentages (1dp only), eg milk is 4.7%, not 5%
// button to add the calculated carb to a new CP entry in the main activity
// be able to resume previous calculation
class CarbCalculatorActivity : AppCompatActivity() {
    private val TAG = "CarbCalculator"

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carb_calculator)

        val ingredientsAdapter = IngredientsListAdapter(this)
        //Button addButton = (Button) findViewById(R.id.)

        ingredients_list.setAdapter(ingredientsAdapter)
        /*listView.setOnItemClickListener(AdapterView.OnItemClickListener() {
            override fun onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                Log.i(TAG, "item at position "+position+" clicked; view:"+stringOf(view))
            }

        });*/

        ingredients_list.onItemLongClickListener = object:AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(adapterView:AdapterView<*>?, view:View?, pos:Int, rowId:Long):Boolean {
                Log.i(TAG, "item at position "+pos+" clicked; view:"+stringOf(view))
                //ArrayAdapter<String> a = ((ArrayAdapter<String>)listView.getAdapter())
                //a.remove(a.getItem(pos))
                return true
            }
        }

        ingredientsAdapter.registerDataSetObserver(object:DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                var sum = 0
                for(i in 0 until ingredientsAdapter.count) {
                    if(ingredientsAdapter.getItem(i) is CarbIngredient) {
                        sum += (ingredientsAdapter.getItem(i) as CarbIngredient).CPx1000
                    }
                    else {
                        Log.e(TAG, "item in adapter isn't the right class!" +
                                "\nExpected class: CarbIngredient; Actual Class: "
                                +ingredientsAdapter.getItem(i)::class.java.simpleName)
                    }
                }
                total_carb_reading.text = "Total: ${sum / 1000.0F} CP"
            }
        })
        val addListener = AddListener(ingredient_grams_edit_box, ingredient_carb_percent_edit_box, ingredientsAdapter)
        ingredient_carb_percent_edit_box.setOnEditorActionListener(addListener)
        add_ingredient_button.setOnClickListener(addListener)

    }

    private inner class AddListener(private val gramsBox:EditText,
                                    private val carbPercentBox:EditText,
                                    private val ingredientsAdapter:IngredientsListAdapter
                                    ) : View.OnClickListener, TextView.OnEditorActionListener {
        override fun onEditorAction(textView:TextView?, actionId:Int, keyEvent:KeyEvent?):Boolean {
            Log.i(TAG, "editor action on $textView: $actionId keyEvent: $keyEvent")
            return if(actionId == EditorInfo.IME_ACTION_DONE) {
                onClick(textView)
                true
            } else false
        }

        override fun onClick(view:View?) {
            val gramsString = gramsBox.text.toString()
            val carbPercentString = carbPercentBox.text.toString()
            try {
                val grames = Integer.parseInt(gramsString)
                val percente = Integer.parseInt(carbPercentString)

                Validator.check(percente >= 0, "Carb percentage must be above zero!")
                Validator.check(percente <= 100, "Carb percentage must be less than 100%!")
                Validator.check(grames >= 0, "Ingredient weight must be above zero!")
                gramsBox.setText("")
                carbPercentBox.setText("")
                ingredientsAdapter.add(CarbIngredient(grames, percente))
                //listy.invalidate()
                gramsBox.requestFocus()
                Log.i(TAG, "add pressed; adapter length:" + ingredientsAdapter.count +
                        "; contents: " + stringsOf(ingredientsAdapter))
            }
            catch (nfe:NumberFormatException) {
                Toast.makeText(this@CarbCalculatorActivity, "Please enter valid numbers!",
                        Toast.LENGTH_LONG)
                        .show()
            }
            catch (nfe:Exception) {
                Toast.makeText(this@CarbCalculatorActivity, nfe.message, Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    private fun stringOf(view:View?):String {
        return if(view == null) "<null>" else view.javaClass.simpleName ?: "<unknown>"
    }
}