import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AssemblerParser {
    public static String toBitString(int number, int bits) {
        int mask = (1 << bits) - 1;
        String binary = Integer.toBinaryString(number & mask);
        while (binary.length() < bits) {
            binary = "0" + binary;
        }
        return binary;
    }
    public static String[] parseInput(String input) {
        String parsed = input.trim().replace(",", "");
        String[] args = parsed.split("\\s+");
        List<String> nonNumericArgs = new ArrayList<>();
        List<String> numericArgs = new ArrayList<>();
        for (String arg : args) {
            if (arg.matches("-?\\d+")) {
                numericArgs.add(arg);
            } else {
                nonNumericArgs.add(arg);
            }
        }
        nonNumericArgs.addAll(numericArgs);
        return nonNumericArgs.toArray(new String[0]);
    }

    public static int parseFlagsToInt(String flags) {
        int result = 0;
        if (flags.contains("i")) result += 8;
        if (flags.contains("o")) result += 4;
        if (flags.contains("r")) result += 2;
        if (flags.contains("w")) result += 1;
        return result;
    }


    public static String getRegisterBinary(String registerName) {
        Map<String, Integer> registerMap = new HashMap<>();
        registerMap.put("zero", 0);
        registerMap.put("ra", 1);
        registerMap.put("sp", 2);
        registerMap.put("gp", 3);
        registerMap.put("tp", 4);
        registerMap.put("t0", 5);
        registerMap.put("t1", 6);
        registerMap.put("t2", 7);
        registerMap.put("s0", 8);
        registerMap.put("s1", 9);
        registerMap.put("a0", 10);
        registerMap.put("a1", 11);
        registerMap.put("a2", 12);
        registerMap.put("a3", 13);
        registerMap.put("a4", 14);
        registerMap.put("a5", 15);
        registerMap.put("a6", 16);
        registerMap.put("a7", 17);
        registerMap.put("s2", 18);
        registerMap.put("s3", 19);
        registerMap.put("s4", 20);
        registerMap.put("s5", 21);
        registerMap.put("s6", 22);
        registerMap.put("s7", 23);
        registerMap.put("s8", 24);
        registerMap.put("s9", 25);
        registerMap.put("s10", 26);
        registerMap.put("s11", 27);
        registerMap.put("t3", 28);
        registerMap.put("t4", 29);
        registerMap.put("t5", 30);
        registerMap.put("t6", 31);
        if (registerMap.containsKey(registerName)) {
            int registerNumber = registerMap.get(registerName);
            return String.format("%05d", Integer.parseInt(Integer.toBinaryString(registerNumber)));
        } else {
            return "null";
        }
    }

    public static String parseR(String func7, String rs1, String rs2, String func3, String rd, String opcode){
        String r2 = getRegisterBinary(rs2);
        String r1 = getRegisterBinary(rs1);
        String d = getRegisterBinary(rd);
        String res = func7 + r2 + r1 + func3 + d + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parseI(String imm, String rs1, String func3, String rd, String opcode){
        String im;
        if (imm.contains("0x")){
            im = toBitString(Integer.parseInt(imm.replace("0x", ""), 16),12);
        }
        else{
            im = toBitString(Integer.parseInt(imm), 12);
        }
        String s1 = getRegisterBinary(rs1);
        String d = getRegisterBinary(rd);
        String res = im + d + func3 + s1 + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parseFence(String rs1, String rs2) {
        int rs1Value = parseFlagsToInt(rs1);
        int rs2Value = parseFlagsToInt(rs2);
        String rs1Bin = toBitString(rs1Value, 5);
        String rs2Bin = toBitString(rs2Value, 5);
        String instruction = "0000" + rs1Bin + rs2Bin + "00000000000000001111";
        String res1 = instruction.substring(0, 8);
        String res2 = instruction.substring(8, 16);
        String res3 = instruction.substring(16, 24);
        String res4 = instruction.substring(24, 32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parseU(String imm, String rd, String opcode){
        String im;
        if (imm.contains("0x")){
            im = String.valueOf(toBitString(Integer.parseInt(imm.replace("0x", ""), 16) >> 12, 20));
            im = "0".repeat(20 - im.length()) + im;
        }
        else{
            im = String.valueOf(Integer.parseInt(imm) >> 12);
            im = "0".repeat(20 - im.length()) + im;
        }
        String d = getRegisterBinary(rd);
        String res = im  + d + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parseB(String offset, String rs1,String rs2, String func3, String opcode){
        String off;
        if (offset.contains("0x")){
            off = toBitString(Integer.parseInt(offset.replace("0x", ""), 16),13);
        }
        else{
            off = toBitString(Integer.parseInt(offset), 13);
        }
        String s1 = getRegisterBinary(rs1);
        String s2 = getRegisterBinary(rs2);
        String res = off.charAt(0) + off.substring(2,8) + s2 + s1 + func3 + off.substring(8,12) + off.charAt(1) + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parseS(String offset, String rs2,String base, String func3, String opcode){
        String off;
        if (offset.contains("0x")){
            off = toBitString(Integer.parseInt(offset.replace("0x", ""), 16),12);
        }
        else{
            off = toBitString(Integer.parseInt(offset), 12);
        }
        String s1 = getRegisterBinary(base);
        String s2 = getRegisterBinary(rs2);
        String res = off.substring(0,7) + s2 + s1 + func3 + off.substring(7,12) + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }
    public static String parseJ(String imm, String rd, String opcode){
        String im;
        if (imm.contains("0x")){
            im = toBitString(Integer.parseInt(imm.replace("0x", ""), 16),21);
        }
        else{
            im = toBitString(Integer.parseInt(imm), 21);
        }
        String d = getRegisterBinary(rd);
        String res = im.charAt(0) + im.substring(10,20) + im.charAt(9) + im.substring(1,9)  + d + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parseShift(String func7, String shmnt,String rs1, String rd,String func3,String opcode){
        String smh;
        if (shmnt.contains("0x")){
            smh = toBitString(Integer.parseInt(shmnt.replace("0x", ""), 16),5);
        }
        else {
            smh = toBitString(Integer.parseInt(shmnt), 5);
        }
        String r1 = getRegisterBinary(rs1);
        String d = getRegisterBinary(rd);
        String res = func7 + smh + r1 + func3 + d + opcode;
        String res1 = res.substring(0,8);
        String res2 = res.substring(8,16);
        String res3 = res.substring(16,24);
        String res4 = res.substring(24,32);
        String hex1 = String.format("%02X", Integer.parseInt(res1, 2));
        String hex2 = String.format("%02X", Integer.parseInt(res2, 2));
        String hex3 = String.format("%02X", Integer.parseInt(res3, 2));
        String hex4 = String.format("%02X", Integer.parseInt(res4, 2));
        return hex4 + hex3 + hex2 + hex1;
    }

    public static String parse(String [] args) {
        switch (args[0]) {
            case ("add"):
                return(parseR("0000000", args[2], args[3], "000", args[1], "0110011"));
            case ("slli"):
                return(parseShift("0000000", args[3], args[2], args[1],"001",  "0010011"));
            case ("srli"):
                return(parseShift("0000000", args[3], args[2], args[1], "101", "0010011"));

            case ("srai"):
                return(parseShift("0100000", args[3], args[2], args[1], "101", "0010011"));

            case ("sub"):
                return(parseR("0100000", args[2], args[3], "000", args[1], "0110011"));

            case "sll":
                return(parseR("0000000", args[2], args[3], "001", args[1], "0110011"));

            case "slt":
                return(parseR("0000000", args[2], args[3], "010", args[1], "0110011"));

            case "sltu":
                return(parseR("0000000", args[2], args[3], "011", args[1], "0110011"));

            case "xor":
                return(parseR("0000000", args[2], args[3], "100", args[1], "0110011"));

            case "srl":
                return(parseR("0000000", args[2], args[3], "101", args[1], "0110011"));

            case "sra":
                return(parseR("0100000", args[2], args[3], "101", args[1], "0110011"));

            case "or":
                return(parseR("0000000", args[2], args[3], "110", args[1], "0110011"));

            case "and":
                return(parseR("0000000", args[2], args[3], "111", args[1], "0110011"));

            case "mul":
                return(parseR("0000001", args[2], args[3], "000", args[1], "0110011"));

            case "mulh":
                return(parseR("0000001", args[2], args[3], "001", args[1], "0110011"));

            case "mulhsu":
                return(parseR("0000001", args[2], args[3], "010", args[1], "0110011"));

            case "mulhu":
                return(parseR("0000001", args[2], args[3], "011", args[1], "0110011"));

            case "div":
                return(parseR("0000001", args[2], args[3], "100", args[1], "0110011"));

            case "divu":
                return(parseR("0000001", args[2], args[3], "101", args[1], "0110011"));

            case "rem":
                return(parseR("0000001", args[2], args[3], "110", args[1], "0110011"));

            case "remu":
                return(parseR("0000001", args[2], args[3], "111", args[1], "0110011"));

            case "addi":
                return(parseI(args[3], args[1], "000", args[2], "0010011"));

            case "jalr":
                return(parseI(args[3], args[1], "000", args[2], "1100111"));

            case "andi":
                return(parseI(args[3], args[1], "111", args[2], "0010011"));

            case "ori":
                return(parseI(args[3], args[1], "110", args[2], "0010011"));

            case "xori":
                return(parseI(args[3], args[1], "100", args[2], "0010011"));

            case "slti":
                return(parseI(args[3], args[1], "010", args[2], "0010011"));

            case "sltiu":
                return(parseI(args[3], args[1], "011", args[2], "0010011"));

            case "lb":
                return(parseI(args[3], args[1], "000", args[2], "0000011"));

            case "lh":
                return(parseI(args[3], args[1], "001", args[2], "0000011"));

            case "lw":
                return(parseI(args[3], args[1], "010", args[2], "0000011"));

            case "lbu":
                return(parseI(args[3], args[1], "100", args[2], "0000011"));

            case "lhu":
                return(parseI(args[3], args[1], "101", args[2], "0000011"));

            case "fence":
                if (args.length > 2) {
                    return (parseFence(args[1], args[2]));
                }
                else if (args.length == 2) {
                    return (parseFence(args[1], ""));
                }
                else return (parseFence("", ""));

            case "fence.i":
                return(parseI("0".repeat(12), "zero", "001", "zero", "0001111"));

            case "ecall":
                return(parseI("0".repeat(12), "zero", "000", "zero", "1110011"));

            case "ebreak":
                return(parseI("0".repeat(12), "zero", "000", "ra", "1110011"));

            case "beq":
                return(parseB(args[3], args[1], args[2], "000", "1100011"));

            case "bne":
                return(parseB(args[3], args[1], args[2], "001", "1100011"));

            case "blt":
                return(parseB(args[3], args[1], args[2], "100", "1100011"));

            case "bge":
                return(parseB(args[3], args[1], args[2], "101", "1100011"));

            case "bltu":
                return(parseB(args[3], args[1], args[2], "110", "1100011"));

            case "bgeu":
                return(parseB(args[3], args[1], args[2], "111", "1100011"));

            case "jal":
                return(parseJ(args[2], args[1], "1101111"));

            case "lui":
                return(parseU(args[2], args[1], "0110111"));

            case "auipc":
                return(parseU(args[2], args[1], "1101111"));

            case "sb":
                return(parseS(args[3], args[1], args[2], "000", "0100011"));

            case "sh":
                return(parseS(args[3], args[1], args[2], "001", "0100011"));

            case "sw":
                return(parseS(args[3], args[1], args[2], "010", "0100011"));

        }
        return "";
    }

}


