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

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board,
                       final Collection<Move> legalMoves,
                       final Collection<Move> opponentMoves) {
        super(board, legalMoves, opponentMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                    final Collection<Move> opponentsLegals) {
        final List<Move> kingCastles = new ArrayList<>();
        if (this.playerKing.isFirstMove()
                && !this.isInCheck()) {
            if (!board.getTile(61).isTileOccupied()
                    && !board.getTile(62).isTileOccupied()) {
                final Tile rookTile = board.getTile(63);
                if (rookTile.isTileOccupied()
                        && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(61, opponentsLegals).isEmpty()
                            && Player.calculateAttacksOnTile(62, opponentsLegals).isEmpty()
                            && rookTile.getPiece().getPieceType().isRook()) {
                        kingCastles.add(new Move.KingSideCastleMove(board,
                                                                    playerKing,
                                                                    62,
                                                                    (Rook) rookTile.getPiece(),
                                                                    rookTile.getTileCoordinate(),
                                                                    61));
                    }
                }
            }

            if (!board.getTile(59).isTileOccupied()
                    && !board.getTile(58).isTileOccupied()
                    && !board.getTile(57).isTileOccupied()) {
                final Tile rookTile = board.getTile(56);
                if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
                        && Player.calculateAttacksOnTile(58, opponentsLegals).isEmpty()
                        && Player.calculateAttacksOnTile(59, opponentsLegals).isEmpty()
                        && rookTile.getPiece().getPieceType().isRook()) {
                    kingCastles.add(new Move.QueenSideCastleMove(board,
                                                                playerKing,
                                                                58,
                                                                (Rook) rookTile.getPiece(),
                                                                rookTile.getTileCoordinate(),
                                                                59));
                }
            }

        }

        return ImmutableList.copyOf(kingCastles);
    }

    @Override
    public String toString() {
        return "White";
    }
}
