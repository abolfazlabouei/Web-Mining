package com.howtodoinjava.demo.lucene.file;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchIndex {

    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "F:\\Master_CS\\Web mining\\Project\\index";
        BufferedReader reader;
        File lfile = new File("F:\\Master_CS\\Web mining\\Project\\t2b1\\out10.txt");
        //Instantiating the PrintStream class
        PrintStream stream = new PrintStream(lfile);

        System.setOut(stream);
        try {
            reader = new BufferedReader(new FileReader("F:\\Master_CS\\Web mining\\Project\\queries.txt"));
            String line = reader.readLine();
            int k = 0;
            while (line != null) {
                k++;
//                System.out.println(line);
                // read next line
                String queryString = line;

                ArrayList<String> test = new ArrayList<>();

                Directory directory = FSDirectory.open(Paths.get(indexDir));
                IndexReader indexReader = DirectoryReader.open(directory);
                IndexSearcher searcher = new IndexSearcher(indexReader);

                QueryParser parser = new QueryParser("title", new StandardAnalyzer());
                org.apache.lucene.search.Query query1 = new BoostQuery(parser.parse(queryString),2);

                parser = new QueryParser("body", new StandardAnalyzer());
                org.apache.lucene.search.Query query2 = new BoostQuery(parser.parse(queryString),1);

                BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
                finalQuery.add(query1, BooleanClause.Occur.SHOULD);
                finalQuery.add(query2, BooleanClause.Occur.SHOULD);

                int hitsPerPage = 10;
                TopDocs topDocs = searcher.search(finalQuery.build(), hitsPerPage);
                ScoreDoc[] hits = topDocs.scoreDocs;


                FileReader file = new FileReader(String.format("F:\\Master_CS\\Web mining\\Project\\true label\\q_out%s.txt", k));
                BufferedReader br = new BufferedReader(file);

                //file_queries.add("");
                while (br.readLine() != null){
                    test.add(br.readLine());
//                    System.out.println(br.readLine());
                }
                //file_queries.add(br.readLine());

                br.close();


                int relevant = 0;

                for (ScoreDoc hit : hits) {
                    Document document = searcher.doc(hit.doc);
//            System.out.println("Title: " + document.get("title"));
//            System.out.println("Body: " + document.get("body"));
//                    System.out.println("DOCID: " + document.get("id") +"    " + "Score: " + hit.score);
//            System.out.println("Score: " + hit.score);
                    for (int i=0; i<test.size(); i++){
                        if(document.get("id").equals(test.get(i)))
                            relevant++;
                    }

                }

                System.out.println(relevant+",");
                line = reader.readLine();
                indexReader.close();
                directory.close();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}