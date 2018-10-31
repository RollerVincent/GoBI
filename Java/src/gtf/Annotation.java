package gtf;

import java.util.HashMap;
import java.util.Iterator;

public class Annotation implements Iterable<Gene> {

    public HashMap<String,Gene> genes = new HashMap<String, Gene>();
    public HashMap<String,Transcript> transcripts = new HashMap<String, Transcript>();
    public HashMap<String,CodingSequence> proteins = new HashMap<String, CodingSequence>();


    public static void setAttributes(Gene gene, Transcript transcript, Exon exon, String data){
        if(data.charAt(0)!=' '){
            data=" "+data;
        }
        String[] s = (data).split(";");
        for (int i = 0; i < s.length; i++) {
            if (s[i].charAt(1) == 'e') {
                String[] a = s[i].split(" ");
                exon.attributes.put(a[1],a[2].replace("\"",""));
            }else {
                if (gene != null) {
                    if (s[i].charAt(1) == 'g') {
                        String[] a = s[i].split(" ");
                        gene.attributes.put(a[1], a[2].replace("\"",""));
                    }
                }
                if (transcript != null) {
                    if (s[i].charAt(1) == 't') {
                        String[] a = s[i].split(" ");
                        transcript.attributes.put(a[1], a[2].replace("\"",""));
                    }
                }
            }
        }

    }

    public static void setAttributes(CodingSequence cds, String data){
        if(data.charAt(0)!=' '){
            data=" "+data;
        }
        String[] s = (data).split(";");
        for (int i = 0; i < s.length; i++) {

            if (s[i].charAt(1) == 'p' || s[i].charAt(1) == 'c') {
                String[] a = s[i].split(" ");
                cds.attributes.put(a[1],a[2].replace("\"",""));
            }
        }
    }

    public Gene getGene(String id){
        return genes.get(id);
    }
    public Transcript getTranscript(String id){
        return transcripts.get(id);
    }
    public CodingSequence getCodingSequence(String id){
        return proteins.get(id);
    }

    @Override
    public Iterator<Gene> iterator() {
        return genes.values().iterator();
    }

}
