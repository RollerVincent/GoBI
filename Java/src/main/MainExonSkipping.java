package main;

import gtf.Annotation;
import gtf.Gene;
import parser.GtfParser;
import parser.Parser;
import tools.ExonSkipping;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainExonSkipping {
    public static void main(String[] args) {

        Parser.ArgParser argParser = Parser.ArgParser(args);
        argParser.addOption("-gtf",true,false);
        argParser.addOption("-o",true,false);
        argParser.addOption("-plot",false,true);
        argParser.addOption("-b",false,true);


        if(argParser.Compile()){

            String input = argParser.getArgument("-gtf");
            String output = argParser.getArgument("-o");

            GtfParser parser = new GtfParser(input);
            ExonSkipping skipping = new ExonSkipping();
            BufferedWriter writer = Parser.Writer(output);

            if(!argParser.hasArgument("-plot")) {
                try {
                    writer.write("id\tsymbol\tchr\tstrand\tnprots\tntrans\tSV\tWT\tWT_prots\tSV_prots\tmin_skipped_exon\tmax_skipped_exon\tmin_skipped_bases\tmax_skipped_bases\n");
                    for (Gene gene : parser) {
                        String skip = skipping.getSkips(gene);
                        if(skip!=null) {
                            if (!skip.equals("")) {
                                writer.write(skipping.getSkips(gene));
                            }
                        }
                    }
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {

                    TreeMap<Integer,String> ranked = new TreeMap<>();
                    Set<String> chrs = new LinkedHashSet<>();


                    writer.write("var plot_data = [\n");

                    int ind = 11;
                    if(argParser.hasArgument("-b")) {
                        ind=13;
                    }

                    int x =0;
                    int sum = 0;
                    for (Gene gene : parser) {

                        chrs.add(gene.chromosome);


                        String skip = skipping.getSkips(gene);
                        if(skip!=null) {
                            if(x!=0){
                                writer.write(",\n");
                            }
                            if(skip.equals("")){
                                writer.write("{'x':"+x+",'y':"+sum+",'group':'"+gene.chromosome+"'}");

                            }else{
                                String[] l = skip.split("\n");
                                int m = 0;
                                for (int i = 0; i < l.length; i++) {
                                    String[] s = l[i].split("\t");
                                    int c = Integer.parseInt(s[ind]);
                                    if(c>m){
                                        m=c;
                                    }
                                }
                                sum+=m;
                                writer.write("{'x':"+x+",'y':"+sum+",'group':'"+gene.chromosome+"'}");
                                ranked.put(-m,",\n{'x':"+x+",'y':"+sum+",'group':'ranked_"+gene.id+"','value':"+m+"}");
                            }
                            x+=1;
                        }

                    }
                    int c = 0;
                    for(Map.Entry<Integer,String> entry : ranked.entrySet()) {
                        System.out.println(entry.getValue());
                        if(c<10) {
                            String value = entry.getValue();
                            writer.write(value);
                            c += 1;
                        }

                    }

                    writer.write("];");
                    writer.write("\nvar stats = ["+x+","+chrs.size()+"];");
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

        }


    }

}
