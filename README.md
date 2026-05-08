# ⚙️ CPU Scheduling Simulator — Round Robin vs Priority

> **Course:** Operating Systems
> **Project:** C3 — Scheduling Comparison Project
> **Algorithms:** Round Robin (RR) & Preemptive Priority Scheduling (PS)

---

## 📌 Table of Contents

1. [Project Overview](#project-overview)
2. [Team Members](#team-members)
3. [Project Structure](#project-structure)
4. [How to Run](#how-to-run)
5. [Features](#features)
6. [Algorithm Details](#algorithm-details)
7. [Input Validation Rules](#input-validation-rules)
8. [Required Scenarios](#required-scenarios)
   - [Scenario A — Basic Mixed Workload](#scenario-a--basic-mixed-workload)
   - [Scenario B — Urgency Case](#scenario-b--urgency-case)
   - [Scenario C — Fairness Case](#scenario-c--fairness-case)
   - [Scenario D — Starvation Case](#scenario-d--starvation-case)
   - [Scenario E — Validation Case](#scenario-e--validation-case)
9. [Analysis & Conclusion](#analysis--conclusion)
10. [GUI Guide](#gui-guide)

---

## Project Overview

This simulator implements and compares two CPU scheduling algorithms:

| Algorithm | Type | Key Idea |
|---|---|---|
| **Round Robin** | Preemptive | Each process gets a fixed time slice (quantum); processes rotate fairly |
| **Priority Scheduling** | Preemptive | The process with the lowest priority number runs first; higher-priority arrivals preempt the current process |

The goal is to analyze:
- **Fairness vs Urgency** — does every process get equal treatment, or do urgent ones win?
- **Response Time** — how quickly does each process first get the CPU?
- **Starvation Risk** — can low-priority processes wait forever in Priority Scheduling?
- **Balanced Service** — does Round Robin give more even CPU distribution?

---

## Team Members

| # | Name | Responsibility |
|---|---|---|
| 1 | *(Ahmed-Shrouk)* | `Process.java` + `SchedulingResult.java` — Data Models |
| 2 | *(Shahd-Samah)* | `RoundRobinScheduler.java` — RR Algorithm |
| 3 | *(Arwa-Jana)* | `PriorityScheduler.java` — Preemptive Priority Algorithm |
| 4 | *(Ali)* | All GUI files — Interface & Integration |

---

## Project Structure

```
RRproject/
│
├── src/
│   ├── Main.java                        ← Entry point
│   │
│   ├── model/
│   │   ├── Process.java                 ← Process data + metrics (WT, TAT, RT)
│   │   └── SchedulingResult.java        ← Stores Gantt chart + averages
│   │
│   ├── scheduler/
│   │   ├── RoundRobinScheduler.java     ← Round Robin algorithm
│   │   └── PriorityScheduler.java       ← Preemptive Priority algorithm
│   │
│   └── gui/
│       ├── MainFrame.java               ← Main application window (4 tabs)
│       ├── InputPanel.java              ← Left panel: process table + controls
│       ├── GanttChartPanel.java         ← Custom-painted Gantt chart
│       ├── ResultsPanel.java            ← Metrics table (WT, TAT, RT, CT)
│       └── ModernUIHelper.java          ← Colors, fonts, comparison & conclusion HTML
│
└── README.md
```

---

## How to Run

### Prerequisites
- Java JDK 11 or higher
- Any Java IDE (IntelliJ IDEA, NetBeans, Eclipse) **or** command line

### Option 1 — Command Line
```bash
# Step 1: Compile
javac -d out -sourcepath src src/Main.java

# Step 2: Run
java -cp out Main
```

### Option 2 — NetBeans / IntelliJ
1. Open the project folder
2. Set `Main.java` as the main class
3. Click **Run**

---

## Features

### ✅ Core Features
- Dynamic process table — add, remove, or clear processes at runtime
- Configurable **Time Quantum** for Round Robin
- **Preemptive Priority Scheduling** — higher-priority arrivals immediately preempt the running process
- Separate **Gantt Charts** for both algorithms (color-coded per process)
- Separate **Results Tables** showing: AT, BT, Priority, CT, TAT, WT, RT
- **Average metrics** displayed at the bottom of each results table
- **Comparison Summary** — side-by-side analysis with winner detection
- **Final Conclusion** — auto-generated analysis answering all required questions
- **Input Validation** — all fields checked before simulation runs
- **Reset** — clear the table and re-enter data without restarting the program
- **Pre-loaded Scenarios** — one-click buttons to load Scenarios A through E

### 📊 Metrics Calculated
| Metric | Formula |
|---|---|
| Completion Time (CT) | Time when process finishes |
| Turnaround Time (TAT) | CT − Arrival Time |
| Waiting Time (WT) | TAT − Burst Time |
| Response Time (RT) | Time of first CPU access − Arrival Time |
| Average WT | Sum of all WT ÷ number of processes |
| Average TAT | Sum of all TAT ÷ number of processes |
| Average RT | Sum of all RT ÷ number of processes |

---

## Algorithm Details

### Round Robin
- Each process receives exactly **Q time units** (the quantum)
- If a process doesn't finish in its slice, it goes to the **back of the ready queue**
- Processes that arrive while a slice is running are added to the queue **after** the current slice ends
- Handles **idle CPU** correctly when no process has arrived yet

**Priority Rule for tie-breaking:** Earlier arrival time runs first (FCFS).

---

### Preemptive Priority Scheduling
- The process with the **lowest priority number** holds the CPU (1 = highest priority)
- At every time unit, the ready queue is re-evaluated
- If a new process arrives with a **strictly lower priority number** than the running process, it **immediately preempts** it
- The preempted process returns to the ready queue and resumes later

**Tie-breaking Rule:** Among equal-priority processes, the one that arrived earliest (FCFS) runs first.

---

## Input Validation Rules

| Field | Rule | Error Message |
|---|---|---|
| PID | Cannot be empty; must be unique | "PID cannot be empty" / "Duplicate PID" |
| Arrival Time | Integer ≥ 0 | "Arrival Time must be integer ≥ 0" |
| Burst Time | Integer ≥ 1 | "Burst Time must be integer ≥ 1" |
| Priority | Integer ≥ 1 | "Priority must be integer ≥ 1" |
| Time Quantum | Integer ≥ 1 | "Time Quantum must be a valid integer ≥ 1" |
| Process Table | At least 1 row required | "Please add at least one process" |

All errors are shown in **red** in the status bar at the bottom of the input panel. The simulation will not run until all inputs are valid.

---

## Required Scenarios

> All scenarios can be loaded instantly using the **Load Scenario** buttons in the input panel.

---

### Scenario A — Basic Mixed Workload

**Description:** A normal workload with 5 processes of varying burst times and priorities.

| PID | Arrival Time | Burst Time | Priority |
|-----|-------------|------------|----------|
| P1  | 0           | 6          | 3        |
| P2  | 1           | 4          | 1        |
| P3  | 2           | 8          | 4        |
| P4  | 3           | 3          | 2        |
| P5  | 4           | 5          | 5        |

**Time Quantum:** 2

**Expected Observations:**
- Round Robin distributes CPU time in equal 2-unit slices — all processes progress simultaneously
- Priority Scheduling runs P2 first (priority 1), then P4, then P1, P3, P5

---

### Scenario B — Urgency Case

**Description:** One process has clearly higher priority to show how Priority Scheduling benefits urgent tasks.

| PID | Arrival Time | Burst Time | Priority |
|-----|-------------|------------|----------|
| P1  | 0           | 10         | 5        |
| P2  | 2           | 4          | 1        |
| P3  | 3           | 6          | 4        |
| P4  | 5           | 2          | 2        |

**Time Quantum:** 3

**Expected Observations:**
- **P2 (priority 1)** preempts P1 the moment it arrives at time 2 — very low WT and RT
- In Round Robin, P2 waits for its turn — higher WT and RT
- Priority Scheduling gives P2 a **massive advantage** — clearly shows the urgency benefit

---

### Scenario C — Fairness Case

**Description:** All processes have the same priority to reveal how Round Robin distributes service more evenly.

| PID | Arrival Time | Burst Time | Priority |
|-----|-------------|------------|----------|
| P1  | 0           | 8          | 2        |
| P2  | 0           | 8          | 2        |
| P3  | 0           | 8          | 2        |
| P4  | 0           | 8          | 2        |

**Time Quantum:** 2

**Expected Observations:**
- Round Robin: all processes finish at nearly the same time — **WT variance ≈ 0**
- Priority Scheduling: same priority → FCFS order (P1 → P2 → P3 → P4) — P4 waits the longest
- Round Robin is **dramatically more fair** in this scenario

---

### Scenario D — Starvation Case

**Description:** One process has very low priority and may wait a very long time while high-priority processes keep arriving.

| PID | Arrival Time | Burst Time | Priority |
|-----|-------------|------------|----------|
| P1  | 0           | 3          | 1        |
| P2  | 1           | 5          | 1        |
| P3  | 2           | 4          | 1        |
| P4  | 3           | 6          | 1        |
| P5  | 0           | 7          | 5        |

**Time Quantum:** 2

**Expected Observations:**
- **P5 (priority 5)** is blocked by all priority-1 processes
- P5 does not run until all other processes complete — **very high WT**
- In Round Robin, P5 gets its fair share from the beginning
- This scenario demonstrates **starvation risk** in Priority Scheduling

---

### Scenario E — Validation Case

**Description:** Demonstrates how the simulator handles invalid inputs.

**Invalid inputs to test:**

| Test | What to Enter | Expected Error |
|---|---|---|
| Empty Burst Time | Leave BT blank | "Burst Time must be integer ≥ 1" |
| Burst Time = 0 | Enter 0 in BT | "Burst Time must be integer ≥ 1" |
| Negative Arrival Time | Enter -1 in AT | "Arrival Time must be integer ≥ 0" |
| Duplicate PID | Two rows both named P1 | "Duplicate PID: P1" |
| Quantum = 0 | Set Q to 0 | "Time Quantum must be a valid integer ≥ 1" |
| Text in number field | Enter "abc" in BT | "Burst Time must be integer ≥ 1" |

**Expected Behavior:** The simulation does NOT run. The error is shown in red in the status bar.

---

## Analysis & Conclusion

### Required Analysis Questions

| Question | Answer |
|---|---|
| Which algorithm gave better average waiting time? | **Depends on workload** — Priority wins when priorities are spread; RR wins for equal-priority workloads |
| Which algorithm gave better response time? | **Priority Scheduling** — high-priority processes get the CPU almost immediately |
| Did higher-priority processes gain significant advantage? | **Yes** — they preempt lower-priority processes and finish much earlier |
| Did Round Robin appear more balanced across all processes? | **Yes** — WT variance is consistently lower in RR |
| Was starvation observed or likely in Priority Scheduling? | **Yes** — Scenario D demonstrates this clearly |
| Which algorithm would you recommend? | **Round Robin** for fairness / interactive systems; **Priority** for real-time / critical tasks |

### General Conclusion

- **Priority Scheduling** gives urgent processes a significant advantage — ideal for real-time systems where deadlines matter.
- **Round Robin** provides balanced, fair service to all processes — ideal for time-sharing and interactive systems.
- **Starvation is a real risk** in Priority Scheduling when high-priority processes keep arriving — low-priority processes can be indefinitely delayed.
- **Round Robin eliminates starvation** by guaranteeing every process gets CPU time within at most `(n-1) × Q` time units.

---

## GUI Guide

### Left Panel — Input
| Element | Description |
|---|---|
| Time Quantum field | Set the RR quantum (integer ≥ 1) |
| Process Table | Enter PID, Arrival Time, Burst Time, Priority |
| ＋ Add Process | Adds a new empty row |
| － Remove Last | Removes the last row |
| ⌫ Clear All | Clears all rows |
| Scenario Buttons | Load pre-defined scenarios A–E instantly |
| RUN SIMULATION | Validates input and runs both algorithms |
| Status bar | Shows success message or validation error in red |

### Right Panel — Tabs
| Tab | Content |
|---|---|
| 📊 Gantt Charts | Color-coded Gantt bar chart for both algorithms |
| 📋 Results Tables | Full metrics table (CT, TAT, WT, RT) + averages |
| ⚖ Comparison | Side-by-side metric comparison + qualitative analysis |
| ✦ Conclusion | Auto-generated conclusion answering all analysis questions |

---

*Operating Systems Course — Scheduling Comparison Project*
