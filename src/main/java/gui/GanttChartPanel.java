package gui;

import model.SchedulingResult;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.*;

public class GanttChartPanel extends JPanel {

    private SchedulingResult result;
    private String title;

    private static final Color[] PROCESS_COLORS = {
        new Color(236,  72, 153), 
        new Color(139,  92, 246), 
        new Color( 20, 184, 166), 
        new Color(245, 158,  11), 
        new Color( 59, 130, 246), 
        new Color(239,  68,  68), 
        new Color( 16, 185, 129), 
        new Color(217,  70, 239), 
    };

    private static final Color IDLE_COLOR  = new Color(209, 213, 219); 
    private static final Color BG_COLOR    = new Color(253, 242, 248); 
    private static final Color GRID_COLOR  = new Color(251, 207, 232); 
    private static final Color LABEL_COLOR = new Color(107, 114, 128); 

    private Map<String, Integer> pidColorMap = new HashMap<>();

    public GanttChartPanel(String title) {
        this.title = title;
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(800, 130));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    public void setResult(SchedulingResult result) {
        this.result = result;
        pidColorMap.clear();
        if (result != null) {
            int colorIdx = 0;
            for (String lbl : result.getGanttLabels()) {
                if (!lbl.equals("IDLE") && !pidColorMap.containsKey(lbl)) {
                    pidColorMap.put(lbl, colorIdx % PROCESS_COLORS.length);
                    colorIdx++;
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        GradientPaint bgGrad = new GradientPaint(0, 0, new Color(255, 241, 248), w, h, BG_COLOR);
        g2.setPaint(bgGrad);
        g2.fillRect(0, 0, w, h);

        g2.setFont(new Font("Consolas", Font.BOLD, 13));
        g2.setColor(new Color(236, 72, 153));
        g2.drawString("▶ " + title, 12, 20);

        if (result == null || result.getGanttLabels().isEmpty()) {
            g2.setColor(new Color(156, 163, 175));
            g2.setFont(new Font("Consolas", Font.PLAIN, 11));
            g2.drawString("[ No data — run simulation first ]", w / 2 - 120, h / 2);
            return;
        }

        List<String> labels = result.getGanttLabels();
        List<int[]>  chart  = result.getGanttChart();
        int totalTime = result.getTotalTime();
        if (totalTime == 0) return;

        int barTop    = 30;
        int barHeight = 44;
        int timeRow   = barTop + barHeight + 4;
        int leftPad   = 14;
        int rightPad  = 14;
        int drawW     = w - leftPad - rightPad;

        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2, 4}, 0));
        for (int t = 0; t <= totalTime; t++) {
            int x = leftPad + (int)((double) t / totalTime * drawW);
            g2.drawLine(x, barTop, x, barTop + barHeight);
        }
        g2.setStroke(new BasicStroke(1f));

        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            int[] seg = chart.get(i);
            int start = seg[0], end = seg[1];

            int x1 = leftPad + (int)((double) start / totalTime * drawW);
            int x2 = leftPad + (int)((double) end   / totalTime * drawW);
            int bw  = Math.max(x2 - x1 - 1, 2);

            Color baseColor = label.equals("IDLE") ? IDLE_COLOR
                    : PROCESS_COLORS[pidColorMap.getOrDefault(label, 0)];

            GradientPaint gp = new GradientPaint(
                x1, barTop,              brighter(baseColor, 0.6f),
                x1, barTop + barHeight,  baseColor);
            g2.setPaint(gp);
            g2.fillRoundRect(x1 + 1, barTop + 1, bw - 1, barHeight - 2, 8, 8);

            g2.setColor(darker(baseColor, 0.80f));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x1 + 1, barTop + 1, bw - 1, barHeight - 2, 8, 8);
            g2.setStroke(new BasicStroke(1f));

            if (bw > 18) {
                g2.setFont(new Font("Consolas", Font.BOLD, bw > 36 ? 11 : 9));
                FontMetrics fm = g2.getFontMetrics();
                int lx = x1 + bw / 2 - fm.stringWidth(label) / 2;
                int ly = barTop + barHeight / 2 + fm.getAscent() / 2 - 2;
                g2.setColor(new Color(0, 0, 0, 60));
                g2.drawString(label, lx + 1, ly + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(label, lx, ly);
            }
        }

        g2.setFont(new Font("Consolas", Font.PLAIN, 9));
        g2.setColor(LABEL_COLOR);
        Set<Integer> drawnTimes = new HashSet<>();
        int prevX = -20;
        for (int[] seg : chart) {
            for (int t : new int[]{seg[0], seg[1]}) {
                if (drawnTimes.contains(t)) continue;
                int x = leftPad + (int)((double) t / totalTime * drawW);
                if (x - prevX < 14) continue;
                String ts = String.valueOf(t);
                g2.drawString(ts, x - g2.getFontMetrics().stringWidth(ts) / 2, timeRow + 12);
                g2.setColor(new Color(249, 168, 212));
                g2.drawLine(x, timeRow, x, timeRow + 4);
                g2.setColor(LABEL_COLOR);
                drawnTimes.add(t);
                prevX = x;
            }
        }

        g2.setPaint(new GradientPaint(0, h - 2, new Color(236, 72, 153, 0),
                                       w, h - 2, new Color(139, 92, 246, 160)));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(0, h - 2, w, h - 2);
    }

    private Color brighter(Color c, float factor) {
        int r = Math.min((int)(c.getRed()   + (255 - c.getRed())   * factor), 255);
        int g = Math.min((int)(c.getGreen() + (255 - c.getGreen()) * factor), 255);
        int b = Math.min((int)(c.getBlue()  + (255 - c.getBlue())  * factor), 255);
        return new Color(r, g, b);
    }

    private Color darker(Color c, float factor) {
        return new Color(
            (int)(c.getRed()   * factor),
            (int)(c.getGreen() * factor),
            (int)(c.getBlue()  * factor)
        );
    }
}