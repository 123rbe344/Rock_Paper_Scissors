/**
 * A Strategy where the computer cheats by looking at the player's move
 * and picking the symbol that beats it.
 */
public class Cheat implements Strategy
{
    /**
     * Returns the symbol that beats the player's move.
     *
     * @param playerMove the player's move for this round: "R", "P", or "S"
     * @return the winning symbol against playerMove
     */
    @Override
    public String getMove(String playerMove)
    {
        String computerMove = "";
        switch (playerMove)
        {
            case "R":
                computerMove = "P";
                break;
            case "P":
                computerMove = "S";
                break;
            case "S":
                computerMove = "R";
                break;
            default:
                computerMove = "X";
                break;
        }
        return computerMove;
    }
}
