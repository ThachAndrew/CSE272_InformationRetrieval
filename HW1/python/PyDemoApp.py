import lucene
from java.io import StringReader
from org.apache.lucene.analysis.ja import JapaneseAnalyzer
from org.apache.lucene.analysis.standard import StandardAnalyzer, StandardTokenizer
from org.apache.lucene.analysis.tokenattributes import CharTermAttribute
from org.apache.lucene.index import IndexWriterConfig, IndexReader, IndexWriter, DirectoryReader
from org.apache.lucene.store import ByteBuffersDirectory
from org.apache.lucene.document import Document, Field, StringField, TextField
from org.apache.lucene.queryparser.classic import QueryParser
from org.apache.lucene.search import IndexSearcher

def addDoc(w: IndexWriter, title: str, isbn: str):
    doc = Document()
    doc.add(TextField("title", title, Field.Store.YES))
    doc.add(StringField("isbn", isbn, Field.Store.YES))
    w.addDocument(doc)

if __name__ == "__main__":
    lucene.initVM(vmargs=['-Djava.awt.headless=true'])
    analyzer = StandardAnalyzer()
    index = ByteBuffersDirectory()
    config = IndexWriterConfig(analyzer)

    w = IndexWriter(index, config)
    addDoc(w, "Lucene in Action", "193398817")
    addDoc(w, "Lucene for Dummies", "55320055Z")
    addDoc(w, "Managing Gigabytes", "55063554A")
    addDoc(w, "The Art of Computer Science", "9900333X")
    w.close()

    querystr = "lucene"
    q = QueryParser("title", analyzer).parse(querystr)
    hitsPerPage = 10
    reader = DirectoryReader.open(index)
    searcher = IndexSearcher(reader)
    docs = searcher.search(q, hitsPerPage)
    hits = docs.scoreDocs

    print(f"Found {len(hits)} hits.")
    for  i in range(len(hits)):
        docId = hits[i].doc
        d = searcher.getIndexReader().document(docId)
        print(f"{i + 1}. {d.get('isbn')}\t {d.get('title')}")