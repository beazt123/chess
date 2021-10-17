package com.chess.engine.board;


import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    protected final int tileCoordinate;
    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE //Immutable. Commonly used values are cached amd fetched when needed
            = createAllPossibleemptyTiles();

    private Tile(int tileCoordinate){
        this.tileCoordinate = tileCoordinate;
    }

    private static Map<Integer, EmptyTile> createAllPossibleemptyTiles() {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        return ImmutableMap.copyOf(emptyTileMap); //Collections.unmodifiableMap(emptyTileMap) also works
    }

    public static Tile createTile(final int tileCoordinate, final Piece piece){
        return (piece != null)
                ? new OccupiedTile(tileCoordinate, piece)
                : new EmptyTile(tileCoordinate);
    }

    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate() {
        return tileCoordinate;
    }

    /*
    One can put these 2 child classes in separate files as well. Style diff
    */
    public static final class EmptyTile extends Tile {
        private EmptyTile(final int coordinate) {
            super(coordinate);
        }

        @Override
        public boolean isTileOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    public static final class OccupiedTile extends Tile {
        private final Piece pieceOnTile;

        private OccupiedTile(int coordinate, Piece pieceOnTile) {
            super(coordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean isTileOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }

        @Override
        public String toString() {
            return pieceOnTile.getPieceAlliance().isBlack() ?
                    pieceOnTile.toString().toLowerCase() : pieceOnTile.toString().toUpperCase();
        }
    }
}


/*
Immutable objects ensure thread safety as well
 */