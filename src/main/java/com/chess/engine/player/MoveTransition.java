package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public class MoveTransition {

  private final Board transitionBoard;
  private final Move move;
  private final MoveStatus
      moveStatus; // whether able to make the move. If it is legal but results in check, unable

  public MoveTransition(final Board transitionBoard, final Move move, final MoveStatus moveStatus) {
    this.transitionBoard = transitionBoard;
    this.move = move;
    this.moveStatus = moveStatus;
  }

  public MoveStatus getMoveStatus() {
    return moveStatus;
  }

  public Board getBoard() {
    return transitionBoard;
  }

  public Board getTransitionBoard() {
    return transitionBoard;
  }
}
