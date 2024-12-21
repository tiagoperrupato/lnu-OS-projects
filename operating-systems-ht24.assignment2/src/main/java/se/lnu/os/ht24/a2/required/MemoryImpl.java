package se.lnu.os.ht24.a2.required;

import se.lnu.os.ht24.a2.provided.data.ProcessInterval;
import se.lnu.os.ht24.a2.provided.data.StrategyType;
import se.lnu.os.ht24.a2.provided.exceptions.InstructionException;
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
        /* TODO
            Structure your memory how you like and initialize it here. This is the only constructor allowed and
            should create an empty memory of the given size. Feel free to add any variable or method you see
            fit for your implementation in this class
         */
        this.size = size;
        this.memory = new int[size];
        Arrays.fill(memory, -1); // Initialize memory to free (-1 indicates free cell)
    }

    @Override
    public boolean containsProcess(int processId) {
        // TODO Replace this return statement with the method that checks if processId is allocated in the memory
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
        /* TODO
            Replace this return statement with the list of processIds of the currently allocated processes
            in the memory. If the memory is empty, return an empty List.
         */
        // Extract unique process IDs in memory (excluding -1 for free cells)
        return Arrays.stream(memory)
                .filter(id -> id != -1)
                .distinct()
                .boxed()
                .collect(Collectors.toList());
    }

    @Override
    public int processSize(int processId) {
        /* TODO
            Replace this return statement with the method that returns the size of the process with processId
            in the memory, 0 if it is not allocated.
         */
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
        /* TODO
            Replace this return statement with the method that returns a ProcessInterval instance containing the
            lower and upper address in memory of the process with processId. Return null if the process is not allocated
         */
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
        /* TODO
            Replace this return statement with the method that returns the Set containing the ids of all the
            contiguous processes to the one that has processId (min. 0 if the process is between two free portions of
            memory and max. 2 if the process is surrounded both left and right by other processes). For no neighboring
            processes, return an empty Set.
         */
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

        return largestFreeBlock;
    }

    @Override
    public double fragmentation() {
        /* TODO
            Replace this return statement with the method that returns the memory fragmentation value. There is
            no need to round decimals, as the Tests will do it before checking.
         */
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
        /* TODO
            Replace this return statement with the method that returns the set of ProcessInterval instances
            corresponding to the free slots of the memory. Return exactly one ProcessInterval per slot, make sure
            that you don't split any slot in two different intervals (e.g. if slot 0-199 is free, adding 0-99
            and 100-199 will be considered an error, while adding 0-199 is the only correct solution). If the
            memory is full, return an empty Set.
         */
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

    public void allocate(int processID, int size, StrategyType strategyType) {
        // Check if the process ID already exists in memory
        if (containsProcess(processId)) {
            // Process ID already allocated
            throw new InstructionException("Allocation Failed: Process already allocated", largestFreeBlock());
        }

        // Find the starting address of a suitable block
        int startAddress = findBlock(size, strategyType);
        if (startAddress == -1) {
            // No suitable block found
            throw new InstructionException("Allocation Failed: Process could not be allocated", largestFreeBlock());
        }

        // Allocate the process in the found block
        for (int i = startAddress; i < startAddress + size; i++) {
            memory[i] = processId;
        }
    }

    private int findBlock(int size, StrategyType strategyType) {
        int startAddress = switch (strategyType) {
            case FIRST_FIT -> findBlockFF(size);
            case BEST_FIT -> findBlockBF(size);
            case WORST_FIT -> findBlockWF(size);
        };

        return startAdress;
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
