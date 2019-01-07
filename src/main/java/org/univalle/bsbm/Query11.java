package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query11 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www.w3.org/2001/XMLSchema#", "rdfs");
        prefixes.put("http://purl.org/dc/elements/1.1/", "dc");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/", "bsbm-inst");

        //step one
        queryAsList = Query.addComment(new int[]{10, 11}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?OfferXYZ");
        queryAsList.set(9, "\t{");
        queryAsList.add(10, "\t\t?OfferXYZ ?property ?hasValue .");
        queryAsList.add(11, "\t\tFILTER regex(str(?OfferXYZ), \"ProductFeature\")");
        queryAsList.add(12, "\t}");
        queryAsList.set(16, "ORDER BY ?OfferXYZ");
        queryAsList.add("LIMIT 10");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/OfferXYZ.txt"));
        queryAsList.remove(10);
        queryAsList.remove(10);
        queryAsList.remove(10);

        //step two
        queryAsList = Query.removeComment(new int[]{10, 11}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?property ?hasValue ?isValueOf");
        queryAsList.remove(13);
        queryAsList.set(13, "LIMIT 5");

        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/OfferXYZ.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(9, "\t{ "+variable+" ?property ?hasValue }");
            queryAsList.set(11, "\t{ ?isValueOf ?property "+variable+" }");

            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 108);

        queryAsList.remove(13);
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> file was created successfully \n\n"+query);
        }
    }
}
