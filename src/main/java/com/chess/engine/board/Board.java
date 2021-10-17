package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;


import java.util.*;

public class Board {
    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;

    private final Player currentPlayer;


    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.blackPlayer, this.whitePlayer);
    }

    public Collection<Piece> getWhitePieces() {
        return whitePieces;
    }
    public Collection<Piece> getBlackPieces() {
        return blackPieces;
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();

        for (final Tile tile : gameBoard){
            final Piece pieceAtTile = tile.getPiece();
            if (tile.isTileOccupied()
                    && pieceAtTile.getPieceAlliance() == alliance){
                activePieces.add(pieceAtTile);
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    private List<Tile> createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for (int i = 0; i < BoardUtils.NUM_TILES; i++){
            tiles[i] = Tile.createTile(i, builder.boardState.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    public static Board createStandardBoard() {
        Builder boardBuilder = new Builder();
        boardBuilder.setPiece(new Rook(0, Alliance.BLACK));
        boardBuilder.setPiece(new Knight(1, Alliance.BLACK));
        boardBuilder.setPiece(new Bishop(2, Alliance.BLACK));
        boardBuilder.setPiece(new Queen(3, Alliance.BLACK));
        boardBuilder.setPiece(new King(4, Alliance.BLACK));
        boardBuilder.setPiece(new Bishop(5, Alliance.BLACK));
        boardBuilder.setPiece(new Knight(6, Alliance.BLACK));
        boardBuilder.setPiece(new Rook(7, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(8, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(9, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(10, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(11, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(12, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(13, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(14, Alliance.BLACK));
        boardBuilder.setPiece(new Pawn(15, Alliance.BLACK));

        boardBuilder.setPiece(new Pawn(48, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(49, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(50, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(51, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(52, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(53, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(54, Alliance.WHITE));
        boardBuilder.setPiece(new Pawn(55, Alliance.WHITE));
        boardBuilder.setPiece(new Rook(56, Alliance.WHITE));
        boardBuilder.setPiece(new Knight(57, Alliance.WHITE));
        boardBuilder.setPiece(new Bishop(58, Alliance.WHITE));
        boardBuilder.setPiece(new Queen(59, Alliance.WHITE));
        boardBuilder.setPiece(new King(60, Alliance.WHITE));
        boardBuilder.setPiece(new Bishop(61, Alliance.WHITE));
        boardBuilder.setPiece(new Knight(62, Alliance.WHITE));
        boardBuilder.setPiece(new Rook(63, Alliance.WHITE));

        boardBuilder.setMoveMaker(Alliance.WHITE);

        return boardBuilder.build();

    }

    public Tile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        for (Piece piece : pieces){
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    public Player whitePlayer() {
        return whitePlayer;
    }

    public BlackPlayer blackPlayer() {
        return blackPlayer;
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.concat(this.whitePlayer.getLegalMoves(),
                this.blackPlayer.getLegalMoves());

    }

    public static class Builder {
        Map<Integer, Piece> boardState = new HashMap<>();
        private Alliance nextMoveMaker;
        private Pawn enPassantPawn;

        public Builder setPiece(final Piece piece){
            this.boardState.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker){
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public Builder setEnPassantPawn(Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
            return this;
        }
    }


    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++){
            final Tile tile = gameBoard.get(i);
            final String tileStr = tile.toString();
            final Piece piece = tile.getPiece();
            sb.append(String.format("%3s", tileStr));

            if ((i + 1) % BoardUtils.NUM_TILES_PER_ROW == 0) sb.append("\n");
        }
        return sb.toString();
    }
}
