package gui;

import game.ConnectFour;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConnectFourStatusPanel extends JPanel implements Observer
{
    private ConnectFour game;
    private JLabel statusLabel;
    private JButton resetButton;

    public ConnectFourStatusPanel(ConnectFour game)
    {
        this.game = game;

        statusLabel = new JLabel("Waiting for other player...",
            SwingConstants.CENTER);

        resetButton = new JButton("New Game");
        resetButton.setEnabled(false);
        resetButton.addActionListener(new ResetButtonListener());
        JPanel resetPanel = new JPanel();
        resetPanel.add(resetButton);

        JButton instructionsButton = new JButton("Instructions");
        instructionsButton.addActionListener(new InstructionsButtonListener());
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.add(instructionsButton);

        setLayout(new BorderLayout());

        add(instructionsPanel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.CENTER);
        add(resetPanel, BorderLayout.EAST);
    }

    public static void showInstructions()
    {
        JOptionPane.showMessageDialog(null,
              "Welcome to Connect Four!\n\n"
            + "Choose a column to drop a piece.\n"
            + "The goal is to get four of your pieces\n"
            + "in a row before the other player.",
            "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showWinner()
    {
        int winner = game.getWinner();
        String status;

        if (winner == 0)
        {
            status = "The game ended in a tie.";
        }
        else
        {
            int me = game.getServerPlayer();

            if (me == 0)
            {
                status = "Player " + winner + " wins!";
            }
            else if (winner == me)
            {
                status = "You win!";
            }
            else
            {
                status = "You lose.";
            }
        }

        final String statusMessage = status; // To pass status to dialog box
        new Thread()
        {
            public void run()
            {
                JOptionPane.showMessageDialog(null,
                      statusMessage + "\n\n"
                    + "Click the New Game button to play again.");
            }
        }.start();
        
        System.out.println(status);
        statusLabel.setText(status);
    }

    public void update(Observable o, Object arg)
    {
        if (game.isFinished())
        {
            resetButton.setEnabled(true);
            showWinner();
        }
        else
        {
            resetButton.setEnabled(false);
            int me = game.getServerPlayer();
            int currentPlayer = game.getCurrentPlayer();

            if (me == 0)
            {
                setStatus("Player " + game.getCurrentPlayer() + "'s turn.");
            }
            else if (currentPlayer == me)
            {
                setStatus("It is your turn.");
            }
            else
            {
                setStatus("It is the other player's turn.");
            }
        }
    }

    public void setStatus(String status)
    {
        statusLabel.setText(status);
    }

    private class ResetButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            game.resetGame();
            game.sendToServer("reset");
        }
    }

    private class InstructionsButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            showInstructions();
        }
    }
}
