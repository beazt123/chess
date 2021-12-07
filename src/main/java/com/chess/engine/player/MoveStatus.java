package com.chess.engine.player;

public enum MoveStatus {
  DONE {
    @Override
    public boolean isDone() {
      return true;
    }
  },
  LEAVES_PLAYER_IN_CHECK {
    @Override
    public boolean isDone() {
      return false;
    }
  },
  ILLEGAL_MOVE {
    @Override
    public boolean isDone() {
      return false;
    }
  };

  public abstract boolean isDone();
}
