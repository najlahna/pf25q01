package TTTGUI;

import java.awt.*;

public class Cell {
    // Ukuran dan padding
    public static final int SIZE = 120;
    public static final int PADDING = SIZE / 5;
    public static final int SEED_SIZE = SIZE - PADDING * 2;

    // Isi sel dan posisi
    Seed content;
    int row, col;

    // Konstruktor
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        content = Seed.NO_SEED;
    }

    // Reset cell saat new game
    public void newGame() {
        content = Seed.NO_SEED;
    }

    // Gambar isi cell
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = col * SIZE + PADDING;
        int y = row * SIZE + PADDING;

        if (content == Seed.CROSS) {
            // Gambar bintang ungu (X)
            g2d.setColor(new Color(216, 7, 239));
            g2d.setStroke(new BasicStroke(5));
            int centerX = x + SEED_SIZE / 2;
            int centerY = y + SEED_SIZE / 2;
            int radius = SEED_SIZE / 2;

            for (int i = 0; i < 5; i++) {
                double angle1 = Math.toRadians(i * 72);
                double angle2 = Math.toRadians((i + 2) * 72);
                int x1 = centerX + (int) (radius * Math.cos(angle1));
                int y1 = centerY + (int) (radius * Math.sin(angle1));
                int x2 = centerX + (int) (radius * Math.cos(angle2));
                int y2 = centerY + (int) (radius * Math.sin(angle2));
                g2d.drawLine(x1, y1, x2, y2);
            }

        } else if (content == Seed.NOUGHT) {
            // Gambar lingkaran biru tebal (O)
            g2d.setColor(new Color(64, 154, 225));
            g2d.setStroke(new BasicStroke(8));
            g2d.drawOval(x, y, SEED_SIZE, SEED_SIZE);
        }
    }
}





