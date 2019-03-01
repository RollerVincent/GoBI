package tools;

import gtf.Gene;
import gtf.Region;
import gtf.RegionVector;

import java.util.*;

public class ExonSkipping {

    public String getSkips(Gene g) {

        int l = g.codingTranscripts.size();
        if(l>0) {

            HashMap<String, Skip> skips = new HashMap<String, Skip>();

            String head = g.id + "\t" + g.attributes.get("gene_name") + "\t" + g.chromosome + "\t" + g.strand + "\t" + l + "\t" + g.transcripts.size() + "\t";

            RegionVector[] inversedTranscripts = new RegionVector[l];
            for (int i = 0; i < l; i++) {
                inversedTranscripts[i] = g.codingTranscripts.get(i).codingSequence.regionVector.inverse();
            }

            for (int i = 0; i < l; i++) {
                for (int j = i + 1; j < l; j++) {

                    int i1 = 0;
                    int i2 = 0;
                    int mod = 0;
                    int skipstart = 0;

                    while (i1 < inversedTranscripts[i].length() && i2 < inversedTranscripts[j].length()) {
                        Region intron_1 = inversedTranscripts[i].get(i1);
                        Region intron_2 = inversedTranscripts[j].get(i2);
                        if (mod == 0) {
                            if (intron_1.start == intron_2.start) {
                                if (intron_1.end == intron_2.end) {
                                    i1 += 1;
                                    i2 += 1;
                                } else if (intron_1.end > intron_2.end) {
                                    mod = 2;
                                    skipstart = i2;
                                    i2 += 1;
                                } else {
                                    mod = 1;
                                    skipstart = i1;
                                    i1 += 1;
                                }
                            } else if (intron_1.start > intron_2.start) {
                                i2 += 1;
                            } else {
                                i1 += 1;
                            }
                        } else {
                            if (intron_1.end == intron_2.end) {

                                // SKIP DETECTED, BUILDING OUTPUT


                                String SV;
                                List<Region> WT = new ArrayList<Region>();
                                String SV_prot;
                                String WT_prot;

                                if (mod == 2) {
                                    SV = intron_1.toString();
                                    for (int k = skipstart; k < i2 + 1; k++) {
                                        WT.add(inversedTranscripts[j].get(k));
                                    }
                                    SV_prot = g.codingTranscripts.get(i).codingSequence.id;
                                    WT_prot = g.codingTranscripts.get(j).codingSequence.id;
                                } else {
                                    SV = intron_2.toString();
                                    for (int k = skipstart; k < i1 + 1; k++) {
                                        WT.add(inversedTranscripts[i].get(k));
                                    }
                                    SV_prot = g.codingTranscripts.get(j).codingSequence.id;
                                    WT_prot = g.codingTranscripts.get(i).codingSequence.id;
                                }

                                if (!skips.containsKey(SV)) {
                                    skips.put(SV, new Skip());
                                }
                                Skip skip = skips.get(SV);

                                skip.SV = SV;

                                for (int k = 0; k < WT.size(); k++) {
                                    if (!skip.WT.contains(WT.get(k).toString())) {
                                        skip.WT.add(WT.get(k).toString());
                                    }
                                }

                                if (!skip.SV_prots.contains(SV_prot)) {
                                    skip.SV_prots.add(SV_prot);
                                }

                                if (!skip.WT_prots.contains(WT_prot)) {
                                    skip.WT_prots.add(WT_prot);
                                }

                                if (WT.size() - 1 > skip.max_skipped_exon) {
                                    skip.max_skipped_exon = WT.size() - 1;
                                }

                                if (WT.size() - 1 < skip.min_skipped_exon) {
                                    skip.min_skipped_exon = WT.size() - 1;
                                }

                                int skipped_l = 0;
                                for (int k = 1; k < WT.size(); k++) {
                                    skipped_l += (WT.get(k).start - WT.get(k - 1).end) - 1;
                                }

                                if (skipped_l > skip.max_skipped_bases) {
                                    skip.max_skipped_bases = skipped_l;
                                }

                                if (skipped_l < skip.min_skipped_bases) {
                                    skip.min_skipped_bases = skipped_l;
                                }

                                mod = 0;
                                i1 += 1;
                                i2 += 1;

                            } else {
                                if (mod == 1) {
                                    if (intron_1.end > intron_2.end) {
                                        mod = 0;
                                        i2 += 1;
                                    } else {
                                        i1 += 1;
                                    }
                                } else if (mod == 2) {
                                    if (intron_2.end > intron_1.end) {
                                        mod = 0;
                                        i1 += 1;
                                    } else {
                                        i2 += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String out = "";
            Iterator<Skip> itr = skips.values().iterator();
            while (itr.hasNext()) {
                out += head + itr.next().toString();
            }

            return out;
        }
        return null;
    }

    public Collection<RawSkip> getRawSkips(Gene g){
        int l = g.codingTranscripts.size();
        if(l>0) {

            HashMap<String, RawSkip> skips = new HashMap<String, RawSkip>();
            RegionVector[] inversedTranscripts = new RegionVector[l];
            for (int i = 0; i < l; i++) {
                inversedTranscripts[i] = g.codingTranscripts.get(i).codingSequence.regionVector.inverse();
            }

            for (int i = 0; i < l; i++) {
                for (int j = i + 1; j < l; j++) {

                    int i1 = 0;
                    int i2 = 0;
                    int mod = 0;
                    int skipstart = 0;

                    while (i1 < inversedTranscripts[i].length() && i2 < inversedTranscripts[j].length()) {
                        Region intron_1 = inversedTranscripts[i].get(i1);
                        Region intron_2 = inversedTranscripts[j].get(i2);
                        if (mod == 0) {
                            if (intron_1.start == intron_2.start) {
                                if (intron_1.end == intron_2.end) {
                                    i1 += 1;
                                    i2 += 1;
                                } else if (intron_1.end > intron_2.end) {
                                    mod = 2;
                                    skipstart = i2;
                                    i2 += 1;
                                } else {
                                    mod = 1;
                                    skipstart = i1;
                                    i1 += 1;
                                }
                            } else if (intron_1.start > intron_2.start) {
                                i2 += 1;
                            } else {
                                i1 += 1;
                            }
                        } else {
                            if (intron_1.end == intron_2.end) {

                                // SKIP DETECTED, BUILDING OUTPUT


                                String SV;
                                List<Region> WT = new ArrayList<Region>();
                                String SV_trans;
                                String WT_trans;

                                if (mod == 2) {
                                    SV = intron_1.toString();
                                    for (int k = skipstart; k < i2 + 1; k++) {
                                        WT.add(inversedTranscripts[j].get(k));
                                    }
                                    SV_trans = g.codingTranscripts.get(i).id;
                                    WT_trans = g.codingTranscripts.get(j).id;
                                } else {
                                    SV = intron_2.toString();
                                    for (int k = skipstart; k < i1 + 1; k++) {
                                        WT.add(inversedTranscripts[i].get(k));
                                    }
                                    SV_trans = g.codingTranscripts.get(j).id;
                                    WT_trans = g.codingTranscripts.get(i).id;
                                }

                                if (!skips.containsKey(SV)) {
                                    skips.put(SV, new RawSkip(g.id,WT_trans,SV_trans));
                                }
                                RawSkip skip = skips.get(SV);
                                for(Region r:WT){
                                    if(!skip.introns.regions.contains(r)) {
                                            skip.introns.add(r);
                                    }
                                }

                                mod = 0;
                                i1 += 1;
                                i2 += 1;

                            } else {
                                if (mod == 1) {
                                    if (intron_1.end > intron_2.end) {
                                        mod = 0;
                                        i2 += 1;
                                    } else {
                                        i1 += 1;
                                    }
                                } else if (mod == 2) {
                                    if (intron_2.end > intron_1.end) {
                                        mod = 0;
                                        i1 += 1;
                                    } else {
                                        i2 += 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return skips.values();
        }
        return null;
    }

    public class RawSkip {
        public String gene_id;
        public RegionVector introns = new RegionVector();
        public String WT_transcript;
        public String SV_transcript;

        public RawSkip(String gene_id, String WT_transcript, String SV_transcript){
            this.gene_id = gene_id;
           // this.exon = exon;
            this.WT_transcript = WT_transcript;
            this.SV_transcript = SV_transcript;
        }
    }


    public class Skip {

        String SV;
        List<String> WT = new ArrayList<String>();
        List<String> WT_prots = new ArrayList<String>();
        List<String> SV_prots = new ArrayList<String>();
        int min_skipped_exon = Integer.MAX_VALUE;
        int max_skipped_exon = 0;
        int min_skipped_bases = Integer.MAX_VALUE;
        int max_skipped_bases = 0;


        @Override
        public String toString() {
            String out=""+SV+"\t";

            if(WT.size()>0) {
                out += WT.get(0);
                for (int i = 1; i < WT.size(); i++) {
                    out += "|" + WT.get(i);
                }
            }
            if(WT_prots.size()>0) {
                out += "\t" + WT_prots.get(0).toString();
                for (int i = 1; i < WT_prots.size(); i++) {

                    out += "|" + WT_prots.get(i).toString();

                }

            }
            if(SV_prots.size()>0) {
                out += "\t" + SV_prots.get(0).toString();
                for (int i = 1; i < SV_prots.size(); i++) {
                    out += "|" + SV_prots.get(i).toString();
                }
            }
            out+="\t"+min_skipped_exon;
            out+="\t"+max_skipped_exon;
            out+="\t"+min_skipped_bases;
            out+="\t"+max_skipped_bases;
            return out+"\n";
        }
    }

}
