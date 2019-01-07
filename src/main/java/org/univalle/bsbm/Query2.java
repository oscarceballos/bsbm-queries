package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query2 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/", "bsbm-inst-data");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://purl.org/dc/elements/1.1/", "dc");

        //step one
        queryAsList.set(7, "SELECT DISTINCT ?ProductXYZ");
        queryAsList = Query.addComment(new int[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}, queryAsList);
        queryAsList.set(9, "\t?ProductXYZ rdfs:label ?label .");
        queryAsList.add(10, "\tFILTER regex(str(?ProductXYZ), \"Product1\")");
        queryAsList.add("ORDER BY ?ProductXYZ");
        queryAsList.add("LIMIT 10");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductXYZ.txt"));
        queryAsList.remove(10);

        //step two
        queryAsList = Query.removeComment(new int[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23}, queryAsList);
        queryAsList.set(7, "SELECT ?label ?comment ?producer ?productFeature ?propertyTextual1 ?propertyTextual2 ?propertyTextual3 ?propertyNumeric1 ?propertyNumeric2 ?propertyTextual4 ?propertyTextual5 ?propertyNumeric4");
        queryAsList.remove(25);
        queryAsList.set(25, "LIMIT 5");

        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductXYZ.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));
            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(9, "\t" + variable + " rdfs:label ?label .");
            queryAsList.set(10, "\t" + variable + " rdfs:comment ?comment .");
            queryAsList.set(11, "\t" + variable + " bsbm:producer ?p .");
            queryAsList.set(13, "\t" + variable + " dc:publisher ?p .");
            queryAsList.set(14, "\t" + variable + " bsbm:productFeature ?f .");
            queryAsList.set(16, "\t" + variable + " bsbm:productPropertyTextual1 ?propertyTextual1 .");
            queryAsList.set(17, "\t" + variable + " bsbm:productPropertyTextual2 ?propertyTextual2 .");
            queryAsList.set(18, "\t" + variable + " bsbm:productPropertyTextual3 ?propertyTextual3 .");
            queryAsList.set(19, "\t" + variable + " bsbm:productPropertyNumeric1 ?propertyNumeric1 .");
            queryAsList.set(20, "\t" + variable + " bsbm:productPropertyNumeric2 ?propertyNumeric2 .");
            queryAsList.set(21, "\tOPTIONAL { " + variable + " bsbm:productPropertyTextual4 ?propertyTextual4 }");
            queryAsList.set(22, "\tOPTIONAL { " + variable + " bsbm:productPropertyTextual5 ?propertyTextual5 }");
            queryAsList.set(23, "\tOPTIONAL { " + variable + " bsbm:productPropertyNumeric4 ?propertyNumeric4 }");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 800);

        queryAsList.remove(25);
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> file was created successfully \n\n"+query);
        }
    }
}