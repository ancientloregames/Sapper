package com.ancientlore.sapper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
//import android.widget.EditText;

public class SettingsActivity extends Activity implements View.OnClickListener {
    GameManager _gm=GameManager.getInstance();
    //LevelManager _lm=LevelManager.getInstance();
    /*EditText gridWidth;
    EditText gridHeight;
    EditText minesCount;*/
    CheckBox debugCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button applyButton = (Button)findViewById(R.id.buttonApply);
        applyButton.setOnClickListener(this);
        /*gridWidth = (EditText)findViewById(R.id.editTextWidth);
        gridWidth.setText(""+_lm.getGridWidth());
        gridHeight = (EditText)findViewById(R.id.editTextHeight);
        gridHeight.setText(""+_lm.getGridHeight());*/
        debugCB=(CheckBox)findViewById(R.id.checkBoxDebugMode);
        debugCB.setChecked(_gm.isDebug());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonApply:
                /*int tmp_int=Integer.parseInt(gridWidth.getText().toString());
                if (tmp_int>15)tmp_int=15;
                _lm.setGridWidth(tmp_int);
                tmp_int=Integer.parseInt(gridHeight.getText().toString());
                if (tmp_int>15)tmp_int=20;
                _lm.setGridHeight(tmp_int);
                _lm.initialize();*/
                _gm.setDebug(debugCB.isChecked());
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
    }
}
