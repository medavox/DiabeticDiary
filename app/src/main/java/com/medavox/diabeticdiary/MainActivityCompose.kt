package com.medavox.diabeticdiary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.medavox.diabeticdiary.carbcalculator.CarbCalculatorActivity
import com.medavox.diabeticdiary.db.EntryType
import com.medavox.diabeticdiary.statusreport.StatusReportActivity
import com.medavox.diabeticdiary.theme.DiabeticDiaryTheme

class MainActivityCompose: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DiabeticDiaryTheme {
                // A surface container using the 'background' color from the theme
                Surface {
/*                    val scaffoldState = rememberScaffoldState()
                    val scope = rememberCoroutineScope()
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            TopAppBar(
                                title = { Text(stringResource(id = R.string.app_name)) }
                            )
                        },
                        content = { innerPadding ->
                            LazyColumn(contentPadding = innerPadding) {
                                items(count = 100) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    )
                                }
                            }
                        }
                    )*/
                    Root()
                }
            }
        }

    }
private val aboveButton = listOf(
    EntryType.BloodGlucose,
    EntryType.CarbPortion,
    EntryType.QuickActing,
    EntryType.BackgroundInsulin,
    EntryType.Ketones
)
    @Composable
    fun Root() {
        Column {
            TimeControlButtonsRow()
            for (e in aboveButton) {
                EntryInputRow(label = e.naam)
            }
            Button(onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.recordButtonLabel))
            }
            EntryInputRow(label = EntryType.Notes.naam, hint = "70 Chars max")
        }
    }

    @Composable
    fun TimeControlButtonsRow() {
        Row (modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f).padding(end = 8.dp)) {

            }
            Button(onClick = { /*TODO*/ }) {
                Text("Reset")
            }
        }
    }
    
    @Composable
    fun EntryInputRow(label: String, hint:String = "") {
        Row (modifier = Modifier.height(40.dp)) {
            Checkbox(checked = false, onCheckedChange ={/*todo*/} )
            Text(text = label, modifier = Modifier.align(Alignment.CenterVertically))
            TextField(value = "", onValueChange = {/*todo*/},label = { Text(text = hint)})
        }
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