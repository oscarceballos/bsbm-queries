package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query8 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/", "bsbm-inst-data");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://purl.org/dc/elements/1.1/", "dc");
        prefixes.put("http://purl.org/stuff/rev#", "rev");
        prefixes.put("http://xmlns.com/foaf/0.1/", "foaf");


        //step one
        queryAsList = Query.addComment(new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19}, queryAsList);
        queryAsList.set(8, "SELECT DISTINCT ?ProductXYZ");
        queryAsList.set(10, "\t?review bsbm:reviewFor ?ProductXYZ .");
        queryAsList.add(11, "\tFILTER regex(str(?ProductXYZ), \"Product1\")");
        queryAsList.set(22, "ORDER BY ?ProductXYZ");
        queryAsList.set(23, "LIMIT 10");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductXYZ.txt"));
        queryAsList.remove(11);


        //step two
        queryAsList = Query.removeComment(new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19}, queryAsList);
        queryAsList.set(8, "SELECT ?title ?text ?reviewDate ?reviewer ?reviewerName ?rating1 ?rating2 ?rating3 ?rating4");
        queryAsList.set(21, "ORDER BY DESC(?reviewDate)");
        queryAsList.set(22, "LIMIT 5");
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductXYZ.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(10, "\t?review bsbm:reviewFor " + variable + " .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 180);

        queryAsList.set(22, "LIMIT 20");
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> file was created successfully \n\n"+query);
        }
    }
}
