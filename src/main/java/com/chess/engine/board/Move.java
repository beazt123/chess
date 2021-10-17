package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;
    public static final Move NULL_MOVE = new NullMove();

    public Move(Board board,
                Piece movedPiece,
                int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public int getDestinationCoordinate() {
        return destinationCoordinate;
    }

    public Board execute() {
        Board.Builder builder = new Board.Builder();

        for (final Piece piece : board.currentPlayer()
                                    .getActivePieces()) {
            // TODO: hashcode & equals for pieces
            if (!movedPiece.equals(piece)) builder.setPiece(piece);
        }

        for (final Piece piece : board.currentPlayer()
                                        .getOpponent()
                                        .getActivePieces()) {
            builder.setPiece(piece);
        }

        builder.setPiece(movedPiece.movePiece(this));
        builder.setMoveMaker(board.currentPlayer()
                .getOpponent()
                .getAlliance());

        return builder.build();
    }

    public boolean isAttack() {
        return false;
    }

    public boolean isCastlingMove() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + destinationCoordinate;
        result = prime * result + movedPiece.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true; //reference equality
        if (!(other instanceof Move)) return false;
        final Move otherMove = (Move) other;

        return destinationCoordinate == otherMove.getDestinationCoordinate()
                && movedPiece.equals(otherMove.getMovedPiece());
    }

    // Separating the same type of class into many types of subclasses
    public static final class MajorMove extends Move {

        public MajorMove(Board board, Piece piece, int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(Board board,
                          Piece piece,
                          int destinationCoordinate,
                          Piece attackedPiece) {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return attackedPiece;
        }

        @Override
        public int hashCode() {
            return attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (this.equals(other)) return true;
            if (!(other instanceof AttackMove)) return false;

            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove)
                    && attackedPiece.equals(otherAttackMove.getAttackedPiece());
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Null move cannot be executed");
        }
    }

    public static final class PawnMove extends Move {
        public PawnMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

    }
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(Board board, Piece piece, int destinationCoordinate, Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

    }
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(Board board, Piece piece, int destinationCoordinate, Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

    }
    public static final class PawnJump extends Move {
        public PawnJump(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            final Pawn movedPawn = (Pawn) movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(board.currentPlayer()
                    .getOpponent()
                    .getAlliance());

            return builder.build();
        }

    }
    static abstract class CastleMove extends Move {
        protected final Rook castleRook;

        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(Board board,
                          Piece movedPiece,
                          int destinationCoordinate,
                          final Rook castleRook,
                          final int castleRookStart,
                          final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : board.currentPlayer().getActivePieces()) {
                if (!movedPiece.equals(piece) && !castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(movedPiece.movePiece(this));
            builder.setPiece(new Rook(castleRook.getPiecePosition(), castleRook.getPieceAlliance()));
            builder.setMoveMaker(board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }
    }
    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(Board board,
                                  Piece movedPiece,
                                  int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board,
                    movedPiece,
                    destinationCoordinate,
                    castleRook,
                    castleRookStart,
                    castleRookDestination);
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }
    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(Board board,
                                  Piece movedPiece,
                                  int destinationCoordinate,
                                  final Rook castleRook,
                                  final int castleRookStart,
                                  final int castleRookDestination) {
            super(board,
                    movedPiece,
                    destinationCoordinate,
                    castleRook,
                    castleRookStart,
                    castleRookDestination);
        }
        @Override
        public String toString() {
            return "O-O-O";
        }

    }

    public static class MoveFactory {


        private MoveFactory() {
            throw new RuntimeException("Move Factory not instantiable.");
        }
        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            //may be good to just allow creation of moves only for the current player
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate
                        && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }

    private int getCurrentCoordinate() {
        return movedPiece.getPiecePosition();
    }


}
