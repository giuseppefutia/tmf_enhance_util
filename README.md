# TellMeFirst Enhance Util

This repository contains useful functions for using
[TellMeFirst](https://github.com/TellMeFirst/TellMeFirst).

TellMeFirst is a tool for classifying and enriching
textual documents via Linked Open Data.
It uses [Lucene](http://lucene.apache.org/core/) indexes
for its classification and enrichment system. To build such
indexes use our [fork of the DBpedia Spotlight
project](https://github.com/TellMeFirst/dbpedia-spotlight/tree/tellmefirst).

This module is a low level component that directly interacts
with Lucene in order to retrieve concepts related to a specific
URI and support the enhancement stage of TellMeFist pipeline.

Use the API exported by this module as follows. 

##API Usage
	
	// Initialize the KB and the ResidualKB Indexes to get the related entities of a specific URI.
    
    // kbPath is the path of the KB Lucene Index
    // residualKbPath Path of the ResidualKB Lucene Index

    String uri = "Barack_Obama"
	KBIndexSearcher kbis = new KBIndexSearcher(kbPath, residualKbPath);
	List<String> kbUriResults = getBagOfConcepts(uri);
	List<String> residualUriResults = getResidualBagOfConcepts(uri);


