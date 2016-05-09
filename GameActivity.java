package com.ancientlore.sapper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;

public class GameActivity extends Activity {
    GameView gameView;
    GameManager _gm;
    InputManager _im;
    LevelManager _lm;
    SoundManager _sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        _gm = GameManager.getInstance();
        _lm = LevelManager.getInstance();
        _im = InputManager.getInstance();
        _sm = SoundManager.getInstance();

        gameView=new GameView(this,displaySize.x,displaySize.y);
        setContentView(gameView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameView.resume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        int x = (int) me.getX();
        int y = (int) me.getY();
        switch (me.getAction() & MotionEvent.ACTION_MASK) {
            default:break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (_im.getMenuButton().getRect().contains((int) me.getX(),(int) me.getY())){
                    Intent i=new Intent(this,MenuActivity.class);
                    startActivity(i);
                }
                else if (_im.getFlagButton().getRect().contains((int) me.getX(),(int) me.getY())){
                    _im.switchFlagButton();
                }
                else if (_gm.getState()==GameState.PLAYING)_im.handleInput(x,y,_gm,_lm,_sm);
                else if (_gm.getState()==GameState.PAUSE) _gm.setState(GameState.PLAYING);
                break;
        }
        return true;
    }
}
