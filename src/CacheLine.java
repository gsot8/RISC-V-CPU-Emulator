public class CacheLine {
    public int tag;
    public int [] data;
    public boolean init = false;
    public CacheLine(int tag){
        this.tag = tag;
        data = new int[32];
    }
}
