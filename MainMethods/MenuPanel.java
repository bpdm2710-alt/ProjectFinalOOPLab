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
            new Color(200, 50, 130), // Magenta-pink
            new Color(40, 180, 90), // Green
            new Color(60, 110, 220), // Blue
            new Color(70, 70, 70), // Grey
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
                if (hoveredBtn != prev)
                    repaint();
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
        for (int x = 0; x < w; x += 40)
            g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 40)
            g2.drawLine(0, y, w, y);

        // ── Accent horizontal bar ──
        float[] fractions = { 0f, 0.5f, 1f };
        Color[] accentColors = { new Color(200, 50, 130, 0), new Color(200, 50, 130, 60), new Color(200, 50, 130, 0) };
        g2.setPaint(new java.awt.LinearGradientPaint(0, 0, w, 0, fractions, accentColors));
        g2.fillRect(0, h / 2 - 1, w, 2);

        // ── Title: "TETRIS" ──
        drawTitle(g2, w, h);

        // ── Buttons ──
        int btnW = 380;
        int btnH = 62;
        int gap = 18;
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
            g2.setColor(new Color(200f / 255, 50f / 255, 130f / 255, alpha));
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
            case 0 -> startGame(false); // MARATHON
            case 1 -> startGame(true); // PRACTICE
            case 2 -> openConfig(); // CONFIG
            case 3 -> System.exit(0); // QUIT
        }
    }

    private void startGame(boolean isPractice) {
        gamePanel.gameManager.setPracticeMode(isPractice);
        cardLayout.show(mainContainer, "GAME");
        gamePanel.requestFocusInWindow();

        if (!gamePanel.isRunning()) {
            gamePanel.launchGame();
        } else {
            gamePanel.gameManager.restartGame();
            gamePanel.getMusic().playAndLoop(0);
        }
    }

    private void openConfig() {
        final double MS_PER_FRAME = 1000.0 / 60.0; // ~16.667ms at 60 FPS

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Handling Config", true);
        dialog.getContentPane().setBackground(new Color(18, 18, 40));
        dialog.setLayout(new BorderLayout());

        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBackground(new Color(18, 18, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Advanced Mode toggle
        JCheckBox advancedToggle = new JCheckBox("Advanced Mode (raw frames)");
        advancedToggle.setBackground(new Color(18, 18, 40));
        advancedToggle.setForeground(new Color(180, 180, 220));
        advancedToggle.setFont(new Font("Arial", Font.PLAIN, 13));
        advancedToggle.setFocusPainted(false);

        // Labels
        JLabel dasLabel = styledLabel("DAS (Delayed Auto Shift) ms:");
        JLabel arrLabel = styledLabel("ARR (Auto Repeat Rate) ms:");
        JLabel sdfLabel = styledLabel("SDF (Soft Drop Factor) ×:");

        // Default values in ms
        JTextField dasInput = styledField(String.valueOf(Math.round(Mino.DAS_DELAY * MS_PER_FRAME)));
        JTextField arrInput = styledField(String.valueOf(Math.round(Mino.ARR_DELAY * MS_PER_FRAME)));
        JTextField sdfInput = styledField(String.valueOf(Mino.SDF_MULTIPLIER));

        // Toggle listener: switch between ms and frame display
        advancedToggle.addActionListener(e -> {
            if (advancedToggle.isSelected()) {
                dasLabel.setText("DAS (frames):");
                arrLabel.setText("ARR (frames):");
                sdfLabel.setText("SDF (cells/frame):");
                // Convert current ms values back to frames
                try {
                    int dasMs = Integer.parseInt(dasInput.getText().trim());
                    int arrMs = Integer.parseInt(arrInput.getText().trim());
                    dasInput.setText(String.valueOf((int) Math.round(dasMs / MS_PER_FRAME)));
                    arrInput.setText(String.valueOf((int) Math.round(arrMs / MS_PER_FRAME)));
                } catch (NumberFormatException ex) {
                    // Leave as-is if invalid
                }
            } else {
                dasLabel.setText("DAS (Delayed Auto Shift) ms:");
                arrLabel.setText("ARR (Auto Repeat Rate) ms:");
                sdfLabel.setText("SDF (Soft Drop Factor) ×:");
                // Convert current frame values to ms
                try {
                    int dasF = Integer.parseInt(dasInput.getText().trim());
                    int arrF = Integer.parseInt(arrInput.getText().trim());
                    dasInput.setText(String.valueOf(Math.round(dasF * MS_PER_FRAME)));
                    arrInput.setText(String.valueOf(Math.round(arrF * MS_PER_FRAME)));
                } catch (NumberFormatException ex) {
                    // Leave as-is if invalid
                }
            }
        });

        // Layout: row 0 = advanced toggle (span 2 cols)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        configPanel.add(advancedToggle, gbc);

        gbc.gridwidth = 1;

        // Row 1: DAS
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(dasLabel, gbc);
        gbc.gridx = 1;
        configPanel.add(dasInput, gbc);

        // Row 2: ARR
        gbc.gridx = 0; gbc.gridy = 2;
        configPanel.add(arrLabel, gbc);
        gbc.gridx = 1;
        configPanel.add(arrInput, gbc);

        // Row 3: SDF
        gbc.gridx = 0; gbc.gridy = 3;
        configPanel.add(sdfLabel, gbc);
        gbc.gridx = 1;
        configPanel.add(sdfInput, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(18, 18, 40));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        cancelBtn.addActionListener(e -> dialog.dispose());
        saveBtn.addActionListener(e -> {
            try {
                int dasVal = Integer.parseInt(dasInput.getText().trim());
                int arrVal = Integer.parseInt(arrInput.getText().trim());
                int sdf = Integer.parseInt(sdfInput.getText().trim());

                if (advancedToggle.isSelected()) {
                    Mino.DAS_DELAY = Math.max(0, dasVal);
                    Mino.ARR_DELAY = Math.max(0, arrVal);
                } else {
                    Mino.DAS_DELAY = Math.max(0, (int) Math.round(dasVal / MS_PER_FRAME));
                    Mino.ARR_DELAY = Math.max(0, (int) Math.round(arrVal / MS_PER_FRAME));
                }
                Mino.SDF_MULTIPLIER = Math.max(1, sdf);

                JOptionPane.showMessageDialog(dialog, "Configuration saved!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(configPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(200, 200, 230));
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        return l;
    }

    private JTextField styledField(String text) {
        JTextField f = new JTextField(text, 8);
        f.setBackground(new Color(30, 30, 60));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 160)));
        f.setFont(new Font("Arial", Font.PLAIN, 14));
        return f;
    }
}
