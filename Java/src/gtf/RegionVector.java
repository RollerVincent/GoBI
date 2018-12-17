package gtf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegionVector implements Iterable<Region> {

    public List<Region> regions = new ArrayList<Region>();
    public boolean turn = false;

    public int length(){
        return regions.size();
    }

    public void add(Region r){ regions.add(r); }

    public Region get(int index){
        return regions.get(index);
    }

    public RegionVector inverse(){
        RegionVector out = new RegionVector();
        Iterator<Region> itr = this.iterator();
        if(itr.hasNext()) {
            Region prev = itr.next();
            while (itr.hasNext()) {
                Region element = itr.next();
                Region r = new Region(prev.end+1,element.start-1);
                out.add(r);
                prev=element;
            }
        }
        return out;
    }

    public RegionVector inverse_clamped(){
        RegionVector out = new RegionVector();
        Iterator<Region> itr = this.iterator();
        if(itr.hasNext()) {
            Region prev = itr.next();
            out.add(new Region(prev.start,prev.start));
            while (itr.hasNext()) {
                Region element = itr.next();
                Region r = new Region(prev.end+1,element.start-1);
                out.add(r);
                prev=element;
            }
            out.add(new Region(prev.end,prev.end));
        }
        return out;
    }

    public Region toRegion(){

        if(!turn){
            return new Region(regions.get(0).start,regions.get(regions.size()-1).end);
        }else{
            return new Region(regions.get(regions.size()-1).start,regions.get(0).end);
        }


    }


    @Override
    public String toString(){
        String s ="[\t";
        Iterator<Region> it = iterator();
        while(it.hasNext()) {
            Region r = it.next();
            s+=r+"\t";
        }
        return s+"]";
    }

    @Override
    public Iterator<Region> iterator() {
        return new RegonIterator(this);
    }
}

class RegonIterator implements Iterator<Region>{

    RegionVector v;
    int cursor;
    boolean turn;
    int size;

    RegonIterator(RegionVector v){
        this.v = v;
        cursor = 0;
        turn = v.turn;
        size = v.regions.size();
    }

    @Override
    public boolean hasNext() {
        return cursor != v.regions.size();
    }

    @Override
    public Region next() {
        Region r;
        if(!turn){
            r=v.regions.get(cursor);
        }else{
            r=v.regions.get(size-cursor-1);
        }
        cursor+=1;
        return r;
    }


}




