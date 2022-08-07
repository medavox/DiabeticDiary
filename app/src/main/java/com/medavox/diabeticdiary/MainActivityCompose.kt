package com.medavox.diabeticdiary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.DialogFragment
import com.medavox.diabeticdiary.carbcalculator.CarbCalculatorActivity
import com.medavox.diabeticdiary.statusreport.StatusReportActivity
import com.medavox.diabeticdiary.theme.DiabeticDiaryTheme

class MainActivityCompose: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DiabeticDiaryTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Root()
                }
            }
        }

    }

    @Composable
    fun Root() {
        
    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        return when(item.itemId) {
            R.id.edit_numbers_menu_item -> {
                val newFragment: DialogFragment = EditNumbersDialogFragment()
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
            R.id.carb_calculator_menu_item -> {
                startActivity(Intent(this, CarbCalculatorActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}