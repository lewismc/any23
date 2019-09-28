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
package org.apache.any23.plugin;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.any23.source.DocumentSource;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Interface provides a consistent way for Any23 to connect/stream
 * data to other services.
 */
public interface Connector {

     /**
      * Interface provides a consistent way for Any23 to connect/stream
      * data to other services e.g. a triple store.
      */
    public interface DataStoreConnector {

      /**
       * Initialize a DataStore connection.
       * @throws IOException on error establishing a connection to the DataStore
       */
      void init() throws IOException;

      /**
       * Close/shutdown a DataStore connection releasing all resources.
       * @throws IOException on error disconnecting from the DataStore
       */
      void close() throws IOException;

      /**
       * Stream Any23 data to the DataStore.
       * @param docSource a {@link org.apache.any23.source.DocumentSource} 
       * associated with the extraction OutputStream.
       * @param os the Any23 data {@link OutputStream} produced by an extraction
       * @param format the {@link org.eclipse.rdf4j.rio.RDFFormat} associated with 
       * {@link org.apache.any23.writer.TripleHandler} implementation used in the 
       * extraction.
       */
      void stream(DocumentSource docSource, OutputStream os, RDFFormat format);
    }

}
