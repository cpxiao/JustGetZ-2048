package com.cpxiao.aa2048.mode.extra;

/**
 * @author cpxiao on 2017/10/16.
 */

public final class Extra {

    public static final class Key {
        public static final String SQUARE_COUNT_X = "SQUARE_COUNT_X";
        public static final String SQUARE_COUNT_Y = "SQUARE_COUNT_Y";
        public static final int SQUARE_COUNT_X_DEFAULT = 4;
        public static final int SQUARE_COUNT_Y_DEFAULT = 4;

        public static final String WIDTH = "WIDTH";
        public static final String HEIGHT = "HEIGHT";

        public static final String SCORE = "SCORE";
        public static final String BEST_SCORE = "BEST_SCORE";
        public static final String UNDO_SCORE = "UNDO_SCORE";
        public static final String CAN_UNDO = "CAN_UNDO";
        public static final String UNDO_GRID = "UNDO_GRID";
        public static final String GAME_STATE = "GAME_STATE";
        public static final String UNDO_GAME_STATE = "UNDO_GAME_STATE";
        public static final String HAS_STATE = "HAS_STATE";

        public static final String getKey(int countX, int countY, String keyFormat) {
            return countX + "_" + countY + keyFormat;
        }
    }

    public static final class Name {
    }
}
