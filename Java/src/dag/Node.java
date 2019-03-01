package dag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Node {

    public String id;
    public String name;
    public List<String> tmpParents = new ArrayList<>();
    public List<Node> parents = new ArrayList<>();
    public List<Node> children = new ArrayList<>();
    public Set<String> genes = new LinkedHashSet<>();
    public boolean label = false;
    public int maxDepth;

    private int status = 0;





    public Node(String id){
        this.id = id;
    }


    public void resetStatus(List<Node> direction){
        status = direction.size();
    }

    public boolean getStatus(){
        status -= 1;
        return (status <= 0);
    }




    @Override
    public String toString(){
        String out = "--------------------------------------------------------------------------------\n";
        for(Node p:parents){
            out += p.id+":"+p.name+"\t";
        }
        out += "\n" + id +":"+name+"\n";
        for(Node c:children){
            out += c.id+":"+c.name+"\t";
        }
        return  out + "\n--------------------------------------------------------------------------------";
    }

}
