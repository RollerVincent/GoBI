package main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import augmentedTree.IntervalTree;
import gtf.Region;
import gtf.RegionVector;
import gtf.forest.Forest;
import gtf.forest.StrandedForest;
import gtf.forest.UnStrandedForest;
import gtf.Gene;
import htsjdk.samtools.AlignmentBlock;
import parser.GtfParser;
import parser.Parser;
import sam.ReadPair;
import tools.ReadAnnotation;


public class MainReadAnnotation {

    public static void main(String args[])
    {

        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-gtf",true,false);
        argParser.addOption("-bam",true,false);
        argParser.addOption("-o",true,false, "output file");
        argParser.addOption("-frstrand",true,true, "experiment strandness");

        if(argParser.Compile()) {

            Boolean frstrand = Boolean.valueOf(argParser.getArgument("-frstrand"));
            boolean strandunspecific = false;
            if(!argParser.hasArgument("-frstrand")){
                strandunspecific = true;

            }


            BufferedWriter annot = Parser.Writer(argParser.getArgument("-o"));


            long s = System.nanoTime();

            GtfParser parser = new GtfParser(argParser.getArgument("-gtf"));
            parser.excludeCodingSequences();


            Forest forest;
            if (strandunspecific) {
                forest = new UnStrandedForest();
            } else {
                forest = new StrandedForest(frstrand);
            }
            //parser.fillForest(forest);

            parser.initForest(forest);


           /* long e1 = System.nanoTime();
            System.out.println("FOREST :    " + (((e1 - s) / 1000000) / 1000.0) + " s");*/


            ReadAnnotation reads = new ReadAnnotation(argParser.getArgument("-bam"), !strandunspecific);

            ReadPair pair;
            List<Gene> genes;

            int total = 0;
            String annotation;


            try {
                while ((pair = reads.nextPair()) != null) {



                    parser.seekTree(pair.first.getReferenceName());








                    pair.updateBounds();
                    genes = forest.getOuterGenes(pair);
                    if (genes.size() == 0) {
                        if (!forest.hasContainedGene(pair)) {
                            annotation = reads.processPair(pair, forest.getNeighbourDistance(pair), forest.getAntisense(pair));
                            annot.write(annotation + "\n");
                            total += 1;
                        }
                    } else {
                        annotation = reads.processPair(pair, genes);
                        annot.write(annotation + "\n");
                        total += 1;
                    }

                   // System.out.println(reads.pcr.cache.size());


                 /*   if(pair.readName.equals("390958")){


                        List<RegionVector> rv = new ArrayList<>();
                        List<String> col = new ArrayList<>();
                        List<String> str = new ArrayList<>();

                        System.out.println(pair.regionVector);
                        System.out.println();
                        System.out.println(pair.fwv);
                        System.out.println(pair.rwv);
                        System.out.println();
                        for (AlignmentBlock ab : pair.first.getAlignmentBlocks()){
                            System.out.println(ab.getReferenceStart()+":"+(ab.getReferenceStart()+ab.getLength()-1));
                        }
                        System.out.println();
                        for (AlignmentBlock ab : pair.second.getAlignmentBlocks()){
                            System.out.println(ab.getReferenceStart()+":"+(ab.getReferenceStart()+ab.getLength()-1));
                        }

                        rv.add(pair.regionVector);
                        col.add("#4444dd");
                        str.add(" ");

                        rv.add(pair.fwv);
                        col.add("#aaaaaa");
                        rv.add(pair.rwv);
                        col.add("#aaaaaa");
                        if(pair.first.getReadNegativeStrandFlag()){
                            str.add("-");
                            str.add("+");
                        }else{
                            str.add("+");
                            str.add("-");
                        }

                        //System.out.println(pair.nsplit);

                        /*for  (String k :reads.pcr.cache.keySet()) {

                            RegionVector v = new RegionVector();


                            String[] rgs = k.substring(2,k.length()-2).split("\t");
                            for(String r:rgs){
                                String[] sp = r.split(":");
                                v.add(new Region(Integer.valueOf(sp[0]),Integer.valueOf(sp[1])));
                            }
                            System.out.println(v + "    "+reads.pcr.cache.get(k));
                            rv.add(v);
                            col.add("#aa5555");
                            str.add(" ");
                        }

                        System.out.println(pair.pcrindex);

                        Parser.drawRegionVectors(rv,str,col,"readpair.html",1000);



                    }*/



                }
                annot.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

          /*  System.out.println("total   : " + total);
            System.out.println("gcount0 : " + reads.gcount0);
            System.out.println("sp-inc  : " + reads.splitincons);*/

            reads.close();

           /* long e2 = System.nanoTime();
            System.out.println("PAIRS  :    " + (((e2 - e1) / 1000000) / 1000.0) + " s");
*/

            long e = System.nanoTime();
            System.out.println("TOTAL  :    " + (((e - s) / 1000000) / 1000.0) + " s");

        }

    }






}
