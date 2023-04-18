import lucene
from lupyne import engine

if __name__ == "__main__":
    # explicitly save index in index/path
    lucene.initVM()
    searcher = engine.IndexSearcher('index/path')
    hits = searcher.search('text:query')