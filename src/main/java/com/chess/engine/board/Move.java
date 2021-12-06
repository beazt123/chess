package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

import javax.xml.bind.helpers.AbstractUnmarshallerImpl;
import java.util.Collection;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;
    public static final Move NULL_MOVE = new NullMove();

    private Move(Board board,
                Piece movedPiece,
                int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(Board board,
                 int destinationCoordinate) {
        this.board = board;
        this.movedPiece = null;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = false;
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
    public String toString() {
        return BoardUtils.getPositionAtCoordinate(destinationCoordinate);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + destinationCoordinate;
        result = prime * result + movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true; //reference equality
        if (!(other instanceof Move)) return false;
        final Move otherMove = (Move) other;

        return getCurrentCoordinate() == otherMove.getCurrentCoordinate()
                && destinationCoordinate == otherMove.getDestinationCoordinate()
                && movedPiece.equals(otherMove.getMovedPiece());
    }

    public int getCurrentCoordinate() {
        return movedPiece.getPiecePosition();
    }

    // Separating the same type of class into many types of subclasses
    public static final class MajorMove extends Move {

        public MajorMove(Board board, Piece piece, int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
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
            if (this == other) return true;
            if (!(other instanceof AttackMove)) return false;

            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove)
                    && attackedPiece.equals(otherAttackMove.getAttackedPiece());
        }
    }

    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove(final Board board,
                               final Piece pieceMoved,
                               final int destinationCoordinate,
                               final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Null move cannot be executed");
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }

        @Override
        public String toString(){
            return "";
        }
    }

    public static final class PawnMove extends Move {
        public PawnMove(Board board, Piece movedPiece, int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(destinationCoordinate);
        }

    }
    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(Board board, Piece piece, int destinationCoordinate, Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1)
                    + "x"
                    + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class PawnEnPassantAttackMove extends PawnAttackMove {
        public PawnEnPassantAttackMove(Board board, Piece piece, int destinationCoordinate, Piece attackedPiece) {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
                if (!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if (!piece.equals(this.getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
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
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof CastleMove)) return false;
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) & this.castleRook.equals(otherCastleMove.getCastleRook());
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
        public boolean equals(Object other) {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
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

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }
    }

    public static class PawnPromotion extends Move {
        private final Pawn promotedPawn;
        private final Move decoratedMove;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }

        @Override
        public Board execute() {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Board.Builder();
            for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) builder.setPiece(piece);
            }
            for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());

            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return "";
        }

        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(Object other) {
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }
    }

    private Board getBoard() {
        return board;
    }

    public static class MoveFactory {


        private MoveFactory() {
            throw new RuntimeException("Move Factory not instantiable.");
        }
        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            //may be good to just allow creation of moves only for the current player
            Iterable<Move> boardLegalMoves = board.getAllLegalMoves();
            for (final Move move : boardLegalMoves) {
                if (move.getCurrentCoordinate() == currentCoordinate
                        && move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }




}
