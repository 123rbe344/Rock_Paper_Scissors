import java.util.Random;

/**
 * A Strategy that picks the computer's move at random, as in the normal game.
 */
public class RandomStrategy implements Strategy
{
    private static final String[] MOVES = {"R", "P", "S"};
    private Random rnd = new Random();

    /**
     * Returns a randomly chosen symbol. The player's move is ignored.
     *
     * @param playerMove the player's move for this round (not used)
     * @return "R", "P", or "S" chosen at random
     */
    @Override
    public String getMove(String playerMove)
    {
        return MOVES[rnd.nextInt(MOVES.length)];
    }
}
