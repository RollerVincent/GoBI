package parser;

import dag.DAG;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OboParser{

    private BufferedReader reader;

    public OboParser(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[") && line.charAt(2) == 'e') {
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DAG construct(String root) {
        DAG dag = new DAG();
        boolean continuing = next(dag, root);
        while (continuing) {
            continuing = next(dag, root);
        }
        dag.compile();
        return dag;
    }

    private boolean next(DAG dag, String root) {
        try {
            String line = reader.readLine();
            String id = line.split(" ")[1].substring(3);
            line = reader.readLine();
            String name = line.split(" ")[1];
            line = reader.readLine();
            if (!line.split(" ")[1].equals(root)) {
                while (line != null && !(line.startsWith("[") && line.charAt(2) == 'e')) {
                    line = reader.readLine();
                }
                if (line == null) {
                    reader.close();
                    return false;
                } else {
                    return next(dag, root);
                }
            }
            List<String> parents = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("[") && line.charAt(2) == 'e') {
                    break;
                } else {
                    if (!line.equals("") && line.charAt(0) == 'i') {
                        if (line.charAt(3) == 'o') {
                            line = reader.readLine();
                            while (line != null && !(line.startsWith("[") && line.charAt(2) == 'e')) {
                                line = reader.readLine();
                            }
                            if (line == null) {
                                reader.close();
                                return false;
                            } else {
                                return next(dag, root);
                            }
                        } else if (line.charAt(3) == 'a' && line.charAt(6) == 'G') {
                            parents.add(line.split(" ")[1].substring(3));
                        }
                    }
                }
            }
            dag.add(id,name, parents);
            if (line == null) {
                reader.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


}
