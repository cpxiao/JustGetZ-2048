package com.cpxiao.justgetz2048.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.cpxiao.AppConfig;
import com.cpxiao.R;
import com.cpxiao.justgetz2048.InputListener;
import com.cpxiao.justgetz2048.mode.AnimationCell;
import com.cpxiao.justgetz2048.mode.MainGame;
import com.cpxiao.justgetz2048.mode.Tile;
import com.cpxiao.justgetz2048.mode.extra.Extra;

import java.util.ArrayList;

import hugo.weaving.DebugLog;

public class GameView extends View {

    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = GameView.class.getSimpleName();

    //Internal Constants
    private static final float MERGING_ACCELERATION = -0.5F;
    private static final float INITIAL_VELOCITY = (1 - MERGING_ACCELERATION) / 4;
    public  final int numCellTypes = 27;
    private final BitmapDrawable[] bitmapCell = new BitmapDrawable[numCellTypes];
    public MainGame game;
    //Internal variables
    private final Paint mPaint = new Paint();
    //    public boolean hasSaveState = false;
    public boolean continueButtonEnabled = false;
    public int boardLeft;
    public int boardTop;
    public int boardRight;
    public int boardBottom;
    //Icon variables
    public int sYIcons;
    public int sXNewGame;
    public int sXUndo;
    public int iconSize;
    //Misc
    public boolean refreshLastTime = true;
    //Timing
    private long lastFPSTime = System.nanoTime();
    //Text
    private float titleTextSize;
    private float bodyTextSize;
    private float headerTextSize;
    private float instructionsTextSize;
    private float gameOverTextSize;
    //Layout variables
    private int cellSize = 0;
    private float textSize = 0;
    private float cellTextSize = 0;
    private int gridWidth = 0;
    private int textPaddingSize;
    private int iconPaddingSize;
    //Assets
    private Drawable backgroundRectangle;
    private Drawable lightUpRectangle;
    private Drawable fadeRectangle;
    private Bitmap background = null;
    private BitmapDrawable loseGameOverlay;
    private BitmapDrawable winGameContinueOverlay;
    private BitmapDrawable winGameFinalOverlay;
    //Text variables
    private int sYAll;
    private int titleStartYAll;
    private int bodyStartYAll;
    private int eYAll;
    private int titleWidthBestScore;
    private int titleWidthScore;

    public GameView(Context context, int squareCountX, int squareCountY) {
        super(context);
        init(context, squareCountX, squareCountY);
    }

    public GameView(Context context) {
        super(context);
        init(context, Extra.Key.SQUARE_COUNT_X_DEFAULT, Extra.Key.SQUARE_COUNT_Y_DEFAULT);
    }


    private void init(Context context, int squareCountX, int squareCountY) {

        //Loading resources
        Resources resources = context.getResources();

        game = new MainGame(context, this, squareCountX, squareCountY);
        try {
            //Getting assets
            backgroundRectangle = ContextCompat.getDrawable(getContext(), R.drawable.background_rectangle);
            lightUpRectangle = ContextCompat.getDrawable(getContext(), R.drawable.light_up_rectangle);
            fadeRectangle = ContextCompat.getDrawable(getContext(), R.drawable.fade_rectangle);
            this.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
            Typeface font = Typeface.createFromAsset(resources.getAssets(), "ClearSans-Bold.ttf");
            mPaint.setTypeface(font);
            mPaint.setAntiAlias(true);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Error getting assets?", e);
            }
        }
        setOnTouchListener(new InputListener(this));
        game.newGame();
    }


    private static int log2(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Reset the transparency of the screen

        canvas.drawBitmap(background, 0, 0, mPaint);

        drawScoreText(canvas, mPaint);

        if (!game.isActive() && !game.aGrid.isAnimationActive()) {
            drawNewGameButton(canvas, true);
        }

        drawCells(canvas, mPaint);

        if (!game.isActive()) {
            drawEndGameState(canvas);
        }

        if (!game.canContinue()) {
            drawEndlessText(canvas, mPaint);
        }

        //Refresh the screen if there is still an animation running
        if (game.aGrid.isAnimationActive()) {
            invalidate(boardLeft, boardTop, boardRight, boardBottom);
            tick();
            //Refresh one last time on game end.
        } else if (!game.isActive() && refreshLastTime) {
            invalidate();
            refreshLastTime = false;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldW, int oldH) {
        super.onSizeChanged(width, height, oldW, oldH);
        getLayout(width, height, mPaint);
        createBitmapCells(mPaint);
        createBackgroundBitmap(width, height, mPaint);
        createOverlays(mPaint);
    }

    private void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
        draw.setBounds(startingX, startingY, endingX, endingY);
        draw.draw(canvas);
    }

    private void drawCellText(Canvas canvas, int value, Paint paint) {
        int textShiftY = centerText();
        if (value >= 8) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.text_white));
        } else {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.text_black));
        }
        canvas.drawText("" + value, cellSize / 2, cellSize / 2 - textShiftY, paint);
    }

    private void drawHeader(Canvas canvas, Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_head));
        paint.setTextSize(headerTextSize);
        paint.setTextAlign(Paint.Align.LEFT);
        int textShiftY = centerText() * 2;
        //        int headerStartY = sYAll - textShiftY;
        int headerStartY = (int) (0.5F * (sYAll + eYAll) - textShiftY);
        canvas.drawText(getResources().getString(R.string.header), boardLeft, headerStartY, paint);
    }

    private void drawScoreText(Canvas canvas, Paint paint) {
        //Drawing the score text: Ver 2
        paint.setTextSize(bodyTextSize);
        paint.setTextAlign(Paint.Align.CENTER);

        int bodyWidthBestScore = (int) (paint.measureText("" + game.bestScore));
        int bodyWidthScore = (int) (paint.measureText("" + game.score));

        int textWidthBestScore = Math.max(titleWidthBestScore, bodyWidthBestScore) + textPaddingSize * 2;
        int textWidthScore = Math.max(titleWidthScore, bodyWidthScore) + textPaddingSize * 2;

        int textMiddleBestScore = textWidthBestScore / 2;
        int textMiddleScore = textWidthScore / 2;

        int endXBestScore = boardRight;
        int startXBestScore = endXBestScore - textWidthBestScore;

        int endXScore = startXBestScore - textPaddingSize;
        int startXScore = endXScore - textWidthScore;

        //Outputting best-scores box
        backgroundRectangle.setBounds(startXBestScore, sYAll, endXBestScore, eYAll);
        backgroundRectangle.draw(canvas);
        paint.setTextSize(titleTextSize);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_brown));
        canvas.drawText(getResources().getString(R.string.best_score), startXBestScore + textMiddleBestScore, titleStartYAll, paint);
        paint.setTextSize(bodyTextSize);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_white));
        canvas.drawText(String.valueOf(game.bestScore), startXBestScore + textMiddleBestScore, bodyStartYAll, paint);

        //Outputting scores box
        backgroundRectangle.setBounds(startXScore, sYAll, endXScore, eYAll);
        backgroundRectangle.draw(canvas);
        paint.setTextSize(titleTextSize);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_brown));
        canvas.drawText(getResources().getString(R.string.score), startXScore + textMiddleScore, titleStartYAll, paint);
        paint.setTextSize(bodyTextSize);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_white));
        canvas.drawText(String.valueOf(game.score), startXScore + textMiddleScore, bodyStartYAll, paint);
    }

    private void drawNewGameButton(Canvas canvas, boolean lightUp) {

        if (lightUp) {
            drawDrawable(canvas,
                    lightUpRectangle,
                    sXNewGame,
                    sYIcons,
                    sXNewGame + iconSize,
                    sYIcons + iconSize
            );
        } else {
            drawDrawable(canvas,
                    backgroundRectangle,
                    sXNewGame,
                    sYIcons,
                    sXNewGame + iconSize,
                    sYIcons + iconSize
            );
        }

        drawDrawable(canvas,
                ContextCompat.getDrawable(getContext(), R.drawable.ic_action_refresh),
                sXNewGame + iconPaddingSize,
                sYIcons + iconPaddingSize,
                sXNewGame + iconSize - iconPaddingSize,
                sYIcons + iconSize - iconPaddingSize
        );
    }

    private void drawUndoButton(Canvas canvas) {

        drawDrawable(canvas,
                backgroundRectangle,
                sXUndo,
                sYIcons,
                sXUndo + iconSize,
                sYIcons + iconSize
        );

        drawDrawable(canvas,
                ContextCompat.getDrawable(getContext(), R.drawable.ic_action_undo),
                sXUndo + iconPaddingSize,
                sYIcons + iconPaddingSize,
                sXUndo + iconSize - iconPaddingSize,
                sYIcons + iconSize - iconPaddingSize
        );
    }

    private void drawInstructions(Canvas canvas, Paint paint) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_black));
        paint.setTextSize(instructionsTextSize);
        paint.setTextAlign(Paint.Align.LEFT);
        int textShiftY = centerText() * 2;
        canvas.drawText(getResources().getString(R.string.instructions),
                boardLeft, boardBottom - textShiftY + textPaddingSize, paint);
    }

    private void drawBackground(Canvas canvas) {
        drawDrawable(canvas, backgroundRectangle, boardLeft, boardTop, boardRight, boardBottom);
    }

    //Renders the set of 16 background squares.
    private void drawBackgroundGrid(Canvas canvas) {
        Drawable backgroundCell = ContextCompat.getDrawable(getContext(), R.drawable.cell_rectangle);
        // Outputting the game grid
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = boardLeft + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = boardTop + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                drawDrawable(canvas, backgroundCell, sX, sY, eX, eY);
            }
        }
    }

    private void drawCells(Canvas canvas, Paint paint) {
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        // Outputting the individual cells
        for (int xx = 0; xx < game.numSquaresX; xx++) {
            for (int yy = 0; yy < game.numSquaresY; yy++) {
                int sX = boardLeft + gridWidth + (cellSize + gridWidth) * xx;
                int eX = sX + cellSize;
                int sY = boardTop + gridWidth + (cellSize + gridWidth) * yy;
                int eY = sY + cellSize;

                Tile currentTile = game.grid.getCellContent(xx, yy);
                if (currentTile != null) {
                    //Get and represent the value of the tile
                    int value = currentTile.getValue();
                    int index = log2(value);

                    //Check for any active animations
                    ArrayList<AnimationCell> aArray = game.aGrid.getAnimationCell(xx, yy);
                    boolean animated = false;
                    for (int i = aArray.size() - 1; i >= 0; i--) {
                        AnimationCell aCell = aArray.get(i);
                        //If this animation is not active, skip it
                        if (aCell.getAnimationType() == MainGame.SPAWN_ANIMATION) {
                            animated = true;
                        }
                        if (!aCell.isActive()) {
                            continue;
                        }

                        if (aCell.getAnimationType() == MainGame.SPAWN_ANIMATION) { // Spawning animation
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) (percentDone);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            bitmapCell[index].setBounds((int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            bitmapCell[index].draw(canvas);
                        } else if (aCell.getAnimationType() == MainGame.MERGE_ANIMATION) { // Merging Animation
                            double percentDone = aCell.getPercentageDone();
                            float textScaleSize = (float) (1 + INITIAL_VELOCITY * percentDone
                                    + MERGING_ACCELERATION * percentDone * percentDone / 2);
                            paint.setTextSize(textSize * textScaleSize);

                            float cellScaleSize = cellSize / 2 * (1 - textScaleSize);
                            bitmapCell[index].setBounds((int) (sX + cellScaleSize), (int) (sY + cellScaleSize), (int) (eX - cellScaleSize), (int) (eY - cellScaleSize));
                            bitmapCell[index].draw(canvas);
                        } else if (aCell.getAnimationType() == MainGame.MOVE_ANIMATION) {  // Moving animation
                            double percentDone = aCell.getPercentageDone();
                            int tempIndex = index;
                            if (aArray.size() >= 2) {
                                tempIndex = tempIndex - 1;
                            }
                            int previousX = aCell.extras[0];
                            int previousY = aCell.extras[1];
                            int currentX = currentTile.getX();
                            int currentY = currentTile.getY();
                            int dX = (int) ((currentX - previousX) * (cellSize + gridWidth) * (percentDone - 1) * 1.0);
                            int dY = (int) ((currentY - previousY) * (cellSize + gridWidth) * (percentDone - 1) * 1.0);
                            bitmapCell[tempIndex].setBounds(sX + dX, sY + dY, eX + dX, eY + dY);
                            bitmapCell[tempIndex].draw(canvas);
                        }
                        animated = true;
                    }

                    //No active animations? Just draw the cell
                    if (!animated) {
                        bitmapCell[index].setBounds(sX, sY, eX, eY);
                        bitmapCell[index].draw(canvas);
                    }
                }
            }
        }
    }

    private void drawEndGameState(Canvas canvas) {
        double alphaChange = 1;
        continueButtonEnabled = false;
        for (AnimationCell animation : game.aGrid.globalAnimation) {
            if (animation.getAnimationType() == MainGame.FADE_GLOBAL_ANIMATION) {
                alphaChange = animation.getPercentageDone();
            }
        }
        BitmapDrawable displayOverlay = null;
        if (game.gameWon()) {
            if (game.canContinue()) {
                continueButtonEnabled = true;
                displayOverlay = winGameContinueOverlay;
            } else {
                displayOverlay = winGameFinalOverlay;
            }
        } else if (game.gameLost()) {
            displayOverlay = loseGameOverlay;
        }

        if (displayOverlay != null) {
            displayOverlay.setBounds(boardLeft, boardTop, boardRight, boardBottom);
            displayOverlay.setAlpha((int) (255 * alphaChange));
            displayOverlay.draw(canvas);
        }
    }

    private void drawEndlessText(Canvas canvas, Paint paint) {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(bodyTextSize);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_black));
        canvas.drawText(getResources().getString(R.string.endless), boardLeft, sYIcons - centerText() * 2, paint);
    }

    private void createEndGameStates(Canvas canvas, boolean win, boolean showButton, Paint paint) {
        int width = boardRight - boardLeft;
        int length = boardBottom - boardTop;
        int middleX = width / 2;
        int middleY = length / 2;
        if (win) {
            lightUpRectangle.setAlpha(127);
            drawDrawable(canvas, lightUpRectangle, 0, 0, width, length);
            lightUpRectangle.setAlpha(255);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.text_white));
            paint.setAlpha(255);
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            int textBottom = middleY - centerText();
            canvas.drawText(getResources().getString(R.string.you_win), middleX, textBottom, paint);
            paint.setTextSize(bodyTextSize);
            String text = showButton ? getResources().getString(R.string.go_on) :
                    getResources().getString(R.string.for_now);
            canvas.drawText(text, middleX, textBottom + textPaddingSize * 2 - centerText() * 2, paint);
        } else {
            fadeRectangle.setAlpha(127);
            drawDrawable(canvas, fadeRectangle, 0, 0, width, length);
            fadeRectangle.setAlpha(255);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.text_black));
            paint.setAlpha(255);
            paint.setTextSize(gameOverTextSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(getResources().getString(R.string.game_over), middleX, middleY - centerText(), paint);
        }
    }

    private void createBackgroundBitmap(int width, int height, Paint paint) {
        background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(background);
        drawHeader(canvas, paint);
        drawNewGameButton(canvas, false);
        drawUndoButton(canvas);
        drawBackground(canvas);
        drawBackgroundGrid(canvas);
        drawInstructions(canvas, paint);

    }

    @DebugLog
    private void createBitmapCells(Paint paint) {
        Resources resources = getResources();
        int[] cellRectangleIds = getCellRectangleIds();
        paint.setTextAlign(Paint.Align.CENTER);
        for (int xx = 1; xx < bitmapCell.length; xx++) {
            int value = (int) Math.pow(2, xx);
            paint.setTextSize(cellTextSize);
            float tempTextSize = cellTextSize * cellSize * 0.9f / Math.max(cellSize * 0.9f, paint.measureText(String.valueOf(value)));
            paint.setTextSize(tempTextSize);
            Bitmap bitmap = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            drawDrawable(canvas, ContextCompat.getDrawable(getContext(), cellRectangleIds[xx]), 0, 0, cellSize, cellSize);
            drawCellText(canvas, value, paint);
            bitmapCell[xx] = new BitmapDrawable(resources, bitmap);
        }
    }

    private int[] getCellRectangleIds() {
        int[] cellRectangleIds = new int[numCellTypes];
        cellRectangleIds[0] = R.drawable.cell_rectangle;
        cellRectangleIds[1] = R.drawable.cell_rectangle_a;
        cellRectangleIds[2] = R.drawable.cell_rectangle_b;
        cellRectangleIds[3] = R.drawable.cell_rectangle_c;
        cellRectangleIds[4] = R.drawable.cell_rectangle_d;
        cellRectangleIds[5] = R.drawable.cell_rectangle_e;
        cellRectangleIds[6] = R.drawable.cell_rectangle_f;
        cellRectangleIds[7] = R.drawable.cell_rectangle_g;
        cellRectangleIds[8] = R.drawable.cell_rectangle_h;
        cellRectangleIds[9] = R.drawable.cell_rectangle_i;
        cellRectangleIds[10] = R.drawable.cell_rectangle_j;
        cellRectangleIds[11] = R.drawable.cell_rectangle_k;
        cellRectangleIds[12] = R.drawable.cell_rectangle_l;
        cellRectangleIds[13] = R.drawable.cell_rectangle_m;
        cellRectangleIds[14] = R.drawable.cell_rectangle_n;
        cellRectangleIds[15] = R.drawable.cell_rectangle_o;
        cellRectangleIds[16] = R.drawable.cell_rectangle_p;
        cellRectangleIds[17] = R.drawable.cell_rectangle_q;
        cellRectangleIds[18] = R.drawable.cell_rectangle_r;
        cellRectangleIds[19] = R.drawable.cell_rectangle_s;
        cellRectangleIds[20] = R.drawable.cell_rectangle_t;
        cellRectangleIds[21] = R.drawable.cell_rectangle_u;
        cellRectangleIds[22] = R.drawable.cell_rectangle_v;
        cellRectangleIds[23] = R.drawable.cell_rectangle_w;
        cellRectangleIds[24] = R.drawable.cell_rectangle_x;
        cellRectangleIds[25] = R.drawable.cell_rectangle_y;
        cellRectangleIds[26] = R.drawable.cell_rectangle_z;
        for (int xx = 27; xx < cellRectangleIds.length; xx++) {
            cellRectangleIds[xx] = R.drawable.cell_rectangle_max;
        }
        return cellRectangleIds;
    }

    private void createOverlays(Paint paint) {
        Resources resources = getResources();
        //Initialize overlays
        Bitmap bitmap = Bitmap.createBitmap(boardRight - boardLeft, boardBottom - boardTop, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        createEndGameStates(canvas, true, true, paint);
        winGameContinueOverlay = new BitmapDrawable(resources, bitmap);
        bitmap = Bitmap.createBitmap(boardRight - boardLeft, boardBottom - boardTop, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        createEndGameStates(canvas, true, false, paint);
        winGameFinalOverlay = new BitmapDrawable(resources, bitmap);
        bitmap = Bitmap.createBitmap(boardRight - boardLeft, boardBottom - boardTop, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        createEndGameStates(canvas, false, false, paint);
        loseGameOverlay = new BitmapDrawable(resources, bitmap);
    }

    private void tick() {
        long currentTime = System.nanoTime();
        game.aGrid.tickAll(currentTime - lastFPSTime);
        lastFPSTime = currentTime;
    }

    public void resyncTime() {
        lastFPSTime = System.nanoTime();
    }

    private void getLayout(int width, int height, Paint paint) {
        // titleBar height
        float titleBarLayoutHeight = 100 * Resources.getSystem().getDisplayMetrics().density;
        // ads height
        float adsLayoutHeight = 100 * Resources.getSystem().getDisplayMetrics().density;
        float boardWidth = 1F * width;
        float boardHeight = height - titleBarLayoutHeight - adsLayoutHeight;

        cellSize = (int) Math.min(boardWidth / (game.numSquaresX + 0.5F), boardHeight / (game.numSquaresY + 0.5F));

        gridWidth = (int) (0.12F * cellSize);
        cellSize = cellSize - gridWidth;

        int boardCenterX = (int) (0.5F * boardWidth);
        int boardCenterY = (int) (titleBarLayoutHeight + 0.5F * boardHeight);
        iconSize = (int) (0.1F * width);

        //Grid Dimensions
        float halfW = 0.5F * (game.numSquaresX * cellSize + (game.numSquaresX + 1) * gridWidth);
        float halfH = 0.5F * (game.numSquaresY * cellSize + (game.numSquaresY + 1) * gridWidth);
        boardLeft = (int) (boardCenterX - halfW);
        boardRight = (int) (boardCenterX + halfW);
        boardTop = (int) (boardCenterY - halfH);
        boardBottom = (int) (boardCenterY + halfH);

        float widthWithPadding = boardRight - boardLeft;

        // Text Dimensions
        paint.setTextSize(cellSize);
        textSize = cellSize * cellSize / Math.max(cellSize, paint.measureText("0000"));

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(1000);
        instructionsTextSize = Math.min(
                1000f * (widthWithPadding / (paint.measureText(getResources().getString(R.string.instructions)))),
                0.68F * textSize
        );
        gameOverTextSize = Math.min(
                Math.min(
                        1000f * ((widthWithPadding - gridWidth * 2) / (paint.measureText(getResources().getString(R.string.game_over)))),
                        1000f * ((widthWithPadding - gridWidth * 2) / (paint.measureText(getResources().getString(R.string.you_win))))
                ),
                2F * textSize
        );

        paint.setTextSize(cellSize);
        cellTextSize = textSize;
        headerTextSize = 0.1F * width;
        titleTextSize = 0.03F * width;
        bodyTextSize = 2F * titleTextSize;
        textPaddingSize = (int) (0.33F * textSize);
        iconPaddingSize = (int) (0.2F * textSize);

        paint.setTextSize(titleTextSize);

        int textShiftYAll = centerText();
        //static variables
        sYAll = (int) (boardTop - cellSize * 1.5);
        sYAll = (int) (0.03F * height);
        titleStartYAll = (int) (sYAll + textPaddingSize + titleTextSize / 2 - textShiftYAll);
        bodyStartYAll = (int) (titleStartYAll + textPaddingSize + titleTextSize / 2 + bodyTextSize / 2);

        titleWidthBestScore = (int) (paint.measureText(getResources().getString(R.string.best_score)));
        titleWidthScore = (int) (paint.measureText(getResources().getString(R.string.score)));
        paint.setTextSize(bodyTextSize);
        textShiftYAll = centerText();
        eYAll = (int) (bodyStartYAll + textShiftYAll + bodyTextSize / 2 + textPaddingSize);

        sYIcons = (boardTop + eYAll) / 2 - iconSize / 2;
        sXNewGame = (boardRight - iconSize);
        sXUndo = sXNewGame - iconSize * 3 / 2 - iconPaddingSize;
        resyncTime();
    }

    private int centerText() {
        return (int) ((mPaint.descent() + mPaint.ascent()) / 2);
    }

}