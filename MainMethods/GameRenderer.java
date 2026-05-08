package MainMethods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import mino.Block;
import mino.Mino;

/**
 * Drawing only — keeps {@link GameManager} focused on simulation state.
 */
final class GameRenderer {

    /** NEXT / HOLD panel width (px). */
    private static final int HUD_PANEL_W = 180;
    /** NEXT column outer height (must fit header + 5 preview rows). */
    private static final int NEXT_PANEL_H = 480;
    /** HOLD panel outer height. */
    private static final int HOLD_PANEL_H = 180;
    /** Title row + divider (px from panel top to top of preview column). */
    private static final int HUD_HEADER_H = 28;
    /** Vertical gap between stacked NEXT preview rows. */
    private static final int NEXT_ROW_GAP = 6;

    void draw(GameManager gm, Graphics2D g2) {
        final int leftX = gm.getLeftX();
        final int rightX = gm.getRightX();
        final int topY = gm.getTopY();
        final int bottomY = gm.getBottomY();
        final int width = gm.WIDTH;
        final int height = gm.HEIGHT;

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(leftX - 8, topY - 8, width + 16, height + 16);

        drawGrid(g2, leftX, rightX, topY, bottomY);

        int previewX = rightX + 80;
        int previewY = topY + 60;
        int holdX = leftX - 220;
        int holdY = topY + 60;

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(previewX, previewY, HUD_PANEL_W, NEXT_PANEL_H);
        g2.drawRect(holdX, holdY, HUD_PANEL_W, HOLD_PANEL_H);

        Font hudTitleFont = new Font("Arial", Font.BOLD, 16);
        g2.setFont(hudTitleFont);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics titleFm = g2.getFontMetrics();

        String nextTitle = "NEXT";
        g2.drawString(nextTitle, previewX + (HUD_PANEL_W - titleFm.stringWidth(nextTitle)) / 2, previewY + 18);
        drawHudDivider(g2, previewX, previewY + HUD_HEADER_H - 1, HUD_PANEL_W);

        String holdTitle = "HOLD";
        g2.drawString(holdTitle, holdX + (HUD_PANEL_W - titleFm.stringWidth(holdTitle)) / 2, holdY + 18);
        drawHudDivider(g2, holdX, holdY + HUD_HEADER_H - 1, HUD_PANEL_W);

        /*
         * Five equal rows (tetr.io-style): immediate next + PREVIEW_COUNT queue entries
         */
        final int nextInnerPadTop = 8;
        final int nextInnerPadBottom = 10;
        int nextColumnTop = previewY + HUD_HEADER_H + nextInnerPadTop;
        int nextColumnH = NEXT_PANEL_H - HUD_HEADER_H - nextInnerPadTop - nextInnerPadBottom;
        int slotH = Math.max(1, (nextColumnH - 4 * NEXT_ROW_GAP) / 5);

        List<Integer> queueList = new ArrayList<>(gm.getPreviewQueue());
        for (int i = 0; i < 5; i++) {
            int pieceType;
            if (i == 0) {
                pieceType = gm.getNextMinoType();
            } else if (i - 1 < queueList.size()) {
                pieceType = queueList.get(i - 1);
            } else {
                continue;
            }
            int rowTop = nextColumnTop + i * (slotH + NEXT_ROW_GAP);
            drawTetrominoPreview(gm, g2, pieceType, previewX, rowTop, HUD_PANEL_W, slotH);
        }

        if (gm.getHoldMinoType() >= 0) {
            int holdInnerPadTop = 8;
            int holdInnerPadBottom = 8;
            int holdColumnTop = holdY + HUD_HEADER_H + holdInnerPadTop;
            int holdColumnH = HOLD_PANEL_H - HUD_HEADER_H - holdInnerPadTop - holdInnerPadBottom;
            int holdSlotY = holdColumnTop + Math.max(0, (holdColumnH - slotH) / 2);
            drawTetrominoPreview(gm, g2, gm.getHoldMinoType(), holdX, holdSlotY, HUD_PANEL_W, slotH);
        }

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.white);
        g2.drawString("SCORE: " + gm.getScore(), previewX + 8, previewY + NEXT_PANEL_H + 18);
        g2.drawString("LEVEL: " + gm.getLevel(), holdX + 8, holdY + HOLD_PANEL_H + 18);
        g2.drawString("LINES: " + gm.getLines(), holdX + 8, holdY + HOLD_PANEL_H + 42);

        if (gm.getCurrentMino() != null) {
            drawGhostMino(gm, g2);
            gm.getCurrentMino().draw(g2);
        }

        List<Block> blocks = gm.getStaticBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).draw(g2);
        }

        if (gm.isEffectCounterOn()) {
            int maxDuration = 15;
            float alpha = 1.0f - (float) gm.getEffectCounter() / maxDuration;

            for (int i = 0; i < gm.getEffectY().size(); i++) {
                int yEffect = gm.getEffectY().get(i);
                g2.setColor(new Color(1.0f, 1.0f, 0.0f, Math.max(0, alpha)));
                g2.fillRect(leftX, yEffect, width, Block.SIZE);
            }
        }

        g2.setColor(Color.yellow);

        if (gm.getState() == GameState.GAME_OVER) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

            g2.setColor(Color.yellow);
            g2.setFont(g2.getFont().deriveFont(60f));
            String gameOverText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, GamePanel.WIDTH / 2 - textWidth / 2, GamePanel.HEIGHT / 2 - 40);

            g2.setFont(g2.getFont().deriveFont(32f));
            String restartText = "Press R to Restart";
            int restartWidth = g2.getFontMetrics().stringWidth(restartText);
            g2.drawString(restartText, GamePanel.WIDTH / 2 - restartWidth / 2, GamePanel.HEIGHT / 2 + 60);
        } else if (gm.getState() == GameState.PAUSED) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

            g2.setColor(Color.yellow);
            g2.setFont(g2.getFont().deriveFont(60f));
            String pausedText = "PAUSED";
            int pausedWidth = g2.getFontMetrics().stringWidth(pausedText);
            g2.drawString(pausedText, GamePanel.WIDTH / 2 - pausedWidth / 2, GamePanel.HEIGHT / 2 - 40);

            g2.setFont(g2.getFont().deriveFont(32f));
            String resumeText = "Press P to Resume";
            int resumeWidth = g2.getFontMetrics().stringWidth(resumeText);
            g2.drawString(resumeText, GamePanel.WIDTH / 2 - resumeWidth / 2, GamePanel.HEIGHT / 2 + 60);
        }
    }

    private void drawHudDivider(Graphics2D g2, int panelLeft, int lineY, int panelWidth) {
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(panelLeft, lineY, panelLeft + panelWidth, lineY);
    }

    private void drawGrid(Graphics2D g2, int leftX, int rightX, int topY, int bottomY) {
        g2.setColor(new Color(100, 100, 100, 50));
        g2.setStroke(new BasicStroke(0.5f));

        for (int x = leftX; x <= rightX; x += Block.SIZE) {
            g2.drawLine(x, topY, x, bottomY);
        }

        for (int y = topY; y <= bottomY; y += Block.SIZE) {
            g2.drawLine(leftX, y, rightX, y);
        }
    }

    private void drawGhostMino(GameManager gm, Graphics2D g2) {
        int dropDistance = gm.calculateDropDistance(gm.getCurrentMino());
        if (dropDistance <= 0) {
            return;
        }

        int margin = 2;
        Color c = gm.getCurrentMino().b[0].c;
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        for (int i = 0; i < gm.getCurrentMino().b.length; i++) {
            g2.fillRect(
                    gm.getCurrentMino().b[i].x + margin,
                    gm.getCurrentMino().b[i].y + dropDistance * Block.SIZE + margin,
                    Block.SIZE - 2 * margin,
                    Block.SIZE - 2 * margin);
        }
    }

    /**
     * Draws a tetromino scaled to fit the box. Uses a fresh piece at origin —
     * preview queue
     * entries must not rely on board coordinates (those were unset or huge and
     * broke scaling).
     */
    private void drawTetrominoPreview(GameManager gm, Graphics2D g2, int pieceType, int boxLeft, int boxTop, int boxW,
            int boxH) {
        if (pieceType < 0) {
            return;
        }
        Mino m = MinoFactory.createByType(gm, pieceType);
        m.setXY(0, 0);

        int minBlockX = m.b[0].x;
        int maxBlockX = m.b[0].x;
        int minBlockY = m.b[0].y;
        int maxBlockY = m.b[0].y;
        for (int i = 1; i < m.b.length; i++) {
            minBlockX = Math.min(minBlockX, m.b[i].x);
            maxBlockX = Math.max(maxBlockX, m.b[i].x);
            minBlockY = Math.min(minBlockY, m.b[i].y);
            maxBlockY = Math.max(maxBlockY, m.b[i].y);
        }

        int cols = (maxBlockX - minBlockX) / Block.SIZE + 1;
        int rows = (maxBlockY - minBlockY) / Block.SIZE + 1;
        cols = Math.max(cols, 1);
        rows = Math.max(rows, 1);

        final int pad = 6;
        int innerW = Math.max(1, boxW - 2 * pad);
        int innerH = Math.max(1, boxH - 2 * pad);
        /*
         * Fixed cell size for all HUD previews: fits spawn shapes in a 4×2 mino grid (I
         * horizontal, O, etc.).
         * Same cell for every piece so blocks match the queue rows — no oversized
         * I/HOLD vs thin S/J.
         */
        int cell = Math.min(innerW / 4, innerH / 2);
        // Cap the preview piece size so it doesn't look clunky (slightly smaller than
        // board blocks)
        int maxCellSize = (int) (Block.SIZE * 0.85);
        cell = Math.min(cell, maxCellSize);
        cell = Math.max(cell, 1);

        int pieceW = cols * cell;
        int pieceH = rows * cell;
        int drawX = boxLeft + pad + (innerW - pieceW) / 2;
        int drawY = boxTop + pad + (innerH - pieceH) / 2;

        int inset = 1;
        for (int i = 0; i < m.b.length; i++) {
            int ox = (m.b[i].x - minBlockX) / Block.SIZE;
            int oy = (m.b[i].y - minBlockY) / Block.SIZE;
            g2.setColor(m.b[i].c);
            g2.fillRect(
                    drawX + ox * cell + inset,
                    drawY + oy * cell + inset,
                    cell - 2 * inset,
                    cell - 2 * inset);
        }
    }
}
