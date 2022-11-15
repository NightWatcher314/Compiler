package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.ir.InstructionKind;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @author night
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {

    private List<Instruction> instructionList = new ArrayList<>();
    private List<Instruction> instructionListPre = new ArrayList<>();

    private List<String> assemblyList = new ArrayList<>();
    private AssemblyMap assemblyMap = new AssemblyMap();


    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        this.instructionListPre = originInstructions;
    }


    public void generate() {
        assemblyMap.setCnt(0);
        assemblyMap.setInstructionList(instructionList);
        for (Instruction instruction : instructionList) {
            switch (instruction.getKind()) {
                case MOV -> {
                    if (instruction.getFrom().isImmediate()) {
                        int reg = assemblyMap.getReg();
                        String variable = instruction.getResult().toString();
                        assemblyList.add("    " + "li " + "t" + reg + ", " + instruction.getFrom() + "\t\t" + "#" + "  " + instruction);
                        assemblyMap.replace(reg, variable);
                    }
                    if (instruction.getFrom().isIRVariable()) {
                        String variable1 = instruction.getResult().toString();
                        String variable2 = instruction.getFrom().toString();
                        int reg1 = assemblyMap.getByValue(variable1);
                        int reg2 = assemblyMap.getByValue(variable2);
                        assemblyList.add("    " + "mv " + "t" + reg1 + ", " + "t" + reg2 + "\t\t" + "#" + "  " + instruction);
                        assemblyMap.replace(reg1, variable1);
                    }
                }

                case SUB, MUL, ADD -> {
                    String variable1 = instruction.getResult().toString();
                    String variable2 = instruction.getLHS().toString();
                    String variable3 = instruction.getRHS().toString();
                    int reg1 = assemblyMap.getByValue(variable1);
                    int reg2 = assemblyMap.getByValue(variable2);
                    int reg3 = assemblyMap.getByValue(variable3);
                    String type = (instruction.getKind() == InstructionKind.SUB) ? "sub" : (instruction.getKind() == InstructionKind.ADD ? "add" : "mul");
                    if (type.equals("add")) {
                        if (instruction.getRHS().isImmediate()) {
                            assemblyList.add("    " + "addi " + "t" + reg1 + ", " + "t" + reg2 + ", " + instruction.getRHS() + "\t\t" + "#" + "  " + instruction);
                        }
                        if (instruction.getRHS().isIRVariable()) {
                            assemblyList.add("    " + "add " + "t" + reg1 + ", " + "t" + reg2 + ", " + "t" + reg3 + "\t\t" + "#" + "  " + instruction);
                        }
                    } else {
                        assemblyList.add("    " + type + " t" + reg1 + ", " + "t" + reg2 + ", " + "t" + reg3 + "\t\t" + "#" + "  " + instruction);
                    }
                    assemblyMap.replace(reg1, variable1);
                }

                case RET -> {
                    String variable = instruction.getReturnValue().toString();
                    int reg = assemblyMap.getByValue(variable);
                    assemblyList.add("    " + "mv " + "a0" + ", " + "t" + reg + "\t\t" + "#" + "  " + instruction);
                }
            }
            assemblyMap.setCnt(assemblyMap.getCnt() + 1);
        }
    }

    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() {
        // TODO: 执行寄存器分配与代码生成
        assemblyList.add(".text");
        // 预处理
        for (Instruction instruction : instructionListPre) {
            switch (instruction.getKind()) {
                case MOV, RET -> {
                    instructionList.add(instruction);
                }
                case ADD -> {
                    if (instruction.getLHS().isImmediate() && instruction.getRHS().isImmediate()) {
                        int LHS = Integer.parseInt(instruction.getLHS().toString());
                        int RHS = Integer.parseInt(instruction.getRHS().toString());
                        instructionList.add(Instruction.createMov(instruction.getResult(), IRImmediate.of(LHS + RHS)));
                    } else if (instruction.getLHS().isImmediate()) {
                        instructionList.add(Instruction.createAdd(instruction.getResult(), instruction.getRHS(), instruction.getLHS()));
                    } else {
                        instructionList.add(instruction);
                    }
                }
                case SUB -> {
                    if (instruction.getLHS().isImmediate() && instruction.getRHS().isImmediate()) {
                        int LHS = Integer.parseInt(instruction.getLHS().toString());
                        int RHS = Integer.parseInt(instruction.getRHS().toString());
                        instructionList.add(Instruction.createMov(instruction.getResult(), IRImmediate.of(LHS - RHS)));
                    } else if (instruction.getLHS().isImmediate()) {
                        Instruction instruction1 = Instruction.createMov(instruction.getResult(), instruction.getLHS());
                        Instruction instruction2 = Instruction.createSub(instruction.getResult(), instruction1.getResult(), instruction.getRHS());
                        instructionList.add(instruction1);
                        instructionList.add(instruction2);

                        assemblyMap.setCnt(assemblyMap.getCnt() + 1);
                    } else if (instruction.getRHS().isImmediate()) {
                        Instruction instruction1 = Instruction.createMov(instruction.getResult(), instruction.getRHS());
                        Instruction instruction2 = Instruction.createSub(instruction1.getResult(), instruction.getLHS(), instruction1.getResult());
                        instructionList.add(instruction1);
                        instructionList.add(instruction2);

                        assemblyMap.setCnt(assemblyMap.getCnt() + 1);
                    } else {
                        instructionList.add(instruction);
                    }
                }
                case MUL -> {
                    if (instruction.getLHS().isImmediate() && instruction.getRHS().isImmediate()) {
                        int LHS = Integer.parseInt(instruction.getLHS().toString());
                        int RHS = Integer.parseInt(instruction.getRHS().toString());
                        instructionList.add(Instruction.createMov(instruction.getResult(), IRImmediate.of(LHS * RHS)));
                    } else if (instruction.getLHS().isImmediate()) {
                        Instruction instruction1 = Instruction.createMov(instruction.getResult(), instruction.getLHS());
                        Instruction instruction2 = Instruction.createMul(instruction.getResult(), instruction1.getResult(), instruction.getRHS());
                        instructionList.add(instruction1);
                        instructionList.add(instruction2);

                        assemblyMap.setCnt(assemblyMap.getCnt() + 1);
                    } else if (instruction.getRHS().isImmediate()) {
                        Instruction instruction1 = Instruction.createMov(instruction.getResult(), instruction.getRHS());
                        Instruction instruction2 = Instruction.createMul(instruction1.getResult(), instruction.getLHS(), instruction1.getResult());
                        instructionList.add(instruction1);
                        instructionList.add(instruction2);

                        assemblyMap.setCnt(assemblyMap.getCnt() + 1);
                    } else {
                        instructionList.add(instruction);
                    }
                }
            }
            assemblyMap.setCnt(assemblyMap.getCnt() + 1);
        }

        generate();

    }


    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        FileUtils.writeLines(path, assemblyList.stream().toList());
    }
}

