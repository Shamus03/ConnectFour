// the exception to be thrown if an invalid move is made

package game;

public class InvalidMoveException extends RuntimeException
{
    public InvalidMoveException(String message)
    {
        super(message);
    }
}
