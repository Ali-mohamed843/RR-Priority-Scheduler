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

        int n           = processes.size();
        int completed   = 0;
        int currentTime = 0;

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
        Comparator.comparingInt(Process::getPriority)
                  .thenComparingInt(Process::getArrivalTime)
                  .thenComparing(Process::getPid)
    );

        int nextIdx   = 0;
        String lastLabel = null;
        int    segStart  = 0;

        Process currentRunning = null;

        while (completed < n) {

            while (nextIdx < n &&
                   processes.get(nextIdx).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(nextIdx));
                nextIdx++;
            }

            if (currentRunning == null) {

                if (!readyQueue.isEmpty()) {
                    currentRunning = readyQueue.poll();
                }

            } else {

                Process topOfQueue = readyQueue.peek();
                if (topOfQueue != null &&
                    topOfQueue.getPriority() < currentRunning.getPriority()) {

                    readyQueue.add(currentRunning);
                    currentRunning = readyQueue.poll();
                }
            }

            if (currentRunning == null) {

                if (lastLabel != null) {
                    result.addGanttEntry(lastLabel, segStart, currentTime);
                    lastLabel = null;
                }

                if (nextIdx < n) {
                    int nextArrival = processes.get(nextIdx).getArrivalTime();
                    result.addGanttEntry("IDLE", currentTime, nextArrival);
                    currentTime = nextArrival;
                }
                continue;
            }

            if (!currentRunning.isFirstResponseRecorded()) {
                currentRunning.setResponseTime(currentTime - currentRunning.getArrivalTime());
                currentRunning.setFirstResponseRecorded(true);
            }

            if (!currentRunning.getPid().equals(lastLabel)) {
                if (lastLabel != null) {
                    result.addGanttEntry(lastLabel, segStart, currentTime);
                }
                lastLabel = currentRunning.getPid();
                segStart  = currentTime;
            }

            currentTime++;
            currentRunning.setRemainingTime(currentRunning.getRemainingTime() - 1);

            if (currentRunning.getRemainingTime() == 0) {

                result.addGanttEntry(lastLabel, segStart, currentTime);
                lastLabel = null;

                currentRunning.setCompletionTime(currentTime);
                currentRunning.setTurnaroundTime(currentTime - currentRunning.getArrivalTime());
                currentRunning.setWaitingTime(currentRunning.getTurnaroundTime() - currentRunning.getBurstTime());

                completed++;
                currentRunning = null; 
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
