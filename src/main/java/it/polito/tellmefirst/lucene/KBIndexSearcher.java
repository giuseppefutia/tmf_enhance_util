/*-
 * Copyright (C) 2012, 2013, 2014, 2015 Federico Cairo, Giuseppe Futia, Federico Benedetto.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polito.tellmefirst.lucene;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KBIndexSearcher {

    static Log LOG = LogFactory.getLog(KBIndexSearcher.class);

    private String kb;
    private String residualKb;

    /**
     * Initialize the KB and the ResidualKB Indexes to get the related entities of a specific URI.
     *
     * @param kbPath Path of the KB Lucene Index
     * @param residualKbPath Path of the ResidualKB Lucene Index
     *
     * @since 3.0.0.0.
     */
    public KBIndexSearcher(String kbPath, String residualKbPath) {
        kb = kbPath;
        residualKb = residualKbPath;

    }

    /**
     * Get DBpedia concepts related to a specific URI from the Lucene Index. These DBpedia concepts appear as wikilink
     * more than once in the Wikipedia page identified by the URI.
     *
     * @param uri Input URI.
     *
     * In the previous versions of TellMeFirst, the getBagOfConcepts method take as input the
     * URI of a DBpedia resource (String) and the language parameter (String). We have decide to
     * modify the API in order to separate this module from the core of TellMeFirst.
     *
     * @since 3.0.0.0.
     */
    public List<String> getBagOfConcepts(String uri) throws Exception {
        LOG.debug("[getBagOfConcepts]  BEGIN");
        List<String> result = new ArrayList<String>();

        try{
            MMapDirectory directory = new MMapDirectory(new File(kb));
            IndexReader reader = IndexReader.open(directory, true);
            IndexSearcher is = new IndexSearcher(directory, true);
            Query q = new TermQuery(new Term("URI", uri));
            TopDocs hits = is.search(q, 1);
            is.close();
            if (hits.totalHits != 0) {
                int docId = hits.scoreDocs[0].doc;
                org.apache.lucene.document.Document doc = reader.document(docId);
                String wikilinksMerged = doc.getField("KB").stringValue();
                String[] wikiSplits = wikilinksMerged.split(" ");
                LOG.debug("Bag of concepts for the resource " + uri + ": ");
                for (String s : wikiSplits) {
                    result.add(s);
                    LOG.debug("* "+s);
                }
            }
            reader.close();
        } catch (Exception e){
            LOG.error("[getBagOfConcepts]  EXCEPTION: ", e);
            throw new Exception(e);
        }
        LOG.debug("[getBagOfConcepts]  END");
        return result;
    }

    /**
     * Get DBpedia concepts related to a specific URI from the Lucene Index. These DBpedia concepts appear as wikilink
     * once in the Wikipedia page identified by the URI.
     *
     * @param uri Input URI.
     *
     * In the previous versions of TellMeFirst, the getResidualBagOfConcepts method take as input the
     * URI of a DBpedia resource (String) and the language parameter (String). We have decide to
     * modify the API in order to separate this module from the core of TellMeFirst.
     *
     * @since 3.0.0.0.
     */
    public ArrayList<String> getResidualBagOfConcepts(String uri) {
        LOG.debug("[getResidualBagOfConcepts] - BEGIN");
        ArrayList<String> result = new ArrayList<String>();
        try{
            MMapDirectory directory = new MMapDirectory(new File(residualKb));
            IndexReader reader = IndexReader.open(directory, true);
            IndexSearcher is = new IndexSearcher(directory,true);
            Query q = new TermQuery(new Term("URI", uri));
            TopDocs hits = is.search(q, 1);
            is.close();
            if (hits.totalHits != 0) {
                int docId = hits.scoreDocs[0].doc;
                org.apache.lucene.document.Document doc = reader.document(docId);
                String wikilinksMerged = doc.getField("KB").stringValue();
                String[] wikiSplits = wikilinksMerged.split(" ");
                //no prod
                LOG.debug("Residual bag of concepts for the resource " + uri + ": ");
                for (String s : wikiSplits) {
                    result.add(s);
                    //no prod
                    LOG.debug("* "+s);
                }
            }
            reader.close();
        }catch (Exception e){
            LOG.error("[getResidualBagOfConcepts] - EXCEPTION: ", e);
        }
        LOG.debug("[getResidualBagOfConcepts] - END");
        return result;
    }
}
