package main;

import parser.Parser;
import tools.DifferentialExpression;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainDifferentialExpression {


    public static void main(String[] args) {


        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-countfiles",true,false, "gene.count file paths");
        argParser.addOption("-outdir",true,false);
        argParser.addOption("-config",true,false);


        if(argParser.Compile()) {


            DifferentialExpression DE = new DifferentialExpression(
                                                                    argParser.getArgument("-outdir"),
                                                                    argParser.getArgument("-countfiles"),
                                                                    argParser.getArgument("-config")
            );


            DE.Count();
            DE.Close();
            DE.Exec();





        }


    }


}
