// Represents a Connect Four board

package game;

public class Board
{
    public static final int DEFAULT_ROWS = 6;
    public static final int DEFAULT_COLUMNS = 7;

    private int board[][];
    private int numRows;
    private int numColumns;

    // Creates a board with DEFAULT_ROWS rows and DEFAULT_COLUMNS numColumns.
    public Board()
    {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    // Creates a board with numRows and numColumns.
    //
    // Throws IllegalArgumentException if numRows < 1 or numColumns < 1.
    public Board(int numRows, int numColumns)
    {
        if (numRows < 1 || numColumns < 1)
            throw new IllegalArgumentException("The board size is too small");

        this.numRows = numRows;
        this.numColumns = numColumns;

        board = new int[numRows][numColumns];

        clearBoard();
    }

    // Adds a player’s piece to the lowest empty slot in a column.
    //
    // Preconditions: column is in the range [0, numColumns)
    //                player > 0
    //
    // Throws an InvalidMoveException if the column is full
    // or if an invalid column is given.
    // 
    // Returns the row where the piece was added.
    public int addPiece(int column, int player)
    {
        if (columnFull(column))
            throw new InvalidMoveException("The column is full");

        int row;

        for (row = 0; row < numRows && board[row][column] != 0; row++)
            ;

        board[row][column] = player;

        return row;
    }

    // Returns the player number of the piece at the specified row and column.
    //
    // Preconditions: row is in the range [0, rows)
    //                column is in the range [0, numColumns)
    //
    // Throws IllegalArgumentException if either row or column is invalid
    public int pieceAt(int row, int column) 
    {
        if (row < 0 || row > numRows - 1 || column < 0 || column > numColumns - 1)
            throw new IllegalArgumentException("Row and column must be within"
                + " the bounds of the board.");

        return board[row][column];
    }

    // Returns true if the entire board is full.
    public boolean isFull()
    {
        for (int col = 0; col < numColumns; col++)
            if (board[numRows - 1][col] == 0) // If any given column is not full,
                return false;              // the board is not full.

        return true;
    }

    // Returns the number of pieces in a column.
    //
    // Precondition: column is in the range [0, numColumns)
    //
    // Throws IllegalArgumentException if column is invalid
    public int columnSize(int column)
    {
        if (column < 0 || column >= numColumns)
            throw new IllegalArgumentException("Column must be in the range "
                + "[0, numColumns)");

        int row;
        for (row = 0; row < numRows && board[row][column] != 0; row++)
            ;

        return row;
    }

    // Returns true if the column is full.
    //
    // Precondition: column is in the range [0, numColumns)
    //
    // Throws IllegalArgumentException if column is invalid
    public boolean columnFull(int column)
    {
        if (column < 0 || column >= numColumns)
            throw new IllegalArgumentException("Column must be in the range "
                + "[0, numColumns)");

        return board[numRows - 1][column] != 0;
    }


    // Removes all pieces from the board.
    public void clearBoard()
    {
        int row;
        int col;

        for (row = 0; row < numRows; row++)
            for (col = 0; col < numColumns; col++)
                board[row][col] = 0;
    }

    public int getNumRows()
    {
        return numRows;
    }

    public int getNumCols()
    {
        return numColumns;
    }

    public String toString()
    {
        int row;
        int col;

        String result = "";

        for (row = numRows - 1; row >= 0; row--)
        {
            result += "|";
            for (col = 0; col < numColumns; col++)
                if (board[row][col] == 0)
                    result += " |";
                else
                    result += board[row][col] + "|";
            result += "\n";
        }

        return result;
    }
}
