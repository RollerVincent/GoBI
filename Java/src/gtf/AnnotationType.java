package gtf;

import java.util.HashMap;

public abstract class AnnotationType {

    public String id="";
    public Region region = new Region();
    public RegionVector regionVector = new RegionVector();
    public HashMap<String,String> attributes = new HashMap<String,String>();


    public String getAttribute(String key){
        return attributes.get(key);
    }

    @Override
    public String toString(){
        String s = this.getClass().getName() + "\t" + id + "\t" + region;
        return s;
    }

}
