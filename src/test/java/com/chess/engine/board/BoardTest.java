package com.chess.engine.board;

import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.Minimax;
import com.chess.engine.player.ai.MoveStrategy;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {
  private static final String[] files = new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};

  @Test
  public void initialBoard() {

    final Board board = Board.createStandardBoard();
    assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
    assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
    assertFalse(board.currentPlayer().isInCheck());
    assertFalse(board.currentPlayer().isInCheckMate());
    //        assertFalse(board.currentPlayer().isCastled());
    //        assertTrue(board.currentPlayer().isKingSideCastleCapable());
    //        assertTrue(board.currentPlayer().isQueenSideCastleCapable());
    assertEquals(board.currentPlayer(), board.whitePlayer());
    assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
    assertFalse(board.currentPlayer().getOpponent().isInCheck());
    assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
    //        assertFalse(board.currentPlayer().getOpponent().isCastled());
    //        assertTrue(board.currentPlayer().getOpponent().isKingSideCastleCapable());
    //        assertTrue(board.currentPlayer().getOpponent().isQueenSideCastleCapable());
    assertTrue(board.whitePlayer().toString().equals("White"));
    assertTrue(board.blackPlayer().toString().equals("Black"));

    //        final Iterable<Piece> allPieces = board.getAllPieces();
    //        final Iterable<Move> allMoves = Iterables.concat(board.whitePlayer().getLegalMoves(),
    // board.blackPlayer().getLegalMoves());
    //        for(final Move move : allMoves) {
    //            assertFalse(move.isAttack());
    //            assertFalse(move.isCastlingMove());
    //            assertEquals(MoveUtils.exchangeScore(move), 1);
    //        }

    //        assertEquals(Iterables.size(allMoves), 40);
    //        assertEquals(Iterables.size(allPieces), 32);
    //        assertFalse(BoardUtils.isEndGame(board));
    //        assertFalse(BoardUtils.isThreatenedBoardImmediate(board));
    //        assertEquals(StandardBoardEvaluator.get().evaluate(board, 0), 0);
    //        assertEquals(board.getPiece(35), null);
  }

  //    @Test
  //    public void testAlgebraicNotation() {
  //        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
  //            assertEquals(BoardUtils.getPositionAtCoordinate(i), boardCoordToPosition.get(i));
  //        }
  //    }

  //    @Test(expected=RuntimeException.class)
  //    public void testInvalidBoard() {}
  //
  //    @Test
  //    public void testBoardConsistency() {}
  //

  @Test
  public void mem() {
    final Runtime runtime = Runtime.getRuntime();
    long start, end;
    runtime.gc();
    start = runtime.freeMemory();
    Board board = Board.createStandardBoard();
    end = runtime.freeMemory();
    System.out.println("That took " + (start - end) + " bytes.");
  }

  @Test
  public void testFoolsMate() {
    final Board board = Board.createStandardBoard();
    final MoveTransition t1 =
        board
            .currentPlayer()
            .makeMove(
                Move.MoveFactory.createMove(
                    board,
                    BoardUtils.getCoordinateAtPosition("f2"),
                    BoardUtils.getCoordinateAtPosition("f3")));
    assertTrue(t1.getMoveStatus().isDone());

    final MoveTransition t2 =
        t1.getTransitionBoard()
            .currentPlayer()
            .makeMove(
                Move.MoveFactory.createMove(
                    t1.getTransitionBoard(),
                    BoardUtils.getCoordinateAtPosition("e7"),
                    BoardUtils.getCoordinateAtPosition("e5")));

    assertTrue(t2.getMoveStatus().isDone());

    final MoveTransition t3 =
        t2.getTransitionBoard()
            .currentPlayer()
            .makeMove(
                Move.MoveFactory.createMove(
                    t2.getTransitionBoard(),
                    BoardUtils.getCoordinateAtPosition("g2"),
                    BoardUtils.getCoordinateAtPosition("g4")));

    assertTrue(t3.getMoveStatus().isDone());

    final MoveStrategy strategy = new Minimax(4);

    final Move aiMove = strategy.execute(t3.getTransitionBoard());

    final Move bestMove =
        Move.MoveFactory.createMove(
            t3.getTransitionBoard(),
            BoardUtils.getCoordinateAtPosition("d8"),
            BoardUtils.getCoordinateAtPosition("h4"));

    assertEquals(aiMove, bestMove);
  }
}
