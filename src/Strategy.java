/**
 * A Strategy determines the computer's move in a game of Rock Paper Scissors.
 * This is the Strategy Design Pattern: each implementation is a different
 * algorithm for choosing the computer's symbol.
 */
public interface Strategy
{
    /**
     * Determines the computer's move.
     *
     * @param playerMove the player's move for this round: "R", "P", or "S"
     * @return the computer's move: "R", "P", or "S"
     */
    public String getMove(String playerMove);
}
