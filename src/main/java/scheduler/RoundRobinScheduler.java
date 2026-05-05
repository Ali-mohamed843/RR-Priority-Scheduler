package scheduler;

import model.Process;
import model.SchedulingResult;

import java.util.*;

public class RoundRobinScheduler {

    public SchedulingResult schedule(List<Process> originalProcesses, int quantum) {
        SchedulingResult result = new SchedulingResult("Round Robin (Q=" + quantum + ")");

        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int n = processes.size();
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0;
        int completed = 0;
        int idx = 0; 

        while (idx < n && processes.get(idx).getArrivalTime() <= currentTime) {
            readyQueue.add(processes.get(idx));
            idx++;
        }

        while (completed < n) {
            if (readyQueue.isEmpty()) {
                if (idx < n) {
                    result.addGanttEntry("IDLE", currentTime, processes.get(idx).getArrivalTime());
                    currentTime = processes.get(idx).getArrivalTime();
                    while (idx < n && processes.get(idx).getArrivalTime() <= currentTime) {
                        readyQueue.add(processes.get(idx));
                        idx++;
                    }
                } else break;
                continue;
            }

            Process current = readyQueue.poll();

            if (!current.isFirstResponseRecorded()) {
                current.setResponseTime(currentTime - current.getArrivalTime());
                current.setFirstResponseRecorded(true);
            }

            int execTime = Math.min(quantum, current.getRemainingTime());
            int start = currentTime;
            currentTime += execTime;
            current.setRemainingTime(current.getRemainingTime() - execTime);

            result.addGanttEntry(current.getPid(), start, currentTime);

            while (idx < n && processes.get(idx).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(idx));
                idx++;
            }

            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(currentTime);
                current.setTurnaroundTime(currentTime - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                completed++;
            } else {
                readyQueue.add(current);
            }
        }

        result.setProcesses(processes);
        result.calculateAverages();
        return result;
    }
}