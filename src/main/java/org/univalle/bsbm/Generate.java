package org.univalle.bsbm;

public class Generate {
    public static void main(String... args) throws Exception {
        if (args != null && args.length == 2) {
            String[] query = args[0].split("/");
            int beginIndex = query[query.length-1].lastIndexOf("y");
            int endIndex = query[query.length-1].length();
            switch(Integer.parseInt(query[query.length-1].substring(beginIndex+1, endIndex))) {
                case 1:
                    Query1.generate(args[0], args[1]);
                    break;
                case 2:
                    Query2.generate(args[0], args[1]);
                    break;
                case 3:
                    Query3.generate(args[0], args[1]);
                    break;
                case 4:
                    Query4.generate(args[0], args[1]);
                    break;
                case 5:
                    Query5.generate(args[0], args[1]);
                    break;
                case 7:
                    Query7.generate(args[0], args[1]);
                    break;
                case 8:
                    Query8.generate(args[0], args[1]);
                    break;
                case 10:
                    Query10.generate(args[0], args[1]);
                    break;
                case 11:
                    Query11.generate(args[0], args[1]);
                    break;
            }
        } else {
            System.out.println("\nYou should to specify two arguments: query template file and dataset file separate them by space.\n" +
                    "For example: path_query_file/query_template_file.rq path_dataset_file/dataset_file");
        }
    }
}

