package com.ancientlore.sapper;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

class MenuActivity extends Activity implements View.OnClickListener{
    GameManager gm=GameManager.getInstance();
    LevelManager lm=LevelManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Button resumeButton=(Button)findViewById(R.id.buttonResume);
        resumeButton.setOnClickListener(this);
        final Button newGameButton=(Button)findViewById(R.id.buttonNewGame);
        newGameButton.setOnClickListener(this);
        final Button settingsButton=(Button)findViewById(R.id.buttonSettings);
        settingsButton.setOnClickListener(this);
        final Button tutorialButton=(Button)findViewById(R.id.buttonTutorial);
        tutorialButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonResume:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.buttonNewGame:
                gm.reset();
                lm.reset();
                break;
            case R.id.buttonSettings:
                Intent i=new Intent(this,SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.buttonTutorial:
                gm.setState(GameState.PAUSE);
                break;
        }
    }
}
