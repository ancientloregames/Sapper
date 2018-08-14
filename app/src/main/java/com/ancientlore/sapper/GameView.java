package com.ancientlore.sapper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


class GameView extends SurfaceView implements Runnable
{
	Thread thread = null;
	private volatile boolean running;
	//private Context context;
	private int displayX, displayY;
	private int upperPanelY;
	private SurfaceHolder surfaceHolder;
	private Canvas canvas;
	private Paint paint;
	private Paint paintInfo;
	private Paint paintTip;
	private Paint paintState;

	GameManager _gm;
	InputManager _im;
	LevelManager _lm;
	SoundManager _sm;

	public GameView(final Context context)
	{
		super(context);
	}

	public GameView(final Context context, final int displayX, final int displayY)
	{
		super(context);
		//this.context=context;
		this.displayX = displayX;
		this.displayY = displayY;
		upperPanelY = displayY / 12;

		surfaceHolder = getHolder();
		paint = new Paint();
		paint.setTextSize(displayX / 20);
		paintInfo = new Paint();
		paintInfo.setTextSize(displayX / 20);
		paintInfo.setTextAlign(Paint.Align.CENTER);
		paintInfo.setColor(Color.WHITE);
		paintTip = new Paint();
		paintTip.setTextSize(displayX / 26);
		paintTip.setColor(Color.WHITE);
		paintTip.setTextAlign(Paint.Align.CENTER);
		paintState = new Paint();
		paintState.setTextSize(displayX / 9);
		paintState.setColor(Color.WHITE);
		paintState.setTextAlign(Paint.Align.CENTER);

		_gm = GameManager.getInstance();
		_gm.initialise();
		_lm = LevelManager.getInstance();
		_lm.initialize(context, displayX, displayY, upperPanelY);
		_im = InputManager.getInstance();
		_im.initialise(context, displayX, upperPanelY);
		_sm = SoundManager.getInstance();
		_sm.initialise(context);

        /*_gm.setBestScore(MainActivity.prefs.getInt("HiScore", 0));
        MainActivity.editor.apply();*/
	}

	private void draw()
	{
		if (surfaceHolder.getSurface().isValid())
		{
			canvas = surfaceHolder.lockCanvas();
			//-----------------
			canvas.drawColor(Color.BLACK);

			paint.setColor(Color.BLACK);
			canvas.drawRect(0, 0, displayX, upperPanelY, paint);
			paint.setColor(Color.WHITE);
			canvas.drawText(getResources().getString(R.string.ingame_flag) + ": " + _lm.getFlagsCount(), 20,
					upperPanelY / 2 + paint.getTextSize() / 2, paint);
			if (_gm.isDebug())
			{
				canvas.drawText(getResources().getString(R.string.ingame_mines) + ": " + _lm.getMinesCount(), paint.getTextSize() * 5 + 20,
						upperPanelY / 2 + paint.getTextSize() / 2, paint);
			}
			canvas.drawBitmap(_im.getFlagButton().getBitmap(),
					_im.getFlagButton().getRect().left, _im.getFlagButton().getRect().top, paint);
			canvas.drawBitmap(_im.getMenuButton().getBitmap(),
					_im.getMenuButton().getRect().left, _im.getMenuButton().getRect().top, paint);

			paint.setColor(Color.GRAY);
			canvas.drawRect(_lm.getGameField(), paint);

			for (int i = 1; i < _lm.getGridHeight() + 1; i++)
			{
				for (int j = 1; j < _lm.getGridWidth() + 1; j++)
				{
					if (_lm.getTile(i, j).getState() == GOState.CLOSED ||
							_lm.getTile(i, j).getState() == GOState.FLAGGED)
					{
						canvas.drawBitmap(_lm.getSprite(_lm.getTile(i, j).getIndex()).bitmap,
								_lm.getTile(i, j).getRect().left,
								_lm.getTile(i, j).getRect().top, paint);
						if (_lm.getTile(i, j).getState() == GOState.FLAGGED)
						{
							canvas.drawBitmap(_lm.getSprite(_lm.bitmapIndex("flag")).bitmap,
									_lm.getTile(i, j).getRect().left,
									_lm.getTile(i, j).getRect().top, paint);
						}
					}
					else if (_lm.getTile(i, j).getMinesAround() != 0)
					{
						canvas.drawText("" + _lm.getTile(i, j).getMinesAround(),
								_lm.getTile(i, j).getRect().centerX(),
								_lm.getTile(i, j).getRect().centerY() - 10, paintInfo);
					}
				}
			}
			if (_gm.isDebug() || _gm.getState() == GameState.LOSE)
			{
				if (_gm.getState() == GameState.LOSE)
				{
					paint.setColor(Color.RED);
					canvas.drawRect(_lm.getTile(_im.getSelectY(), _im.getSelectX()).getRect(), paint);
				}
				for (GameObject obj : _lm.getMines())
				{
					canvas.drawBitmap(_lm.getSprite(_lm.bitmapIndex("mine")).bitmap,
							obj.getRect().left,
							obj.getRect().top, paint);
				}
				if (_gm.isDebug())
				{
					for (int i = 1; i < _lm.getGridHeight() + 1; i++)
					{
						for (int j = 1; j < _lm.getGridWidth() + 1; j++)
						{
							canvas.drawText("" + _lm.m[i][j],
									_lm.getTile(i, j).getRect().centerX(),
									_lm.getTile(i, j).getRect().centerY() + paintInfo.getTextSize(), paintInfo);
						}
					}
				}
			}

			if (_gm.getState() == GameState.PAUSE)
			{
				canvas.drawColor(Color.argb(200, 0, 0, 0));
				canvas.drawText(getResources().getString(R.string.ingame_tut_pause), displayX / 2, displayY / 2 + upperPanelY, paintState);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_1),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 5, paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_2),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 5 + paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_3),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 10 + 2 * paintTip.getTextSize(), paintTip);
				canvas.drawText(getResources().getString(R.string.ingame_tut_line_end),
						displayX / 2, displayY / 2 + upperPanelY + paintState.getTextSize() + 15 + 3 * paintTip.getTextSize(), paintTip);
			}

			//-----------------
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public void run()
	{
		while (running)
		{
			if (_gm.getState() == GameState.PLAYING)
			{
				_lm.update(_gm, _sm);
			}
			draw();
			try
			{
				Thread.sleep(51);//17 = 1000(milliseconds)/60(FPS)
			}
			catch (InterruptedException e)
			{
				Log.e("Error", "Can't sleep on run()");
			}
		}
	}

	protected void pause()
	{
		running = false;
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			Log.e("Error", "Can't pause");
		}
	}

	protected void resume()
	{
		running = true;
		thread = new Thread(this);
		thread.start();
	}
}
