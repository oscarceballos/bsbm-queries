package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query5 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/", "bsbm-inst-data");

        //step one
        queryAsList = Query.addComment(new int[]{9, 10, 12, 13, 14, 15, 16, 17, 18, 21}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductXYZ");
        queryAsList.set(11, "\t?ProductXYZ bsbm:productFeature ?prodFeature .");
        queryAsList.add(12, "\tFILTER regex(str(?ProductXYZ), \"Product1\")");
        queryAsList.set(21, "ORDER BY ?ProductXYZ");
        queryAsList.set(22, "LIMIT 10");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductXYZ.txt"));
        queryAsList.remove(12);

        //step two
        queryAsList = Query.removeComment(new int[]{9, 10, 12, 13, 14, 15, 16, 17, 18, 21}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?product ?productLabel");
        queryAsList.set(20, "ORDER BY ?productLabel");
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductXYZ.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(10, "\tFILTER (" + variable + " != ?product)");
            queryAsList.set(11, "\t" + variable + " bsbm:productFeature ?prodFeature .");
            queryAsList.set(13, "\t" + variable + " bsbm:productPropertyNumeric1 ?origProperty1 .");
            queryAsList.set(16, "\t" + variable + " bsbm:productPropertyNumeric2 ?origProperty2 .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 108);

        queryAsList.set(21, "LIMIT 5");
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> file was created successfully \n\n"+query);
        }
    }
}
