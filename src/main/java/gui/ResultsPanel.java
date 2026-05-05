package gui;

import model.Process;
import model.SchedulingResult;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ResultsPanel extends JPanel {

    private static final Color BG         = new Color(253, 242, 248);
    private static final Color CARD       = new Color(255, 255, 255); 
    private static final Color TEXT       = new Color( 31,  41,  55); 
    private static final Color FIELD_BG   = new Color(255, 241, 242);
    private static final Color BORDER_COL = new Color(249, 168, 212); 
    private static final Color ROW_ALT    = new Color(253, 242, 248); 

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel avgLabel;
    private String algorithmTitle;
    private Color  accentColor;

    public ResultsPanel(String algorithmTitle, Color accentColor) {
        this.algorithmTitle = algorithmTitle;
        this.accentColor    = accentColor;
        setLayout(new BorderLayout(0, 4));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        header.setBackground(new Color(252, 231, 243));
        header.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor));

        JLabel title = new JLabel(algorithmTitle);
        title.setFont(new Font("Consolas", Font.BOLD, 13));
        title.setForeground(accentColor);
        header.add(title);
        add(header, BorderLayout.NORTH);

        String[] cols = {"PID", "AT", "BT", "Priority", "CT", "TAT", "WT", "RT"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(
                        accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
                    c.setForeground(TEXT);
                } else {
                    c.setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                    c.setForeground(TEXT);
                }
                return c;
            }
        };

        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(CARD);
        scroll.getViewport().setBackground(CARD);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        add(scroll, BorderLayout.CENTER);

        avgLabel = new JLabel("  ∅ —");
        avgLabel.setFont(new Font("Consolas", Font.BOLD, 11));
        avgLabel.setForeground(accentColor);
        avgLabel.setBackground(new Color(252, 231, 243));
        avgLabel.setOpaque(true);
        avgLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(avgLabel, BorderLayout.SOUTH);
    }

    public void setResult(SchedulingResult result) {
        tableModel.setRowCount(0);
        if (result == null) return;

        List<Process> processes = result.getProcesses();
        processes.sort((a, b) -> a.getPid().compareTo(b.getPid()));

        for (Process p : processes) {
            tableModel.addRow(new Object[]{
                p.getPid(),
                p.getArrivalTime(),
                p.getBurstTime(),
                p.getPriority(),
                p.getCompletionTime(),
                p.getTurnaroundTime(),
                p.getWaitingTime(),
                p.getResponseTime()
            });
        }

        avgLabel.setText(String.format(
            "  ∅  WT = %.2f   |   TAT = %.2f   |   RT = %.2f",
            result.getAvgWaitingTime(),
            result.getAvgTurnaroundTime(),
            result.getAvgResponseTime()
        ));
    }

    private void styleTable() {
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setFont(new Font("Consolas", Font.PLAIN, 12));
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(
            accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 50));
        table.setSelectionForeground(TEXT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(252, 231, 243));
        header.setForeground(accentColor);
        header.setFont(new Font("Consolas", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        int[] widths = {50, 45, 45, 65, 50, 55, 55, 55};
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
}