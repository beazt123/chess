package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;


public class Table {
    private static final Dimension OUTER_FRAME_DIMENSION
            = new Dimension(900,900);
    private static final Dimension BOARD_PANEL_DIMENSION
            = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION
            = new Dimension(10, 10);
    private static final Color lightTileColor = Color.decode("#FFFACD");
    private static final Color darkTileColor = Color.decode("#593E1A");

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;

    private static final String defaultPieceImagesPath = Paths.get("art","simple").toString();
    private boolean highlightLegalMoves;
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    BoardDirection boardDirection;

    private final MoveLog moveLog;


    public Table() {
        this.gameFrame = new JFrame("JChess");
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        gameHistoryPanel = new GameHistoryPanel();
        takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);


    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckBox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckBox.isSelected();
            }
        });

        preferencesMenu.add(legalMoveHighlighterCheckBox);
        return preferencesMenu;
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN file");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file");
            }
        });
        fileMenu.add(openPGN);
        return fileMenu;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8,8));
            boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.decode("#8B4726"));
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            Collection<TilePanel> tp = boardDirection.traverse(boardTiles);
            for (final TilePanel tilePanel : tp) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {
            super(new GridBagLayout());

            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(isLeftMouseButton(e)) {
                        if(sourceTile == null) {
                            System.out.println("1st click at " + tileId);
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
//                            System.out.println("Available moves: " + humanMovedPiece.calculateLegalMoves(chessBoard).toString());
                            if (humanMovedPiece == null) {
                                System.out.println("Empty tile clicked. Undoing..");
                                sourceTile = null;
                            }
                        } else {
                            System.out.println("2nd click at " + tileId);
                            destinationTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                    sourceTile.getTileCoordinate(),
                                    destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            System.out.println(transition.getMoveStatus());
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                    } else if (isRightMouseButton(e)) {
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gameHistoryPanel.redo(chessBoard, moveLog);
                            takenPiecesPanel.redo(moveLog);
                            boardPanel.drawBoard(chessBoard);
                        }
                    });
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            validate();
        }

        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)){
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            final String filePathToGreenDot = Paths.get("art","misc", "green_dot.png").toString();
                            add(new JLabel(new ImageIcon(ImageIO.read(new File(filePathToGreenDot)))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTilePieceIcon(final Board board) {
             this.removeAll();
             if (board.getTile(tileId)
                     .isTileOccupied()) {
                 try {
                     final String fileName = board.getTile(tileId)
                                                 .getPiece()
                                                 .getPieceAlliance()
                                                 .toString()
                                                 .substring(0, 1)
                             + board.getTile(tileId).getPiece().toString()
                             + ".gif";
                     final String pathToFile = Paths.get(defaultPieceImagesPath, fileName).toString();
                     final BufferedImage image = ImageIO.read(new File(pathToFile));
                     add(new JLabel(new ImageIcon(image)));
                 } catch (Exception e) {
                     //TODO: handle exception
                 }
                    

             }
         }

        private void assignTileColor() {
            if (BoardUtils.EIGHTH_RANK[tileId]
                    || BoardUtils.SIXTH_RANK[tileId]
                    || BoardUtils.FOURTH_RANK[tileId]
                    || BoardUtils.SECOND_RANK[tileId]) {
                setBackground(tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[tileId]
                    || BoardUtils.FIFTH_RANK[tileId]
                    || BoardUtils.THIRD_RANK[tileId]
                    || BoardUtils.FIRST_RANK[tileId]) {
                setBackground(tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return BoardDirection.FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return BoardDirection.NORMAL;
            }
        };
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    public static class MoveLog {
        private final List<Move> moves;

        public MoveLog() {
            this.moves = new ArrayList<>();
        }



        public List<Move> getMoves() {
            return moves;
        }

        public void addMove(Move move) {
            moves.add(move);
        }

        public int size() {
            return moves.size();
        }

        public void clear() {
            moves.clear();
        }

        public Move removeMove(int index) {
            return moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return moves.remove(move);
        }
    }

}
