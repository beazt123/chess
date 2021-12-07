package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends Piece {
  private static final int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

  public Queen(int piecePosition, Alliance pieceAlliance) {
    super(PieceType.QUEEN, pieceAlliance, piecePosition, true);
  }

  public Queen(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
    super(PieceType.QUEEN, pieceAlliance, piecePosition, isFirstMove);
  }

  @Override
  public String toString() {
    return PieceType.QUEEN.toString();
  }

  @Override
  public Collection<Move> calculateLegalMoves(Board board) {
    final List<Move> legalMoves = new ArrayList<>();
    for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
      int candidateDestinationCoordinate = this.piecePosition;
      while (true) {
        if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)
            || isEightColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
          break;
        }
        candidateDestinationCoordinate += currentCandidateOffset;
        if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
          break;
        } else {
          final Piece pieceAtDestination = board.getTile(candidateDestinationCoordinate).getPiece();
          if (pieceAtDestination == null) {
            legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
          } else {
            final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAlliance();
            if (this.pieceAlliance != pieceAtDestinationAllegiance) {
              legalMoves.add(
                  new Move.MajorAttackMove(
                      board, this, candidateDestinationCoordinate, pieceAtDestination));
            }
            break;
          }
        }
      }
    }

    return ImmutableList.copyOf(legalMoves);
  }

  @Override
  public Piece movePiece(Move move) {
    final Piece movedPiece = move.getMovedPiece();
    return new Queen(move.getDestinationCoordinate(), movedPiece.getPieceAlliance());
  }

  private static boolean isFirstColumnExclusion(
      final int currentPosition, final int candidatePosition) {
    return BoardUtils.FIRST_COLUMN[candidatePosition]
        && ((currentPosition == -9) || (currentPosition == -1) || (currentPosition == 7));
  }

  private static boolean isEightColumnExclusion(
      final int currentPosition, final int candidatePosition) {
    return BoardUtils.EIGHTH_COLUMN[candidatePosition]
        && ((currentPosition == -7) || (currentPosition == 1) || (currentPosition == 9));
  }
}
