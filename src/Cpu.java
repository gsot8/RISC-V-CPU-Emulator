public class Cpu {
    public Cache cache;
    public Ram ram;
    public int PC;
    public int[] registers;
    public int cacheHits;
    public int cacheMisses;
    public int cacheHitsData;
    public int cacheMissesData;

    public Cpu(Cache cache, Ram ram) {
        this.cache = cache;
        this.ram = ram;
        PC = 1 << 17;
        cacheHits = 0;
        cacheMisses = 0;
        registers = new int[32];
    }
    public int getRegisterIndex(String registerName) {
        switch (registerName) {
            case "zero": return 0;
            case "ra": return 1;
            case "sp": return 2;
            case "gp": return 3;
            case "tp": return 4;
            case "t0": return 5;
            case "t1": return 6;
            case "t2": return 7;
            case "s0": return 8;
            case "s1": return 9;
            case "a0": return 10;
            case "a1": return 11;
            case "a2": return 12;
            case "a3": return 13;
            case "a4": return 14;
            case "a5": return 15;
            case "a6": return 16;
            case "a7": return 17;
            case "s2": return 18;
            case "s3": return 19;
            case "s4": return 20;
            case "s5": return 21;
            case "s6": return 22;
            case "s7": return 23;
            case "s8": return 24;
            case "s9": return 25;
            case "s10": return 26;
            case "s11": return 27;
            case "t3": return 28;
            case "t4": return 29;
            case "t5": return 30;
            case "t6": return 31;
            default: throw new IllegalArgumentException("Unknown register: " + registerName);
        }
    }
    public int parseNumber(String str) {
        try {
            if (str.startsWith("0x") || str.startsWith("0X")) {
                return Integer.parseInt(str.substring(2), 16);
            } else if (str.startsWith("-0x") || str.startsWith("-0X")) {
                return -Integer.parseInt(str.substring(3), 16);
            } else {
                return Integer.parseInt(str);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка: строка не является допустимым числом в 16 или 10 системе счисления");
        }
    }

    public void runCpu(int startAddress, int elementCount) {
        registers[getRegisterIndex("ra")] = startAddress + elementCount;
        PC = startAddress;

        while (registers[getRegisterIndex("ra")] != PC) {
            String instruction = Cache.byteToHex(read(PC++)) +
                    Cache.byteToHex(read(PC++)) +
                    Cache.byteToHex(read(PC++)) +
                    Cache.byteToHex(read(PC++));
            PC = PC % (1 << 18);
            try {
                command(ram.cmd.get(instruction));
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("обращение к пустому адресу");
            }
        }
    }

    public int readData(int adr){
        int offset = adr & 0b11111;
        int index = (adr >> 5) % (1 << 5);
        int tag = adr >> 10;
        if (cache.checkTag(tag,index)){
            cacheHitsData++;
            cache.cache_add(tag,index,ram,adr);
            return cache.cache[cache.findCacheWay(tag,index)][index].data[offset];
        }
        else{
            cacheMissesData++;
            cache.cache_add(tag,index,ram,adr);
            return cache.cache[cache.findCacheWay(tag,index)][index].data[offset];
        }
    }
    public int read(int adr) {
        int offset = adr & 0b11111;
        int index = (adr >> 5) % (1 << 5);
        int tag = adr >> 10;
        if (cache.checkTag(tag,index)){
            if (offset % 4 == 0) {
                cacheHits++;
            }
            cache.cache_add(tag,index,ram,adr);
            return cache.cache[cache.findCacheWay(tag,index)][index].data[offset];
        }
        else{
            cacheMisses++;
            cache.cache_add(tag,index,ram,adr);
            return cache.cache[cache.findCacheWay(tag,index)][index].data[offset];
        }
    }

    private void writeToCache(int address, int value, int size) {
        int offsetCache = address & 0b11111;
        int index = (address >> 5) % (1 << 5);
        int tag = address >> 10;

        if (cache.checkTag(tag, index)) {
            cache.cache_add(tag,index,ram,address);
            cacheHitsData++;
            int way = cache.findCacheWay(tag, index);
            for (int i = 0; i < size; i++) {
                cache.cache[way][index].data[offsetCache + i] = (value >> (i * 8)) & 0xFF;
            }
        } else {
            cacheMissesData++;
            cache.cache_add(tag, index, ram, address);
            int way = cache.findCacheWay(tag, index);
            for (int i = 0; i < size; i++) {
                cache.cache[way][index].data[offsetCache + i] = (value >> (i * 8)) & 0xFF;
            }
        }
        for (int i = 0; i < 32; i++) {
            int addr = (cache.cache[cache.findCacheWay(tag, index)][index].tag << 10) + (index << 5) + i;
            ram.ram[addr] = Cache.byteToHex(cache.cache[cache.findCacheWay(tag, index)][index].data[i]);
        }
    }

    public void command(String cmd) {
        String[] parts = cmd.split("[ ,]+");
        String opcode = parts[0];
        String rd = parts[0];
        if (parts.length > 1){
            rd = parts[1];
        }
        switch (opcode) {
            case "addi": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                if (getRegisterIndex(rd) != 0){
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] + imm;
                }
                break;
            }
            case "add": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0){
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] + registers[getRegisterIndex(rs2)];
                    break;
                }
            }
            case "slli": {
                String rs1 = parts[2];
                int shamt = parseNumber(parts[3]) & 0x1F;
                if (getRegisterIndex(rd) != 0){
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] << shamt;
                }
                break;
            }
            case "srli": {
                String rs1 = parts[2];
                int shamt = parseNumber(parts[3]) & 0x1F;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] >>> shamt;
                }
                break;
            }
            case "srai": {
                String rs1 = parts[2];
                int shamt = parseNumber(parts[3]) & 0x1F;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] >> shamt;
                }
                break;
            }
            case "sub": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] - registers[getRegisterIndex(rs2)];
                }
                break;
            }
            case "sll": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int shamt = registers[getRegisterIndex(rs2)] & 0x1F;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] << shamt;
                }
                break;
            }
            case "slt": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] < registers[getRegisterIndex(rs2)] ? 1 : 0;
                }
                break;
            }
            case "sltu": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = Integer.compareUnsigned(registers[getRegisterIndex(rs1)], registers[getRegisterIndex(rs2)]) < 0 ? 1 : 0;
                }
                break;
            }
            case "xor": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] ^ registers[getRegisterIndex(rs2)];
                }
                break;
            }
            case "srl": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int shamt = registers[getRegisterIndex(rs2)] & 0x1F;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] >>> shamt;
                }
                break;
            }
            case "sra": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int shamt = registers[getRegisterIndex(rs2)] & 0x1F;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] >> shamt;
                }
                break;
            }
            case "or": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] | registers[getRegisterIndex(rs2)];
                }
                break;
            }
            case "and": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = registers[getRegisterIndex(rs1)] & registers[getRegisterIndex(rs2)];
                }
                break;
            }
            case "mul": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                long result = (long) registers[getRegisterIndex(rs1)] * registers[getRegisterIndex(rs2)];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = (int) result;
                }
                break;
            }
            case "mulh": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                long result = (long) registers[getRegisterIndex(rs1)] * registers[getRegisterIndex(rs2)];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = (int) (result >> 32);
                }
                break;
            }
            case "mulhsu": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                long result = (long) registers[getRegisterIndex(rs1)] * Integer.toUnsignedLong(registers[getRegisterIndex(rs2)]);
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = (int) (result >> 32);
                }
                break;
            }
            case "mulhu": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                long result = Integer.toUnsignedLong(registers[getRegisterIndex(rs1)]) * Integer.toUnsignedLong(registers[getRegisterIndex(rs2)]);
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = (int) (result >>> 32);
                }
                break;
            }
            case "div": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int dividend = registers[getRegisterIndex(rs1)];
                int divisor = registers[getRegisterIndex(rs2)];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = dividend / divisor;
                }
                break;
            }
            case "divu": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int dividend = registers[getRegisterIndex(rs1)];
                int divisor = registers[getRegisterIndex(rs2)];
                int result = Integer.divideUnsigned(dividend, divisor);
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "rem": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int dividend = registers[getRegisterIndex(rs1)];
                int divisor = registers[getRegisterIndex(rs2)];
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = dividend % divisor;
                }
                break;
            }
            case "remu": {
                String rs1 = parts[2];
                String rs2 = parts[3];
                int dividend = registers[getRegisterIndex(rs1)];
                int divisor = registers[getRegisterIndex(rs2)];
                int result = Integer.remainderUnsigned(dividend, divisor);
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "jalr": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int returnAddress = PC + 4;
                PC -= 4;
                PC = (registers[getRegisterIndex(rs1)] + imm) & ~1;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = returnAddress;
                }
                break;
            }
            case "andi": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int result = registers[getRegisterIndex(rs1)] & imm;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "ori": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int result = registers[getRegisterIndex(rs1)] | imm;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "xori": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int result = registers[getRegisterIndex(rs1)] ^ imm;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "slti": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int result = (registers[getRegisterIndex(rs1)] < imm) ? 1 : 0;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "sltiu": {
                String rs1 = parts[2];
                int imm = Integer.parseUnsignedInt(parts[3]);
                int result = Integer.compareUnsigned(registers[getRegisterIndex(rs1)], imm) < 0 ? 1 : 0;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = result;
                }
                break;
            }
            case "lb": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int address = (registers[getRegisterIndex(rs1)] + imm) % (1 << 18);
                int value = readData(address);
                value = (value << 24) >> 24;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = value;
                }
                break;
            }
            case "lh": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int address = (registers[getRegisterIndex(rs1)] + imm) % (1 << 18);
                int lowByte = readData(address) & 0xFF;
                int highByte = readData(address + 1) & 0xFF;
                cacheHitsData -= 1;
                int value = (highByte << 8) | lowByte;
                value = (value << 16) >> 16;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = value;
                }
                break;
            }
            case "lw": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int address = registers[getRegisterIndex(rs1)] + imm;
                int value = readData(address);
                value |= readData(address + 1) << 8;
                value |= readData(address + 2) << 16;
                value |= readData(address + 3) << 24;
                cacheHitsData -= 3;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = value;
                }
                break;
            }
            case "lbu": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int address = registers[getRegisterIndex(rs1)] + imm;
                int value = readData(address) & 0xFF;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = value;
                }
                break;
            }
            case "lhu": {
                String rs1 = parts[2];
                int imm = parseNumber(parts[3]);
                int address = registers[getRegisterIndex(rs1)] + imm;
                int lowByte = readData(address) & 0xFF;
                int highByte = readData(address + 1) & 0xFF;
                cacheHitsData -= 1;
                int value = (highByte << 8) | lowByte;
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = value;
                }
                break;
            }
            case "beq": {
                String rs1 = parts[2];
                int offset = parseNumber(parts[3]);
                if (registers[getRegisterIndex(rs1)] == registers[getRegisterIndex(rd)]) {
                    PC -= 4;
                    PC += offset;
                }
                break;
            }
            case "bne": {
                String rs1 = parts[2];
                int offset = parseNumber(parts[3]);
                if (registers[getRegisterIndex(rs1)] != registers[getRegisterIndex(rd)]) {
                    PC += offset;
                    PC -= 4;
                }
                break;
            }
            case "bge": {
                String rs1 = parts[2];
                int offset = parseNumber(parts[3]);
                if (registers[getRegisterIndex(rs1)] >= registers[getRegisterIndex(rd)]) {
                    PC += offset;
                    PC -= 4;
                }
                break;
            }
            case "blt": {
                String rs1 = parts[2];
                int offset = parseNumber(parts[3]);
                if (registers[getRegisterIndex(rs1)] < registers[getRegisterIndex(rd)]) {
                    PC += offset;
                    PC -= 4;
                }
                break;
            }
            case "bltu": {
                String rs1 = parts[2];
                int offset = parseNumber(parts[3]);
                if (Integer.compareUnsigned(registers[getRegisterIndex(rs1)], registers[getRegisterIndex(rd)]) < 0) {
                    PC += offset;
                    PC -= 4;
                }
                break;
            }
            case "bgeu": {
                String rs1 = parts[2];
                int offset = parseNumber(parts[3]);
                if (Integer.compareUnsigned(registers[getRegisterIndex(rs1)], registers[getRegisterIndex(rd)]) >= 0) {
                    PC += offset;
                    PC -= 4;
                }
                break;
            }
            case "jal": {
                int imm = parseNumber(parts[2]);
                if (!"zero".equals(rd)) {
                    registers[getRegisterIndex(rd)] = PC + 4;
                }
                PC -= 4;
                PC += imm;
                break;
            }
            case "auipc": {
                int imm = parseNumber(parts[2]);
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = PC + (imm << 12);
                }
                break;
            }
            case "lui": {
                int imm = parseNumber(parts[2]);
                if (getRegisterIndex(rd) != 0) {
                    registers[getRegisterIndex(rd)] = imm << 12;
                }
                break;
            }
            case "sb": {
                String rs2 = parts[1];
                String base = parts[2];
                int offset = Integer.parseInt(parts[3]);
                int address = registers[getRegisterIndex(base)] + offset;
                int value = registers[getRegisterIndex(rs2)] & 0xFF;
                writeToCache(address, value, 1);
                break;
            }
            case "sh": {
                String rs2 = parts[1];
                String base = parts[2];
                int offset = parseNumber(parts[3]);
                int address = registers[getRegisterIndex(base)] + offset;
                int value = registers[getRegisterIndex(rs2)] & 0xFFFF;
                writeToCache(address, value, 2);
                break;
            }
            case "sw": {
                String rs2 = parts[1];
                String base = parts[2];
                int offset = parseNumber(parts[3]);
                int address = registers[getRegisterIndex(base)] + offset;
                int value = registers[getRegisterIndex(rs2)];
                writeToCache(address, value, 4);
                break;
            }
            case "ebreak", "ecall", "fence", "fence.i": {
                break;
            }
            default:
                throw new IllegalArgumentException("Ошибка: неизвестная команда");
        }
    }

}

