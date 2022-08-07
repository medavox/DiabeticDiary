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
import com.medavox.diabeticdiary.databinding.ActivityCarbCalculatorBinding
import com.medavox.diabeticdiary.stringsOf

import com.medavox.util.validate.Validator

//TODO:
// expand the text input fields to fill the rest of the row not occupied by the text
// support optional decimal percentages (1dp only), eg milk is 4.7%, not 5%
// button to add the calculated carb to a new CP entry in the main activity
// be able to resume previous calculation
// reverse CP calculator: in order to have X CP of food at Y % carb, how many grams should I eat?
class CarbCalculatorActivity : AppCompatActivity() {
    private val TAG = "CarbCalculator"
    private lateinit var binding: ActivityCarbCalculatorBinding

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarbCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ingredientsAdapter = IngredientsListAdapter(this)

        binding.ingredientsList.setAdapter(ingredientsAdapter)
        /*listView.setOnItemClickListener(AdapterView.OnItemClickListener() {
            override fun onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                Log.i(TAG, "item at position "+position+" clicked; view:"+stringOf(view))
            }

        });*/

        binding.ingredientsList.onItemLongClickListener = object:AdapterView.OnItemLongClickListener {
            override fun onItemLongClick(adapterView:AdapterView<*>?, view:View?, pos:Int, rowId:Long):Boolean {
                Log.i(TAG, "item at position "+pos+" clicked; view:"+stringOf(view))
                return true
            }
        }

        //it's a shame we can't convert this into a lambda
        ingredientsAdapter.registerDataSetObserver(object:DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                val sum = ingredientsAdapter.items.fold(0F) {
                    acc:Float, elem:CarbIngredient -> acc + elem.CPx1000
                }
                binding.totalCarbReading.text = "Total: ${sum / 1000.0F} CP"
            }
        })
        val addListener = AddListener(binding.ingredientGramsEditBox, binding.ingredientCarbPercentEditBox, ingredientsAdapter)
        binding.ingredientCarbPercentEditBox.setOnEditorActionListener(addListener)
        binding.addIngredientButton.setOnClickListener(addListener)

    }

    override fun onResume() {
        super.onResume()
        binding.ingredientGramsEditBox.requestFocus()
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
                val percente = carbPercentString.toFloat()

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
