package main;

import gtf.Annotation;
import gtf.Gene;
import parser.GtfParser;
import parser.Parser;
import tools.ExonSkipping;

import java.io.*;

public class MainExonSkipping {
    public static void main(String[] args) {

        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-gtf",true,false);
        argParser.addOption("-o",true,false);
        argParser.addOption("-plot",false,true);

        if(argParser.Compile()){

            String input = argParser.getArgument("-gtf");
            String output = argParser.getArgument("-o");

            GtfParser parser = new GtfParser(input);
            ExonSkipping skipping = new ExonSkipping();
            BufferedWriter writer = Parser.Writer(output);

            try {
                writer.write("id\tsymbol\tchr\tstrand\tnprots\tntrans\tSV\tWT\tWT_prots\tSV_prots\tmin_skipped_exon\tmax_skipped_exon\tmin_skipped_bases\tmax_skipped_bases\n");
                for(Gene gene : parser){
                    writer.write(skipping.getSkips(gene));
                }
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if(argParser.hasArgument("-plot")){
                ToPlot(output);
            }



        }


    }
    public static void ToPlot(String data){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(data));
            BufferedWriter writer = Parser.Writer(data.split("\\.")[0]+"_plot_exons.csv");
            BufferedWriter writer2 = Parser.Writer(data.split("\\.")[0]+"_plot_bases.csv");
            writer.write("x,y,group\n");
            writer2.write("x,y,group\n");
            int count = 0;
            int exons = 0;
            int bases = 0;
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] s = line.split("\t");
                exons+=Integer.valueOf(s[11]);
                bases+=Integer.valueOf(s[13]);
                writer.write(count+","+exons+","+s[2]+"\n");
                writer2.write(count+","+bases+","+s[2]+"\n");
                count+=1;
            }
            writer.close();
            writer2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
