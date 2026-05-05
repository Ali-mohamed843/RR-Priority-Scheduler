package model;

public class Process {
    private String pid;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingTime;

    private int completionTime;
    private int waitingTime;
    private int turnaroundTime;
    private int responseTime;
    private boolean firstResponseRecorded;

    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.firstResponseRecorded = false;
        this.responseTime = -1;
    }

    public Process copy() {
        Process p = new Process(pid, arrivalTime, burstTime, priority);
        p.remainingTime = this.burstTime;
        p.firstResponseRecorded = false;
        p.responseTime = -1;
        return p;
    }

    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int t) { this.remainingTime = t; }

    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int t) { this.completionTime = t; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int t) { this.waitingTime = t; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int t) { this.turnaroundTime = t; }

    public int getResponseTime() { return responseTime; }
    public void setResponseTime(int t) { this.responseTime = t; }

    public boolean isFirstResponseRecorded() { return firstResponseRecorded; }
    public void setFirstResponseRecorded(boolean b) { this.firstResponseRecorded = b; }

    @Override
    public String toString() {
        return "Process{pid=" + pid + ", AT=" + arrivalTime + ", BT=" + burstTime + ", P=" + priority + "}";
    }
}