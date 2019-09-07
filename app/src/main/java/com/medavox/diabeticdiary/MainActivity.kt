package com.medavox.diabeticdiary

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

import com.medavox.diabeticdiary.db.EntryType
import com.medavox.diabeticdiary.db.SqliteWriter
import com.medavox.diabeticdiary.writers.CsvWriter
import com.medavox.diabeticdiary.writers.DataSink
import com.medavox.diabeticdiary.writers.SmsWriter
import com.medavox.util.io.DateTime

import java.util.regex.Pattern

import com.medavox.util.io.DateTime.DateFormat
import com.medavox.util.io.DateTime.TimeFormat
import kotlinx.android.synthetic.main.activity_main.*

//consider importing numberpicker module from https://github.com/SimonVT/android-numberpicker,
//then customising it to my needs
/*TODO:
    ask the user for confirmation before recording:
    * BG < 1.0
    * BG > 25.0
    * CP > 25
    * QA > 25
    * KT > 2
    * nonzero notes length < 3
* */
class MainActivity : AppCompatActivity() {

    private lateinit var inputs:Array<EditText>
    private lateinit var checkBoxes:Array<CheckBox>

    private val entryTypes:Array<EntryType> = arrayOf(EntryType.BloodGlucose,
            EntryType.CarbPortion, EntryType.QuickActing, EntryType.BackgroundInsulin,
            EntryType.Ketones, EntryType.Notes
    )

    /**Remembers the SMS message we want to send, when we need to ask permisson first*/
    @JvmField
    var pendingTextMessage:String? = null
    /**The moment in time that the entry has occurred*/

    private lateinit var smsWriter:SmsWriter
    private lateinit var outputs:Array<DataSink>

    companion object {
        private val TAG = "DiabeticDiary"
        const val INDEX_BG = 0
        const val INDEX_CP = 1
        const val INDEX_QA = 2
        const val INDEX_BI = 3
        const val INDEX_KT = 4
        const val INDEX_NOTES = 5

        const val SP_KEY = "Diabetic Diary SharedPreferences Key"
        const val ENTRIES_CACHE_KEY = "Diabetic Diary cached entries"
        const val SMS_RECIPIENTS_KEY = "Diabetic Diary entry SMS recipients"

        @JvmField
        var eventInstant:Long = 0L

        fun updateEntryTime(entryTimeButton:Button?) {
            if(entryTimeButton != null) {
                entryTimeButton.text = "At " + DateTime.get(eventInstant, TimeFormat.MINUTES,
                        DateFormat.BRIEF_WITH_DAY)
            }
            else {
                Log.e(TAG, "entry time (static) button was null during onResume!")
            }
        }
    }
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //initialise writer modules
        smsWriter = SmsWriter(this)
        outputs = arrayOf<DataSink>(CsvWriter(), smsWriter, SqliteWriter())

        inputs = arrayOf(BGinput, CPinput, QAinput, BIinput, KTinput, notesInput)
        checkBoxes = arrayOf(BGcheckBox, CPcheckBox, QAcheckBox, BIcheckBox, KTcheckBox,
                notesCheckbox)

        //add an anonymous TextWatcher for every input field
        for(i in inputs.indices) {
            val cb = checkBoxes[i]
            val index = i
            val recordButton:Button = record_button
            inputs[i].addTextChangedListener(object:TextWatcher {
                override fun beforeTextChanged(c:CharSequence,i:Int,j:Int,k:Int){}
                override fun onTextChanged(c:CharSequence,i:Int,j:Int,k:Int){}

                //tick the box when there's text in the input field, and untick it when there's not
                override fun afterTextChanged(editable:Editable) {
                    recordButton.isEnabled = true
                    if(editable.isEmpty() && cb.isChecked) {
                        cb.isChecked = false
                    }
                    else if(editable.isNotEmpty()) {
                        //disable RECORD button until all these are met:
                        //if not blank, then BG, CP, QA, BI and KT must be > 0
                        if(!cb.isChecked) {
                            cb.isChecked = true
                        }
                        if(index != INDEX_NOTES) {
                            try {
                                val numericValue:Float = editable.toString().toFloat()
                                if(numericValue <= 0.0001 ) {
                                    //recordButton.setEnabled(false)
                                    recordButton.isEnabled = false
                                }
                            }catch(nfe:NumberFormatException) {
                                Log.e(TAG, "this should never happen! exception:"+nfe.localizedMessage)
                            }
                        }
                    }

                    if(index == INDEX_BG || index == INDEX_KT) {
                        //only enable the record button
                        //if the BG and KT inputs have a decimal component
                        //(if non-empty)
                        // the string input follows the format x.y

                        val matches:Boolean = Pattern.compile("[0-9]{1,2}\\.[0-9]{1}").
                                matcher(editable.toString()).matches()
                        if(!matches && editable.isNotEmpty()) {
                            recordButton.isEnabled = false
                        }
                    }
                }
            })
        }

        //add the onClickListeners
        entry_time_button.setOnClickListener {
            val newFragment:DialogFragment = DateTimePickerFragment()
            newFragment.show(supportFragmentManager, "DateTimePicker")
        }

        reset_time_button.setOnClickListener {
            eventInstant = System.currentTimeMillis()
            updateEntryTime(entry_time_button)
        }

        record_button.setOnClickListener { _ ->
            val anyTicked = checkBoxes.any { it.isChecked }

            if(!anyTicked) {
                Toast.makeText(this, "No inputs ticked!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values:MutableMap<EntryType, String> =  mutableMapOf()
            for(i in inputs.indices) {
                if (checkBoxes[i].isChecked) {
                    values.put(entryTypes[i], inputs[i].getText().toString())
                }
            }

            val results:BooleanArray = outputs.map { it.write(this, eventInstant, values) }
                    .toTypedArray().toBooleanArray()
            //results[i] = outputs[i].write(this, eventInstant, values)

            //TODO:
            //first cache the entry in SharedPreferences before attempting any writes
            //check which if any datasinks failed to write last time, and get the entry(ies) that failed
            //make an extra write() call for the previously failed datasinks, along with this data
            //... call write() for all datasinks, on this data

            //for any that failed (returned false), write their toString() to the string array in SharedPrefs,
            //and a copy of the data

            //clear the UI fields, ready for another entry
            inputs.forEach {it.setText("") }
            checkBoxes.forEach { it.isChecked = false }

            //eventInstant++;//add 1ms to the event time, to prevent repeated entry times crashing sqlite
        }


        //for BG input, only allow 2 digits before the decimal place, and 1 after
        //Log.i(TAG, "existing filters: "+inputs[0].getFilters().length)
        inputs[INDEX_BG].filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2,1))
        //the same with CP
        inputs[INDEX_CP].filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2,1))

        //for QA, allow no digits after the decimal point.
        inputs[INDEX_QA].filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2,0))

        //with BI, allow 3 digit integers. I used to take ~80, so it's not impossible
        inputs[INDEX_BI].filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(3,0))

        //for KT, I'd be very worried if ketones were > 9.9, but again it's not impossible
        inputs[INDEX_KT].filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(2,1))
    }

    override fun onResume() {
        super.onResume()
        eventInstant = System.currentTimeMillis()
        //clear all fields on resume, to prepare the app for a fresh entry
        //clearInputs()
        inputs[INDEX_BG].requestFocus()
        updateEntryTime(entry_time_button)

    }

    override fun onCreateOptionsMenu(menu:Menu):Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item:MenuItem):Boolean {
        return when(item.itemId) {
            R.id.edit_numbers_menu_item -> {
                val newFragment:DialogFragment = EditNumbersDialogFragment()
                newFragment.show(supportFragmentManager, "EditNumbersDialog")
                true
            }
            R.id.review_entries_menu_item -> {
                startActivity(Intent(this, EntryReviewActivity::class.java))
                true
            }
            R.id.status_report_menu_item -> {
                startActivity(Intent(this, StatusReportActivity::class.java))
                true
            }
            R.id.edit_last_entry_menu_item -> {
                //todo
                Toast.makeText(this, "Not yet implemented, sorry", Toast.LENGTH_LONG).show()
                true
            }
            R.id.carb_calculator_menu_item -> {
                startActivity(Intent(this, CarbCalculatorActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //derived from stackoverflow.com/questions/5357455
    private class DecimalDigitsInputFilter(digitsBeforeZero:Int, digitsAfterZero:Int) : InputFilter {
        val mPattern:Pattern
        init {
            val pat:String= "[0-9]{0,$digitsBeforeZero}((\\.[0-9]{0,$digitsAfterZero})|(\\.)?)"
            mPattern=Pattern.compile(pat)
            //Log.i(TAG, "pattern:"+pat)
        }

        override fun filter(source:CharSequence, start:Int, end:Int, dest:Spanned, dstart:Int, dend:Int):CharSequence? {
            val matcher = mPattern.matcher(dest.toString()+source.toString())
            //Log.i(TAG, "dest:"+dest+"; dstart:"+dstart+"; dend:"+dend)
            //Log.i(TAG, "source:"+source+"; start:"+start+"; end:"+end)
            if(!matcher.matches()) {
                return ""
            }
            return null
        }
    }

    private fun matchesDecimalFormat(s:String, digitsBeforeZero:Int, digitsAfterZero:Int):Boolean {
        val pat:String= "[0-9]{0,$digitsBeforeZero}((\\.[0-9]{0,$digitsAfterZero})|(\\.)?)"
        return Pattern.compile(pat).matcher(s).matches()
    }


    //i really wish i could put this method in SmsWriter
    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>,
                                           grantResults:IntArray) {
        //if there are no permissions etc to process, return early.
//See https://developer.android.com/reference/android/support/v4/app/ActivityCompat.OnRequestPermissionsResultCallback.html#onRequestPermissionsResult%28int,%20java.lang.String[],%20int[]%29
        if(permissions.size != 1 || grantResults.size != 1) {
            Log.e(TAG, "Got weird permissions results. Permissions length:"+permissions.size+
            "; Grant results length: "+grantResults.size+";  permissions:"+ permissions.contentToString())
            return
        }

        Log.i(TAG, "permission \"" + permissions[0] + "\" result: " + grantResults[0])
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //if there was a message due to go out when we had to ask permission, send it now
            if(pendingTextMessage != null) {
                smsWriter.sendSms(pendingTextMessage)
                pendingTextMessage = null
            }
        }
        else {
            Toast.makeText(this, "Permission Refused!", Toast.LENGTH_SHORT).show()
            //permission refused
        }
    }
}
