package com.chess.engine.board;

public class BoardUtils {
    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] EIGHTH_RANK = initRow(0);
    public static final boolean[] SEVENTH_RANK = initRow(1);
    public static final boolean[] SIXTH_RANK = initRow(2);
    public static final boolean[] FIFTH_RANK = initRow(3);
    public static final boolean[] FOURTH_RANK = initRow(4);
    public static final boolean[] THIRD_RANK = initRow(5);
    public static final boolean[] SECOND_RANK = initRow(6);
    public static final boolean[] FIRST_RANK = initRow(7);

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    // Actually applicable to every piece on the board,
    // moved from Knight
    private BoardUtils() {
        throw new RuntimeException("No instantiation");
    }
    public static boolean isValidTileCoordinate(final int candidateDestinationCoordinate) {
        return candidateDestinationCoordinate >= 0 &&
                candidateDestinationCoordinate < NUM_TILES;
    }

    private static boolean[] initColumn(int columnNumber){
        boolean[] bitArr = new boolean[NUM_TILES];
        int coordinate = columnNumber;
        do {
            bitArr[coordinate] = true;
            coordinate += NUM_TILES_PER_ROW;
        } while(coordinate < NUM_TILES);

        return bitArr;
    }

    private static boolean[] initRow(int rowNumber){
        boolean[] bitArr = new boolean[NUM_TILES];
        int startIdx = rowNumber * NUM_TILES_PER_ROW;
        int endIdx = (rowNumber + 1) * NUM_TILES_PER_ROW;

        for (int i = 0; i < NUM_TILES; i++) {
            bitArr[i] = false;
            if (i < endIdx && i >= startIdx){
                bitArr[i] = true;
            }
        }

        return bitArr;

    }
}
