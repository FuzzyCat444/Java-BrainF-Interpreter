/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turingmachine;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 *
 * @author harry
 */
public class TuringMachine {
    private double time;
    private long step;
    private String program;
    private ArrayList<Byte> leftTape;
    private ArrayList<Byte> rightTape;
    private int instructionPtr;
    private int dataPtr;
    
    private int depth;
    private double nextStepTime;
    private int nextInstructionPtr;
    private int nextDataPtr;
    
    private String input;
    private String output;
    
    public TuringMachine() {
        this.program = "";
    }
    
    public void run(String program) {
        this.time = 0.0;
        this.step = 0;
        this.program = program;
        this.leftTape = new ArrayList<Byte>();
        this.rightTape = new ArrayList<Byte>();
        this.instructionPtr = 0;
        this.dataPtr = 0;
        
        this.depth = 0;
        this.nextStepTime = 0.0;
        this.nextInstructionPtr = 0;
        this.nextDataPtr = 0;
        
        this.input = "";
        this.output = "";
    }
    
    public void update(double delta) {
        if (program.length() == 0)
            return;
        
        if (!done())
            time += delta;
        while (step <= time - 1 && !done()) {
            instructionPtr = nextInstructionPtr;
            dataPtr = nextDataPtr;
            while (-1 - dataPtr >= leftTape.size()) {
                leftTape.add(Byte.MIN_VALUE);
            }
            while (dataPtr >= rightTape.size()) {
                rightTape.add(Byte.MIN_VALUE);
            }
            char instruction = getInstruction(instructionPtr);
            switch (instruction) {
            case '>':
                nextDataPtr = dataPtr + 1;
                nextInstructionPtr = instructionPtr + 1;
                step++;
                break;
            case '<':
                nextDataPtr = dataPtr - 1;
                nextInstructionPtr = instructionPtr + 1;
                step++;
                break;
            case '-':
                decrementData(dataPtr);
                nextInstructionPtr = instructionPtr + 1;
                step++;
                break;
            case '+':
                incrementData(dataPtr);
                nextInstructionPtr = instructionPtr + 1;
                step++;
                break;
            case ',':
                if (input != null) {
                    if (input.length() > 0) {
                        byte b = String.valueOf(input.charAt(0)).getBytes(StandardCharsets.UTF_8)[0];
                        setData(dataPtr, (byte) (b + 128));
                        input = input.substring(1);
                        nextInstructionPtr = instructionPtr + 1;
                        step++;
                    } else {
                        time = step;
                    }
                }
                break;
            case '.':
                byte[] outputByte = new byte[] { (byte) (getData(dataPtr) + 128) };
                output += new String(outputByte, StandardCharsets.UTF_8);
                nextInstructionPtr = instructionPtr + 1;
                step++;
                break;

            case '[':
                if (getData(dataPtr) != Byte.MIN_VALUE) {
                    depth++;
                    nextInstructionPtr = instructionPtr + 1;
                } else {
                    int depth2 = 1;
                    int address = instructionPtr + 1;
                    while (depth2 != 0) {
                        if (getInstruction(address) == '[')
                            depth2++;
                        else if (getInstruction(address) == ']')
                            depth2--;
                        address++;
                    }
                    nextInstructionPtr = address;
                }
                step++;
                break;
            case ']':
                int depth2 = 1;
                int address = instructionPtr;
                while (depth2 != 0) {
                    address--;
                    if (getInstruction(address) == ']')
                        depth2++;
                    else if (getInstruction(address) == '[')
                        depth2--;
                }
                nextInstructionPtr = address;
                step++;
                break;
            default:
                step--;
                nextInstructionPtr = instructionPtr + 1;
                break;
            }
        }
        nextStepTime = time - step;
        if (done())
            nextStepTime = 0.0;
    }
    
    public char getInstruction(int ptr) {
        if (ptr < 0 || ptr >= program.length())
            return ' ';
        return program.charAt(ptr);
    }
    
    public byte getData(int ptr) {
        boolean left = ptr < 0;
        ArrayList<Byte> tape = left ? leftTape : rightTape;
        int address = left ? -1 - ptr : ptr;
        if (address >= tape.size())
            return Byte.MIN_VALUE;
        return tape.get(address);
    }
    
    public void setData(int ptr, byte b) {
        boolean left = ptr < 0;
        ArrayList<Byte> tape = left ? leftTape : rightTape;
        int address = left ? -1 - ptr : ptr;
        tape.set(address, b);
    }
    
    public void decrementData(int ptr) {
        setData(ptr, (byte) (getData(ptr) - 1));
    }
    
    public void incrementData(int ptr) {
        setData(ptr, (byte) (getData(ptr) + 1));
    }
    
    public void printTape() {
        for (int i = leftTape.size() - 1; i >= 0; i--) {
            System.out.print(leftTape.get(i) + 128 + " ");
        }
        for (int i = 0; i < rightTape.size(); i++) {
            System.out.print(rightTape.get(i) + 128 + " ");
        }
        System.out.println();
    }
    
    public boolean done() {
        return nextInstructionPtr >= program.length();
    }

    public int getInstructionPtr() {
        return instructionPtr;
    }
    
    public int getInstructionPtrExact() {
        double ptr = nextStepTime * nextInstructionPtr + (1.0 - nextStepTime) * instructionPtr;
        if (nextInstructionPtr < instructionPtr)
            return (int) ptr + 1;
        return (int) ptr;
    }

    public int getDataPtr() {
        return dataPtr;
    }
    
    public double getNextStepTime() {
        return nextStepTime;
    }
    
    public int getInstructionPtrDir() {
        return nextInstructionPtr - instructionPtr;
    }
    
    public int getDataPtrDir() {
        return nextDataPtr - dataPtr;
    }
    
    public static boolean isValidProgram(String program) {
        int depth = 0;
        for (int i = 0; i < program.length(); i++) {
            if (program.charAt(i) == '[')
                depth++;
            else if (program.charAt(i) == ']')
                depth--;
            if (depth < 0)
                return false;
        }
        if (depth != 0)
            return false;
        return true;
    }

    public void setInput(String input) {
        this.input += input;
    }

    public String getOutput() {
        String ret = output;
        output = "";
        return ret;
    }

    public String getProgram() {
        return program;
    }
}
