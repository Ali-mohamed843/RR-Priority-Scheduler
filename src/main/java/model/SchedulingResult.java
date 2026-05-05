package model;

import java.util.List;
import java.util.ArrayList;

public class SchedulingResult {
    private List<Process> processes;
    private List<int[]> ganttChart; 
    private List<String> ganttLabels;
    private double avgWaitingTime;
    private double avgTurnaroundTime;
    private double avgResponseTime;
    private String algorithmName;

    public SchedulingResult(String algorithmName) {
        this.algorithmName = algorithmName;
        this.processes = new ArrayList<>();
        this.ganttChart = new ArrayList<>();
        this.ganttLabels = new ArrayList<>();
    }

    public void addGanttEntry(String label, int start, int end) {
        ganttLabels.add(label);
        ganttChart.add(new int[]{start, end});
    }

    public void calculateAverages() {
        if (processes.isEmpty()) return;
        double totalWT = 0, totalTAT = 0, totalRT = 0;
        for (Process p : processes) {
            totalWT += p.getWaitingTime();
            totalTAT += p.getTurnaroundTime();
            totalRT += p.getResponseTime();
        }
        int n = processes.size();
        avgWaitingTime = totalWT / n;
        avgTurnaroundTime = totalTAT / n;
        avgResponseTime = totalRT / n;
    }

    public List<Process> getProcesses() { return processes; }
    public void setProcesses(List<Process> p) { this.processes = p; }

    public List<int[]> getGanttChart() { return ganttChart; }
    public List<String> getGanttLabels() { return ganttLabels; }

    public double getAvgWaitingTime() { return avgWaitingTime; }
    public double getAvgTurnaroundTime() { return avgTurnaroundTime; }
    public double getAvgResponseTime() { return avgResponseTime; }
    public String getAlgorithmName() { return algorithmName; }

    public int getTotalTime() {
        if (ganttChart.isEmpty()) return 0;
        return ganttChart.get(ganttChart.size() - 1)[1];
    }
}