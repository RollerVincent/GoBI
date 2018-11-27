package main;

import gtf.Annotation;
import gtf.Gene;
import parser.GtfParser;
import parser.Parser;
import tools.ReadSimulation;

import java.io.IOException;

public class MainReadSimulation {

    public static void main(String[] args) {

        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-length",true,false, "readlength");
        argParser.addOption("-frlength",true,false, "fragmentlength");
        argParser.addOption("-SD",true,false);
        argParser.addOption("-readcounts",true,false);
        argParser.addOption("-mutationrate",true,false);
        argParser.addOption("-fasta",true,false);
        argParser.addOption("-fidx",true,false, "fasta indices");
        argParser.addOption("-gtf",true,false);
        argParser.addOption("-od",true,false, "output directory");

        if(argParser.Compile()){

            GtfParser parser = new GtfParser(argParser.getArgument("-gtf"));
            ReadSimulation simulation = new ReadSimulation(
                    Integer.parseInt(argParser.getArgument("-length")),
                    Integer.parseInt(argParser.getArgument("-frlength")),
                    Integer.parseInt(argParser.getArgument("-SD")),
                    Float.parseFloat(argParser.getArgument("-mutationrate"))/100
            );
            simulation.initReadcounts(argParser.getArgument("-readcounts"));
            simulation.initFasta(argParser.getArgument("-fasta"), argParser.getArgument("-fidx"));
            simulation.initWriters(argParser.getArgument("-od"));

            for (Gene g : parser) {
                simulation.simulateReads(g);
            }

            try {
                simulation.fastaRaf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            simulation.closeWriters();
        }
    }
}
