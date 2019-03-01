package main;

import gtf.Gene;
import gtf.Region;
import gtf.forest.Forest;
import gtf.forest.StrandedForest;
import gtf.forest.UnStrandedForest;
import htsjdk.samtools.util.Tuple;
import parser.GtfParser;
import parser.Parser;
import sam.ReadPair;
import tools.DifferentialAlternativeSplicing;
import tools.ReadAnnotation;
import java.io.BufferedWriter;
import java.util.List;

public class MainDifferentialAlternativeSplicing {

    public static void main(String[] args) {

        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-gtf",true,false);
        argParser.addOption("-bam",true,false);
        argParser.addOption("-o",true,false);

        if(argParser.Compile()) {

            BufferedWriter writer = Parser.Writer(argParser.getArgument("-o"));
            DifferentialAlternativeSplicing differential = new DifferentialAlternativeSplicing(argParser.getArgument("-gtf"));

            Boolean frstrand = true;
            boolean strandunspecific = true;

            GtfParser parser = new GtfParser(argParser.getArgument("-gtf"));

            Forest forest;
            if (strandunspecific) {
                forest = new UnStrandedForest();
            } else {
                forest = new StrandedForest(frstrand);
            }

            parser.initForest(forest);
            ReadAnnotation reads = new ReadAnnotation(argParser.getArgument("-bam"), !strandunspecific);
            ReadPair pair;
            List<Gene> genes;
            List<String> matches;
            while ((pair = reads.nextPair()) != null) {
                if (pair.newRef) {
                    parser.seekTree(pair.first.getReferenceName());
                }
                pair.updateBounds();
                genes = forest.getOuterGenes(pair);
                if (genes.size() != 0) {
                    pair.updateRegions();
                    pair.mergeRegions();
                    matches = pair.matchedTranscriptsRaw(genes);

                    for(String m:matches){ // matched transcripts
                        Tuple<String,List<Tuple<Region, String>>[]> data = differential.getData(m);
                        if(data!=null) {
                            List<Tuple<Region, String>> inclusion_exons;
                            List<Tuple<Region, String>> exclusion_exons;

                            if (!matches.contains(data.a)) { // not matching sv and wt at the same time
                                inclusion_exons = data.b[0];
                                exclusion_exons = data.b[1];
                                for (Tuple<Region, String> exon : inclusion_exons) {

                                    if(pair.alignmentStart<=exon.a.end && pair.alignmentEnd>=exon.a.start){ //inclusion: check if read region overlapping with exon
                                        differential.updateCount(exon.b,1);
                                    }
                                }
                                for (Tuple<Region, String> exon : exclusion_exons) {

                                    if(pair.alignmentStart<exon.a.start && pair.alignmentEnd>exon.a.end){ //exclusion: check if read region spanning exon
                                        differential.updateCount(exon.b,-1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            reads.close();
            differential.write(writer);
        }
    }
}
