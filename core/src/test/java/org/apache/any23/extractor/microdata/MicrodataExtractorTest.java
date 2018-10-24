/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.extractor.microdata;

import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorFactory;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.html.AbstractExtractorTestCase;
import org.apache.any23.rdf.RDFUtils;
import org.apache.any23.vocab.SINDICE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reference test class for {@link MicrodataExtractor}.
 *
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 */
public class MicrodataExtractorTest extends AbstractExtractorTestCase {

    private static final Logger logger = LoggerFactory.getLogger(MicrodataExtractorTest.class);

    @Override
    protected ExtractorFactory<?> getExtractorFactory() {
        return new MicrodataExtractorFactory();
    }

    /**
     * Reference test for <a href="http://schema.org">Schema.org</a>.
     *
     * @throws RepositoryException
     * @throws RDFHandlerException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testSchemaOrgNestedProps()
            throws RepositoryException, RDFHandlerException, IOException, RDFParseException, ExtractionException {
        extractAndVerifyAgainstNQuads(
                "microdata-nested.html",
                "microdata-nested-expected.nquads"
        );
        logger.debug(dumpModelToNQuads());
    }

    @Test
    public void testExample2() {
        //Property URI generation for hcard
        assertExtract("/microdata/example2.html");
        assertContains(null, RDF.TYPE, RDFUtils.iri("http://microformats.org/profile/hcard"));
        assertContains(null, RDFUtils.iri("http://microformats.org/profile/hcard#given-name"), (Value)null);
        assertContains(null, RDFUtils.iri("http://microformats.org/profile/hcard#n"), (Value)null);
    }

    @Test
    public void testExample5() {
        //Vocabulary expansion for schema.org
        assertExtract("/microdata/example5.html");
        assertContains(null, RDF.TYPE, RDFUtils.iri("http://schema.org/Person"));
        assertContains(null, RDF.TYPE, RDFUtils.iri("http://xmlns.com/foaf/0.1/Person"));
        assertContains(null, RDFUtils.iri("http://schema.org/additionalType"), RDFUtils.iri("http://xmlns.com/foaf/0.1/Person"));
        assertContains(null, RDFUtils.iri("http://schema.org/email"), RDFUtils.iri("mailto:mail@gmail.com"));
        assertContains(null, RDFUtils.iri("http://xmlns.com/foaf/0.1/mbox"), RDFUtils.iri("mailto:mail@gmail.com"));
    }

    @Test
    public void testMicrodataBasic() {
        assertExtract("/microdata/microdata-basic.html");
        assertModelNotEmpty();
        assertStatementsSize(null, null, null, 40);
        assertStatementsSize(RDFUtils.iri("urn:isbn:0-330-34032-8"), null, null, 4);
    }

    @Test
    public void testMicrodataMissingScheme() {
        assertExtract("/microdata/microdata-missing-scheme.html");
        assertModelNotEmpty();
        assertContains(null, RDF.TYPE, RDFUtils.iri("http://schema.org/Answer"));
    }

    /**
     * Reference test as provided by <a href="http://googlewebmastercentral.blogspot.com/2010/03/microdata-support-for-rich-snippets.html">Google Rich Snippet for Microdata.</a>
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testMicrodataGoogleRichSnippet()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "microdata-richsnippet.html",
                "microdata-richsnippet-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * First reference test  for <a href="http://www.w3.org/TR/microdata/">Microdata Extraction algorithm</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExample5221()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "5.2.1-non-normative-example-1.html",
                "5.2.1-non-normative-example-1-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * Second reference test  for <a href="http://www.w3.org/TR/microdata/">Microdata Extraction algorithm</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExample5222()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "5.2.1-non-normative-example-1.html",
                "5.2.1-non-normative-example-1-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * First reference test for <a href="http://schema.org/">http://schema.org/</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExampleSchemaOrg1()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "schemaorg-example-1.html",
                "schemaorg-example-1-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    /**
     * Second reference test for <a href="http://schema.org/">http://schema.org/</a>.
     *
     * @throws RDFHandlerException
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Test
    public void testExampleSchemaOrg2()
            throws RDFHandlerException, RepositoryException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(
                "schemaorg-example-2.html",
                "schemaorg-example-2-expected.nquads"
        );
        logger.debug(dumpHumanReadableTriples());
    }

    @Test
    public void testMicrodataNestedUrlResolving() throws IOException {
        IRI oldBaseIRI = baseIRI;
        try {
            baseIRI = RDFUtils.iri("https://ruben.verborgh.org/tmp/schemaorg-test.html");
            extractAndVerifyAgainstNQuads("microdata-nested-url-resolving.html",
                    "microdata-nested-url-resolving-expected.nquads");
        } finally {
            baseIRI = oldBaseIRI;
        }
    }

    @Test
    public void testTel() {
        assertExtract("/microdata/tel-test.html");
        assertModelNotEmpty();
        assertContains(RDFUtils.iri("http://schema.org/telephone"), RDFUtils.iri("tel:(909)%20484-2020"));
    }

    @Test
    public void testBadTypes() throws IOException {
        extractAndVerifyAgainstNQuads("microdata-bad-types.html", "microdata-bad-types-expected.nquads");
    }

    @Test
    public void testBadPropertyNames() throws IOException {
        extractAndVerifyAgainstNQuads("microdata-bad-properties.html", "microdata-bad-properties-expected.nquads", false);
        assertIssue(IssueReport.IssueLevel.ERROR, ".*invalid property name ''.*\"path\" : \"/HTML\\[1\\]/BODY\\[1\\]/DIV\\[1\\]/DIV\\[2\\]/DIV\\[1\\]\".*");
    }

    private void extractAndVerifyAgainstNQuads(String actual, String expected)
            throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        extractAndVerifyAgainstNQuads(actual, expected, true);
    }

    private void extractAndVerifyAgainstNQuads(String actual, String expected, boolean assertNoIssues)
    throws RepositoryException, RDFHandlerException, IOException, RDFParseException {
        assertExtract("/microdata/" + actual, assertNoIssues);
        assertModelNotEmpty();
        logger.debug( dumpModelToNQuads() );
        List<Statement> expectedStatements = loadResultStatement("/microdata/" + expected);
        int actualStmtSize = getStatementsSize(null, null, null);
        Assert.assertEquals( expectedStatements.size(), actualStmtSize);
        for (Statement statement : expectedStatements) {
            if (!statement.getPredicate().equals(SINDICE.getInstance().date)) {
                assertContains(
                        statement.getSubject() instanceof BNode ? null : statement.getSubject(),
                        statement.getPredicate(),
                        statement.getObject() instanceof BNode ? null : statement.getObject()
                );
            }
        }
    }

    private List<Statement> loadResultStatement(String resultFilePath)
            throws RDFHandlerException, IOException, RDFParseException {
        RDFParser nQuadsParser = Rio.createParser(RDFFormat.NQUADS);
        TestRDFHandler rdfHandler = new TestRDFHandler();
        nQuadsParser.setRDFHandler(rdfHandler);
        File file = copyResourceToTempFile(resultFilePath);
        nQuadsParser.parse(
                new FileReader(file),
                baseIRI.toString()
        );
        return rdfHandler.getStatements();
    }

    public static class TestRDFHandler implements RDFHandler {

        private final List<Statement> statements = new ArrayList<Statement>();

        protected List<Statement> getStatements() {
            return statements;
        }

        public void startRDF() throws RDFHandlerException {
        }

        public void endRDF() throws RDFHandlerException {
        }

        public void handleNamespace(String s, String s1) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }

        public void handleStatement(Statement statement) throws RDFHandlerException {
            statements.add(statement);
        }

        public void handleComment(String s) throws RDFHandlerException {
            throw new UnsupportedOperationException();
        }
    }

}
