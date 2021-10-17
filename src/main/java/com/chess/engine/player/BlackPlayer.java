package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player{

    public BlackPlayer(final Board board,
                       final Collection<Move> legalMoves,
                       final Collection<Move> opponentMoves) {
        super(board, legalMoves, opponentMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (playerKing.isFirstMove() && !this.isInCheck()) {
            if (!board.getTile(5).isTileOccupied()
                    && !board.getTile(6).isTileOccupied()) {
                final Tile rookTile = board.getTile(7);
                if (rookTile.isTileOccupied()
                        && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(5, opponentsLegals).isEmpty()
                            && Player.calculateAttacksOnTile(6, opponentsLegals).isEmpty()
                            && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingSideCastleMove(board,
                                                                    playerKing,
                                                                    6,
                                                                    (Rook) rookTile.getPiece(),
                                                                    rookTile.getTileCoordinate(),
                                                                    5));
                    }
                }
            }

            if (!board.getTile(3).isTileOccupied()
                    && !board.getTile(2).isTileOccupied()
                    && !board.getTile(1).isTileOccupied()) {
                final Tile rookTile = board.getTile(0);
                if (rookTile.isTileOccupied()
                        && rookTile.getPiece().isFirstMove()
                        && Player.calculateAttacksOnTile(2, opponentsLegals).isEmpty()
                        && Player.calculateAttacksOnTile(3, opponentsLegals).isEmpty()
                        && rookTile.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new Move.QueenSideCastleMove(board,
                                                                playerKing,
                                                                2,
                                                                (Rook) rookTile.getPiece(),
                                                                rookTile.getTileCoordinate(),
                                                                3));
                }
            }

        }

        return ImmutableList.copyOf(kingCastles);
    }


}
