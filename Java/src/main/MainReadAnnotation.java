package main;

import java.util.List;

import augmentedTree.forest.Forest;
import augmentedTree.forest.StrandedForest;
import augmentedTree.forest.UnStrandedForest;
import gtf.Gene;
import htsjdk.samtools.AlignmentBlock;
import parser.GtfParser;
import sam.ReadPair;
import tools.ReadAnnotation;


public class MainReadAnnotation {

    public static void main(String args[])
    {

        boolean frstrand = false;
        boolean strandunspecific = false;
        long s = System.nanoTime();


        //GtfParser parser = new GtfParser("/Users/vincentroller/Home/Studies/genprakt/BamFeatures/Saccharomyces_cerevisiae.R64-1-1.75.gtf");
        GtfParser parser = new GtfParser("/Users/vincentroller/Home/Studies/genprakt/BamFeatures/Homo_sapiens.GRCh37.75.gtf");



        Forest forest;
        if(strandunspecific){
            forest = new UnStrandedForest();
        }else{
            forest = new StrandedForest(frstrand);
        }
        parser.fillForest(forest);




        long e1 = System.nanoTime();
        System.out.println("FOREST :    "+(((e1-s)/1000000)/1000.0)+" s");


        //ReadAnnotation reads = new ReadAnnotation("/Users/vincentroller/Home/Studies/genprakt/BamFeatures/complete_bams/hes_star.bam");
        ReadAnnotation reads = new ReadAnnotation("/Users/vincentroller/Home/Studies/genprakt/BamFeatures/h.sn.1.bam");
        //ReadAnnotation reads = new ReadAnnotation("/Users/vincentroller/Home/Studies/genprakt/BamFeatures/y.ns.5.bam");
        ReadPair pair;
        List<Gene> genes;

        int total=0;

        while((pair = reads.nextPair()) != null){

            /* TODO: ANTISENSE ERROR

                h.sp.3.annot                    pair(17074550) on positive strand with region 1190124:1190307
                Homo_sapiens.GRCh37.75.gtf      gene(ENSG00000160087) on negative strand with region 1189289:1209265

                but antisense:false ?

             */




            String result;
            pair.updateBounds();
            genes = forest.getOuterGenes(pair);
            if (genes.size() == 0) {
                if (!forest.hasContainedGene(pair)) {
                    result = reads.processPair(pair,forest.getNeighbourDistance(pair),forest.getAntisense(pair));
                    total+=1;
                }
            }else{
                result = reads.processPair(pair,genes);

                total+=1;
            }

        }

        System.out.println("total   : "+total);
        System.out.println("gcount0 : "+reads.gcount0);
        System.out.println("sp-inc  : "+reads.splitincons);


        reads.close();

        long e2 = System.nanoTime();
        System.out.println("PAIRS  :    "+(((e2-e1)/1000000)/1000.0)+" s");




        long e = System.nanoTime();
        System.out.println("TOTAL  :    "+(((e-s)/1000000)/1000.0)+" s");


    }






}
