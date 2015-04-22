// Thread that handles input from one client and updates the second client
package server;

import java.io.*;
import java.util.Scanner;

public class ConnectFourServerThread extends Thread
{
    private Scanner inFromClientOne;
    private PrintWriter outToClientTwo;

    public ConnectFourServerThread(Scanner inFromClientOne,
        PrintWriter outToClientTwo)
    {
        this.inFromClientOne = inFromClientOne;
        this.outToClientTwo = outToClientTwo;
    }

    public void run()
    {
        String command;
        String argument;

        try
        {
            while (true)
            {
                // pass commands to the other client
                outToClientTwo.println(inFromClientOne.nextLine());
                outToClientTwo.flush();
            }
        }
        catch (Exception e)
        {
            outToClientTwo.println("quit");
            outToClientTwo.flush();
        }
    }
}
