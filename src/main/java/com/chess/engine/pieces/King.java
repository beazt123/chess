package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{
    private final static int[] CANDIDATE_LEGAL_MOVES
            = {-7, 7, -9, 9, -1, 1, -8, 8};
    private boolean isCastled;
    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;

    public King(final Alliance alliance,
                final int piecePosition,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, alliance, piecePosition, true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(final Alliance pieceAlliance,
                 final int piecePosition,
                 final boolean isFirstMove,
                final boolean isCastled,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, pieceAlliance, piecePosition, isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (int candidateLegalMove : CANDIDATE_LEGAL_MOVES){
            final int candidateDestinationCoordinate = this.piecePosition + candidateLegalMove;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                if (isFirstColumnExclusion(this.piecePosition, candidateLegalMove)
                    || isEighthColumnExclusion(this.piecePosition, candidateLegalMove)
                    || isFirstRowExclusion(this.piecePosition, candidateLegalMove)
                    || isEighthRowExclusion(this.piecePosition, candidateLegalMove)){
                    continue;
                } else {
                    final Tile destinationTile = board.getTile(candidateDestinationCoordinate);
                    final Piece pieceAtTile = destinationTile.getPiece();
                    if (destinationTile.isTileOccupied()
                            && pieceAtTile.getPieceAlliance() != this.pieceAlliance) {
                        legalMoves.add(
                                new Move.MajorAttackMove(board,
                                    this,
                                    candidateDestinationCoordinate,
                                    pieceAtTile)
                        );
                    } else if (!destinationTile.isTileOccupied()) {
                        legalMoves.add(
                                new Move.MajorMove(board,
                                        this,
                                        candidateDestinationCoordinate)
                        );
                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    public boolean isCastled() {
        return this.isCastled;
    }

    @Override
    public Piece movePiece(Move move) {
        final Piece movedPiece = move.getMovedPiece();
        return new King(this.pieceAlliance,
                move.getDestinationCoordinate(),
                false,
                move.isCastlingMove(),
                false,
                false);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    private static boolean isFirstColumnExclusion(int piecePosition, int candidateLegalMove){
        return BoardUtils.FIRST_COLUMN[piecePosition]
                && (candidateLegalMove == -9
                    || candidateLegalMove == 7
                    || candidateLegalMove == -1);
    }

    private static boolean isEighthColumnExclusion(int piecePosition, int candidateLegalMove){
        return BoardUtils.EIGHTH_COLUMN[piecePosition]
                && (candidateLegalMove == 9
                || candidateLegalMove == -7
                || candidateLegalMove == 1);
    }

    private static boolean isFirstRowExclusion(int piecePosition, int candidateLegalMove){
        return BoardUtils.EIGHTH_RANK[piecePosition]
                && (candidateLegalMove == -9
                || candidateLegalMove == -7
                || candidateLegalMove == -8);
    }

    private static boolean isEighthRowExclusion(int piecePosition, int candidateLegalMove){
        return BoardUtils.FIRST_RANK[piecePosition]
                && (candidateLegalMove == 9
                || candidateLegalMove == 7
                || candidateLegalMove == 8);
    }
}
