package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query7 {

    public static void generate(String pathQuery, String pathDataset) {
        String query = Query.loadQueryFile(pathQuery + "/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://purl.org/stuff/rev#", "rev");
        prefixes.put("http://xmlns.com/foaf/0.1/", "foaf");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer1/", "bsbm-inst-data");
        prefixes.put("http://purl.org/dc/elements/1.1/", "dc");

        //step one
        queryAsList = Query.addComment(new int[]{12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29}, queryAsList);
        queryAsList.set(9, "SELECT DISTINCT ?ProductXYZ");
        queryAsList.set(11, "\t?ProductXYZ rdfs:label ?productLabel .");
        queryAsList.add(12, "\tFILTER regex(str(?ProductXYZ), \"Producer\")");
        queryAsList.add("ORDER BY ?ProductXYZ");
        queryAsList.add("LIMIT 10");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery + "/ProductXYZ.txt"));
        queryAsList.remove(12);
        queryAsList.remove(31);

        //step two
        do{
            queryAsList = Query.removeComment(new int[]{12, 13, 14, 15, 16, 18, 19, 21}, queryAsList);
            queryAsList.set(9, "SELECT DISTINCT ?date");
            queryAsList.set(17, "\t\t?vendor bsbm:country <http://downlode.org/rdf/iso-3166/countries#GB> .");
            do {
                String url = "", prefix = "";
                do {
                    url = Query.getURL(pathQuery + "/ProductXYZ.txt");
                    prefix = url.substring(1, url.lastIndexOf("/") + 1);
                } while (!prefixes.containsKey(prefix));

                String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
                queryAsList.set(11, "\t" + variable + " rdfs:label ?productLabel .");
                queryAsList.set(13, "\t\t?offer bsbm:product " + variable + " .");
                queryAsList.set(23, "#\t\t?review bsbm:reviewFor " + variable + " .");

                query = Query.concat(queryAsList);
                queryResult = Query.executeQuery(query, Paths.get(pathDataset));
                Query.createFile(queryResult, Paths.get(pathQuery + "/date.txt"));
            } while (queryResult.length() <= 45);
            Query.createFile(queryResult, Paths.get(pathQuery + "/date.txt"));

            queryAsList = Query.removeComment(new int[]{22, 23, 24, 25, 26, 27, 28, 29}, queryAsList);
            queryAsList.set(9, "SELECT ?offer ?price ?vendor ?vendorTitle ?review ?revTitle ?reviewer ?revName ?rating1 ?rating2");
            queryAsList.set(31, "LIMIT 5");
            String date = Query.getDate(pathQuery + "/date.txt");
            queryAsList.set(20, "\t\tFILTER ( ?date >= " + date + " )");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        } while (queryResult.length() <= 305);

        queryAsList.remove(31);
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> was created successfully \n\n"+query);
        }
    }
}
