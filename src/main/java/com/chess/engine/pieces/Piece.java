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
          final int piecePosition,
          boolean isFirstMove) {
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        this.isFirstMove = isFirstMove;
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

    public int getPieceValue() {
        return pieceType.getPieceValue();
    }

    public enum PieceType {
        PAWN(100, "P"),
        ROOK(500,"R"),
        KNIGHT(300,"N"),
        BISHOP(300,"B"),
        QUEEN(900,"Q"),
        KING(10000,"K");

        private String pieceName;
        private int pieceValue;


        PieceType(final int pieceValue, final String pieceName) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
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

        public int getPieceValue() {
            return pieceValue;
        }
    }
}
