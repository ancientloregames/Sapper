package com.ancientlore.sapper;

class GameManager
{
	private static volatile GameManager instance = null;
	private boolean debug = false;
	private GameState state;

	private GameManager()
	{
	}

	static GameManager getInstance()
	{
		if (instance == null)
		{
			synchronized (GameManager.class)
			{
				if (instance == null)
				{
					instance = new GameManager();
				}
			}
		}
		return instance;
	}

	void initialise()
	{
		reset();
	}

	void reset()
	{
		state = GameState.PAUSE;
	}

	boolean isDebug()
	{
		return debug;
	}

	public GameState getState()
	{
		return state;
	}

	void setDebug(boolean value)
	{
		debug = value;
	}

	public void setState(GameState state)
	{
		this.state = state;
	}
}
