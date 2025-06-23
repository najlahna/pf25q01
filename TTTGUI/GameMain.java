package TTTGUI;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(255, 255, 255);
    public static final Color COLOR_CROSS = new Color(216, 7, 239);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    private boolean endSoundPlayed = false;

    public GameMain() {
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

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
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            if (!endSoundPlayed) {
                playSound("sounds/draw.wav");
                endSoundPlayed = true;
            }
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            if (!endSoundPlayed) {
                playSound("sounds/win.wav");
                endSoundPlayed = true;
            }
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            if (!endSoundPlayed) {
                playSound("sounds/win.wav");
                endSoundPlayed = true;
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        boolean wrongPassword = true;
        do {
            Scanner sc = new Scanner(System.in);
            System.out.print("Username: ");
            String un = sc.next();
            System.out.print("Password: ");
            String pass = sc.next();
            String rPassword = retrievePassword(un);
            if (pass.equals(rPassword)) {
                wrongPassword = false;
                System.out.println("Login successful, enjoy the game!");
            } else {
                System.out.println("Wrong password! Please try again.");
                playSound("sounds/error.wav");
            }
        } while (wrongPassword);

        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
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

        if (host == null || port == null || databaseName == null) {
            System.out.println("Host, port, database information is required");
            return rPassword;
        }

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
