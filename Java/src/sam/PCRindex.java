package sam;

import java.util.HashMap;
import java.util.PriorityQueue;

public class PCRindex {

    int currentMaxStart = 0;
    String ref = "";
    public HashMap<String,Integer> cache = new HashMap<>();
    String h;
    String s = "";
    Integer i;
    int m;
    boolean strandness;
    int maxcache = 0;
    int step=0;


    public PriorityQueue<Integer> queue = new PriorityQueue<>();



    public PCRindex(boolean strdspec){
        strandness = strdspec;
    }

    public int getIndex2(ReadPair p){


        return 0;
    }

    public int getIndex(ReadPair p){

        if(strandness) {
            if (p.first.getReadNegativeStrandFlag()) {
                s = "-";
            } else {
                s = "+";
            }
        }

        if(p.newRef){
            currentMaxStart = 0;
            ref = p.first.getReferenceName();

            cache.clear();
            cache.put(p.regionVector.toString()+s,0);
            return 0;
        }


        if(p.first.getAlignmentStart()>p.second.getAlignmentStart()){
            m=p.first.getAlignmentStart();
            if(p.second.getAlignmentEnd()>=m-1){
                currentMaxStart = Math.max(currentMaxStart,p.alignmentEnd);
            }

        }else{
            m=p.second.getAlignmentStart();
            if(p.first.getAlignmentEnd()>=m-1){
                currentMaxStart = Math.max(currentMaxStart,p.alignmentEnd);
            }
        }





        if(m>currentMaxStart+1){
            currentMaxStart = m;

            cache.clear();
            cache.put(p.regionVector.toString()+s,0);
            return 0;
        }else{
            h = p.regionVector.toString()+s;
            i = cache.get(h);
            if(i != null){
                cache.put(h,i+1);
                return i+1;
            }else{
                cache.put(h,0);
                return 0;
            }


        }


    }


    public int getIndex3(ReadPair p) {

        if (strandness) {
            if (p.first.getReadNegativeStrandFlag()) {
                s = "-";
            } else {
                s = "+";
            }
        }

        if (p.newRef) {
            currentMaxStart = 0;
            ref = p.first.getReferenceName();
            cache.clear();
            step=0;
            cache.put(p.regionVector.toString() + s, 0);
            return 0;
        }

        if (step == 10) {




            step = 0;
            if (p.first.getAlignmentStart() > p.second.getAlignmentStart()) {
                m = p.first.getAlignmentStart()-1;
                if (p.second.getAlignmentEnd() >= m-1) {
                    currentMaxStart = Math.max(currentMaxStart, p.alignmentEnd);
                }

            } else {
                m = p.second.getAlignmentStart()-1;
                if (p.first.getAlignmentEnd() >= m-1) {
                    currentMaxStart = Math.max(currentMaxStart, p.alignmentEnd);
                }
            }


            if (m > currentMaxStart + 1) {
                currentMaxStart = m;
                //System.out.println(cache.size());
                //if (cache.size() > maxcache) {
                  //  maxcache = cache.size();
                    //System.out.println(maxcache);
                //}


                cache.clear();
                cache.put(p.regionVector.toString() + s, 0);
                return 0;
            } else {
                h = p.regionVector.toString() + s;
                i = cache.get(h);
                if (i != null) {
                    cache.put(h, i + 1);
                    return i + 1;
                } else {
                    cache.put(h, 0);
                    return 0;
                }


            }


        }else{
            step+=1;
            h = p.regionVector.toString() + s;
            i = cache.get(h);
            if (i != null) {
                cache.put(h, i + 1);
                return i + 1;
            } else {
                cache.put(h, 0);
                return 0;
            }

        }

    }


}
