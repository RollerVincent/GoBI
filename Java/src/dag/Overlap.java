package dag;

public class Overlap {
    public boolean related;
    private int size = 0;

    public Overlap(boolean related){
        this.related = related;
    }
    public void increment(){
        size += 1;
    }
    public void increment(int s){
        size += s;
    }
    public int size(){
        return size;
    }

    @Override
    public String toString() {
        return "["+related+","+size+"]";
    }
}
