package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query10 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/", "bsbm-inst-data");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www.w3.org/2001/XMLSchema#", "xsd");
        prefixes.put("http://purl.org/dc/elements/1.1/", "dc");

        //step one
        queryAsList = Query.addComment(new int[]{10, 11, 12, 13, 14, 15, 16, 17}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductXYZ");
        queryAsList.set(9, "\t?offer bsbm:product ?ProductXYZ .");
        queryAsList.add(10, "\tFILTER regex(str(?ProductXYZ), \"Product1\")");
        queryAsList.add(20, "ORDER BY ?ProductXYZ");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductXYZ.txt"));
        queryAsList.remove(10);
        queryAsList.remove(19);

        //step two
        queryAsList = Query.removeComment(new int[]{10, 11, 12, 13, 14, 15, 16, 17}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?offer ?price");
        queryAsList.set(12, "\t?vendor bsbm:country <http://downlode.org/rdf/iso-3166/countries#GB> .");
        queryAsList.set(17, "\tFILTER (?date > \"2000-01-01T00:00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime>)");
        queryAsList.set(19, "LIMIT 5");
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductXYZ.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(9, "\t?offer bsbm:product "+variable+" .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 72);

        queryAsList.set(19, "LIMIT 10");
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> was created successfully \n\n"+query);
        }
    }
}
