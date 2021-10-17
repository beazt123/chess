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

public class Bishop extends Piece {
    private final static int[] CANDIDATE_LEGAL_MOVES
            = {-9, -7, 7, 9};

    public Bishop(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.BISHOP , pieceAlliance, piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int candidateMove : CANDIDATE_LEGAL_MOVES) {
            int destinationCoordinate = piecePosition;
            while (BoardUtils.isValidTileCoordinate(destinationCoordinate)) {
                destinationCoordinate += candidateMove;
                if (isFirstColumnExclusion(this.piecePosition, candidateMove)
                        || isEighthColumnExclusion(this.piecePosition, candidateMove)) {
                    break;
                }

                if (BoardUtils.isValidTileCoordinate(destinationCoordinate)) {
                    Tile targetTile = board.getTile(destinationCoordinate);
                    if (targetTile.isTileOccupied()) {
                        Piece pieceAtTile = targetTile.getPiece();
                        if (pieceAtTile.getPieceAlliance() != pieceAlliance) {
                            legalMoves.add(
                                    new Move.AttackMove(board,
                                            this,
                                            destinationCoordinate,
                                            pieceAtTile)
                            );
                        }
                        break;
                    } else {
                        legalMoves.add(
                                new Move.MajorMove(board,
                                        this,
                                        destinationCoordinate)
                        );
                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        final Piece movedPiece = move.getMovedPiece();
        return new Bishop(movedPiece.getPiecePosition(),
                movedPiece.getPieceAlliance());
        //TODO: pre-compute all possible pieces into a lookup table
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    private static boolean isFirstColumnExclusion(int piecePosition, int destinationCoordinate) {
        return BoardUtils.FIRST_COLUMN[piecePosition]
                && (destinationCoordinate == -9 || destinationCoordinate == 7);
    }
    private static boolean isEighthColumnExclusion(int piecePosition, int destinationCoordinate) {
        return BoardUtils.EIGHTH_COLUMN[piecePosition]
                && (destinationCoordinate == -7 || destinationCoordinate == 9);
    }
}



