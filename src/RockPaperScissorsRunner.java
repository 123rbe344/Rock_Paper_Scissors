import javax.swing.*;

/**
 * Launches the Rock Paper Scissors game.
 */
public class RockPaperScissorsRunner
{
    /**
     * Creates and shows the game frame.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            RockPaperScissorsFrame frame = new RockPaperScissorsFrame();
            frame.setTitle("Rock Paper Scissors Game");
            frame.setSize(700, 650);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
