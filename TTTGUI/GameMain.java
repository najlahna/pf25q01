package TTTGUI;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Font FONT_STATUS = new Font("Comic Sans MS", Font.PLAIN, 16);

    private Board board;
    private State currentState;
    private Seed currentPlayer;

    private JLabel statusBar;
    private JLabel playerLabel;
    private JLabel scoreLabel;
    private JButton restartButton;
    private JButton resetScoreButton;

    private int xWins = 0;
    private int oWins = 0;
    private int draws = 0;

    private boolean endSoundPlayed = false;
    private BufferedImage backgroundImage;

    public GameMain() {
        loadBackgroundImage();

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / Cell.SIZE;
                int col = e.getX() / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);
                        playSound("sounds/move.wav");
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                } else {
                    newGame();
                    endSoundPlayed = false;
                }
                repaint();
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(Color.WHITE);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Score label
        scoreLabel = new JLabel(getScoreText());
        scoreLabel.setFont(FONT_STATUS);
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 30));
        scoreLabel.setForeground(new Color(80, 80, 80));

        // Restart Game button
        restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        restartButton.setFocusPainted(false);
        restartButton.setBackground(new Color(255, 105, 180));
        restartButton.setForeground(Color.WHITE);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartButton.addActionListener(e -> {
            newGame();
            endSoundPlayed = false;
            repaint();
        });

        // Reset Score button
        resetScoreButton = new JButton("Reset Score");
        resetScoreButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
        resetScoreButton.setBackground(new Color(220, 220, 220));
        resetScoreButton.setFocusPainted(false);
        resetScoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetScoreButton.addActionListener(e -> {
            xWins = 0;
            oWins = 0;
            draws = 0;
            updateScoreLabel();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(restartButton);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(resetScoreButton);
        bottomPanel.add(Box.createVerticalStrut(5));
        bottomPanel.add(statusBar);
        bottomPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 110));

        super.setLayout(new BorderLayout());
        super.add(bottomPanel, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 110));
        super.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, false));

        initGame();
        newGame();
    }

    public JLabel getPlayerLabel() {
        if (playerLabel == null) {
            playerLabel = new JLabel("Player X vs Player O ");
            playerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            playerLabel.setForeground(new Color(255, 105, 180));
            playerLabel.setHorizontalAlignment(JLabel.CENTER);
            playerLabel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 40));
        }
        return playerLabel;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    private String getScoreText() {
        return "Score — X: " + xWins + " | O: " + oWins + " | Draws: " + draws;
    }

    private void updateScoreLabel() {
        scoreLabel.setText(getScoreText());
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("TTTGUI/images/background.png"));
        } catch (IOException e) {
            System.out.println("Background image not found!");
        }
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row)
            for (int col = 0; col < Board.COLS; ++col)
                board.cells[row][col].content = Seed.NO_SEED;

        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(Color.WHITE);
        }

        board.paint(g);

        if (currentState == State.PLAYING) {
            if (currentPlayer == Seed.CROSS) {
                statusBar.setText("X's Turn");
                statusBar.setForeground(new Color(255, 105, 180));
            } else {
                statusBar.setText("O's Turn");
                statusBar.setForeground(new Color(64, 154, 225));
            }
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            if (!endSoundPlayed) {
                draws++;
                updateScoreLabel();
                playSound("sounds/draw.wav");
                endSoundPlayed = true;
            }
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            if (!endSoundPlayed) {
                xWins++;
                updateScoreLabel();
                playSound("sounds/win.wav");
                endSoundPlayed = true;
            }
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            if (!endSoundPlayed) {
                oWins++;
                updateScoreLabel();
                playSound("sounds/win.wav");
                endSoundPlayed = true;
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        boolean successLogin = false;

        while (!successLogin) {
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Login",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                String realPassword = retrievePassword(username);
                if (password.equals(realPassword)) {
                    successLogin = true;
                    JOptionPane.showMessageDialog(null, "Login successful! Enjoy the game");
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong username or password. Try again.");
                    playSound("sounds/error.wav");
                }
            } else {
                System.exit(0);
            }
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            GameMain gamePanel = new GameMain();

            JPanel topPanel = new JPanel(new GridLayout(2, 1));
            topPanel.add(gamePanel.getPlayerLabel());
            topPanel.add(gamePanel.getScoreLabel());

            frame.setLayout(new BorderLayout());
            frame.add(topPanel, BorderLayout.PAGE_START);
            frame.add(gamePanel, BorderLayout.CENTER);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    static String retrievePassword(String uName) throws ClassNotFoundException {
        String rPassword = "";
        String host = "mysql-tictactoe-najla.c.aivencloud.com";
        String port = "28746";
        String databaseName = "tictactoe";
        String userName = "avnadmin";
        String password = "AVNS__MxxTgqLNZFvbVm6MHd";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?sslmode=require",
                userName, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT password from users where username = '" + uName + "'")) {
            while (resultSet.next()) {
                rPassword = resultSet.getString("password");
            }
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
        return rPassword;
    }

    public static void playSound(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + filePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}






