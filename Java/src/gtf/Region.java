package gtf;

public class Region {

    public int start;
    public int end;


    public Region (){}


    public Region (int start, int end){
        this.start=start;
        this.end=end;
    }


    @Override
    public String toString(){
        return ""+start+":"+end+"";
    }
}
