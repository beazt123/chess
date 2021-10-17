package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    public Player(Board board,
                     Collection<Move> legalMoves,
                     Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
        this.legalMoves = ImmutableList.copyOf(
                Iterables.concat(
                        legalMoves,
                        calculateKingCastles(
                                legalMoves,
                                opponentMoves)
                )
        );
    }

    protected static Collection<Move> calculateAttacksOnTile(final int piecePosition, final Collection<Move> opponentMoves) {

        final List<Move> allAtksOnTile = new ArrayList<>();
        for (Move move : opponentMoves) {
            if (move.getDestinationCoordinate() == piecePosition) {
                allAtksOnTile.add(move);
            }
        }
        return ImmutableList.copyOf(allAtksOnTile);
    }

    private King establishKing() throws RuntimeException {
        for (Piece piece : getActivePieces()){
            if (piece.getPieceType().isKing())
                return (King) piece;
        }
        throw new RuntimeException("Player does not have a King");
    }

    public boolean isMoveLegal(Move move) {
        return legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return isInCheck;
    }

    public boolean isInCheckMate() {
        return isInCheck && !hasEscapeMoves();
    }



    protected boolean hasEscapeMoves() {
        for (final Move move : legalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) return true;
        }
        return false;
    }

    public boolean isInStaleMate(Move move) {
        return !isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled(Move move) {
        return legalMoves.contains(move);
    }

    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final Collection<Move> attacksOnKing = Player.calculateAttacksOnTile(
                transitionBoard.currentPlayer()
                        .getOpponent()
                        .getPlayerKing()
                        .getPiecePosition(),
                transitionBoard.currentPlayer()
                        .getLegalMoves()
        );

        if (!attacksOnKing.isEmpty()) {
            return new MoveTransition(board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);

    }

    public Collection<Move> getLegalMoves() {
        return legalMoves;
    }

    private Piece getPlayerKing() {
        return playerKing;
    }


    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentsLegals);

}
