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

public class Pawn extends Piece {
  private static final int[] PAWN_FORWARD_MOVE = {8, 16, 7, 9};

  public Pawn(final int piecePosition, final Alliance pieceAlliance) {
    super(PieceType.PAWN, pieceAlliance, piecePosition, true);
  }

  public Pawn(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
    super(PieceType.PAWN, pieceAlliance, piecePosition, isFirstMove);
  }

  @Override
  public String toString() {
    return PieceType.PAWN.toString();
  }

  @Override
  public Collection<Move> calculateLegalMoves(Board board) {
    final List<Move> legalMoves = new ArrayList<>();

    for (final int forwardMove : PAWN_FORWARD_MOVE) {
      final int destinationCoordinate =
          piecePosition + (this.pieceAlliance.getDirection() * forwardMove);
      if (BoardUtils.isValidTileCoordinate(destinationCoordinate)) {
        final Tile destinationTile = board.getTile(destinationCoordinate);
        final Piece pieceAtTile = destinationTile.getPiece();
        if (forwardMove == 8 && !destinationTile.isTileOccupied()) {
          if (this.pieceAlliance.isPawnPromotionSquare(destinationCoordinate)) {
            legalMoves.add(
                new Move.PawnPromotion(new Move.PawnMove(board, this, destinationCoordinate)));
          } else legalMoves.add(new Move.PawnMove(board, this, destinationCoordinate));
        } else if (forwardMove == 16
            && this.isFirstMove()
            && (BoardUtils.SEVENTH_RANK[piecePosition] & this.pieceAlliance.isBlack()
                || BoardUtils.SECOND_RANK[piecePosition] & this.pieceAlliance.isWhite())) {

          Tile behindDestinationCoordinateTile =
              board.getTile(piecePosition + (this.pieceAlliance.getDirection() * forwardMove));
          Tile destinationCoordinateTile = board.getTile(destinationCoordinate);
          if (!behindDestinationCoordinateTile.isTileOccupied()
              && !destinationCoordinateTile.isTileOccupied()) {
            legalMoves.add(new Move.PawnJump(board, this, destinationCoordinate));
          }
        } else if (forwardMove == 7) {
          if ((destinationTile.isTileOccupied()
                  && pieceAtTile.getPieceAlliance() != this.pieceAlliance)
              && ((this.pieceAlliance.isWhite() && !BoardUtils.EIGHTH_COLUMN[piecePosition])
                  || (this.pieceAlliance.isBlack() && !BoardUtils.FIRST_COLUMN[piecePosition]))) {
            if (this.pieceAlliance.isPawnPromotionSquare(destinationCoordinate)) {
              legalMoves.add(
                  new Move.PawnPromotion(
                      new Move.PawnAttackMove(board, this, destinationCoordinate, pieceAtTile)));
            } else
              legalMoves.add(
                  new Move.PawnAttackMove(board, this, destinationCoordinate, pieceAtTile));
          } else if (board.getEnPassantPawn() != null
              && board.getEnPassantPawn().getPiecePosition()
                  == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
            final Piece pieceOnCandidate = board.getEnPassantPawn();
            if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
              legalMoves.add(
                  new Move.PawnEnPassantAttackMove(
                      board, this, destinationCoordinate, pieceOnCandidate));
            }
          }
        } else if (forwardMove == 9) {
          if ((destinationTile.isTileOccupied()
                  && pieceAtTile.getPieceAlliance() != this.pieceAlliance)
              && ((this.pieceAlliance.isWhite() && !BoardUtils.FIRST_COLUMN[piecePosition])
                  || (this.pieceAlliance.isBlack() && !BoardUtils.EIGHTH_COLUMN[piecePosition]))) {
            if (this.pieceAlliance.isPawnPromotionSquare(destinationCoordinate)) {
              legalMoves.add(
                  new Move.PawnPromotion(
                      new Move.PawnAttackMove(board, this, destinationCoordinate, pieceAtTile)));
            } else
              legalMoves.add(
                  new Move.PawnAttackMove(board, this, destinationCoordinate, pieceAtTile));
          } else if (board.getEnPassantPawn() != null
              && board.getEnPassantPawn().getPiecePosition()
                  == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
            final Piece pieceOnCandidate = board.getEnPassantPawn();
            if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
              legalMoves.add(
                  new Move.PawnEnPassantAttackMove(
                      board, this, destinationCoordinate, pieceOnCandidate));
            }
          }
        }
      }
    }
    return ImmutableList.copyOf(legalMoves);
  }

  @Override
  public Piece movePiece(Move move) {
    final Piece movedPiece = move.getMovedPiece();
    return new Pawn(move.getDestinationCoordinate(), movedPiece.getPieceAlliance());
  }

  public Piece getPromotionPiece() {
    return new Queen(this.pieceAlliance, this.piecePosition, false);
  }
}
