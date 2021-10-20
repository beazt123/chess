package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;


public class Table {
    private static final Dimension OUTER_FRAME_DIMENSION
            = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION
            = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION
            = new Dimension(10, 10);
    private static final Color lightTileColor = Color.decode("#FFFACD");
    private static final Color darkTileColor = Color.decode("#593E1A");

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private static String defaultPieceImagesPath = Paths.get("art","simple").toString();
    private Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;


    public Table() {
        this.gameFrame = new JFrame("JChess");
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);

    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
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
            for (final TilePanel tilePanel : boardTiles) {
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
                            System.out.println("Available moves: " + humanMovedPiece.calculateLegalMoves(chessBoard).toString());
//                            System.out.println();
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
                                System.out.println(chessBoard);
                                //TODO: Add move to move log
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
            validate();
            repaint();
        }
    }

}
