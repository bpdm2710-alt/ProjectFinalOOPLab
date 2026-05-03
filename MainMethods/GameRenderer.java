package MainMethods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

    void draw(GameManager gm, Graphics2D g2) {
        final int leftX = GameManager.left_x;
        final int rightX = GameManager.right_x;
        final int topY = gm.top_y;
        final int bottomY = gm.bottom_y;
        final int width = gm.WIDTH;
        final int height = gm.HEIGHT;

        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(leftX - 8, topY - 8, width + 16, height + 16);

        drawGrid(g2, leftX, rightX, topY, bottomY);

        int previewX = rightX + 80;
        int previewY = topY + 60;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(previewX, previewY, 180, 400);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", previewX + 65, previewY + 28);

        int holdX = leftX - 220;
        int holdY = topY + 60;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(holdX, holdY, 180, 180);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString("HOLD", holdX + 60, holdY + 28);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.white);
        g2.drawString("SCORE: " + gm.score, previewX + 5, previewY + 425);
        g2.drawString("LEVEL: " + gm.level, holdX + 5, holdY + 210);
        g2.drawString("LINES: " + gm.lines, holdX + 5, holdY + 240);

        if (gm.currentMino != null) {
            drawGhostMino(gm, g2);
            gm.currentMino.draw(g2);
        }

        drawMiniMino(g2, gm.nextMino, previewX, previewY, 60);

        List<Integer> queueList = new ArrayList<>(gm.previewQueue);
        int queueStartY = previewY + 110;
        for (int i = 0; i < Math.min(GameManager.PREVIEW_COUNT, queueList.size()); i++) {
            Mino previewPiece = MinoFactory.createByType(queueList.get(i));
            drawMiniMino(g2, previewPiece, previewX, queueStartY + i * 85, 40);
        }

        if (gm.holdMino != null) {
            drawMiniMino(g2, gm.holdMino, holdX, holdY, 50);
        }

        List<Block> blocks = GameManager.getStaticBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).draw(g2);
        }

        if (gm.effectCounterOn) {
            gm.effectCounter++;

            int maxDuration = 15;
            float alpha = 1.0f - (float) gm.effectCounter / maxDuration;

            for (int i = 0; i < gm.effectY.size(); i++) {
                int yEffect = gm.effectY.get(i);
                g2.setColor(new Color(1.0f, 1.0f, 0.0f, Math.max(0, alpha)));
                g2.fillRect(leftX, yEffect, width, Block.SIZE);
            }

            if (gm.effectCounter >= maxDuration) {
                gm.effectCounter = 0;
                gm.effectCounterOn = false;
                gm.effectY.clear();
            }
        }

        g2.setColor(Color.yellow);

        if (gm.state == GameState.GAME_OVER) {
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
        } else if (gm.state == GameState.PAUSED) {
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
        int dropDistance = gm.calculateDropDistance(gm.currentMino);
        if (dropDistance <= 0) {
            return;
        }

        int margin = 2;
        Color c = gm.currentMino.b[0].c;
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        for (int i = 0; i < gm.currentMino.b.length; i++) {
            g2.fillRect(
                    gm.currentMino.b[i].x + margin,
                    gm.currentMino.b[i].y + dropDistance * Block.SIZE + margin,
                    Block.SIZE - 2 * margin,
                    Block.SIZE - 2 * margin
            );
        }
    }

    private void drawMiniMino(Graphics2D g2, Mino mino, int boxX, int boxY, int blockSize) {
        if (mino == null) {
            return;
        }

        int minBlockX = mino.b[0].x;
        int maxBlockX = mino.b[0].x;
        int minBlockY = mino.b[0].y;
        int maxBlockY = mino.b[0].y;

        for (int i = 1; i < mino.b.length; i++) {
            minBlockX = Math.min(minBlockX, mino.b[i].x);
            maxBlockX = Math.max(maxBlockX, mino.b[i].x);
            minBlockY = Math.min(minBlockY, mino.b[i].y);
            maxBlockY = Math.max(maxBlockY, mino.b[i].y);
        }

        int cols = (maxBlockX - minBlockX) / Block.SIZE + 1;
        int rows = (maxBlockY - minBlockY) / Block.SIZE + 1;
        int pieceWidth = cols * blockSize;
        int pieceHeight = rows * blockSize;

        int boxWidth = 180;
        int boxHeight = 80;

        int drawX = boxX + (boxWidth - pieceWidth) / 2;
        int drawY = boxY + 5 + (boxHeight - pieceHeight) / 2;

        for (int i = 0; i < mino.b.length; i++) {
            int offsetX = (mino.b[i].x - minBlockX) / Block.SIZE;
            int offsetY = (mino.b[i].y - minBlockY) / Block.SIZE;
            g2.setColor(mino.b[i].c);
            g2.fillRect(drawX + offsetX * blockSize, drawY + offsetY * blockSize, blockSize - 2, blockSize - 2);
        }
    }
}
