package gui;

import game.ConnectFour;

import java.io.*;
import java.net.Socket;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayConnectFour extends JFrame
{
    public static final int WIDTH = 716;
    public static final int HEIGHT = 664;

    private JPanel statusPanel;
    private JPanel gamePanel;

    private ConnectFour game;

    private PrintWriter outToServer;
    private Socket connection;
    private Scanner inFromServer;

    public static void main(String[] args)
    {
        String address;
        int port;

        try
        {
            if (args.length > 0)
            {
                address = args[0];
            }
            else
            {
                address =
                    JOptionPane.showInputDialog("Enter the server address");
            }

            if (args.length > 1)
            {
                port = Integer.parseInt(args[1]);
            }
            else
            {
                port = Integer.parseInt(
                    JOptionPane.showInputDialog("Enter the server port"));
            }

            PlayConnectFour window = new PlayConnectFour(address, port);
            window.setVisible(true);
            ConnectFourStatusPanel.showInstructions();
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Invalid port number", "Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public PlayConnectFour(String serverAddress, int serverPort)
    {
        super("Connect Four");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        try
        {
            System.out.println("Connecting to server...");

            connection = new Socket(serverAddress, serverPort);
            outToServer = new PrintWriter(connection.getOutputStream());
            inFromServer = new Scanner(connection.getInputStream());

            System.out.println("Connected to server...");
    
            game = new ConnectFour();
            game.setServerOutput(outToServer);

            statusPanel = new ConnectFourStatusPanel(game);
            gamePanel = new ConnectFourPanel(game);

            add(statusPanel, BorderLayout.NORTH);
            add(gamePanel, BorderLayout.CENTER);

            game.addObserver((Observer) statusPanel);
            game.addObserver((Observer) gamePanel);

            addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    String ObjButtons[] = {"Quit", "Cancel"};
                    int PromptResult = JOptionPane.showOptionDialog(null,
                        "Are you sure you want to quit?", "Connect Four",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, ObjButtons, ObjButtons[1]);
                    if (PromptResult == 0)
                    {
                        game.sendToServer("quit");
                        try
                        {
                            connection.close();
                        }
                        catch (IOException ex)
                        {
                            System.err.println(ex);
                        }
                        System.exit(0);
                    }
                }
            });

            new Thread()  // Listener thread
            {
                public void run()
                {
                    String[] command;

                    try
                    {
                        while (true)
                        {
                            command = inFromServer.nextLine().split(" ");
                            
                            if (command[0].equals("play"))
                            {
                                int column = Integer.parseInt(command[1]);
                                game.playTurn(column);
                            }
                            else if (command[0].equals("start"))
                            {
                                game.start();
                            }
                            else if (command[0].equals("setPlayer"))
                            {
                                int player = Integer.parseInt(command[1]);
                                game.setServerPlayer(player);
                            }
                            else if (command[0].equals("ping"))
                            {
                                game.sendToServer("pong");
                                System.out.println("Ping received from "
                                    + "server.");
                            }
                            else if (command[0].equals("reset"))
                            {
                                game.resetGame();
                            }
                            else if (command[0].equals("quit"))
                            {
                               System.out.println("The other player has quit.");
                               game.setFinished(true);
                               ((ConnectFourStatusPanel) statusPanel).setStatus(
                                    "The other player has quit.");
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.println("Disconnected from server.");
                    }
                }
            }.start();
        }
        catch (IOException e)
        {
            String message = "Could not connect to " + serverAddress
                + " on port " + serverPort;
            System.err.println(message);
            JOptionPane.showMessageDialog(null, message, "Error", 
                JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }
    }
}
