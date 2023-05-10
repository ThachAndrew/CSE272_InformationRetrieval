/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.luence.app;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.search.similarities.BooleanSimilarity;
// import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.CharArraySet;



import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * refer to <a href="https://www.lucenetutorial.com/lucene-in-5-minutes.html"/>
 * A more comprehensive implementation, please check <a href="http://www.java2s.com/example/java-api/org/apache/lucene/search/indexsearcher/setsimilarity-1-1.html"/>
 */
public class App {

	// private static CustomTFIDFSimilarity similarity = new CustomTFIDFSimilarity();
	// private static TFSimilarity similarity = new TFSimilarity();

	public static void listFilesInCurrentDirectory() {
		File currentDir = new File(".");
		File[] files = currentDir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				System.out.println(file.getName());
			}
		}
	}

	public static List<String> removeStopwords(List<String> input) {
		CharArraySet stopWords = EnglishAnalyzer.ENGLISH_STOP_WORDS_SET; 
		List<String> output = new ArrayList<>();
		for (String word : input) {
			if (!stopWords.contains(word)) {
				output.add(word);
			}
		}
		return output;
	}
	// Returns a list of query strings.
	public static LinkedHashMap<String, String> parse_queries(String filepath) {
		LinkedHashMap<String, String> queryStrings = new LinkedHashMap<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			String line;
			// Match tags and blank lines.
			Pattern tagPattern = Pattern.compile("<[^>]+>|^\\s*$");
                        Pattern queryIDpattern = Pattern.compile("<num>.*:\\s*(.*)");
			String queryNum = "";
			String querystr = "";
			while ((line = reader.readLine()) != null) {
				Matcher queryIDmatcher = queryIDpattern.matcher(line);
				if (queryIDmatcher.find()) {
					String textAfterColon = queryIDmatcher.group(1);
					queryNum = textAfterColon;
				}
				// Ignore lines with tags, and also blank lines.
				// TODO: Consider storing title.
				Matcher tagMatcher = tagPattern.matcher(line); 
				if (!tagMatcher.find()) {
					String cleaned_line = line.trim().toLowerCase().replaceAll("[\\p{Punct}&&[^']]", " ");
					List<String> tokens = Arrays.asList(cleaned_line.split("\\s+"));
					tokens = removeStopwords(tokens);
					// String querystr = String.join(" AND ", tokens);
					querystr = String.join(" OR ", tokens);
					queryStrings.put(queryNum, querystr);
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Query file not found.");
		}
		catch (IOException e) {
			System.out.println("IO Exception when opening Query file.");
		}
		return queryStrings;
	}
	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("isbn", isbn, Field.Store.YES));
		w.addDocument(doc);
	}

	static Directory index(StandardAnalyzer analyzer)  throws IOException {
		// see: on-disk index
		// Directory index = new NIOFSDirectory(Paths.get("<your file index location>"));
		// see: in-memory index
		Directory index = new ByteBuffersDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try (IndexWriter w = new IndexWriter(index, config)) {
			BufferedReader reader = new BufferedReader(new FileReader("data/ohsumed.88-91"));
			String current_field = "";
			String line;
			Document doc = new Document();
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\\s+");
				String token = parts[0];
				switch(token) {
					case ".I":
						if (!current_field.equals("")) {
							// System.out.println("Adding to index.");
							w.addDocument(doc);
						}
						// Start a new one when another Identifier shows up.
						doc = new Document();
						String id = parts[1];
						current_field = id;
						doc.add(new StringField("id", id, Field.Store.YES));
						break;
					case ".U":
						String medlineID = reader.readLine();
						// System.out.println("medLINE: " + medlineID);
						doc.add(new StringField("medlineID", medlineID, Field.Store.YES));
						break;
					case ".S":
						String source = reader.readLine();
						// System.out.println("source: " + source);
						// FIXME: Consider making this a TextField.
						doc.add(new StringField("source", source, Field.Store.YES));
						break;
					case ".M":
						String meshTerm = reader.readLine();
						// System.out.println("meshTerm: " + meshTerm);
						doc.add(new TextField("meshTerm", meshTerm, Field.Store.YES));
						break;
					case ".T":
						String title = reader.readLine();
						// System.out.println("title: " + title);
						doc.add(new TextField("title", title, Field.Store.YES));
						break;
					case ".P":
						String pubType = reader.readLine();
						// System.out.println("pubType: " + pubType);
						doc.add(new StringField("pubType", pubType, Field.Store.YES));
						break;
					case ".W":
						String abstractText = reader.readLine();
						// System.out.println("abstractText: " + abstractText);
						doc.add(new TextField("abstractText", abstractText, Field.Store.YES));
						break;
					case ".A":
						String author = reader.readLine();
						// System.out.println("author: " + author);
						doc.add(new TextField("author", author, Field.Store.YES));
						break;
				}
			}
			// Add the remaining last one.
			w.addDocument(doc);
			reader.close();
			w.close();
		}
		return index;
	}

	public static void main(String[] args) throws IOException, ParseException {
		StandardAnalyzer analyzer = new StandardAnalyzer();


		// FIXME: Possible mem leak or redundant class declaration, since the helper function already does this.
		LinkedHashMap<String, String> queryStrings = parse_queries("data/query.ohsu.1-63");
		
		Directory index = index(analyzer);
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		// searcher.setSimilarity(new BooleanSimilarity());
		searcher.setSimilarity(new ClassicSimilarity());
		// searcher.setSimilarity(new TFSimilarity());

		String[] fields_to_search = {"title", "abstractText", "meshTerm", "author"};
		int hitsPerPage = 50;
		int i = 0;
	        for (Map.Entry<String, String> query : queryStrings.entrySet()) {
			// if (i == 1)
			//	break;
			String queryID = query.getKey();
			String querystr = query.getValue();
			// System.out.println("QueryID: " + queryID);
			// System.out.println("Querystr: " + querystr);

			Query q = new MultiFieldQueryParser(fields_to_search, analyzer).parse(querystr);
			TopDocs docs = searcher.search(q, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;
			// System.out.println("Found " + hits.length + " hits.");
			for (int j = 0; j < hits.length; ++j) {
				int docId = hits[j].doc;
				Document d = searcher.getIndexReader().document(docId);
				// System.out.println((j + 1) + ". " + d.get("id") + "\t" + d.get("title"));
				// System.out.println("Doc ID: " + docId);
				// System.out.println("medLine ID: " + d.get("medlineID"));
				// System.out.println("Score: " + hits[j].score);
				// System.out.println("\n");
				// Print as: QueryID Q0 DocID Rank Score RunID
				int rank = j + 1;
				System.out.println(queryID + " Q0 " + d.get("medlineID") + " " + rank + " " + hits[j].score + " TFIDF" );
			}

			i += 1;
		}
		/*
		for (String query: queryStrings) {
			if (i == 1)
				break;
			System.out.println("Searching query: " + query);
			Query q = new MultiFieldQueryParser(fields_to_search, analyzer).parse(query);
			TopDocs docs = searcher.search(q, hitsPerPage);
			ScoreDoc[] hits = docs.scoreDocs;
			System.out.println("Found " + hits.length + " hits.");
			for (int j = 0; j < hits.length; ++j) {
				int docId = hits[j].doc;
				Document d = searcher.getIndexReader().document(docId);
				System.out.println((j + 1) + ". " + d.get("id") + "\t" + d.get("title"));
				//System.out.println("Doc ID: " + docId);
				System.out.println("medLine ID: " + d.get("medlineID"));
				System.out.println("Score: " + hits[j].score);
				System.out.println("\n");
			}
			i += 1;
		}
	        */	

		// String querystr = args.length > 0 ? args[0] : "lucene";
		/*	
			String[] fields_to_search = {"title", "abstractText", "meshTerm", "author"};
			Query q = new MultiFieldQueryParser(fields_to_search, analyzer).parse(querystr);

			int hitsPerPage = Integer.MAX_VALUE;
			Directory index = index(analyzer);
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
		// searcher.setSimilarity(similarity);
		searcher.setSimilarity(new BooleanSimilarity());
		// searcher.setSimilarity(new TFIDFSimilarity());
		TopDocs docs = searcher.search(q, hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		// TODO: Make this 50 later.
		for (int i = 0; i < 10; ++i) {
		int docId = hits[i].doc;
		Document d = searcher.getIndexReader().document(docId);
		System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("title"));
		System.out.println("Doc ID: " + docId);
		System.out.println("Score: " + hits[i].score);
		System.out.println("\n");
		// System.out.println("abstractText: " + d.get("abstractText") + "\n");
		}
		*/

	}
}
