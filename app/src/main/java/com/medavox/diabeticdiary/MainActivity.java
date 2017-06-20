package com.medavox.diabeticdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableLayout;
//todo: import numberpicker module from https://github.com/SimonVT/android-numberpicker,
//todo: then customise it to my needs

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //bg, cp, qa, bi, kt
        int[] inputIDs = new int[] {R.id.BGinput, R.id.CPinput, R.id.QAinput, R.id.BIinput,
                R.id.KTinput};

        EditText[] inputs = new EditText[inputIDs.length];
        for(int i = 0; i < inputs.length; i++) {
            inputs[i]  = (EditText) findViewById(inputIDs[i]);

        }

        //TableLayout


        //old
        /*
        //Initialise BG values
        NumeroPiquer majorBG = (NumeroPiquer)findViewById(R.id.majorBG);
        majorBG.setMaxValue(35);
        majorBG.setMinValue(1);
        majorBG.setValue(6);
        majorBG.setWrapSelectorWheel(false);

        NumeroPiquer minorBG = (NumeroPiquer)findViewById(R.id.minorBG);
        minorBG.setMaxValue(9);
        minorBG.setMinValue(0);

        //Initialise CP values

        NumberPicker majorCP = (NumberPicker)findViewById(R.id.majorCP);
        majorCP.setMinValue(0);
        majorCP.setMaxValue(100);
        majorCP.setValue(1);
        majorCP.setWrapSelectorWheel(false);

        NumberPicker minorCP = (NumberPicker)findViewById(R.id.minorCP);
        minorCP.setMinValue(0);
        minorCP.setMaxValue(9);*/

        //Initialise QA values
        /*NumberPicker QAunits = (NumberPicker)findViewById(R.id.QAunits);
        QAunits.setMinValue(0);
        QAunits.setMaxValue(80);
        QAunits.setWrapSelectorWheel(false);

        //Initialise BI values
        NumeroPiquer BIunits = (NumeroPiquer)findViewById(R.id.BIunits);
        BIunits.setMinValue(0);
        BIunits.setMaxValue(255);
        BIunits.setWrapSelectorWheel(false);

        //Initialise KT values
        NumeroPiquer majorKT = (NumeroPiquer)findViewById(R.id.majorKT);
        majorKT.setMaxValue(20);
        majorKT.setMinValue(0);
        majorKT.setValue(0);
        majorKT.setWrapSelectorWheel(false);

        NumeroPiquer minorKT = (NumeroPiquer)findViewById(R.id.minorKT);
        minorKT.setMinValue(0);
        minorKT.setMaxValue(9);
        */
    }
}
