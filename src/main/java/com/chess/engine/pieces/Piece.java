package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;

public abstract class Piece {
    protected final int piecePosition;
    private PieceType pieceType;
    protected final Alliance pieceAlliance;
    private boolean isFirstMove = true;
    private final int cachedHashCode;

    Piece(final PieceType pieceType,
          final Alliance pieceAlliance,
          final int piecePosition) {
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.cachedHashCode = computeHashCode();
    }

    // Different pieces calculate their legal moves differently

    public abstract Collection<Move> calculateLegalMoves(final Board board);
    public abstract Piece movePiece(Move move);

    public boolean isFirstMove() {
        return isFirstMove;
    }
    public Alliance getPieceAlliance() {
        return pieceAlliance;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true; //reference equality
        if (!(other instanceof Piece)) return false;
        final Piece otherPiece = (Piece) other;

        return piecePosition == otherPiece.getPiecePosition()
                && pieceType == otherPiece.getPieceType()
                && pieceAlliance == otherPiece.getPieceAlliance()
                && isFirstMove == otherPiece.isFirstMove(); //
    }

    public enum PieceType {
        PAWN("P"),
        ROOK("R"),
        KNIGHT("N"),
        BISHOP("B"),
        QUEEN("Q"),
        KING("K");

        private String pieceName;


        PieceType(String pieceName) {
            this.pieceName = pieceName;
        }


        @Override
        public String toString() {
            return this.pieceName;
        }

        public boolean isKing() {
            return pieceName.equals("K");
        }

        public boolean isRook() {
            return pieceName.equals("R");
        }
    }
}
