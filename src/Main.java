import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        String asmFile = null;
        String binFile = null;

        for (int i = 0; i < args.length; i++) {
            if ("--asm".equals(args[i])) {
                asmFile = args[++i];
            } else if ("--bin".equals(args[i])) {
                binFile = args[++i];
            }
        }
        if (asmFile == null) {
            System.out.println("Ошибка: Не указан файл с кодом на ассемблере.");
            return;
        }
        if (binFile == null) {
            System.err.println("Compiling asm code is not supported");
        }
        else {
            try {
                List<String> lines = Files.readAllLines(Path.of(asmFile));
                try {
                    try (FileOutputStream fos = new FileOutputStream(binFile)) {
                        for (String line : lines) {
                            if (!line.isEmpty()) {
                                String machineCode = AssemblerParser.parse(AssemblerParser.parseInput(line));
                                byte[] byteArray = convertStringToByteArray(machineCode);
                                fos.write(byteArray);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка записи в файл: " + e.getMessage());
                }
            } catch (IOException e) {
                System.out.println("Ошибка чтения файла: " + e.getMessage());
            }
        }

        Cache cache_lru = new Cache(1);
        Cache cache_plru = new Cache(2);
        Ram ram = new Ram();
        Ram ram2 = new Ram();
        ram.loadRam(asmFile);
        ram2.loadRam(asmFile);
        Cpu cpu = new Cpu(cache_lru, ram);
        cpu.runCpu(1 << 17, (ram.adr - (1 << 17)) % (1 << 18));
        Cpu cpu2 = new Cpu(cache_plru, ram2);
        cpu2.runCpu(1 << 17, (ram2.adr - (1 << 17)) % (1 << 18));
        System.out.printf("replacement\thit rate\thit rate (inst)\thit rate (data)\n");
        System.out.printf("        LRU\t%3.5f%%\t%3.5f%%\t%3.5f%%\n",
                100.0 * (double) (cpu.cacheHitsData + cpu.cacheHits) / ((cpu.cacheHitsData + cpu.cacheHits) + (cpu.cacheMissesData + cpu.cacheMisses)),
                100.0 * (double) cpu.cacheHits / (cpu.cacheHits + cpu.cacheMisses),
                100.0 * (double) cpu.cacheHitsData / (cpu.cacheHitsData + cpu.cacheMissesData));

        System.out.printf("       pLRU\t%3.5f%%\t%3.5f%%\t%3.5f%%\n",
                100.0 * (double) (cpu2.cacheHitsData + cpu2.cacheHits) / ((cpu2.cacheHitsData + cpu2.cacheHits) + (cpu2.cacheMissesData + cpu2.cacheMisses)),
                100.0 * (double) cpu2.cacheHits / (cpu2.cacheHits + cpu2.cacheMisses),
                100.0 * (double) cpu2.cacheHitsData / (cpu2.cacheHitsData + cpu2.cacheMissesData));
    }

    private static byte[] convertStringToByteArray(String machineCode) {
        byte[] byteArray = new byte[machineCode.length() / 2];
        for (int i = 0; i < machineCode.length(); i += 2) {
            String byteStr = machineCode.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(byteStr, 16);
        }
        return byteArray;
    }
}
