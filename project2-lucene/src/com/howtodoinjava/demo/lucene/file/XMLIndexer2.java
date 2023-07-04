package com.howtodoinjava.demo.lucene.file;

import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XMLIndexer2 {
    public static void main(String[] args) {
        String xmlDirectory = "F:\\Master_CS\\lucene\\xml-doc";
        String indexDirectory = "F:\\Master_CS\\lucene\\index";

        try {
            // Set up Lucene index writer
            Path indexPath = Paths.get(indexDirectory);
            Directory directory = FSDirectory.open(indexPath);
            PersianAnalyzer analyzer = new PersianAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            // Iterate through XML files in the directory
            File folder = new File(xmlDirectory);
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                        // Open XML file for parsing
                        FileInputStream fileInputStream = new FileInputStream(file);
                        XMLInputFactory factory = XMLInputFactory.newInstance();
                        XMLStreamReader reader = factory.createXMLStreamReader(fileInputStream);

                        // Parse XML and create Lucene documents
                        String currentElement = "";
                        String title = "";
                        String body = "";
                        String url = "";
                        String id = "";

                        while (reader.hasNext()) {
                            int event = reader.next();
                            switch (event) {
                                case XMLStreamConstants.START_ELEMENT:
                                    currentElement = reader.getLocalName();
                                    break;
                                case XMLStreamConstants.CHARACTERS:
                                    String text = reader.getText().trim();
                                    if (!text.isEmpty()) {
                                        if (currentElement.equals("URL")){
                                            url = text;
                                        }
                                        else if(currentElement.equals("DOCID")){
                                            id = text;
                                        }
                                        else if (currentElement.equals("TITLE")) {
                                            //System.out.print("TITLE");
                                            title = text;
                                        } else if (currentElement.equals("BODY")) {
                                            body = text;
                                        }
                                    }
                                    break;
                                case XMLStreamConstants.END_ELEMENT:
                                    if (reader.getLocalName().equals("DOC")) {
                                        // Create a Lucene document and add fields
                                        Document doc = new Document();
                                        doc.add(new StringField("id", id, Field.Store.YES));
                                        doc.add(new StringField("url", url, Field.Store.YES));
                                        doc.add(new TextField("title", title, Field.Store.YES));
                                        doc.add(new TextField("body", body, Field.Store.YES));

                                        // Add document to the index
                                        indexWriter.addDocument(doc);
                                    }
                                    break;
                            }
                        }

                        // Close resources for current file
                        fileInputStream.close();
                        reader.close();
                    }
                }
            }

            // Close resources
            indexWriter.close();
            directory.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
