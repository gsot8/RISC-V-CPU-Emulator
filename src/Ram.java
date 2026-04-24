import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ram {
    public String [] ram;
    public int adr = 1 << 17;
    public Map<String,String> cmd;
    public Ram(){
        this.ram = new String [1 << 18];
        this.cmd = new HashMap<>();
    }

    public void loadRam(String path){
        try {
            List<String> lines = Files.readAllLines(Path.of(path));

            adr %= 1 << 18;
            for (String line : lines) {
                if (line.isEmpty()){
                    continue;
                }
                ram[adr] = AssemblerParser.parse(AssemblerParser.parseInput(line)).substring(0, 2);
                adr++;
                ram[adr] = AssemblerParser.parse(AssemblerParser.parseInput(line)).substring(2, 4);
                adr++;
                ram[adr] = AssemblerParser.parse(AssemblerParser.parseInput(line)).substring(4, 6);
                adr++;
                ram[adr] = AssemblerParser.parse(AssemblerParser.parseInput(line)).substring(6, 8);
                adr++;
                StringBuilder res = new StringBuilder();
                for (int i = 0; i < AssemblerParser.parseInput(line).length - 1; i++) {
                    res.append(AssemblerParser.parseInput(line)[i]).append(", ");
                }
                res.append(AssemblerParser.parseInput(line)[AssemblerParser.parseInput(line).length - 1]);
                cmd.put(AssemblerParser.parse(AssemblerParser.parseInput(line)), res.toString());
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    public int[] getNextCacheLine(int adr){
        int cacheLineSize = 32;
        int start = (adr / cacheLineSize) * cacheLineSize;
        int[] cacheLine = new int[cacheLineSize];
        for (int i = 0; i < cacheLineSize; i++) {
            int memoryAddress = start + i;
            if (ram[memoryAddress] == null) {
                break;
            }
            cacheLine[i] = Integer.parseInt(ram[memoryAddress], 16);
        }

        return cacheLine;
    }

}
