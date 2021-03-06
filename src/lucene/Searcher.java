package lucene;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import util.Variables;

public class Searcher {
  private static final Logger logger = LogManager.getLogger(Searcher.class);
  private Searcher() {}
  private static Searcher instance = null;
  public static Searcher getInstance() {
    if ( instance == null ) {
      synchronized (Searcher.class) {
        if ( instance == null )
          instance = new Searcher();
      }
    }
    return instance;
  }

  public static void main(String[] args) throws IOException, ParseException {
    final IndexReader reader = DirectoryReader.open(FSDirectory.open(
        new File(Variables.getInstance().getProperties().getProperty("indexPath"))));
    final IndexSearcher searcher = new IndexSearcher(reader);

    final int start = 10;
    final int limit = 20;
    System.out.println(Searcher.getInstance().hot(searcher, null, start, limit));

    reader.close();
  }

  public JSONObject hot(IndexSearcher searcher, String fileType,
      int start, int limit) throws IOException {
    logger.entry(fileType, start, limit);
    final TopDocs tops = searcher.search(QueryParser.getInstance().parseHot(fileType), start + limit,
        new Sort(new SortField("size", SortField.Type.LONG, true)));
    return makeup(searcher, tops, start, limit);
  }

  public JSONObject search(IndexSearcher searcher, Query query,
        int start, int limit) throws IOException {
    logger.entry(query, start, limit);
    final TopDocs tops = searcher.search(query, start + limit);
    return makeup(searcher, tops, start, limit);
  }

  private static JSONObject makeup(IndexSearcher searcher, TopDocs tops,
      int start, int limit) throws IOException {
    final JSONObject res = new JSONObject();
    final ScoreDoc[] hits = tops.scoreDocs;
    final int totalHits = tops.totalHits;
    res.put("totalNum", totalHits);
    logger.info("totalNum=" + totalHits);
    JSONArray list = new JSONArray();
    for (int i = start; i < start + limit && i < hits.length; i ++) {
      final JSONObject file = new JSONObject();
      final Document doc = searcher.doc(hits[i].doc);
      file.put("name", new String(doc.get("name").getBytes("utf8"), "utf8"));
      file.put("url", doc.get("url"));
      file.put("size", doc.get("storedSize"));
      file.put("md5", doc.get("md5"));
      file.put("download", doc.get("storedDownload"));
      list.add(file);
    }
    res.put("filesList", list);
    logger.exit(res.toString().substring(0, Math.min(res.toString().length(), 100)) + "...");
    return res;
  }
}
