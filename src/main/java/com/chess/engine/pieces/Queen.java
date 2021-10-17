package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends Piece {
    public Queen(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.QUEEN, pieceAlliance, piecePosition);
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        Rook alliedRook = new Rook(piecePosition, pieceAlliance);
        Bishop alliedBishop = new Bishop(piecePosition, pieceAlliance);

        List<Move> rookLegalMoves = (List<Move>) alliedRook.calculateLegalMoves(board);
        List<Move> bishopLegalMoves = (List<Move>) alliedBishop.calculateLegalMoves(board);

        legalMoves.addAll(rookLegalMoves);
        legalMoves.addAll(bishopLegalMoves);


        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        final Piece movedPiece = move.getMovedPiece();
        return new Queen(movedPiece.getPiecePosition(),
                movedPiece.getPieceAlliance());
    }
}

