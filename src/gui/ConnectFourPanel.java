package gui;

import game.ConnectFour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;

public class ConnectFourPanel extends JPanel implements Observer
{
    public static final int SLOT_SIZE = 80;
    public static final int SLOT_BORDER = 2;

    public static final Color PLAYER_COLORS[] = {Color.WHITE, Color.RED,
                                                 Color.YELLOW, Color.GREEN,
                                                 Color.MAGENTA};

    private ConnectFour game;

    private int buttonWidth;
    private int columnOffset;

    private int buttonHeight;
    private int rowOffset;

    private int highlightedColumn;
    private boolean mouseInPanel;

    private int columnSelectorBounce;

    public ConnectFourPanel(ConnectFour game)
    {
        super();
        setBackground(Color.BLUE);

        this.game = game;

        highlightedColumn = 0;
        mouseInPanel = false;

        columnSelectorBounce = 0;

        setBorder(BorderFactory.createLineBorder(Color.BLACK, SLOT_BORDER));

        addMouseListener(new ColumnMouseListener());
        addMouseMotionListener(new ColumnMouseMotionListener());

        new Timer(10, new WobbleAction()).start();
    }

    private void updateButtonSizes()
    {
        buttonWidth = getWidth() / game.getNumCols();
        columnOffset = (buttonWidth - SLOT_SIZE) / 2;

        buttonHeight = getHeight() / game.getNumRows();
        rowOffset = (buttonHeight - SLOT_SIZE) / 2;
    }

    public void update(Observable o, Object arg)
    {
        repaint();
    }

    public void paintComponent(Graphics g1)
    {
        super.paintComponent(g1);

        // Set up antialiasing to fix jagged edges
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        updateButtonSizes();

        int row;
        int col;
        int player;

        for (row = 0; row < game.getNumRows(); row++)
        {
            for (col = 0; col < game.getNumCols(); col++)
            {
                g.setColor(Color.BLACK);

                g.fillOval(buttonWidth * col + columnOffset - SLOT_BORDER,
                           buttonHeight * (game.getNumRows() - row - 1)
                               + rowOffset - SLOT_BORDER,
                           SLOT_SIZE + SLOT_BORDER * 2,
                           SLOT_SIZE + SLOT_BORDER * 2);

                player = game.pieceAt(row, col);

                g.setColor(getPlayerColor(player));

                g.fillOval(buttonWidth * col + columnOffset,
                           buttonHeight * (game.getNumRows() - row - 1)
                               + rowOffset,
                           SLOT_SIZE, SLOT_SIZE);
            }
        }

        if (mouseInPanel && game.isStarted() && !game.isFinished()
            && game.myTurn())
        {
            g.setColor(Color.BLACK);
            g.fillOval(
                buttonWidth * highlightedColumn + columnOffset - SLOT_BORDER,
                -SLOT_SIZE / 2 - SLOT_BORDER + columnSelectorBounce,
                SLOT_SIZE + SLOT_BORDER * 2, SLOT_SIZE + SLOT_BORDER * 2);

            g.setColor(getPlayerColor(game.getCurrentPlayer()));
            g.fillOval(buttonWidth * highlightedColumn + columnOffset,
                -SLOT_SIZE / 2 + columnSelectorBounce, SLOT_SIZE, SLOT_SIZE);
        }
    }

    private Color getPlayerColor(int player)
    {
        if (player < PLAYER_COLORS.length)
            return PLAYER_COLORS[player];
        else
            return Color.GRAY;
    }

    private class ColumnMouseListener extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if (game.isStarted() && !game.isFinished() && game.myTurn())
               if (game.playTurn(highlightedColumn))
                   game.sendToServer("play " + highlightedColumn);
        }

        public void mouseEntered(MouseEvent e)
        {
            mouseInPanel = true;
            repaint();
        }

        public void mouseExited(MouseEvent e)
        {
            mouseInPanel = false;
            repaint();
        }
    }

    private class ColumnMouseMotionListener extends MouseMotionAdapter
    {
        public void mouseMoved(MouseEvent e)
        {
            int newColumn = -1;

            for (int xOffset = e.getX(); xOffset >= 0;
                    xOffset -= buttonWidth)
                newColumn++;

            if (newColumn != highlightedColumn)
                repaint();

            highlightedColumn = newColumn;
        }
    }

    private class WobbleAction implements ActionListener
    {
        private final int WOBBLE_AMOUNT = 10;
        private final double WOBBLE_SPEED = 200.0;

        public void actionPerformed(ActionEvent e)
        {
            columnSelectorBounce =
                (int) (Math.sin(System.currentTimeMillis() / WOBBLE_SPEED)
                    * WOBBLE_AMOUNT);
            repaint();
        }
    }
}
