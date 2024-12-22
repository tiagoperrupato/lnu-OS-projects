package se.lnu.os.ht24.a2.required;

import se.lnu.os.ht24.a2.provided.data.ProcessInterval;
import se.lnu.os.ht24.a2.provided.data.StrategyType;
import se.lnu.os.ht24.a2.provided.interfaces.Memory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MemoryImpl implements Memory {

    private final int[] memory; // Array to represent the memory
    private final int size; // Total size of the memory

    public MemoryImpl(int size){
        this.size = size;
        this.memory = new int[size];
        Arrays.fill(memory, -1); // Initialize memory to free (-1 indicates free cell)
    }

    @Override
    public boolean containsProcess(int processId) {
        // Check if the processId exists in the memory array
        for (int cell : memory) {
            if (cell == processId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Integer> processes() {
        // Extract unique process IDs in memory (excluding -1 for free cells)
        return Arrays.stream(memory)
                .filter(id -> id != -1)
                .distinct()
                .boxed()
                .collect(Collectors.toList());
    }

    @Override
    public int processSize(int processId) {
        // Count the number of cells occupied by the given processId
        int count = 0;
        for (int cell : memory) {
            if (cell == processId) {
                count++;
            }
        }
        return count;
    }

    @Override
    public ProcessInterval getProcessInterval(int processId) {
        int start = -1, end = -1;
        for (int i = 0; i < size; i++) {
            if (memory[i] == processId) {
                if (start == -1) start = i; // First occurrence of processId
                end = i; // Keep updating end as long as processId is found
            }
        }
        return start == -1 ? null : new ProcessInterval(start, end);
    }

    @Override
    public Set<Integer> neighboringProcesses(int processId) {
        Set<Integer> neighbors = new HashSet<>();
        for (int i = 0; i < size; i++) {
            if (memory[i] == processId) {
                if (i > 0 && memory[i - 1] != -1 && memory[i - 1] != processId) {
                    neighbors.add(memory[i - 1]); // Add left neighbor
                }
                if (i < size - 1 && memory[i + 1] != -1 && memory[i + 1] != processId) {
                    neighbors.add(memory[i + 1]); // Add right neighbor
                }
            }
        }
        return neighbors;
    }

    public int largestFreeBlock() {
        int largestFreeBlock = 0;
        int count = 0;

        for (int cell : memory) {
            if (cell == -1) { // Free cell
                count++;
            } else {
                if (largestFreeBlock < count) {
                    largestFreeBlock = count;
                }
                count = 0;
            }
        }

        if (largestFreeBlock < count) {
            largestFreeBlock = count;
        }
        return largestFreeBlock;
    }

    @Override
    public double fragmentation() {
        int largestFreeBlock = 0;
        int freeBlockCount = 0;

        for (int cell : memory) {
            if (cell == -1) { // Free cell
                freeBlockCount++;
            } 
            largestFreeBlock = largestFreeBlock();
        }

        return (double) 1 - (largestFreeBlock / freeBlockCount);
    }

    @Override
    public Set<ProcessInterval> freeSlots() {
        Set<ProcessInterval> freeIntervals = new HashSet<>();
        int start = -1;

        for (int i = 0; i < size; i++) {
            if (memory[i] == -1) { // Free cell
                if (start == -1) start = i; // Start of a new free block
            } else {
                if (start != -1) { // End of a free block
                    freeIntervals.add(new ProcessInterval(start, i - 1));
                    start = -1;
                }
            }
        }

        // Add the last free block if it reaches the end
        if (start != -1) {
            freeIntervals.add(new ProcessInterval(start, size - 1));
        }

        return freeIntervals;
    }

    public boolean allocate(int processID, int size, StrategyType strategyType) {
        boolean success = true;
        // Check if the process ID already exists in memory
        if (containsProcess(processID)) {
            // Process ID already allocated
            success = false;
        }

        // Find the starting address of a suitable block
        int startAddress = findBlock(size, strategyType);
        if (startAddress == -1) {
            // No suitable block found
            success = false;
        }

        // Allocate the process in the found block
        for (int i = startAddress; i < startAddress + size; i++) {
            memory[i] = processID;
        }

        return success;
    }

    private int findBlock(int size, StrategyType strategyType) {
        int startAddress = switch (strategyType) {
            case FIRST_FIT -> findBlockFF(size);
            case BEST_FIT -> findBlockBF(size);
            case WORST_FIT -> findBlockWF(size);
        };

        return startAddress;
    }

    private int findBlockFF(int size) {
        int freeCount = 0;
        for (int i = 0; i < this.size; i++) {
            if (memory[i] == -1) {
                freeCount++;
                if (freeCount == size) {
                    return i - size + 1; // Return the starting address of the block
                }
            } else {
                freeCount = 0; // Reset count if a non-free cell is found
            }
        }
        return -1; // No suitable block found
    }

    private int findBlockBF(int size) {
        int largestFreeBlock = 0;
        int count = 0;
        int bestStart = -1;

        for (int i = 0; i < this.size; i++) {
            if (memory[i] == -1) { // Free cell
                count++;
            } else {
                if (largestFreeBlock < count) {
                    bestStart = i - count;
                    largestFreeBlock = count;
                }
                count = 0;
            }
        }

        if (largestFreeBlock < size) {
            bestStart = -1;
        }

        return bestStart; // Return the starting address of the best block found
    }

    private int findBlockWF(int size) {
        int worstStart = -1;
        int worstSize = 0;
        int currentStart = -1;
        int currentSize = 0;

        for (int i = 0; i < this.size; i++) {
            if (memory[i] == -1) { // Free cell
                if (currentStart == -1) {
                    currentStart = i;
                }
                currentSize++;
            } else {
                if (currentSize > worstSize && currentSize >= size) {
                    worstStart = currentStart;
                    worstSize = currentSize;
                }
                currentStart = -1;
                currentSize = 0;
            }
        }

        // Check the last block
        if (currentSize > worstSize && currentSize >= size) {
            worstStart = currentStart;
            worstSize = currentSize;
        }

        return worstStart;
    }

    public boolean deallocate(int processId) {
        boolean success = true;
        // Check if the process ID exists in memory
        if (!containsProcess(processId)) {
            // Process ID not found
            success = false;
        }

        // Deallocate the process
        for (int i = 0; i < size; i++) {
            if (memory[i] == processId) {
                memory[i] = -1; // Free the cell
            }
        }

        return success;
    }

    public void compact() {
        int writeIndex = 0; // Pointer for the next position to write a non-empty cell

        // Traverse the memory array
        for (int readIndex = 0; readIndex < size; readIndex++) {
            if (memory[readIndex] != -1) { // If the cell is occupied
                memory[writeIndex] = memory[readIndex]; // Move it to the left
                writeIndex++;
            }
        }

        // Fill the remaining positions with -1 (free space)
        for (int i = writeIndex; i < size; i++) {
            memory[i] = -1;
        }
    }

    @Override
    public String toString() {
        StringBuilder retStr = new StringBuilder("Memory Size = " + size + "\n");
        if(processes() != null) {
            for (int processId : processes()) {
                ProcessInterval inter = getProcessInterval(processId);
                retStr.append("(").append(inter.getLowAddress()).append("-").append(inter.getHighAddress()).append(")")
                        .append(" --> ").append("ID ").append(processId).append("\n");
            }
        }
        if(freeSlots() != null) {
            for (ProcessInterval bi : freeSlots()) {
                retStr.append("(").append(bi.getLowAddress()).append("-").append(bi.getHighAddress()).append(")")
                        .append(" --> ").append("EMPTY").append("\n");
            }
        }
        return retStr.toString();
    }
}
