package gui;

import model.Process;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class InputPanel extends JPanel {

    private static final Color BG         = new Color(253, 242, 248);
    private static final Color CARD       = new Color(255, 255, 255);
    private static final Color ACCENT     = new Color(236,  72, 153);
    private static final Color ACCENT2    = new Color(139,  92, 246);
    private static final Color TEXT       = new Color( 31,  41,  55);
    private static final Color SUBTEXT    = new Color(107, 114, 128);
    private static final Color FIELD_BG   = new Color(255, 241, 242);
    private static final Color BORDER_COL = new Color(249, 168, 212);
    private static final Color ERR_COLOR  = new Color(239,  68,  68);
    private static final Color ROW_ALT    = new Color(253, 242, 248);

    private static final Object[][][] SCENARIOS = {
        {{"2"}, {"P1","0","6","3"}, {"P2","1","4","1"},
                {"P3","2","8","4"}, {"P4","3","3","2"}, {"P5","4","5","5"}},
        {{"3"}, {"P1","0","10","5"}, {"P2","2","4","1"},
                {"P3","3","6","4"}, {"P4","5","2","2"}},
        {{"2"}, {"P1","0","8","2"}, {"P2","0","8","2"},
                {"P3","0","8","2"}, {"P4","0","8","2"}},
        {{"2"}, {"P1","0","3","1"}, {"P2","1","5","1"},
                {"P3","2","4","1"}, {"P4","3","6","1"}, {"P5","0","7","5"}},
        {{"0"}, {"P1","-1","0","0"}, {"P1","abc","xyz","!!"}}
    };

    private static final String[] SCENARIO_LABELS = {"A", "B", "C", "D", "E"};
    private static final String[] SCENARIO_TIPS   = {
        "A: Basic Mixed Workload",
        "B: Urgency — P2 has highest priority",
        "C: Fairness — equal burst & priority",
        "D: Starvation — P5 (priority 5) starves",
        "E: Validation — invalid inputs (click Run)"
    };
    private static final Color[] SCENARIO_COLORS  = {
        new Color(236,  72, 153),   
        new Color(139,  92, 246),   
        new Color( 20, 184, 166),   
        new Color(245, 158,  11),   
        new Color(239,  68,  68),   
    };

    private JTextField        quantumField;
    private JTable            processTable;
    private DefaultTableModel tableModel;
    private JLabel            statusLabel;
    private JButton           addRowBtn, removeRowBtn, clearBtn, simulateBtn, resetBtn;
    private Runnable          onSimulate;

    public InputPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        header.setBackground(CARD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        JLabel sysLabel = new JLabel("⬡  OS SCHEDULER SIM");
        sysLabel.setFont(new Font("Consolas", Font.BOLD, 15));
        sysLabel.setForeground(ACCENT);

        JLabel subLabel = new JLabel("Round Robin ↔ Priority");
        subLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        subLabel.setForeground(SUBTEXT);

        header.add(sysLabel);
        header.add(Box.createHorizontalStrut(6));
        header.add(subLabel);
        add(header, BorderLayout.NORTH);

        JPanel south = new JPanel(new GridLayout(2, 1, 0, 6));
        south.setBackground(BG);
        south.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        simulateBtn = new JButton("▶  RUN SIMULATION") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed()  ? new Color(190,  24,  93)
                           : getModel().isRollover() ? new Color(244, 114, 182) : ACCENT;
                g2.setPaint(new GradientPaint(0, 0, brighter(base), 0, getHeight(), darker(base)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setFont(new Font("Consolas", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                int tx = (getWidth()  - fm.stringWidth(txt)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(new Color(0, 0, 0, 40));
                g2.drawString(txt, tx + 1, ty + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(txt, tx, ty);
                g2.dispose();
            }
        };
        simulateBtn.setPreferredSize(new Dimension(0, 42));
        simulateBtn.setContentAreaFilled(false);
        simulateBtn.setBorderPainted(false);
        simulateBtn.setFocusPainted(false);
        simulateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        simulateBtn.addActionListener(e -> { if (onSimulate != null) onSimulate.run(); });

        resetBtn = new JButton("↺  RESET ALL") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? new Color(209, 213, 219)
                         : getModel().isRollover() ? new Color(229, 231, 235)
                                                   : new Color(243, 244, 246);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setFont(new Font("Consolas", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                g2.setColor(SUBTEXT);
                g2.drawString(txt, (getWidth()-fm.stringWidth(txt))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        resetBtn.setPreferredSize(new Dimension(0, 32));
        resetBtn.setContentAreaFilled(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setFocusPainted(false);
        resetBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetBtn.setToolTipText("Clear all inputs and start fresh without restarting");
        resetBtn.addActionListener(e -> resetAll());

        south.add(simulateBtn);
        south.add(resetBtn);
        add(south, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 10, 8, 10));

        JPanel topPane = new JPanel();
        topPane.setLayout(new BoxLayout(topPane, BoxLayout.Y_AXIS));
        topPane.setBackground(BG);

        JPanel qRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        qRow.setBackground(BG);
        qRow.add(styledLabel("Time Quantum (Q):", ACCENT));
        quantumField = styledTextField("2", 65);
        qRow.add(quantumField);
        JLabel hint = new JLabel("  ← integer ≥ 1");
        hint.setFont(new Font("Consolas", Font.ITALIC, 10));
        hint.setForeground(SUBTEXT);
        qRow.add(hint);
        topPane.add(qRow);
        topPane.add(Box.createVerticalStrut(8));

        JPanel noteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        noteRow.setBackground(BG);
        JLabel prioNote = new JLabel("ⓘ  Lower priority number = Highest priority");
        prioNote.setFont(new Font("Consolas", Font.ITALIC, 10));
        prioNote.setForeground(ACCENT2);
        noteRow.add(prioNote);
        topPane.add(noteRow);
        topPane.add(Box.createVerticalStrut(12));

        JPanel scenSection = new JPanel(new BorderLayout(0, 5));
        scenSection.setBackground(BG);

        JPanel scenLabelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        scenLabelRow.setBackground(BG);
        JLabel scenLabel = new JLabel("▸ Load Scenario");
        scenLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        scenLabel.setForeground(ACCENT2);
        scenLabelRow.add(scenLabel);
        scenSection.add(scenLabelRow, BorderLayout.NORTH);

        JPanel scenBtns = new JPanel(new GridLayout(1, 5, 5, 0));
        scenBtns.setBackground(BG);
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            JButton btn = scenarioBtn(SCENARIO_LABELS[i], SCENARIO_COLORS[i], SCENARIO_TIPS[i]);
            btn.addActionListener(e -> loadScenario(idx));
            scenBtns.add(btn);
        }
        scenSection.add(scenBtns, BorderLayout.CENTER);
        topPane.add(scenSection);
        topPane.add(Box.createVerticalStrut(12));

        JPanel tblLabelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tblLabelRow.setBackground(BG);
        JLabel tblLabel = new JLabel("▸ Process Table");
        tblLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        tblLabel.setForeground(ACCENT2);
        tblLabelRow.add(tblLabel);
        topPane.add(tblLabelRow);
        topPane.add(Box.createVerticalStrut(5));

        center.add(topPane, BorderLayout.NORTH);

        String[] cols = {"PID", "Arrival Time", "Burst Time", "Priority"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };

        processTable = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(249, 168, 212));
                    c.setForeground(new Color(31, 41, 55));
                } else {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                    c.setForeground(TEXT);
                }
                return c;
            }
        };
        styleTable(processTable);
        addDefaultRows();

        JScrollPane scrollPane = new JScrollPane(processTable);
        scrollPane.setBackground(CARD);
        scrollPane.getViewport().setBackground(CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.Y_AXIS));
        bottomPane.setBackground(BG);
        bottomPane.add(Box.createVerticalStrut(8));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setBackground(BG);
        addRowBtn    = smallBtn("＋ Add Process", new Color(236,  72, 153));
        removeRowBtn = smallBtn("－ Remove Last", new Color(239,  68,  68));
        clearBtn     = smallBtn("⌫  Clear All",   new Color(139,  92, 246));
        btnRow.add(addRowBtn);
        btnRow.add(removeRowBtn);
        btnRow.add(clearBtn);
        bottomPane.add(btnRow);
        bottomPane.add(Box.createVerticalStrut(5));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        statusLabel.setForeground(ERR_COLOR);
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        statusRow.setBackground(BG);
        statusRow.add(statusLabel);
        bottomPane.add(statusRow);

        center.add(bottomPane, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        // Wire listeners
        addRowBtn.addActionListener(e -> addRow());
        removeRowBtn.addActionListener(e -> removeLastRow());
        clearBtn.addActionListener(e -> clearTable());
    }


    private void loadScenario(int idx) {
        Object[][] data = SCENARIOS[idx];
        quantumField.setText(data[0][0].toString());
        tableModel.setRowCount(0);
        for (int i = 1; i < data.length; i++) tableModel.addRow(data[i]);
        if (idx == 4) {
            setStatus("Scenario E loaded — press RUN SIMULATION to see validation errors.", false);
        } else {
            setStatus("✔  Scenario " + SCENARIO_LABELS[idx] + " loaded — press RUN SIMULATION.", false);
        }
    }

    private void resetAll() {
        tableModel.setRowCount(0);
        quantumField.setText("2");
        statusLabel.setText("  Reset complete — enter your processes and run.");
        statusLabel.setForeground(SUBTEXT);
    }

    private void addDefaultRows() {
        Object[][] defaults = {
            {"P1", "0", "6", "3"}, {"P2", "1", "4", "1"},
            {"P3", "2", "8", "4"}, {"P4", "3", "3", "2"}, {"P5", "4", "5", "5"},
        };
        for (Object[] row : defaults) tableModel.addRow(row);
    }

    private void addRow() {
        int n = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{"P" + n, "0", "1", "1"});
    }

    private void removeLastRow() {
        int rows = tableModel.getRowCount();
        if (rows > 1) tableModel.removeRow(rows - 1);
    }

    private void clearTable() { tableModel.setRowCount(0); }

    public void setOnSimulate(Runnable r) { this.onSimulate = r; }

    public int getQuantum() throws IllegalArgumentException {
        try {
            int q = Integer.parseInt(quantumField.getText().trim());
            if (q < 1) throw new IllegalArgumentException("Time Quantum must be ≥ 1.");
            return q;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Time Quantum must be a valid integer ≥ 1.");
        }
    }

    public List<Process> getProcesses() throws IllegalArgumentException {
        List<Process> list = new ArrayList<>();
        Set<String>   pids = new HashSet<>();
        if (tableModel.getRowCount() == 0)
            throw new IllegalArgumentException("Please add at least one process.");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String pid   = String.valueOf(tableModel.getValueAt(i, 0)).trim();
            String atStr = String.valueOf(tableModel.getValueAt(i, 1)).trim();
            String btStr = String.valueOf(tableModel.getValueAt(i, 2)).trim();
            String prStr = String.valueOf(tableModel.getValueAt(i, 3)).trim();

            if (pid.isEmpty())      throw new IllegalArgumentException("Row " + (i+1) + ": PID cannot be empty.");
            if (pids.contains(pid)) throw new IllegalArgumentException("Duplicate PID: " + pid);
            pids.add(pid);

            int at, bt, pr;
            try { at = Integer.parseInt(atStr); if (at < 0) throw new NumberFormatException(); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Row "+(i+1)+" ["+pid+"]: Arrival Time must be integer ≥ 0."); }

            try { bt = Integer.parseInt(btStr); if (bt < 1) throw new NumberFormatException(); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Row "+(i+1)+" ["+pid+"]: Burst Time must be integer ≥ 1."); }

            try { pr = Integer.parseInt(prStr); if (pr < 1) throw new NumberFormatException(); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Row "+(i+1)+" ["+pid+"]: Priority must be integer ≥ 1."); }

            list.add(new Process(pid, at, bt, pr));
        }
        return list;
    }

    public void setStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setForeground(isError ? ERR_COLOR : new Color(20, 184, 166));
    }


    private JLabel styledLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Consolas", Font.BOLD, 12));
        l.setForeground(color);
        return l;
    }

    private JTextField styledTextField(String text, int width) {
        JTextField f = new JTextField(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("Consolas", Font.BOLD, 13));
        f.setForeground(ACCENT);
        f.setCaretColor(ACCENT);
        f.setBackground(FIELD_BG);
        f.setOpaque(false);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setPreferredSize(new Dimension(width, 30));
        return f;
    }

    private JButton smallBtn(String text, Color base) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                    ? base
                    : new Color(base.getRed(), base.getGreen(), base.getBlue(), 30);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 180));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setColor(getModel().isRollover() ? Color.WHITE : base);
                g2.setFont(new Font("Consolas", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(base);
        b.setFont(new Font("Consolas", Font.BOLD, 11));
        b.setPreferredSize(new Dimension(135, 28));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton scenarioBtn(String label, Color base, String tooltip) {
        JButton b = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(base.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(base);
                } else {
                    g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 22));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(),
                            getModel().isRollover() ? 255 : 130));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.setFont(new Font("Consolas", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(getModel().isRollover() ? Color.WHITE : base);
                g2.drawString(getText(),
                    (getWidth()  - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(base);
        b.setFont(new Font("Consolas", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(0, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tooltip);
        return b;
    }

    private void styleTable(JTable t) {
        t.setBackground(CARD);
        t.setForeground(TEXT);
        t.setFont(new Font("Consolas", Font.PLAIN, 13));
        t.setRowHeight(30);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 2));
        t.setSelectionBackground(new Color(249, 168, 212));
        t.setSelectionForeground(new Color(31, 41, 55));
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.setFillsViewportHeight(true);

        JTableHeader hdr = t.getTableHeader();
        hdr.setBackground(new Color(252, 231, 243));
        hdr.setForeground(ACCENT2);
        hdr.setFont(new Font("Consolas", Font.BOLD, 12));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        hdr.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        int[] minW = {50, 105, 100, 90};
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(center);
            t.getColumnModel().getColumn(i).setMinWidth(minW[i]);
            t.getColumnModel().getColumn(i).setPreferredWidth(minW[i]);
        }
    }

    private static Color brighter(Color c) {
        return new Color(Math.min(c.getRed()+30,255), Math.min(c.getGreen()+30,255), Math.min(c.getBlue()+30,255));
    }
    private static Color darker(Color c) {
        return new Color(Math.max(c.getRed()-30,0), Math.max(c.getGreen()-30,0), Math.max(c.getBlue()-30,0));
    }
}