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

package org.apache.any23.plugin.rya;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.plugin.Author;
import org.apache.any23.plugin.Connector;
import org.apache.any23.source.DocumentSource;
import org.apache.rya.accumulo.AccumuloRdfConfiguration;
import org.apache.rya.accumulo.AccumuloRyaDAO;
import org.apache.rya.rdftriplestore.RdfCloudTripleStore;
import org.apache.rya.rdftriplestore.RyaSailRepository;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;

/**
 * Provides functionality to stream data 
 * directly into <a href="http://rya.apache.org">Apache Rya</a> using the 
 * <a href="https://github.com/apache/incubator-rya/blob/master/extras/rya.manual/src/site/markdown/loaddata.md#direct-rdf4j-api">RDF4J API</a>.
 */
@Author(name = "lewismc")
public class RyaConnector implements Connector.DataStoreConnector {

    private Repository ryaRepository;
    private RepositoryConnection conn;

    public RyaConnector() {}

    @Override
    public void init() throws IOException {
        final RdfCloudTripleStore store = new RdfCloudTripleStore();
        AccumuloRdfConfiguration conf = new AccumuloRdfConfiguration();
        AccumuloRyaDAO dao = new AccumuloRyaDAO();
        AuthenticationToken token = new PasswordToken("password");
        org.apache.accumulo.core.client.Connector connector;
        try {
          connector = new ZooKeeperInstance("instance", "zoo1,zoo2,zoo3").getConnector("user", token);
        } catch (AccumuloException | AccumuloSecurityException e) {
          throw new RuntimeException("Error connecting to Zookeeper instance(s).", e);
        }
        dao.setConnector(connector);
        conf.setTablePrefix("rya_");
        dao.setConf(conf);
        store.setRyaDAO(dao);
  
        Repository ryaRepository = new RyaSailRepository(store);
        ryaRepository.init();
        RepositoryConnection conn = ryaRepository.getConnection();
    }

    @Override
    public void stream(DocumentSource docContextIRI, OutputStream os, RDFFormat format) {
      ExtractionParameters extractionParameters = ExtractionParameters.newDefault();
      String contextIRI = extractionParameters.getProperty(ExtractionParameters.EXTRACTION_CONTEXT_IRI_PROPERTY);
      ByteArrayOutputStream baos = (ByteArrayOutputStream)os;
      try {
          conn.add(new ByteArrayInputStream(baos.toByteArray()),
                  "?".equals(contextIRI) ? docContextIRI.getDocumentIRI() : contextIRI,
                  format, new Resource[]{});
      } catch (RDFParseException | RepositoryException | IOException e) {
          e.printStackTrace();
      }
    }

    @Override
    public void close() throws IOException {
        conn.commit();
        conn.close();
        ryaRepository.shutDown();
    }

}
