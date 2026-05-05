package gui;

import model.Process;
import model.SchedulingResult;
import scheduler.PriorityScheduler;
import scheduler.RoundRobinScheduler;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private static final Color BG         = ModernUIHelper.BG;
    private static final Color PANEL_BG   = ModernUIHelper.PANEL_BG;
    private static final Color ACCENT     = ModernUIHelper.ACCENT;
    private static final Color ACCENT2    = ModernUIHelper.ACCENT2;
    private static final Color ACCENT3    = ModernUIHelper.ACCENT3;
    private static final Color BORDER_COL = ModernUIHelper.BORDER_COL;
    private static final Color TEXT       = ModernUIHelper.TEXT;

    private InputPanel      inputPanel;
    private GanttChartPanel rrGantt, psGantt;
    private ResultsPanel    rrResults, psResults;
    private JEditorPane     comparisonPane, conclusionPane;

    private RoundRobinScheduler rrScheduler = new RoundRobinScheduler();
    private PriorityScheduler   psScheduler = new PriorityScheduler();

    private SchedulingResult lastRR, lastPS;

    public MainFrame() {
        super("⬡ OS Scheduler Simulator — Round Robin vs Priority");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setPreferredSize(new Dimension(1280, 800));
        getContentPane().setBackground(BG);
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        inputPanel = new InputPanel();
        inputPanel.setPreferredSize(new Dimension(420, 0));
        inputPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL));
        inputPanel.setOnSimulate(this::runSimulation);
        add(inputPanel, BorderLayout.WEST);

        JTabbedPane tabs = buildTabs();
        add(tabs, BorderLayout.CENTER);
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(BG);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Consolas", Font.BOLD, 12));
        tabs.putClientProperty("JTabbedPane.tabAreaBackground", PANEL_BG);

        JPanel ganttTab = new JPanel(new GridLayout(2, 1, 0, 8));
        ganttTab.setBackground(BG);
        ganttTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rrGantt = new GanttChartPanel("Round Robin  ·  Gantt Chart");
        psGantt = new GanttChartPanel("Priority Scheduling  ·  Gantt Chart");

        ganttTab.add(wrapWithTitle(rrGantt, "ROUND ROBIN", ACCENT));
        ganttTab.add(wrapWithTitle(psGantt, "PRIORITY SCHEDULING", ACCENT2));
        tabs.addTab("  📊 Gantt Charts  ", ganttTab);

        JPanel resultsTab = new JPanel(new GridLayout(2, 1, 0, 8));
        resultsTab.setBackground(BG);
        resultsTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rrResults = new ResultsPanel("Round Robin  —  Process Metrics", ACCENT);
        psResults = new ResultsPanel("Priority Scheduling  —  Process Metrics", ACCENT2);

        resultsTab.add(rrResults);
        resultsTab.add(psResults);
        tabs.addTab("  📋 Results Tables  ", resultsTab);

        comparisonPane = buildEditorPane();
        comparisonPane.setText(ModernUIHelper.buildComparisonHTML(null, null));
        JScrollPane compScroll = lightScroll(comparisonPane);
        JPanel compTab = new JPanel(new BorderLayout());
        compTab.setBackground(BG);
        compTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        compTab.add(sectionHeader("◈ Comparison Summary", ACCENT), BorderLayout.NORTH);
        compTab.add(compScroll, BorderLayout.CENTER);
        tabs.addTab("  ⚖ Comparison  ", compTab);

        conclusionPane = buildEditorPane();
        conclusionPane.setText(ModernUIHelper.buildConclusionHTML(null, null));
        JScrollPane concScroll = lightScroll(conclusionPane);
        JPanel concTab = new JPanel(new BorderLayout());
        concTab.setBackground(BG);
        concTab.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        concTab.add(sectionHeader("◈ Final Conclusion", ACCENT3), BorderLayout.NORTH);
        concTab.add(concScroll, BorderLayout.CENTER);
        tabs.addTab("  ✦ Conclusion  ", concTab);

        tabs.setUI(new LightTabbedPaneUI());
        return tabs;
    }

    private void runSimulation() {
        try {
            List<Process> processes = inputPanel.getProcesses();
            int quantum = inputPanel.getQuantum();

            lastRR = rrScheduler.schedule(processes, quantum);
            lastPS = psScheduler.schedule(processes);

            rrGantt.setResult(lastRR);
            psGantt.setResult(lastPS);
            rrResults.setResult(lastRR);
            psResults.setResult(lastPS);
            comparisonPane.setText(ModernUIHelper.buildComparisonHTML(lastRR, lastPS));
            conclusionPane.setText(ModernUIHelper.buildConclusionHTML(lastRR, lastPS));

            inputPanel.setStatus("✔  Simulation complete — " + processes.size() + " processes, Q=" + quantum, false);

        } catch (IllegalArgumentException ex) {
            inputPanel.setStatus("✘  " + ex.getMessage(), true);
        }
    }


    private JPanel wrapWithTitle(JComponent comp, String title, Color accentColor) {
        JPanel wrap = new JPanel(new BorderLayout(0, 0));
        wrap.setBackground(PANEL_BG);
        wrap.setBorder(BorderFactory.createCompoundBorder(
            new SoftBorder(accentColor, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        titleBar.setBackground(new Color(252, 231, 243));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Consolas", Font.BOLD, 11));
        lbl.setForeground(accentColor);
        titleBar.add(lbl);
        wrap.add(titleBar, BorderLayout.NORTH);
        wrap.add(comp, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel sectionHeader(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(new Color(252, 231, 243));
        p.setBorder(BorderFactory.createMatteBorder(0, 4, 1, 0, color));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Consolas", Font.BOLD, 14));
        l.setForeground(color);
        p.add(l);
        return p;
    }

    private JEditorPane buildEditorPane() {
        JEditorPane ep = new JEditorPane("text/html", "");
        ep.setEditable(false);
        ep.setBackground(PANEL_BG);
        ep.setForeground(TEXT);
        ep.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        return ep;
    }

    private JScrollPane lightScroll(JComponent comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBackground(PANEL_BG);
        sp.getViewport().setBackground(PANEL_BG);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        return sp;
    }

    static class SoftBorder extends AbstractBorder {
        private final Color color;
        private final int   thickness;
        SoftBorder(Color c, int t) { this.color = c; this.thickness = t; }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.setStroke(new BasicStroke(thickness + 2));
            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, 10, 10);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, 10, 10);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    static class LightTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {
        private static final Color BG_TAB   = new Color(252, 231, 243); 
        private static final Color SEL_TAB  = new Color(255, 255, 255); 
        private static final Color SEL_LINE = new Color(236,  72, 153); 
        private static final Color FG_NORM  = new Color(107, 114, 128); 
        private static final Color FG_SEL   = new Color(236,  72, 153); 

        @Override protected void paintTabBackground(Graphics g, int tp, int idx, int x, int y, int w, int h, boolean sel) {
            g.setColor(sel ? SEL_TAB : BG_TAB);
            g.fillRect(x, y, w, h);
        }
        @Override protected void paintTabBorder(Graphics g, int tp, int idx, int x, int y, int w, int h, boolean sel) {
            if (sel) {
                g.setColor(SEL_LINE);
                g.fillRect(x, y + h - 3, w, 3);
            }
        }
        @Override protected void paintFocusIndicator(Graphics g, int tp, java.awt.Rectangle[] tr, int idx,
                                                     java.awt.Rectangle ir, java.awt.Rectangle tr2, boolean sel) {}
        @Override protected void paintContentBorder(Graphics g, int tp, int sel) {
            g.setColor(new Color(249, 168, 212));
            g.drawLine(tabPane.getX(), rects[0].y + rects[0].height,
                       tabPane.getX() + tabPane.getWidth(),
                       rects[0].y + rects[0].height);
        }
        @Override protected int calculateTabHeight(int tp, int idx, int fh) { return 34; }
        @Override protected void paintText(Graphics g, int tp, Font font, FontMetrics fm, int idx, String title,
                                           java.awt.Rectangle textRect, boolean sel) {
            g.setFont(font.deriveFont(Font.BOLD, 11f));
            g.setColor(sel ? FG_SEL : FG_NORM);
            g.drawString(title, textRect.x, textRect.y + fm.getAscent());
        }
    }
}