package gtf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegionVector implements Iterable<Region> {

    public List<Region> regions = new ArrayList<Region>();


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

    public Region toRegion(){


        if(regions.size()>1){
            if(regions.get(0).start>regions.get(1).start){
                return new Region(regions.get(regions.size()-1).start,regions.get(0).end);
            }else{
                return new Region(regions.get(0).start,regions.get(regions.size()-1).end);
            }
        }else{
            return regions.get(0);
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


    RegonIterator(RegionVector v){
        this.v = v;
        cursor = 0;
        if(v.regions.size()==0) {
            cursor=-1;
        }
        if(v.regions.size()>1){
            if(v.regions.get(0).start>v.regions.get(1).start){
                turn=true;
            }
        }
    }

    @Override
    public boolean hasNext() {
        if(cursor==-1){
            return false;
        }
        return cursor != v.regions.size();
    }

    @Override
    public Region next() {
        Region r;
        if(!turn){
            r=v.regions.get(cursor);
        }else{
            r=v.regions.get(v.regions.size()-cursor-1);
        }
        cursor+=1;
        return r;
    }


}




