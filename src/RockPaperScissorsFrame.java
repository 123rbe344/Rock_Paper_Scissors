import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * GUI frame for a Rock Paper Scissors game played against the computer.
 * The computer picks its move using one of five Strategy implementations,
 * chosen by probability each round.
 */
public class RockPaperScissorsFrame extends JFrame
{
    JPanel mainPnl;
    JPanel titlePnl;
    JPanel controlPnl;
    JPanel buttonPnl;
    JPanel statsPnl;
    JPanel resultsPnl;
    JLabel titleLbl;
    JButton rockBtn;
    JButton paperBtn;
    JButton scissorsBtn;
    JButton quitBtn;
    JLabel playerWinsLbl;
    JLabel computerWinsLbl;
    JLabel tiesLbl;
    JTextField playerWinsTF;
    JTextField computerWinsTF;
    JTextField tiesTF;
    JTextArea resultsTA;
    JScrollPane scroller;
    Font titleFont = new Font("SansSerif", Font.BOLD, 32);
    Font buttonFont = new Font("SansSerif", Font.BOLD, 16);
    Font statsFont = new Font("SansSerif", Font.BOLD, 16);
    Font resultsFont = new Font("Monospaced", Font.PLAIN, 14);

    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    // Player history used by the inner strategy classes
    private int rockCnt = 0;
    private int paperCnt = 0;
    private int scissorsCnt = 0;
    private String lastPlayerMove = "";

    private Random rnd = new Random();
    private Strategy cheat = new Cheat();
    private Strategy random = new RandomStrategy();
    private Strategy leastUsed = new LeastUsed();
    private Strategy mostUsed = new MostUsed();
    private Strategy lastUsed = new LastUsed();

    /**
     * Builds the game frame and all of its panels.
     */
    public RockPaperScissorsFrame()
    {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        createTitlePanel();
        mainPnl.add(titlePnl, BorderLayout.NORTH);
        createResultsPanel();
        mainPnl.add(resultsPnl, BorderLayout.CENTER);
        add(mainPnl);
    }

    /**
     * Creates the top panel holding the game title, the move buttons and the stats panel.
     */
    private void createTitlePanel()
    {
        titlePnl = new JPanel();
        titlePnl.setLayout(new BorderLayout());
        titleLbl = new JLabel("Rock Paper Scissors", SwingConstants.CENTER);
        titleLbl.setFont(titleFont);
        titlePnl.add(titleLbl, BorderLayout.NORTH);
        createControlPanel();
        titlePnl.add(controlPnl, BorderLayout.CENTER);
    }

    /**
     * Creates the panel holding the move buttons and the stats panel.
     */
    private void createControlPanel()
    {
        controlPnl = new JPanel();
        controlPnl.setLayout(new BorderLayout());
        createButtonPanel();
        controlPnl.add(buttonPnl, BorderLayout.NORTH);
        createStatsPanel();
        controlPnl.add(statsPnl, BorderLayout.SOUTH);
    }

    /**
     * Creates the Rock, Paper, Scissors and Quit buttons. The three move
     * buttons share a single ActionListener.
     */
    private void createButtonPanel()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1, 4, 10, 0));
        buttonPnl.setBorder(new TitledBorder("Make Your Move"));

        rockBtn = createButton("Rock", "rock.png");
        paperBtn = createButton("Paper", "paper.png");
        scissorsBtn = createButton("Scissors", "scissors.png");
        quitBtn = createButton("Quit", "quit.png");

        MoveListener listener = new MoveListener();
        rockBtn.addActionListener(listener);
        paperBtn.addActionListener(listener);
        scissorsBtn.addActionListener(listener);
        quitBtn.addActionListener((ae) -> System.exit(0));

        buttonPnl.add(rockBtn);
        buttonPnl.add(paperBtn);
        buttonPnl.add(scissorsBtn);
        buttonPnl.add(quitBtn);
    }

    /**
     * Creates a button with an icon above its text.
     *
     * @param text the button text
     * @param imageName the name of the icon image in the src directory
     * @return the new button
     */
    private JButton createButton(String text, String imageName)
    {
        JButton btn = new JButton(text);
        URL url = getClass().getResource("/" + imageName);
        if (url != null)
        {
            btn.setIcon(new ImageIcon(url));
        }
        btn.setFont(buttonFont);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * Creates the stats panel showing running totals for the session.
     */
    private void createStatsPanel()
    {
        statsPnl = new JPanel();
        statsPnl.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 4));
        statsPnl.setBorder(new TitledBorder("Stats"));

        playerWinsLbl = new JLabel("Player Wins:");
        computerWinsLbl = new JLabel("Computer Wins:");
        tiesLbl = new JLabel("Ties:");
        playerWinsTF = createStatField();
        computerWinsTF = createStatField();
        tiesTF = createStatField();

        playerWinsLbl.setFont(statsFont);
        computerWinsLbl.setFont(statsFont);
        tiesLbl.setFont(statsFont);

        statsPnl.add(playerWinsLbl);
        statsPnl.add(playerWinsTF);
        statsPnl.add(computerWinsLbl);
        statsPnl.add(computerWinsTF);
        statsPnl.add(tiesLbl);
        statsPnl.add(tiesTF);
    }

    /**
     * Creates a read only text field for a stat count.
     *
     * @return the new text field
     */
    private JTextField createStatField()
    {
        JTextField tf = new JTextField("0", 4);
        tf.setEditable(false);
        tf.setFont(statsFont);
        tf.setHorizontalAlignment(JTextField.CENTER);
        return tf;
    }

    /**
     * Creates the panel with a scrolling text area that lists every game result.
     */
    private void createResultsPanel()
    {
        resultsPnl = new JPanel();
        resultsPnl.setLayout(new BorderLayout());
        resultsPnl.setBorder(new TitledBorder("Results"));
        resultsTA = new JTextArea(12, 50);
        resultsTA.setFont(resultsFont);
        resultsTA.setEditable(false);
        scroller = new JScrollPane(resultsTA);
        resultsPnl.add(scroller, BorderLayout.CENTER);
    }

    /**
     * Single listener for the Rock, Paper and Scissors buttons. Uses the
     * ActionEvent source to determine the player's move.
     */
    private class MoveListener implements ActionListener
    {
        /**
         * Plays one round using the clicked button as the player's move.
         *
         * @param ae the button click event
         */
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            Object src = ae.getSource();
            if (src == rockBtn)
            {
                playRound("R");
            }
            else if (src == paperBtn)
            {
                playRound("P");
            }
            else if (src == scissorsBtn)
            {
                playRound("S");
            }
        }
    }

    /**
     * Plays one round: picks a strategy by probability, gets the computer's
     * move, scores the round, and updates the display.
     *
     * @param playerMove the player's move: "R", "P", or "S"
     */
    private void playRound(String playerMove)
    {
        int prob = rnd.nextInt(100) + 1;
        Strategy strategy;
        String strategyName;

        if (prob <= 10)
        {
            strategy = cheat;
            strategyName = "Cheat";
        }
        else if (prob <= 30)
        {
            strategy = leastUsed;
            strategyName = "Least Used";
        }
        else if (prob <= 50)
        {
            strategy = mostUsed;
            strategyName = "Most Used";
        }
        else if (prob <= 70 && !lastPlayerMove.isEmpty())
        {
            strategy = lastUsed;
            strategyName = "Last Used";
        }
        else
        {
            // Also used for Last Used on the first round, when there is no last move yet
            strategy = random;
            strategyName = "Random";
        }

        String computerMove = strategy.getMove(playerMove);
        recordPlayerMove(playerMove);

        String result;
        if (playerMove.equals(computerMove))
        {
            ties++;
            result = name(playerMove) + " vs " + name(computerMove) + ". (Tie! Computer: " + strategyName + ")";
        }
        else if (beats(playerMove, computerMove))
        {
            playerWins++;
            result = describe(playerMove) + ". (Player wins! Computer: " + strategyName + ")";
        }
        else
        {
            computerWins++;
            result = describe(computerMove) + ". (Computer wins! Computer: " + strategyName + ")";
        }

        resultsTA.append(result + "\n");
        playerWinsTF.setText("" + playerWins);
        computerWinsTF.setText("" + computerWins);
        tiesTF.setText("" + ties);
    }

    /**
     * Records the player's move for the history based strategies.
     *
     * @param playerMove the player's move: "R", "P", or "S"
     */
    private void recordPlayerMove(String playerMove)
    {
        switch (playerMove)
        {
            case "R":
                rockCnt++;
                break;
            case "P":
                paperCnt++;
                break;
            case "S":
                scissorsCnt++;
                break;
        }
        lastPlayerMove = playerMove;
    }

    /**
     * Determines whether one symbol beats another.
     *
     * @param a the first symbol
     * @param b the second symbol
     * @return true if a beats b
     */
    private boolean beats(String a, String b)
    {
        return (a.equals("R") && b.equals("S")) ||
                (a.equals("P") && b.equals("R")) ||
                (a.equals("S") && b.equals("P"));
    }

    /**
     * Builds the description of how the winning symbol beats the other one.
     *
     * @param winner the winning symbol
     * @return text such as "Rock breaks Scissors"
     */
    private String describe(String winner)
    {
        switch (winner)
        {
            case "R":
                return "Rock breaks Scissors";
            case "P":
                return "Paper covers Rock";
            default:
                return "Scissors cuts Paper";
        }
    }

    /**
     * Converts a symbol to its full name.
     *
     * @param move "R", "P", or "S"
     * @return "Rock", "Paper", or "Scissors"
     */
    private String name(String move)
    {
        switch (move)
        {
            case "R":
                return "Rock";
            case "P":
                return "Paper";
            default:
                return "Scissors";
        }
    }

    /**
     * Returns the symbol that beats the given symbol.
     *
     * @param move "R", "P", or "S"
     * @return the symbol that beats move
     */
    private String winnerAgainst(String move)
    {
        switch (move)
        {
            case "R":
                return "P";
            case "P":
                return "S";
            default:
                return "R";
        }
    }

    /**
     * Picks one of the symbols whose count matches the target, breaking ties at random.
     *
     * @param target the count to match
     * @return a symbol with that count
     */
    private String pickWithCount(int target)
    {
        ArrayList<String> matches = new ArrayList<>();
        if (rockCnt == target)
        {
            matches.add("R");
        }
        if (paperCnt == target)
        {
            matches.add("P");
        }
        if (scissorsCnt == target)
        {
            matches.add("S");
        }
        return matches.get(rnd.nextInt(matches.size()));
    }

    /**
     * Strategy that assumes the player will next use the symbol they have used
     * the least, and picks the symbol that beats it. An inner class because it
     * needs the player's symbol counts.
     */
    private class LeastUsed implements Strategy
    {
        /**
         * Returns the symbol that beats the player's least used symbol.
         *
         * @param playerMove the player's move for this round (not used)
         * @return the computer's move
         */
        @Override
        public String getMove(String playerMove)
        {
            int min = Math.min(rockCnt, Math.min(paperCnt, scissorsCnt));
            return winnerAgainst(pickWithCount(min));
        }
    }

    /**
     * Strategy that assumes the player will next use the symbol they have used
     * the most, and picks the symbol that beats it. An inner class because it
     * needs the player's symbol counts.
     */
    private class MostUsed implements Strategy
    {
        /**
         * Returns the symbol that beats the player's most used symbol.
         *
         * @param playerMove the player's move for this round (not used)
         * @return the computer's move
         */
        @Override
        public String getMove(String playerMove)
        {
            int max = Math.max(rockCnt, Math.max(paperCnt, scissorsCnt));
            return winnerAgainst(pickWithCount(max));
        }
    }

    /**
     * Strategy that plays the symbol the player used on the previous round
     * (tit for tat). An inner class because it needs the player's last move.
     * Only used after the first round.
     */
    private class LastUsed implements Strategy
    {
        /**
         * Returns the player's move from the previous round.
         *
         * @param playerMove the player's move for this round (not used)
         * @return the player's last move
         */
        @Override
        public String getMove(String playerMove)
        {
            return lastPlayerMove;
        }
    }
}
