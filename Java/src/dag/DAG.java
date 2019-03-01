package dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DAG {

    public HashMap<String,Node> map = new HashMap<>();
    public Node root;
    public List<Node> leaves = new ArrayList<>();


    public void add(String id, String name, List<String> parents){
        Node n = new Node(id);
        n.tmpParents = parents;
        n.name = name;
        map.put(id,n);
    }

    public void compile(){
        Node node;
        Node tmp;
        boolean isroot;
        for (String key : map.keySet()){
            node = map.get(key);
            isroot = true;
            for(String p: node.tmpParents){
                tmp = map.get(p);
                if(tmp!=null){
                    node.parents.add(tmp);
                    tmp.children.add(node);
                    isroot = false;
                }
            }
            node.tmpParents.clear();
            node.resetStatus(node.parents);
            if(isroot){
                root = node;
            }
        }
    }

    public void setMaxDepths() {
        root.maxDepth = 0;
        List<Node> depthChildren = root.children;
        List<Node> tmp;
        int depth = 1;
        while (depthChildren.size() != 0) {
            tmp = new ArrayList<>();
            for(Node dch:depthChildren){
                if(dch.getStatus()){
                    dch.maxDepth=depth;

                    dch.resetStatus(dch.children);      // next run upwards

                    if(dch.children.size()==0){
                        leaves.add(dch);
                    }else{
                        tmp.addAll(dch.children);
                    }
                }
            }
            depthChildren = tmp;
            depth+=1;
        }


        System.out.println("\tDiscovered "+leaves.size()+" leaves with maximum distance from root: "+(depth-1));

    }

    public void upwardPropagation(HashMap<String,Overlap> overlaps){
        List<Node> parents = new ArrayList<>();
        List<Node> tmp;
        String h;
        int s;
        Overlap ov;
        parents.addAll(leaves);
        while(parents.size()!=0) {
            tmp = new ArrayList<>();
            for (Node p : parents) {
                if (p.getStatus()) {
                    s = p.genes.size();
                    for(Node sp: p.parents){
                        sp.genes.addAll(p.genes);
                        tmp.add(sp);
                       /* if(sp.id.compareTo(p.id)<0){
                            h=sp.id+":"+p.id;
                        }else{
                            h=p.id+":"+sp.id;
                        }*/
                        ov = new Overlap(true);
                        ov.increment(s);
                        overlaps.put(p.id+":"+sp.id,ov);

                    }
                    //TODO: Resetting status if needed
                }
            }
            parents=tmp;
        }
        System.out.println("\tFinished upward propagation, root has "+root.genes.size()+" associated genes");
        System.out.println("\tDone extracting related Overlaps, total overlaps: "+overlaps.size());
    }

}
