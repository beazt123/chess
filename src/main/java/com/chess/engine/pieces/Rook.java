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

public class Rook extends Piece {
  private static final int[] CANDIDATE_VECTOR_MOVES = {-8, -1, 1, 8};

  public Rook(int piecePosition, Alliance pieceAlliance) {
    super(PieceType.ROOK, pieceAlliance, piecePosition, true);
  }

  public Rook(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
    super(PieceType.ROOK, pieceAlliance, piecePosition, isFirstMove);
  }

  @Override
  public Collection<Move> calculateLegalMoves(Board board) {
    final List<Move> legalMoves = new ArrayList<>();
    for (int vector : CANDIDATE_VECTOR_MOVES) {
      int destionationTileCoordinate = piecePosition;
      while (BoardUtils.isValidTileCoordinate(destionationTileCoordinate)) {
        destionationTileCoordinate += vector;

        if (isFirstColumnExclusion(this.piecePosition, vector)
            || isEighthColumnExclusion(this.piecePosition, vector)) {
          break;
        }

        if (BoardUtils.isValidTileCoordinate(destionationTileCoordinate)) {
          Tile destinationTile = board.getTile(destionationTileCoordinate);
          if (!destinationTile.isTileOccupied()) {
            legalMoves.add(new Move.MajorMove(board, this, destionationTileCoordinate));
          } else {
            Piece pieceAtTile = destinationTile.getPiece();
            if (pieceAtTile.getPieceAlliance() != this.pieceAlliance) {
              legalMoves.add(
                  new Move.MajorAttackMove(board, this, destionationTileCoordinate, pieceAtTile));
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
    return new Rook(move.getDestinationCoordinate(), movedPiece.getPieceAlliance());
  }

  @Override
  public String toString() {
    return PieceType.ROOK.toString();
  }

  private static boolean isFirstColumnExclusion(int piecePosition, int destinationCoordinate) {
    return BoardUtils.FIRST_COLUMN[piecePosition] && destinationCoordinate == -1;
  }

  private static boolean isEighthColumnExclusion(int piecePosition, int destinationCoordinate) {
    return BoardUtils.EIGHTH_COLUMN[piecePosition] && destinationCoordinate == 1;
  }
}
