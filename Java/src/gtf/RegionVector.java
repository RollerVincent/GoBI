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


    public void add(int index, Region r){ regions.add(index, r); }


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
        return new Region(regions.get(0).start,regions.get(regions.size()-1).end);
    }


    public RegionVector cut(Region region){
        int s = region.start;
        int e=region.end;
        RegionVector out = new RegionVector();
        for (int i = 0; i < length()-1; i++) {
            Region r = regions.get(i);

            if(r.end>=s && r.start<=s){
                if(r.end>=e){
                    out.add(new Region(s,e));
                    return out;
                }

                out.add(new Region(s,r.end));
                s=regions.get(i+1).start;
            }
        }
        Region last = regions.get(length()-1);
        if(last.end>=s && last.start<=s) {
            if (e < last.end) {
                out.add(new Region(s, e));
            } else {
                out.add(new Region(s, last.end));
            }
        }
        return out;
    }


    public boolean equals(RegionVector o){
        if(o.length()!=length()){
            return false;
        }
        Region r1;
        Region r2;
        for (int i = 0; i < length(); i++) {
            r1=regions.get(i);
            r2=o.regions.get(i);
            if(r1.start!=r2.start){
                return false;
            }
            if(r1.end!=r2.end){
                return  false;
            }
        }
        return true;
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
        return new RegionIterator(this);
    }
}

class RegionIterator implements Iterator<Region>{

    RegionVector v;
    int cursor;
    int size;

    RegionIterator(RegionVector v){
        this.v = v;
        cursor = 0;
        size = v.regions.size();
    }

    @Override
    public boolean hasNext() {
        return cursor != v.regions.size();
    }

    @Override
    public Region next() {
        Region r;

        r=v.regions.get(cursor);


        cursor+=1;
        return r;
    }


}




