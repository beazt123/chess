package com.chess.gui;

import com.chess.engine.board.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.chess.engine.board.Board;


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

    public Table() {
        gameFrame = new JFrame("JChess");
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);
        boardPanel = new BoardPanel();
        gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        gameFrame.setVisible(true);

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
    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();
        }

        // private void assignTilePieceIcon(final Board board) {
        //     this.removeAll();
        //     if (board.getTile(tileId).isTileOccupied()) {
        //         try {
        //             final BufferedImage image = ImageIO.read(new File(pieceIconPath + board.getTile(tileId)
        //                                                                                     .getPiece()
        //                                                                                     .getPieceAlliance()
        //                                                                                     .toString()
        //                                                                                     .substring(0,1))
        //                                                                             + board.getTile(tileId).getPiece().toString() + ".gif");
        //             add(new JLabel(new ImageIcon(image)));
        //         } catch (Exception e) {
        //             //TODO: handle exception
        //         }
                    

        //     }
        // }

        private void assignTileColor() {
            if (BoardUtils.FIRST_ROW[tileId]
                    || BoardUtils.THIRD_ROW[tileId]
                    || BoardUtils.FIFTH_ROW[tileId]
                    || BoardUtils.SEVENTH_ROW[tileId]) {
                System.out.println("light");
                setBackground(tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SECOND_ROW[tileId]
                    || BoardUtils.FOURTH_ROW[tileId]
                    || BoardUtils.SIXTH_ROW[tileId]
                    || BoardUtils.EIGHTH_ROW[tileId]) {
                System.out.println("dark");
                setBackground(tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
            System.out.println(tileId);
        }
    }

}
