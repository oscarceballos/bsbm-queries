package org.univalle.bsbm.common;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Query {

    public static ArrayList<String> addComment(int[] indices, ArrayList<String> queryAsList){
        for (int indice: indices) {
            queryAsList.set(indice, "#" + queryAsList.get(indice));
        }
        return queryAsList;
    }

    public static ArrayList<String> removeComment(int[] indices, ArrayList<String> queryAsList){
        for (int indice: indices) {
            queryAsList.set(indice, queryAsList.get(indice).replace("#", ""));
        }
        return queryAsList;
    }

    public static ArrayList<String> split(String query){
        String[] queryArray = query.split("\n");
        ArrayList<String> queryList = new ArrayList<>(Arrays.asList(queryArray));
        return queryList;
    }

    public static String concat(ArrayList<String> queryAsList){
        String query="";
        for (String line : queryAsList) {
            query += line + "\n";
        }
        return query;
    }

    public static boolean isEmpty(String queryResult) {
        boolean flag=false;
        String[] queryArray = queryResult.split("\n");
        System.out.println("line 3: "+queryArray[3]);
        String[] values = queryArray[3].split("\\|");
        for (String value : values) {
            String[] space = value.split(" ");
            if(space.length < 1) {
                System.out.println("flag: "+true);
            }
        }
        return flag;
    }

    public static String loadQueryFile(String queryFile) {
        String line = "", query = "";
        try {
            File inputFile = new File(queryFile);
            FileReader in = new FileReader(inputFile);
            BufferedReader inputStream = new BufferedReader(in);
            while ((line = inputStream.readLine()) != null) {
                query += line + "\n";
            }
            in.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return query;
    }

    public static String getURL(String file) {
        List<String> listURL= new ArrayList<>();
        String line="", url="";
        try{
            File inputFile = new File(file);
            FileReader in = new FileReader(inputFile);
            BufferedReader inputStream = new BufferedReader(in);
            while((line = inputStream.readLine()) != null) {
                if(line.contains("<") && line.contains(">")) {
                    listURL.add(line.substring(line.indexOf('<'), line.indexOf('>')+1));
                }
            }
            Random random = new Random();
            int i = random.nextInt(listURL.size());
            //System.out.println("random = "+i);
            url = listURL.get(i);
            in.close();
        }catch(IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return url;
    }

    public static String getDate(String file) {
        List<String> listURL= new ArrayList<>();
        String line="", url="";
        try{
            File inputFile = new File(file);
            FileReader in = new FileReader(inputFile);
            BufferedReader inputStream = new BufferedReader(in);
            while((line = inputStream.readLine()) != null) {
                if(line.contains("\"") && line.contains(">")) {
                    listURL.add(line.substring(line.indexOf('\"'), line.indexOf('>')+1));
                    //System.out.println("line-data: "+line);
                }
            }
            Random random = new Random();
            int i = random.nextInt(listURL.size());
            //System.out.println("random = "+i);
            url = listURL.get(i);
            in.close();
        }catch(IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return url;
    }

    public static String executeQuery(String queryString, Path datasetFile) {
        String queryResult = "";
        Model model = FileManager.get().loadModel(datasetFile.toString());
        org.apache.jena.query.Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        try {
            ResultSetRewindable results = ResultSetFactory.makeRewindable(qe.execSelect());
            //ResultSetFormatter.out(System.out, results);
            queryResult = ResultSetFormatter.asText(results);
            results.reset();
        } finally {
            qe.close();
        }
        return queryResult;
    }

    public static void createFile(String query, Path path) {
        byte data[] = query.getBytes();
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE, TRUNCATE_EXISTING))) {
            out.write(data, 0, data.length);
        } catch (IOException e) {
            System.err.println("Error: "+e);
        }
    }
}
