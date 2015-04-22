// Contains a Board and information about the state of the game

package game;

import java.io.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Observable;

public class ConnectFour extends Observable
{
    public static final int MIN_ROWS = 6;
    public static final int MIN_COLS = 7;
    public static final int MIN_NUM_PLAYERS = 2;
    public static final int MIN_PIECES_TO_WIN = 4;

    private int piecesToWin;
    private int rows;
    private int columns;
    private int numPlayers;

    private int currentPlayer;
    private boolean gameFinished;
    private int winner;

    private Board board;

    private PrintWriter outToServer;
    private int serverPlayer;
    private boolean gameStarted;

    // Creates the game with the default values (default Board,
    // DEFUALT_PLAYERS players, DEFAULT_PIECES_TO_WIN pieces to win).
    public ConnectFour()
    {
        this(MIN_ROWS, MIN_COLS, MIN_NUM_PLAYERS, MIN_PIECES_TO_WIN);
    }

    // Creates the Board and initializes players and pieces to win.
    //
    // Throws IllegalArgumentException if
    //    rows < MIN_ROWS, columns < MIN_COLS,
    //    players < MIN_NUM_PLAYERS, or piecesToWin < MIN_PIECES_TO_WIN
    public ConnectFour(int rows, int columns, int numPlayers, int piecesToWin)
    {
        if (rows < MIN_ROWS || columns < MIN_COLS)
            throw new IllegalArgumentException("Board size too small.");

        if (numPlayers < MIN_NUM_PLAYERS)
            throw new IllegalArgumentException("Too few players.");

        if (piecesToWin < MIN_PIECES_TO_WIN)
            throw new IllegalArgumentException("Too few pieces to win.");

        this.rows = rows;
        this.columns = columns;
        this.numPlayers = numPlayers;
        this.piecesToWin = piecesToWin;

        currentPlayer = 1;
        gameFinished = false;
        winner = 0;

        board = new Board(rows, columns);

        outToServer = null;
        serverPlayer = 0;
        gameStarted = false;
    }

    public boolean isFinished()
    {
        return gameFinished;
    }

    public void setFinished(boolean finished)
    {
        gameFinished = finished;
    }

    public int getWinner()
    {
        return winner;
    }

    public int getNumPlayers()
    {
        return numPlayers;
    }
    
    public int getNumRows()
    {
        return rows;
    }

    public int getNumCols()
    {
        return columns;
    }
    
    public int getCurrentPlayer()
    {
        return currentPlayer;
    }

    public int pieceAt(int row, int column)
    {
        return board.pieceAt(row, column);
    }

    public void setServerOutput(PrintWriter outToServer)
    {
        this.outToServer = outToServer;
    }

    public void setServerPlayer(int player)
    {
        System.out.println("Player set to " + player);
        this.serverPlayer = player;
    }

    public int getServerPlayer()
    {
        return serverPlayer;
    }

    public boolean myTurn()
    {
        return serverPlayer == 0 || currentPlayer == serverPlayer;
    }

    public boolean isStarted()
    {
        return gameStarted;
    }

    public void start()
    {
        System.out.println("Game started");
        gameStarted = true;
        notifyChange();
    }

    public synchronized void resetGame()
    {
        currentPlayer = 1;
        gameFinished = false;
        winner = 0;
        board.clearBoard();

        System.out.println("Game reset.");

        notifyChange();
    }

    // Returns true if a column's top piece was a winning move.
    // No need to check the entire board, since one piece will only affect
    // adjacent spaces.
    //
    // Precondition: checkColumn is in the range [0, columns)
    //
    // Throws IllegalArgumentException if checkColumn is invalid
    private boolean checkWin(int checkColumn)
    {
        if (checkColumn < 0 || checkColumn >= columns)
            throw new IllegalArgumentException("checkColumn must be a valid "
                + "column number.");

        int verticalRun = 0;
        int horizontalRun = 0;
        int upRightRun = 0;
        int upLeftRun = 0;

        int checkRow = board.columnSize(checkColumn) - 1;
        int checkPiece = board.pieceAt(checkRow, checkColumn);

        for (int offset = 1 - piecesToWin;
            !gameFinished && offset < piecesToWin; offset++)
        {
            // check vertical run
            if (checkRow + offset >= 0 && checkRow + offset < rows
                && board.pieceAt(checkRow + offset, checkColumn) == checkPiece)
                verticalRun++;
            else
                verticalRun = 0;

            // check horizontal run
            if (checkColumn + offset >= 0 && checkColumn + offset < columns
                && board.pieceAt(checkRow, checkColumn + offset) == checkPiece)
                horizontalRun++;
            else
                horizontalRun = 0;

            // check up right run
            if (checkRow + offset >= 0 && checkRow + offset < rows
                && checkColumn + offset >= 0 && checkColumn + offset < columns
                && board.pieceAt(checkRow + offset, checkColumn + offset)
                    == checkPiece)
                upRightRun++;
            else
                upRightRun = 0;

            // check up left run
            if (checkColumn - offset >= 0 && checkColumn - offset < columns
                && checkRow + offset >= 0 && checkRow + offset < rows
                && board.pieceAt(checkRow + offset, checkColumn - offset)
                    == checkPiece)
                upLeftRun++;
            else
                upLeftRun = 0;

            // check runs
            if (horizontalRun >= piecesToWin || upRightRun >= piecesToWin
                || verticalRun >= piecesToWin || upLeftRun >= piecesToWin)
            {
                gameFinished = true;
                winner = currentPlayer;
            }
        }

        if (board.isFull())
            gameFinished = true;

        return gameFinished;
    }

    // Places a piece for the current player in the specified column
    //
    // Predondition: column is in the range [0, columns)
    public synchronized boolean playTurn(int column)
    {
        if (column < 0 || column > columns - 1)
            throw new InvalidMoveException("Invalid column");

        boolean successful = false;

        String message = "Player " + currentPlayer
            + " tried to move in column " + (column + 1) + " and ";

        if (gameStarted && !board.columnFull(column))
        {
            board.addPiece(column, currentPlayer);

            checkWin(column);

            currentPlayer++;
            if (currentPlayer > numPlayers)
                currentPlayer = 1;

            successful = true;
        }

        if (successful)
            message += "succeeded";
        else
            message += "failed";

        System.out.println(message);

        notifyChange();

        return successful;
    }

    public void notifyChange()
    {
        setChanged();
        notifyObservers();
    }

    public void sendToServer(String command)
    {
        if (outToServer != null)
        {
            outToServer.println(command);
            outToServer.flush();
        }
    }

    public String toString()
    {
        return board.toString() + "\n" 
            + "Current Player: " + currentPlayer + "\n"
            + "Game Finished: " + gameFinished + "\n"
            + "Winner: " + winner;
    }
}
