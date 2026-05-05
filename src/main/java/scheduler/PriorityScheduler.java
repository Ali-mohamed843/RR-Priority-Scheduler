package scheduler;

import model.Process;
import model.SchedulingResult;

import java.util.*;

public class PriorityScheduler {

    public SchedulingResult schedule(List<Process> originalProcesses) {
        SchedulingResult result = new SchedulingResult("Priority Scheduling (Preemptive)");

        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int n          = processes.size();
        int completed  = 0;
        int currentTime = 0;

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt(Process::getPriority)
                      .thenComparingInt(Process::getArrivalTime)
        );

        int nextIdx = 0; 

        String  lastLabel = null;
        int     segStart  = 0;

        while (completed < n) {

            while (nextIdx < n &&
                   processes.get(nextIdx).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(nextIdx));
                nextIdx++;
            }

            if (readyQueue.isEmpty()) {
                if (lastLabel != null) {
                    result.addGanttEntry(lastLabel, segStart, currentTime);
                    lastLabel = null;
                }
                int nextArrival = processes.get(nextIdx).getArrivalTime();
                result.addGanttEntry("IDLE", currentTime, nextArrival);
                currentTime = nextArrival;
                continue;
            }

            Process running = readyQueue.poll();

            if (!running.isFirstResponseRecorded()) {
                running.setResponseTime(currentTime - running.getArrivalTime());
                running.setFirstResponseRecorded(true);
            }

            if (!running.getPid().equals(lastLabel)) {
                if (lastLabel != null) {
                    result.addGanttEntry(lastLabel, segStart, currentTime);
                }
                lastLabel = running.getPid();
                segStart  = currentTime;
            }

            currentTime++;
            running.setRemainingTime(running.getRemainingTime() - 1);

            if (running.getRemainingTime() == 0) {
                result.addGanttEntry(lastLabel, segStart, currentTime);
                lastLabel = null;

                running.setCompletionTime(currentTime);
                running.setTurnaroundTime(currentTime - running.getArrivalTime());
                running.setWaitingTime(running.getTurnaroundTime() - running.getBurstTime());
                completed++;
            } else {
                readyQueue.add(running);
            }

            while (nextIdx < n &&
                   processes.get(nextIdx).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(nextIdx));
                nextIdx++;
            }
        }

        if (lastLabel != null) {
            result.addGanttEntry(lastLabel, segStart, currentTime);
        }

        result.setProcesses(processes);
        result.calculateAverages();
        return result;
    }
}