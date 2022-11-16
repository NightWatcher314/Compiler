package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.ir.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.hitsz.compiler.ir.InstructionKind.*;

/**
 * @author night
 */
public class AssemblyMap {

    private final Map<Integer, String> KVmap = new HashMap<>();
    private final Map<String, Integer> VKmap = new HashMap<>();

    private boolean[] regFlag = {false, false, false, false, false, false, false};
    private int cnt = 0;

    private List<Instruction> instructionList;

    public AssemblyMap() {
    }


    public void replace(int reg, String value) {
        KVmap.remove(reg);
        VKmap.remove(value);
        KVmap.put(reg, value);
        VKmap.put(value, reg);
    }


    public String getByKey(int reg) {
        return KVmap.get(reg);
    }

    public int getByValue(String value) {
        if (VKmap.containsKey(value)) {
            return VKmap.get(value);
        } else {
            int reg = getReg();
            replace(reg, value);
            return reg;
        }
    }

    public int getReg() {
        for (int i = 0; i < regFlag.length; i++) {
            if (!regFlag[i]) {
                regFlag[i] = true;
                return i;
            }
        }
        boolean flag = false;
        for (int i = 0; i <= regFlag.length; i++) {
            String value = getByKey(i);
            for (int j = cnt; j < instructionList.size() - 1; j++) {
                switch (instructionList.get(j).getKind()) {
                    case ADD, SUB, MUL -> {
                        flag = value.equals(instructionList.get(j).getRHS().toString()) || value.equals(instructionList.get(j).getLHS().toString());

                    }
                    case MOV -> {
                        flag = value.equals(instructionList.get(j).getFrom().toString());
                    }
                    case RET -> {
                        instructionList.get(j).getReturnValue();
                    }
                }
                if (flag) {
                    flag = false;
                    break;
                }
                regFlag[i] = true;
                return i;
            }
        }
        throw new RuntimeException();
    }

    public void setInstructionList(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    public int getCnt() {
        return this.cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

}
