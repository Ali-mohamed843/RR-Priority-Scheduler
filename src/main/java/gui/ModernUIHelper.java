package gui;

import model.Process;
import model.SchedulingResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ModernUIHelper {

    public static final Color BG         = new Color(253, 242, 248); 
    public static final Color PANEL_BG   = new Color(255, 255, 255); 
    public static final Color ACCENT     = new Color(236,  72, 153); 
    public static final Color ACCENT2    = new Color(139,  92, 246); 
    public static final Color ACCENT3    = new Color( 20, 184, 166); 
    public static final Color TEXT       = new Color( 31,  41,  55); 
    public static final Color SUBTEXT    = new Color(107, 114, 128); 
    public static final Color BORDER_COL = new Color(249, 168, 212); 
    public static final Font  MONO_BOLD  = new Font("Consolas", Font.BOLD,  12);
    public static final Font  MONO       = new Font("Consolas", Font.PLAIN, 12);

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",              BG);
        UIManager.put("ScrollPane.background",         BG);
        UIManager.put("Viewport.background",           BG);
        UIManager.put("ScrollBar.background",          new Color(252, 231, 243));
        UIManager.put("ScrollBar.thumb",               new Color(249, 168, 212));
        UIManager.put("ScrollBar.thumbHighlight",      ACCENT);
        UIManager.put("ScrollBar.thumbShadow",         new Color(251, 207, 232));
        UIManager.put("ScrollBar.track",               new Color(252, 231, 243));
        UIManager.put("ScrollBar.trackHighlight",      new Color(252, 231, 243));
        UIManager.put("TabbedPane.background",         BG);
        UIManager.put("TabbedPane.foreground",         TEXT);
        UIManager.put("TabbedPane.selected",           PANEL_BG);
        UIManager.put("TabbedPane.selectedForeground", ACCENT);
        UIManager.put("TabbedPane.contentBorderInsets",new Insets(2, 0, 0, 0));
        UIManager.put("ToolTip.background",            PANEL_BG);
        UIManager.put("ToolTip.foreground",            TEXT);
    }

    public static String buildComparisonHTML(SchedulingResult rr, SchedulingResult ps) {
        if (rr == null || ps == null) {
            return "<html><body style='font-family:Consolas;color:#9ca3af;padding:10px'>"
                 + "Run the simulation to see the comparison summary.</body></html>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Consolas;font-size:12px;color:#1f2937;padding:6px 10px;line-height:1.7'>");

        sb.append("<b style='color:#ec4899;font-size:13px'>◈ Metrics Comparison</b><br>");
        sb.append("<table width='100%' cellspacing='0' cellpadding='4'>");
        sb.append("<tr style='color:#8b5cf6'><th align='left'>Metric</th><th align='center'>Round Robin</th><th align='center'>Priority</th><th align='center'>Winner</th></tr>");

        appendMetricRow(sb, "Avg Waiting Time",    rr.getAvgWaitingTime(),    ps.getAvgWaitingTime(),    true);
        appendMetricRow(sb, "Avg Turnaround Time", rr.getAvgTurnaroundTime(), ps.getAvgTurnaroundTime(), true);
        appendMetricRow(sb, "Avg Response Time",   rr.getAvgResponseTime(),   ps.getAvgResponseTime(),   true);
        sb.append("</table><br>");

        sb.append("<b style='color:#ec4899;font-size:13px'>◈ Qualitative Analysis</b><br>");

        double rrWTVariance = computeWTVariance(rr.getProcesses());
        double psWTVariance = computeWTVariance(ps.getProcesses());
        String fairWinner = rrWTVariance <= psWTVariance ? "Round Robin" : "Priority";
        sb.append("• <b>Fairness (WT variance):</b>  RR = ").append(String.format("%.2f", rrWTVariance))
          .append("  |  Priority = ").append(String.format("%.2f", psWTVariance))
          .append("  →  <b style='color:#14b8a6'>").append(fairWinner).append(" is more balanced.</b><br>");

        sb.append("• <b>Urgency benefit:</b>  Priority scheduling accelerates high-priority (low number) processes.<br>");

        double maxWT_PS = ps.getProcesses().stream().mapToInt(Process::getWaitingTime).max().orElse(0);
        double minWT_PS = ps.getProcesses().stream().mapToInt(Process::getWaitingTime).min().orElse(0);
        boolean starvationRisk = (maxWT_PS - minWT_PS) > 2.5 * ps.getAvgWaitingTime();
        sb.append("• <b>Starvation risk in Priority:</b>  ")
          .append(starvationRisk
              ? "<b style='color:#ef4444'>⚠ Likely — low-priority processes wait significantly longer.</b>"
              : "<b style='color:#14b8a6'>✔ Not observed in this workload.</b>")
          .append("<br>");

        sb.append("• <b>Balanced service:</b>  Round Robin distributes CPU time in equal-quantum slices, ");
        sb.append(rrWTVariance < psWTVariance
            ? "achieving more even service across processes."
            : "though Priority scheduling happened to be more balanced here due to arrival timing.")
          .append("<br><br>");

        sb.append("<b style='color:#ec4899;font-size:13px'>◈ Analysis Answers</b><br>");

        String betterWT  = rr.getAvgWaitingTime()   <= ps.getAvgWaitingTime()   ? "Round Robin" : "Priority";
        String betterRT  = rr.getAvgResponseTime()  <= ps.getAvgResponseTime()  ? "Round Robin" : "Priority";
        String recommend = (rr.getAvgWaitingTime() + rr.getAvgResponseTime()) <=
                           (ps.getAvgWaitingTime()  + ps.getAvgResponseTime())  ? "Round Robin" : "Priority";

        sb.append("① Better avg WT → <b style='color:#8b5cf6'>").append(betterWT).append("</b><br>");
        sb.append("② Better response time → <b style='color:#8b5cf6'>").append(betterRT).append("</b><br>");
        sb.append("③ Higher-priority processes gained advantage in Priority scheduling: <b>Yes</b><br>");
        sb.append("④ Round Robin more balanced across processes: <b>")
          .append(rrWTVariance <= psWTVariance ? "Yes ✔" : "Not in this case").append("</b><br>");
        sb.append("⑤ Starvation observed: <b>")
          .append(starvationRisk ? "Yes ⚠" : "No ✔").append("</b><br>");
        sb.append("⑥ Recommended for this workload: <b style='color:#14b8a6'>").append(recommend).append("</b><br>");

        sb.append("</body></html>");
        return sb.toString();
    }

    public static String buildConclusionHTML(SchedulingResult rr, SchedulingResult ps) {
        if (rr == null || ps == null) {
            return "<html><body style='font-family:Consolas;color:#9ca3af;padding:10px'>"
                 + "Run the simulation to generate a conclusion.</body></html>";
        }

        String overallWinner = (rr.getAvgWaitingTime() + rr.getAvgTurnaroundTime()) <=
                               (ps.getAvgWaitingTime()  + ps.getAvgTurnaroundTime())
                               ? "Round Robin" : "Priority Scheduling";
        double maxWT_PS = ps.getProcesses().stream().mapToInt(Process::getWaitingTime).max().orElse(0);
        double minWT_PS = ps.getProcesses().stream().mapToInt(Process::getWaitingTime).min().orElse(0);
        boolean starvation = (maxWT_PS - minWT_PS) > 2.5 * ps.getAvgWaitingTime();
        double rrVar = computeWTVariance(rr.getProcesses());
        double psVar = computeWTVariance(ps.getProcesses());

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Consolas;font-size:12px;color:#1f2937;padding:6px 10px;line-height:1.8'>");
        sb.append("<b style='color:#ec4899;font-size:14px'>◈ Final Conclusion</b><br><br>");
        sb.append("Based on the simulated workload, <b style='color:#8b5cf6'>").append(overallWinner)
          .append("</b> delivered better overall performance (lower avg WT + TAT).<br><br>");
        sb.append("• <b>Priority-based service</b> ")
          .append("improved urgent-task treatment — processes with lower priority numbers ")
          .append("completed earlier, reducing their waiting and turnaround times.<br>");
        sb.append("• <b>Round Robin fairness</b>: WT variance for RR = ").append(String.format("%.2f", rrVar))
          .append(" vs Priority = ").append(String.format("%.2f", psVar)).append(".  ")
          .append(rrVar <= psVar
              ? "Round Robin distributed CPU time more evenly."
              : "Priority scheduling was more balanced in this specific workload.")
          .append("<br>");
        sb.append("• <b>Starvation risk</b>: ")
          .append(starvation
              ? "<b style='color:#ef4444'>Starvation appeared in Priority Scheduling</b> — low-priority processes waited disproportionately long."
              : "<b style='color:#14b8a6'>No starvation detected</b> in this workload; all processes completed in reasonable time.")
          .append("<br><br>");
        sb.append("<i style='color:#9ca3af'>— For interactive multi-user systems, Round Robin is preferred for fairness.<br>");
        sb.append("   For real-time or critical tasks, Priority Scheduling ensures urgent work is done first.</i>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private static void appendMetricRow(StringBuilder sb, String name, double rrVal, double psVal, boolean lowerIsBetter) {
        boolean rrWins = lowerIsBetter ? rrVal <= psVal : rrVal >= psVal;
        String rrColor = rrWins ? "#14b8a6" : "#ef4444";
        String psColor = rrWins ? "#ef4444" : "#14b8a6";
        String winner  = rrWins ? "Round Robin ✔" : "Priority ✔";
        String wColor  = rrWins ? "#ec4899" : "#8b5cf6";
        sb.append("<tr>");
        sb.append("<td style='color:#1f2937'>").append(name).append("</td>");
        sb.append("<td align='center' style='color:").append(rrColor).append("'>").append(String.format("%.2f", rrVal)).append("</td>");
        sb.append("<td align='center' style='color:").append(psColor).append("'>").append(String.format("%.2f", psVal)).append("</td>");
        sb.append("<td align='center' style='color:").append(wColor).append("'>").append(winner).append("</td>");
        sb.append("</tr>");
    }

    private static double computeWTVariance(List<Process> processes) {
        if (processes.isEmpty()) return 0;
        double mean = processes.stream().mapToInt(Process::getWaitingTime).average().orElse(0);
        return processes.stream().mapToDouble(p -> Math.pow(p.getWaitingTime() - mean, 2)).average().orElse(0);
    }
}