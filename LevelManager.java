package com.ancientlore.sapper;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

class LevelManager {
    private static volatile LevelManager instance=null;
    private Rect gameField;
    private int gridWidth=8;
    private int gridHeight;
    private int tileSize;
    private GameSprite[] sprites;
    private GameObject[][] grid;
    private ArrayList<GameObject> mines;

    private int minesCount;
    private int flagsCount;
    int[][] m;

    private LevelManager(){}
    static LevelManager getInstance(){
        if (instance==null) {
            synchronized (LevelManager.class) {
                if (instance == null)
                    instance = new LevelManager();
            }
        }
        return instance;
    }

    void initialize(final Context context, final int displayX, final int displayY,final int upperPanelY) {
        tileSize = displayX/gridWidth;
        gridHeight = (displayY-upperPanelY)/tileSize;
        final int paddingX=(displayX-(gridWidth*tileSize))/2;
        final int paddingY=(displayY-upperPanelY-(gridHeight*tileSize))/2;
        gameField = new Rect(paddingX,upperPanelY+paddingY,
                paddingX+tileSize*gridWidth,upperPanelY+paddingY+gridHeight*tileSize);

        sprites = loadBitmaps(R.drawable.class,context);
        for (GameSprite sprite:sprites)
            sprite.bitmap = Bitmap.createScaledBitmap(sprite.bitmap, tileSize,tileSize, false);

        reset();

    }
    public void reset(){
        grid=new GameObject[gridHeight+2][gridWidth+2];
        for (int i=0;i<gridHeight+2;i++){
            for (int j=0;j<gridWidth+2;j++){
                grid[i][j] = new GameObject(j,i,new Rect(
                        gameField.left+(j-1)*tileSize, gameField.top+(i-1)*tileSize,
                        gameField.left+j*tileSize, gameField.top+i*tileSize));
                grid[i][j].setIndex(bitmapIndex("tile"));
            }
        }

        minesCount=gridHeight*gridWidth/2;
        flagsCount=minesCount;
        mines=new ArrayList<>();
        generateMineField();
        setMineCounters();

        m=new int[gridHeight+2][gridWidth+2];
        for (int i=0;i<gridHeight+2;i++)
            for (int j=0;j<gridWidth+2;j++) {
                if (i == 0 || j == 0 || i == gridHeight+1 || j == gridWidth+1) {
                    grid[i][j].setMinesAround(-1);
                    m[i][j] = -1;
                }
                else
                    m[i][j] = -2;
            }
        for (GameObject obj:mines)
            m[obj.getY()][obj.getX()]=-1;
    }

    private void setMineCounters(){
        int[] dx={-1,0,1,1,1,0,-1,-1};
        int[] dy={-1,-1,-1,0,1,1,1,0};
        for (GameObject obj:mines){
            for (int i=0;i<8;i++){
                if (!grid[obj.getY()+dy[i]][obj.getX()+dx[i]].isMined())
                    grid[obj.getY()+dy[i]][obj.getX()+dx[i]].addMinesAround();
            }
        }
    }

    private void generateMineField(){
        Random rand=new Random();
        int i,j;
        for (int k=0;k<minesCount;k++){
            do{
                i = rand.nextInt(gridHeight)+1;
                try {
                    Thread.sleep(17);//17 = 1000(milliseconds)/60(FPS)
                }catch(InterruptedException e){
                    Log.e("Error","Can't sleep on generateMineField()");}
                j = rand.nextInt(gridWidth)+1;
                if (!grid[i][j].isMined()) {
                    grid[i][j].setMined(true);
                    grid[i][j].setMinesAround(-1);
                    mines.add(grid[i][j]);
                    break;
                }
            }while (true);
        }
    }

    void update(GameManager gm, SoundManager sm){
        if (minesCount==0) {
            gm.setState(GameState.WIN);
            sm.playSound("win");

        }
    }

    void openTile(int y,int x){
        boolean stop;
        int[] dx1={-1,0,1,1,1,0,-1,-1};
        int[] dy1={-1,-1,-1,0,1,1,1,0};
        int[] dx2 = {1,0,-1,0};
        int[] dy2 = {0,1,0,-1};
        m[y][x]=0;
        int d=0;
        do{
            stop=true;
            for(int i = 1; i<gridHeight+1;++i)
                for(int j = 1; j<gridWidth+1;++j)
                    if (m[i][j]==d) {
                        if (grid[i][j].getMinesAround()>0) {
                            for (int k = 0; k < 4; ++k)
                                if (m[i + dx2[k]][j + dy2[k]] == -2) {
                                    stop = false;
                                    m[i + dx2[k]][j + dy2[k]] = d + 1;
                                }
                        }else {
                            for (int k = 0; k < 8; ++k)
                                if (m[i + dx1[k]][j + dy1[k]] == -2) {
                                    stop = false;
                                    m[i + dx1[k]][j + dy1[k]] = d + 1;
                                }
                        }
                    }
            d++;
        }while (!stop);
        for(int i = 1; i<gridHeight+1;++i)
            for(int j = 1; j<gridWidth+1;++j){
                if (m[i][j]>=0){
                    grid[i][j].setState(GOState.OPENED);
                    m[i][j]=-1;
                }
            }
    }

    private GameSprite[] loadBitmaps(Class<?> aClass, Context context)
            throws IllegalArgumentException{
        Field[] fields = aClass.getFields();

        ArrayList<GameSprite> res = new ArrayList<>();
        try {
            for(Field field:fields){
                if (field.getName().contains("sapper_")) {
                    res.add(new GameSprite(
                            BitmapFactory.decodeResource(context.getResources(),
                                    field.getInt(null)), field.getName()));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
            /* Exception will only occur on bad class submitted. */
        }
        return res.toArray(new GameSprite[res.size()]);
    }
    int bitmapIndex(String name){
        name = name.replace(' ','_');
        for (int i=0;i<sprites.length;i++)
            if (sprites[i].name.contains(name))
                return i;
        return -1;
    }


    /*public void setGridWidth(int gridWidth) {this.gridWidth = gridWidth;}
    public void setGridHeight(int gridHeight) {this.gridHeight = gridHeight;}
    public void setMinesCount(int minesCount) {this.minesCount = minesCount;}
    public GameObject[][] getGrid() {return grid;}
    public GameSprite[] getSprites() {return sprites;}*/
    public GameSprite getSprite(int index) {return sprites[index];}
    public Rect getGameField() {return gameField;}
    public int getGridWidth() {return gridWidth;}
    public int getGridHeight() {return gridHeight;}
    public int getTileSize() {return tileSize;}
    public GameObject getTile(int i, int j) {return grid[i][j];}
    public ArrayList<GameObject> getMines() {return mines;}
    public int getMinesCount() {return minesCount;}
    public int getFlagsCount() {return flagsCount;}

    public void setFlag(int i, int j) {
        if (grid[i][j].getState()==GOState.CLOSED) {
            grid[i][j].setState(GOState.FLAGGED);
            m[i][j] = -1;
            flagsCount--;
            if (grid[i][j].isMined())
                minesCount--;
        }else if(grid[i][j].getState()==GOState.FLAGGED){
            grid[i][j].setState(GOState.CLOSED);
            flagsCount++;
            if (grid[i][j].isMined())
                minesCount++;
            else m[i][j]=-2;
        }
    }
}
