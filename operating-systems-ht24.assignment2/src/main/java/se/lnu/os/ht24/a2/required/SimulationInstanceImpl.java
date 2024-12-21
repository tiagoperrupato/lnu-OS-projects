package se.lnu.os.ht24.a2.required;

import se.lnu.os.ht24.a2.provided.data.StrategyType;
import se.lnu.os.ht24.a2.provided.abstract_.Instruction;
import se.lnu.os.ht24.a2.provided.exceptions.InstructionException;
import se.lnu.os.ht24.a2.provided.instructions.AllocationInstruction;
import se.lnu.os.ht24.a2.provided.instructions.CompactInstruction;
import se.lnu.os.ht24.a2.provided.instructions.DeallocationInstruction;
import se.lnu.os.ht24.a2.provided.interfaces.Memory;
import se.lnu.os.ht24.a2.provided.interfaces.SimulationInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SimulationInstanceImpl implements SimulationInstance {
    private Queue<Instruction> remainingInstructions;
    private final MemoryImpl memory;
    private final StrategyType strategyType;
    private List<InstructionException> instructionExceptions;

    public SimulationInstanceImpl(Queue<Instruction> instructions, MemoryImpl memory, StrategyType strategyType){
        this.remainingInstructions = instructions;
        this.memory = memory;
        this.strategyType = strategyType;
        this.instructionExceptions = new ArrayList<>();
    }

    @Override
    public void runAll() {
        /* TODO
             Implement the method to run all the remaining part of the simulation at once. If there are no more
             instructions, just do nothing. You should remove instructions from the queue when executing them.
             IMPORTANT: remember that, if you are adopting BEST_FIT or WORST_FIT and you have more than one eligible
             hole for an AllocationInstruction, you should choose the one with the lowest address among them.
             For FIRST_FIT, always start from the address 0 when searching for a valid hole.
         */
        while (!remainingInstructions.isEmpty()) {
            executeNextInstruction();
        }
    }

    @Override
    public void run(int steps) {
        /* TODO
            Implement the method to run a stepped simulation (one step = one instruction). If steps > actual available
            instructions, just run all the simulation.
         */
        for (int i = 0; i < steps && !remainingInstructions.isEmpty(); i++) {
            executeNextInstruction();
        }
    }

    @Override
    public Memory getMemory() {
        return this.memory;
    }

    @Override
    public Queue<Instruction> getInstructions() {
        return this.remainingInstructions;
    }

    @Override
    public StrategyType getStrategyType() {
        return this.strategyType;
    }

    @Override
    public List<InstructionException> getExceptions() {
        return this.instructionExceptions;
    }

    @Override
    public String toString() {
        return "Simulation Details:\n" +
                "Strategy: " + strategyType + "\n" +
                "List of Remaining Instructions: " + remainingInstructions + "\n" +
                "Current Memory Structure:\n\n" + memory + "\n" +
                "List of Occurred Exceptions: " + instructionExceptions;
    }

    /**
     * Helper method to execute the next instruction in the queue.
     */
    private void executeNextInstruction() {
        Instruction instruction = remainingInstructions.poll();
        if (instruction == null) return;

        try {
            if (instruction instanceof AllocationInstruction alloc) {
                executeAllocation(alloc);
            } else if (instruction instanceof DeallocationInstruction dealloc) {
                executeDeallocation(dealloc);
            } else if (instruction instanceof CompactInstruction) {
                executeCompaction();
            }
        } catch (InstructionException ex) {
            instructionExceptions.add(ex);
        }
    }

    /**
     * Execute an allocation instruction.
     */
    private void executeAllocation(AllocationInstruction alloc) throws InstructionException {
        memory.allocate(alloc.getProcessId(), alloc.getSize(), strategyType);
    }

    /**
     * Execute a deallocation instruction.
     */
    private void executeDeallocation(DeallocationInstruction dealloc) throws InstructionException {
        boolean success = memory.deallocate(dealloc.getProcessId());

        if (!success) {
            throw new InstructionException("Deallocation failed", memory.largestFreeBlock());
        }
    }

    /**
     * Execute a compaction instruction.
     */
    private void executeCompaction() {
        memory.compact();
    }
}
