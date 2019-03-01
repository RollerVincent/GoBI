package main;

import dag.DAG;
import dag.EnrichmentData;
import dag.Mapping;
import parser.OboParser;
import parser.Parser;
import tools.GeneSetEnrichmentAnalysis;

public class MainGeneSetEnrichmentAnalysis {

    public static void main(String[] args) {

        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-obo",true,false);
        argParser.addOption("-root",true,false);
        argParser.addOption("-mapping",true,false);
        argParser.addOption("-mappingtype",true,false);
        argParser.addOption("-enrich",true,false);


        if(argParser.Compile()) {

            GeneSetEnrichmentAnalysis analysis = new GeneSetEnrichmentAnalysis();
            analysis.loadDag(argParser.getArgument("-obo"), argParser.getArgument("-root"));


            analysis.initMapping(argParser.getArgument("-mappingtype"),argParser.getArgument("-mapping"));


            Mapping.checkSorting(analysis.mapping);

            analysis.initDag();


            //Mapping.asString(analysis.mapping);




        /*    Mapping mapping = new Mapping(argParser.getArgument("-mappingtype"),argParser.getArgument("-mapping"));
            mapping.construct(dag);

            EnrichmentData enrich = new EnrichmentData(argParser.getArgument("-enrich"));
            enrich.construct(dag);





            System.out.println(dag.root);/*/


        }


    }
}
