# UCSC CSE272 code sample skeleton

## Home assignment 1

You can use Java Lucene or PyLucene to finish your homework 1. 

### Java Lucene shown in subfolder [HW1/java](HW1/java)

This subfolder use gradle for compiling code and you can directly launch via Intellj.
  
### PyLuence show in subfolder [HW1/python](HW1/python)

1, Install PyLuence that depends on Java Interface, refer to [Install JCC](https://lucene.apache.org/pylucene/jcc/install.html) and [Install PyLuence](https://michaelaalcorn.medium.com/how-to-use-pylucene-e2e2f540024c)

```bash
tar -xvf pylucene-9.4.1-src.tar.gz
cd jcc
# install JCC
python setup.py build
python setup.py install
cd ..
```

Revise Makefile as follow:

```bash
# Linux     (Debian Jessie 64-bit, Python 3.4.2, Oracle Java 1.8
# Be sure to also set JDK['linux'] in jcc's setup.py to the JAVA_HOME value
# used below for ANT (and rebuild jcc after changing it).
PREFIX_PYTHON=<your python path>
ANT=JAVA_HOME=<your java path> <your ant path>
PYTHON=$(PREFIX_PYTHON)/bin/python3
JCC=$(PYTHON) -m jcc --shared
NUM_FILES=8
```

Install pyLuence:

```bash
make
make test
make install
```
