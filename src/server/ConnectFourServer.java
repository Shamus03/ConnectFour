// The server for connect four
package server;

import java.io.*;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;

public class ConnectFourServer
{
    public static void main(String[] args)
    {
        ServerSocket welcomeSocket = null;
        int serverPort = 0;

        try
        {
            if (args.length > 0)
            {
                serverPort = Integer.parseInt(args[0]);
            }
            else
            {
                Scanner scan = new Scanner(System.in);

                System.out.print("Enter the port to listen on: ");
                serverPort = scan.nextInt();
            }

            welcomeSocket = new ServerSocket(serverPort);
        }
        catch (NumberFormatException e)
        {
            System.err.println("Invalid port number.");
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port " + serverPort);
            System.exit(1);
        }

        System.out.println("Server listening on port " + serverPort
            + "...");

        try
        {
            Socket waiting = null;
            Socket current;

            while (true)
            {
                current = welcomeSocket.accept();

                if (waiting == null)
                {
                    System.out.println("Found player one.");
                    waiting = current;
                }
                else
                {
                    System.out.println("Found player two.");

                    PrintWriter outToPlayerOne =
                        new PrintWriter(waiting.getOutputStream());
                    PrintWriter outToPlayerTwo =
                        new PrintWriter(current.getOutputStream());

                    Scanner inFromPlayerOne =
                        new Scanner(waiting.getInputStream());
                    Scanner inFromPlayerTwo =
                        new Scanner(current.getInputStream());

                    outToPlayerOne.println("ping");
                    outToPlayerOne.flush();

                    if (inFromPlayerOne.hasNext()
                        && inFromPlayerOne.nextLine().equals("pong"))
                    {
                        System.out.println("Successfully pinged player 1.");

                        outToPlayerOne.println("setPlayer 1");
                        outToPlayerOne.println("start");
                        outToPlayerTwo.println("setPlayer 2");
                        outToPlayerTwo.println("start");

                        outToPlayerOne.flush();
                        outToPlayerTwo.flush();

                        new ConnectFourServerThread(
                            inFromPlayerOne, outToPlayerTwo).start();

                        new ConnectFourServerThread(
                            inFromPlayerTwo, outToPlayerOne).start();

                        System.out.println("Game starting.");
                        waiting = null;
                    }
                    else
                    {
                        System.out.println("Player one disconnected, "
                            + "reconfiguring players.");
                        waiting = current;
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
    }
}
