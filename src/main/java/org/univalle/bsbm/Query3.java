package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query3 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<String, String>();
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/", "bsbm-inst");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");

        //step one
        queryAsList.set(7, "SELECT DISTINCT ?ProductType");
        queryAsList = Query.addComment(new int[]{9, 11, 12, 13, 14, 15, 16, 17, 18, 19}, queryAsList);
        queryAsList.set(10, "\t?product a ?ProductType .");
        queryAsList.add(11, "\tFILTER regex(str(?ProductType), \"ProductType\")");
        queryAsList.set(22, "ORDER BY ?ProductType");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductType.txt"));
        queryAsList.remove(11);

        //step two
        queryAsList = Query.removeComment(new int[]{9, 11}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductFeature1");
        queryAsList.set(11, "\t?product bsbm:productFeature ?ProductFeature1 .");
        queryAsList.set(21, "ORDER BY ?ProductFeature1");

        boolean flagPT = false;
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductType.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(10, "\t?product a "+variable+" .");
            if(!flagPT) {
                queryAsList.add(12, "\tFILTER regex(str(?ProductFeature1), \"ProductFeature\")");
                flagPT = true;
            }
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductFeature1.txt"));
        queryAsList.remove(12);

        //step three
        queryAsList = Query.removeComment(new int[]{12, 13, 14, 15, 16, 17, 19}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductFeature2");
        queryAsList.set(13, "\tFILTER ( ?p1 > 10 )");
        queryAsList.set(15, "\tFILTER ( ?p3 < 500 )");
        queryAsList.set(17, "\t\t?product bsbm:productFeature ?ProductFeature2 .");
        queryAsList.set(21, "ORDER BY ?ProductFeature2");

        boolean flagPF = false;
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductFeature1.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(11, "\t?product bsbm:productFeature "+variable+" .");
            if(!flagPF) {
                queryAsList.add(13, "\tFILTER regex(str(?ProductFeature2), \"ProductFeature\")");
                flagPF = true;
            }
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductFeature2.txt"));
        queryAsList.remove(13);

        //step four
        queryAsList = Query.removeComment(new int[]{18}, queryAsList);
        queryAsList.set(7, "SELECT ?product ?label");
        queryAsList.set(21, "ORDER BY ?label");
        queryAsList.set(22, "LIMIT 5");
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductFeature2.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));
            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(17, "\t\t?product bsbm:productFeature " + variable + " .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);
        queryAsList.set(22, "LIMIT 10");
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> file was created successfully \n\n"+query);
        }
    }
}
