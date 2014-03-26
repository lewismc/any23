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

package org.apache.any23.io.akn;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.akomantoso.api.AnVersion;
import org.akomantoso.schema.v3.csd08.AkomaNtosoType;
import org.w3c.dom.Document;

/**
 * <i>Akoma Ntoso</i> parser implementation.
 * See the format specification <a href="http://sw.deri.org/2008/07/n-quads/">here</a>.
 *
 */
public class AkomaNtosoParser {

  enum ErrorMode {
    /** This mode raises an exception at first encountered error. */
    StopAtFirstError,
    /**  This mode produces a full error report. */
    FullReport
  }

  private final Document document;

  /**
   * Public constructor for this parser.
   * @param doc input {@link org.w3c.dom.Document} which should represent
   * an entire HTML or XML document.
   */
  public AkomaNtosoParser(Document doc) {
    if (doc == null) {
      throw new NullPointerException("Document cannot be 'null'.");
    }
    this.document = doc;
  }

  public static AkomaNtosoParserReport getEmbeddedStructure(Document in) throws FileNotFoundException, JAXBException {
    try {
      return getEmbeddedStructure(in, ErrorMode.FullReport);
    } catch (AkomaNtosoParserException anpe) {
      throw new IllegalStateException("Unexpected exception.", anpe);
    }
    
  }

  private static AkomaNtosoParserReport getEmbeddedStructure(Document doc, ErrorMode fullreport) 
    throws AkomaNtosoParserException, JAXBException, FileNotFoundException {
    AnVersion vSchema = new AnVersion(3, "CSD08");
    JAXBContext cxt = vSchema.getContext();
    Unmarshaller unmarshaller = cxt.createUnmarshaller();
    JAXBElement<AkomaNtosoType> anType = 
        (JAXBElement<AkomaNtosoType>)unmarshaller.unmarshal(doc);
    return null;
  }

}
