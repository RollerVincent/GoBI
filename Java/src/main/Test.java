package main;

import gtf.Region;
import gtf.RegionVector;
import sam.ReadPair;

public class Test {
    public static void main(String[] args) {

        ReadPair rp1 = new ReadPair("testpair1");
        rp1.fwv = new RegionVector();
        rp1.rwv = new RegionVector();
        rp1.fwv.add(new Region(565103,565204));
        rp1.rwv.add(new Region(565020,565121));
        rp1.fwl = 1;
        rp1.rwl = 1;
        rp1.alignmentStart = 565020;
        rp1.alignmentEnd = 565204;
        rp1.mergeRegions();



        ReadPair rp2 = new ReadPair("testpair2");
        rp2.fwv = new RegionVector();
        rp2.rwv = new RegionVector();
        rp2.fwv.add(new Region(565103,565204));
        rp2.rwv.add(new Region(565020,565120));
        rp2.fwl = 1;
        rp2.rwl = 1;
        rp2.alignmentStart = 565020;
        rp2.alignmentEnd = 565204;
        rp2.mergeRegions();



        ReadPair rp3 = new ReadPair("testpair3");
        rp3.fwv = new RegionVector();
        rp3.rwv = new RegionVector();
        rp3.fwv.add(new Region(565121,565204));
        rp3.rwv.add(new Region(565020,565121));
        rp3.fwl = 1;
        rp3.rwl = 1;
        rp3.alignmentStart = 565020;
        rp3.alignmentEnd = 565204;
        rp3.mergeRegions();


        System.out.println(rp1.regionVector);
        System.out.println(rp2.regionVector);
        System.out.println(rp3.regionVector);



    }
}
