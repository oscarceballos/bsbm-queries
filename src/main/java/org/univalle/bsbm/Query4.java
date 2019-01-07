package org.univalle.bsbm;

import org.univalle.bsbm.common.Query;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Query4 {

    public static void generate(String pathQuery, String pathDataset){
        String query = Query.loadQueryFile(pathQuery+"/query-template.rq");
        ArrayList<String> queryAsList = Query.split(query);

        Map<String, String> prefixes = new HashMap<>();
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/", "bsbm-inst");
        prefixes.put("http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/", "bsbm");
        prefixes.put("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
        prefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");

        //step one
        queryAsList = Query.addComment(new int[]{10, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductType");
        queryAsList.set(11, "\t\t?product rdf:type ?ProductType .");
        queryAsList.add(12, "\t\tFILTER regex(str(?ProductType), \"ProductType\")");
        queryAsList.set(30, "ORDER BY ?ProductType");
        query = Query.concat(queryAsList);
        String queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductType.txt"));
        queryAsList.remove(11);

        //step two
        queryAsList = Query.removeComment(new int[]{10, 12}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductFeature1");
        queryAsList.set(12, "\t\t?product bsbm:productFeature ?ProductFeature1 .");
        queryAsList.set(29, "ORDER BY ?ProductFeature1");

        boolean flagPT = false;
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductType.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(11, "\t\t?product rdf:type " + variable + " .");
            if(!flagPT) {
                queryAsList.add(13, "\t\tFILTER regex(str(?ProductFeature1), \"ProductFeature\")");
                flagPT = true;
            }
            queryAsList.set(22, "#\t\t?product rdf:type " + variable + " .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductFeature1.txt"));
        queryAsList.remove(13);

        //step three
        queryAsList = Query.removeComment(new int[]{13}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductFeature2");
        queryAsList.set(13, "\t\t?product bsbm:productFeature ?ProductFeature2 .");
        queryAsList.set(29, "ORDER BY ?ProductFeature2");

        boolean flagPF1 = false;
        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductFeature1.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));
            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(12, "\t\t?product bsbm:productFeature " + variable + " .");
            if(!flagPF1) {
                queryAsList.add(14, "\t\tFILTER regex(str(?ProductFeature2), \"ProductFeature\")");
                flagPF1 = true;
            }
            queryAsList.set(23, "#\t\t?product bsbm:productFeature " + variable + " .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductFeature2.txt"));
        queryAsList.remove(14);

        //step four
        queryAsList = Query.removeComment(new int[]{14, 15, 16}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?product ?label ?propertyTextual");
        queryAsList.set(16, "\t\tFILTER ( ?p1 > 10 )");
        queryAsList.set(29, "ORDER BY ?label");

        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductFeature2.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(13, "\t\t?product bsbm:productFeature " + variable + " .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);

        //step five
        queryAsList = Query.addComment(new int[]{9, 10, 11, 12, 13, 17}, queryAsList);
        queryAsList = Query.removeComment(new int[]{19, 20, 21, 22, 23, 27}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?ProductFeature3");
        queryAsList.set(23, "\t\t?product bsbm:productFeature ?ProductFeature3 .");
        queryAsList.add(24, "\t\tFILTER regex(str(?ProductFeature3), \"ProductFeature\")");
        queryAsList.set(30, "ORDER BY ?ProductFeature3");

        query = Query.concat(queryAsList);
        queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        Query.createFile(queryResult, Paths.get(pathQuery+"/ProductFeature3.txt"));
        queryAsList.remove(24);

        //step six
        queryAsList = Query.removeComment(new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24, 25, 26}, queryAsList);
        queryAsList.set(7, "SELECT DISTINCT ?product ?label ?propertyTextual");
        queryAsList.set(26, "\t\tFILTER ( ?p2 > 10 )");
        queryAsList.set(29, "ORDER BY ?label");
        queryAsList.set(30, "LIMIT 5");

        do {
            String url = "", prefix = "";
            do {
                url = Query.getURL(pathQuery+"/ProductFeature3.txt");
                prefix = url.substring(1, url.lastIndexOf("/") + 1);
            } while (!prefixes.containsKey(prefix));

            String variable = prefixes.get(prefix) + ":" + url.substring(url.lastIndexOf("/") + 1, url.length() - 1);
            queryAsList.set(23, "\t\t?product bsbm:productFeature " + variable + " .");
            query = Query.concat(queryAsList);
            queryResult = Query.executeQuery(query, Paths.get(pathDataset));
        }while(queryResult.length() <= 80);

        queryAsList.set(30, "LIMIT 10");
        query = Query.concat(queryAsList);
        Query.createFile(query, Paths.get(pathQuery+"/query.rq"));
        if(Files.exists(Paths.get(pathQuery+"/query.rq"))){
            System.out.println("The << query.rq >> file was created successfully \n\n"+query);
        }
    }
}
