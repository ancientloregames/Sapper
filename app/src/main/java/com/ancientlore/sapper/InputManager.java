package com.ancientlore.sapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;


class InputManager
{
	private static volatile InputManager instance = null;
	private GameButton flagButton;
	private GameButton menuButton;
	private Bitmap flagBitmapActive;
	private Bitmap flagBitmapInactive;
	private boolean flagButtonActive;
	private int selectX;
	private int selectY;

	private InputManager()
	{
	}

	static InputManager getInstance()
	{
		if (instance == null)
		{
			synchronized (InputManager.class)
			{
				if (instance == null)
				{
					instance = new InputManager();
				}
			}
		}
		return instance;
	}

	void initialise(Context context, final int maxX, final int maxY)
	{
		flagButtonActive = false;
		final int padding = maxY / 10;

		Bitmap menuBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.button_menu);
		int ratio = menuBitmap.getWidth() / menuBitmap.getHeight();
		menuBitmap = Bitmap.createScaledBitmap(menuBitmap, maxX / 10 * ratio,
				(maxY - 2 * padding), false);
		menuButton = new GameButton("Menu", new Rect(maxX - menuBitmap.getWidth() - padding,
				padding, maxX - padding, menuBitmap.getHeight() + padding), menuBitmap);

		flagBitmapActive = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.supper_button_flag_active);
		flagBitmapActive = Bitmap.createScaledBitmap(flagBitmapActive, maxX / 10 * ratio,
				(maxY - 2 * padding), false);
		flagBitmapInactive = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.supper_button_flag_unactive);
		flagBitmapInactive = Bitmap.createScaledBitmap(flagBitmapInactive, maxX / 10 * ratio,
				(maxY - 2 * padding), false);
		flagButton = new GameButton("Flag", new Rect(
				menuButton.getRect().left - flagBitmapInactive.getWidth() - padding,
				padding, menuButton.getRect().left - padding, menuBitmap.getHeight() + padding), flagBitmapInactive);
	}

	void handleInput(final int x, final int y, GameManager gm, LevelManager lm, SoundManager sm)
	{
		selectY = (y - lm.getGameField().top) / lm.getTileSize() + 1;
		selectX = (x - lm.getGameField().left) / lm.getTileSize() + 1;
		if (flagButtonActive)
		{
			lm.setFlag(selectY, selectX);
		}
		else if (lm.getTile(selectY, selectX).isMined() &&
				lm.getTile(selectY, selectX).getState() != GOState.FLAGGED)
		{
			gm.setState(GameState.LOSE);
			sm.playSound("lose");
		}
		else if (lm.getTile(selectY, selectX).getState() == GOState.CLOSED)
		{
			lm.openTile(selectY, selectX);
		}
	}

	public int getSelectY()
	{
		return selectY;
	}

	public int getSelectX()
	{
		return selectX;
	}

	public GameButton getFlagButton()
	{
		return flagButton;
	}

	public GameButton getMenuButton()
	{
		return menuButton;
	}

	public void switchFlagButton()
	{
		if (flagButtonActive)
		{
			flagButton.setBitmap(flagBitmapInactive);
		}
		else
		{
			flagButton.setBitmap(flagBitmapActive);
		}
		flagButtonActive = !flagButtonActive;
	}
}
