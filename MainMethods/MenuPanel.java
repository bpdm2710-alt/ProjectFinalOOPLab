package MainMethods;

import mino.Mino;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MenuPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanel gamePanel;

    // Button rects (populated in paintComponent / recalculated each resize)
    private final Rectangle[] btnRects = new Rectangle[4];
    private int hoveredBtn = -1;

    private static final String[] BTN_LABELS = { "MARATHON", "PRACTICE", "CONFIG", "QUIT" };
    private static final Color[] BTN_COLORS = {
        new Color(200, 50, 130),  // Magenta-pink
        new Color(40, 180, 90),   // Green
        new Color(60, 110, 220),  // Blue
        new Color(70, 70, 70),    // Grey
    };

    public MenuPanel(CardLayout cardLayout, JPanel mainContainer, GamePanel gamePanel) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setOpaque(true);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int prev = hoveredBtn;
                hoveredBtn = -1;
                for (int i = 0; i < btnRects.length; i++) {
                    if (btnRects[i] != null && btnRects[i].contains(e.getPoint())) {
                        hoveredBtn = i;
                        break;
                    }
                }
                if (hoveredBtn != prev) repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredBtn = -1;
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < btnRects.length; i++) {
                    if (btnRects[i] != null && btnRects[i].contains(e.getPoint())) {
                        handleButtonClick(i);
                        break;
                    }
                }
            }
        });
    }

    // ─── Rendering ───────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // ── Background gradient ──
        GradientPaint bg = new GradientPaint(0, 0, new Color(8, 8, 20), 0, h, new Color(18, 18, 40));
        g2.setPaint(bg);
        g2.fillRect(0, 0, w, h);

        // ── Subtle grid lines ──
        g2.setColor(new Color(255, 255, 255, 8));
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x < w; x += 40) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 40) g2.drawLine(0, y, w, y);

        // ── Accent horizontal bar ──
        float[] fractions = {0f, 0.5f, 1f};
        Color[] accentColors = {new Color(200, 50, 130, 0), new Color(200, 50, 130, 60), new Color(200, 50, 130, 0)};
        g2.setPaint(new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, accentColors));
        g2.fillRect(0, h / 2 - 1, w, 2);

        // ── Title: "TETRIS" ──
        drawTitle(g2, w, h);

        // ── Buttons ──
        int btnW = 380;
        int btnH = 62;
        int gap  = 18;
        int totalH = BTN_LABELS.length * btnH + (BTN_LABELS.length - 1) * gap;
        int startX = (w - btnW) / 2;
        int startY = h / 2 - 10;

        for (int i = 0; i < BTN_LABELS.length; i++) {
            int y = startY + i * (btnH + gap);
            btnRects[i] = new Rectangle(startX, y, btnW, btnH);
            drawButton(g2, btnRects[i], BTN_LABELS[i], BTN_COLORS[i], hoveredBtn == i);
        }

        // ── Footer hint ──
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        g2.setColor(new Color(120, 120, 160));
        String hint = "Hold ESC in-game to return to this menu";
        int hw = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hw) / 2, h - 24);
    }

    private void drawTitle(Graphics2D g2, int w, int h) {
        // Glow shadow layers
        Font titleFont = new Font("Arial", Font.BOLD, 90);
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        String title = "TETRIS";
        int tx = (w - fm.stringWidth(title)) / 2;
        int ty = h / 2 - 160;

        for (int radius = 24; radius >= 4; radius -= 4) {
            float alpha = 0.04f + (24 - radius) * 0.003f;
            g2.setColor(new Color(200f/255, 50f/255, 130f/255, alpha));
            g2.drawString(title, tx, ty + radius / 3);
        }

        // Main title — gradient fill
        GradientPaint titleGrad = new GradientPaint(tx, ty - fm.getAscent(), new Color(255, 120, 200),
                tx, ty, new Color(200, 50, 130));
        g2.setPaint(titleGrad);
        g2.drawString(title, tx, ty);

        // Subtitle
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(new Color(160, 160, 200));
        String sub = "Select a game mode";
        int sw = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, (w - sw) / 2, ty + 28);
    }

    private void drawButton(Graphics2D g2, Rectangle r, String label, Color base, boolean hovered) {
        Color fill = hovered ? base.brighter() : base.darker();
        Color border = hovered ? base.brighter().brighter() : base;

        // Background
        g2.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), hovered ? 220 : 170));
        g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, 12, 12));

        // Border
        g2.setColor(border);
        g2.setStroke(new BasicStroke(hovered ? 2.5f : 1.5f));
        g2.draw(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, 12, 12));

        // Left accent bar
        g2.setColor(hovered ? base.brighter() : base);
        g2.fillRoundRect(r.x, r.y + 14, 4, r.height - 28, 4, 4);

        // Label
        g2.setFont(new Font("Arial", Font.BOLD, 26));
        g2.setColor(Color.WHITE);
        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + 20;
        int ty = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(label, tx, ty);
    }

    // ─── Button Actions ───────────────────────────────────────────────────────

    private void handleButtonClick(int index) {
        switch (index) {
            case 0 -> startGame(false);  // MARATHON
            case 1 -> startGame(true);   // PRACTICE
            case 2 -> openConfig();      // CONFIG
            case 3 -> System.exit(0);    // QUIT
        }
    }

    private void startGame(boolean isPractice) {
        gamePanel.gameManager.practiceMode = isPractice;
        cardLayout.show(mainContainer, "GAME");
        gamePanel.requestFocusInWindow();

        if (gamePanel.gameThread == null) {
            gamePanel.launchGame();
        } else {
            gamePanel.gameManager.restartGame();
            GamePanel.music.playAndLoop(0);
        }
    }

    private void openConfig() {
        // Style the config panel to match the dark theme
        UIManager.put("OptionPane.background",        new Color(18, 18, 40));
        UIManager.put("Panel.background",             new Color(18, 18, 40));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Label.foreground",             Color.WHITE);

        JPanel configPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        configPanel.setBackground(new Color(18, 18, 40));

        configPanel.add(styledLabel("DAS (Delay Auto Shift) ms:"));
        JTextField dasInput = styledField(String.valueOf(Mino.DAS_DELAY * 16));
        configPanel.add(dasInput);

        configPanel.add(styledLabel("ARR (Auto Repeat Rate) ms:"));
        JTextField arrInput = styledField(String.valueOf(Mino.ARR_DELAY * 16));
        configPanel.add(arrInput);

        configPanel.add(styledLabel("SDF (Soft Drop Factor) x:"));
        JTextField sdfInput = styledField(String.valueOf(Mino.SDF_MULTIPLIER));
        configPanel.add(sdfInput);

        int result = JOptionPane.showConfirmDialog(this, configPanel,
                "Handling Config", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int dasMs = Integer.parseInt(dasInput.getText().trim());
                int arrMs = Integer.parseInt(arrInput.getText().trim());
                int sdf   = Integer.parseInt(sdfInput.getText().trim());

                Mino.DAS_DELAY    = Math.max(0, dasMs / 16);
                Mino.ARR_DELAY    = Math.max(0, arrMs / 16);
                Mino.SDF_MULTIPLIER = Math.max(1, sdf);

                JOptionPane.showMessageDialog(this, "Configuration saved!",
                        "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(200, 200, 230));
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        return l;
    }

    private JTextField styledField(String text) {
        JTextField f = new JTextField(text);
        f.setBackground(new Color(30, 30, 60));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 160)));
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        return f;
    }
}
