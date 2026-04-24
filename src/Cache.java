public class Cache {
    public final int addr_len = 18;
    public final int cache_offset_len = 5;
    public final int cache_index_len = 5;
    public final int cache_tag_len = 8;
    public final int cache_line_count = 128;
    public final int cache_line_size = 32 * 8;
    public final int cache_sets = 32;
    public final int cache_way = 4;
    public CacheLine[][] cache;
    public int[][] lru;
    private int[][] bitPlru = new int[cache_way][cache_sets];
    public int cache_policy;

    public Cache(int cache_policy) {
        this.cache_policy = cache_policy;
        this.cache = new CacheLine[cache_way][cache_sets];
        for (int i = 0; i < cache_way; i++) {
            for (int j = 0; j < cache_sets; j++) {
                cache[i][j] = new CacheLine(-1);
            }
        }
        this.lru = new int[cache_way][cache_sets];
        for (int i = 0; i < cache_sets; i++) {
            int number = 3;
            for (int j = 0; j < cache_way; j++) {
                lru[j][i] = number;
                number--;
            }
        }
    }
    public boolean checkTag(int tag, int index){
        for (int i = 0; i < cache_way; i++) {
            if (!this.cache[i][index].init){
                break;
            }
            if (cache[i][index].tag < 10) {
            }
            if (this.cache[i][index].tag == tag){
                return true;
            }
        }
        if (index == 0) {
        }
        return false;
    }

    public int findCacheWay(int tag, int index) {
        for (int i = 0; i < cache_way; i++) {
            if (cache[i][index].init && cache[i][index].tag == tag) {
                return i;
            }
        }
        return -1;
    }

    public void cache_add(int tag, int index, Ram ram, int adr) {
        if (cache_policy == 1){
            lru_add(tag,index,ram,adr);
        }
        else bitPlruAdd(tag,index,ram,adr);
    }

    private void lru_add(int tag, int index, Ram ram, int adr) {
        if (checkTag(tag, index)) {
            int hitIndex = findCacheWay(tag, index);
            for (int i = 0; i < cache_way; i++) {
                if (lru[i][index] < lru[hitIndex][index]) {
                    lru[i][index]++;
                }
            }
            lru[hitIndex][index] = 0;
            return;
        }
        for (int i = 0; i < cache_way; i++) {
            if (!cache[i][index].init) {
                cache[i][index].tag = tag;
                cache[i][index].data = ram.getNextCacheLine(adr);
                cache[i][index].init = true;
                for (int j = 0; j < cache_way; j++) {
                    if (lru[j][index] < lru[i][index]) {
                        lru[j][index]++;
                    }
                }
                lru[i][index] = 0;
                return;
            }
        }

        int cell = -1;
        for (int i = 0; i < cache_way; i++) {
            if (lru[i][index] == cache_way - 1){
                cell = i;
            }
        }
        for (int i = 0; i < 32; i++) {
            ram.ram[(cache[cell][index].tag << 10) + (index << 5) + i] = byteToHex(cache[cell][index].data[i]);
        }
        cache[cell][index].tag = tag;
        cache[cell][index].data = ram.getNextCacheLine(adr);
        for (int i = 0; i < cache_way; i++) {
            if (i != cell) {
                lru[i][index]++;
            }
        }
        lru[cell][index] = 0;
    }



    private void bitPlruAdd(int tag, int index, Ram ram, int adr) {
        if (checkTag(tag, index)) {
            int hitIndex = findCacheWay(tag, index);
            updateBitPlruOnHit(index, hitIndex);
            return;
        }
        for (int i = 0; i < cache_way; i++) {
            if (!cache[i][index].init) {
                cache[i][index].tag = tag;
                cache[i][index].data = ram.getNextCacheLine(adr);
                cache[i][index].init = true;
                updateBitPlruOnHit(index,i);
                return;
            }
        }
        int cell = 0;
        for (int i = 0; i < cache_way; i++) {
            if (bitPlru[i][index] == 0){
                cell = i;
                bitPlru[cell][index] = 1;
                break;
            }
        }
        for (int i = 0; i < 32; i++) {
            ram.ram[(cache[cell][index].tag << 10) + (index << 5) + i] = byteToHex(cache[cell][index].data[i]);
        }
        cache[cell][index].tag = tag;
        cache[cell][index].data = ram.getNextCacheLine(adr);
        updateBitPlruOnHit(index,cell);
    }
    private void updateBitPlruOnHit(int index, int hitIndex) {
        bitPlru[hitIndex][index] = 1;
        int summ = 0;
        for (int i = 0; i < cache_way; i++) {
            summ += bitPlru[i][index];
        }
        if (summ == cache_way){
            for (int i = 0; i < cache_way; i++) {
                if (hitIndex != i){
                    bitPlru[i][index] = 0;
                }
            }
        }
    }


    public static String byteToHex(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Число должно быть в диапазоне 0-255");
        }
        return String.format("%02X", value);
    }

}
