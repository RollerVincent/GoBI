package tools;

import htsjdk.samtools.util.Tuple;
import parser.Parser;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DifferentialExpression {

    BufferedWriter exprs_writer;
    BufferedWriter pdat_writer;
    BufferedWriter fdat_writer;

    BufferedReader countfile_reader;
    String[] configs;

    String outdir;

    HashMap<String,Integer> conditionIndexMap = new HashMap<>();
    int currentIndex = 0;

    List<BufferedReader> count_readers = new ArrayList<>();

    HashMap<String,Tuple<Integer[],Integer>> cache = new HashMap<>();

    public DifferentialExpression(String out, String countfiles, String config){

        exprs_writer = Parser.Writer(out+"/exprs.txt");
        pdat_writer = Parser.Writer(out+"/p_data.txt");
        fdat_writer = Parser.Writer(out+"/f_data.txt");

        outdir = out;

        countfile_reader = Parser.Reader(countfiles);
        BufferedReader config_reader = Parser.Reader(config);

        try {
            configs = new String[2];
            String co = config_reader.readLine();
            while (co != null) {
                String[] sp = co.split("\t");
                if (sp[0].equals("R")) {
                    configs[0] = sp[1];
                } else if (sp[0].equals("diffscript")) {
                    configs[1] = sp[1];
                }
                co = config_reader.readLine();
            }
            config_reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }



    }




    public void Count(){

        try {
            String[] countFileinfo = nextCountfileInfo();
            while(countFileinfo!=null){
                p_dat_entry(countFileinfo);
                BufferedReader tmp = Parser.Reader(countFileinfo[1]);
                tmp.readLine();
                count_readers.add(tmp);
                countFileinfo = nextCountfileInfo();
            }
            int samplecount = count_readers.size();
            String l;
            String[] s;
            Tuple<Integer[],Integer> c;

            int done = 0;
            while(done!=samplecount){
                done=0;
                for (int i = 0; i < samplecount; i++) {
                    l = count_readers.get(i).readLine();
                    if(l==null){
                        done+=1;
                    }else{
                        s = l.split("\t");
                        c = cache.get(s[0]);
                        if(c==null){
                            c = new Tuple<>(new Integer[samplecount],0);
                        }
                        c.a[i] = Integer.valueOf(s[8]);

                        if(c.b==samplecount-1){
                            //// complete gene


                            for(Integer it:c.a){
                                exprs_writer.write(it+"\t");
                            }
                            exprs_writer.write("\n");

                            for (int j = 0; j < currentIndex; j++) {
                                fdat_writer.write(s[0]+"\t");
                            }
                            fdat_writer.write("\n");

                            cache.remove(s[0]);


                        }else{
                            cache.put(s[0],new Tuple<>(c.a,c.b+1));
                        }

                    }
                }





            }

            for(String k:cache.keySet()){
                c = cache.get(k);
                for(Integer it:c.a){
                    if(it==null){
                        it=0;
                    }
                    exprs_writer.write(it+"\t");
                }
                exprs_writer.write("\n");

                for (int j = 0; j < currentIndex; j++) {
                    fdat_writer.write(k+"\t");
                }
                fdat_writer.write("\n");
                //cache.remove(k);
            }










        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Exec(){
        try{


            /* BufferedReader in = new BufferedReader(
                    new InputStreamReader(shell.getInputStream()) );
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();

            in = new BufferedReader(
                    new InputStreamReader(shell.getErrorStream()) );
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();*/
            String[] methods = {"limma","edgeR","DESeq"};
            String s;
           // TreeMap<Double,String> pValues;

            for(String m: methods){
                Process p = Runtime.getRuntime().exec(new String[] {configs[0],configs[1],outdir+"/exprs.txt",outdir+"/p_data.txt",outdir+"/f_data.txt",m,outdir+"/tmp.out"});
                p.waitFor();
                BufferedReader br = Parser.Reader(outdir+"/tmp.out");
                BufferedWriter wr = Parser.Writer(outdir+"/"+m+".out");

                wr.write(br.readLine()+"\tADJ.PVAL\n");

                String v;
                HashMap<String,Double> pv = new HashMap<>();
                while((s=br.readLine())!=null){
                    v = s.split("\t")[2];
                    if(!v.equals("NA")){
                       // System.out.println(Double.parseDouble(v));
                        pv.put(s,Double.parseDouble(v));
                    }else {
                        wr.write(s + "\tNA\n");
                    }
                }
                Map<String, Double> sorted = sortByValue(pv);
                double mfdr = Double.MAX_VALUE;
                int n = sorted.size();
                double k=n;
                double fdrk;
                for(String sk:sorted.keySet()){
                    fdrk = sorted.get(sk)*(n/k);
                    if(fdrk<mfdr){
                        mfdr=fdrk;
                    }else{
                        fdrk=mfdr;
                    }

                    wr.write(sk+"\t"+fdrk+"\n");

                    k-=1;

                }







                br.close();
                wr.close();




            }
            File file = new File(outdir+"/tmp.out");
            file.delete();


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void Close(){
        try {
            exprs_writer.close();
            pdat_writer.close();
            fdat_writer.close();
            countfile_reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String[] nextCountfileInfo() throws Exception{

        String s = countfile_reader.readLine();
        if(s!=null){
            return s.split("\t");
        }
        return null;

    }

    public void p_dat_entry(String[] cfi) throws Exception{
        Integer i = conditionIndexMap.get(cfi[0]);
        if(i==null){
            conditionIndexMap.put(cfi[0],currentIndex);
            i=currentIndex;
            currentIndex+=1;
        }
        pdat_writer.write(cfi[0]+"."+cfi[2]+"\t"+i+"\n");
    }


}
