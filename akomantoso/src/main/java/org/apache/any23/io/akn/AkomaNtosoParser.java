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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.akomantoso.api.AnVersion;
import org.akomantoso.schema.v3.csd08.AkomaNtosoType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <i>Akoma Ntoso</i> parser implementation based on the 
 * <a href="https://github.com/kohsah/akomantoso-lib">akomantoso-lib Java API</a>.
 * 
 * @author lewismc
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

  public static void main (String[] args) throws SAXException, IOException, AkomaNtosoParserException, JAXBException{
    File inputFile = new File(args[0]);
    javax.xml.parsers.DocumentBuilderFactory factory =
        javax.xml.parsers.DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    javax.xml.parsers.DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    }
    catch (javax.xml.parsers.ParserConfigurationException ex) {
    }  
    org.w3c.dom.Document doc = builder.parse(inputFile);
    AkomaNtosoParser.getEmbeddedStructure(doc, "CSD08");
  }

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

  /**
   * Public method enabling us to specify the Akoma Ntoso XML 
   * {@link org.w3c.dom.Document} we wish to process along with 
   * the Akoma Ntoso schema version.
   * The Schema version is important as it determines how the Akoma Ntoso
   * Java API interprets the document structure, elements and attributes.
   * 
   * @param in the Akoma Ntoso XML {@link org.w3c.dom.Document}
   * @param schema a String containing the Akoma Ntoso schema version for processing. 
   * This is set to <b>CSD08</b> within the <code>any23.extraction.akn.version</code> 
   * configuration property by default.
   * @return all of the Akoma Ntoso embedded structure we can then convert to triples.
   * @throws AkomaNtosoParserException 
   * @throws FileNotFoundException
   * @throws JAXBException
   */
  public static AkomaNtosoParserReport getEmbeddedStructure(Document in, String schema) throws FileNotFoundException, AkomaNtosoParserException, JAXBException {
    return getEmbeddedStructure(in, schema, ErrorMode.FullReport);
  }

  private static AkomaNtosoParserReport getEmbeddedStructure(Document doc, String schema, ErrorMode fullreport) 
      throws AkomaNtosoParserException, JAXBException, FileNotFoundException {
    AnVersion vSchema = new AnVersion(3, schema);
    JAXBContext cxt = vSchema.getContext();
    Unmarshaller unmarshaller = cxt.createUnmarshaller();
    JAXBElement<AkomaNtosoType> anType = 
        (JAXBElement<AkomaNtosoType>)unmarshaller.unmarshal(doc);
    AkomaNtosoType aknType = anType.getValue();
    System.out.println(aknType.getAct().getName());
    System.out.println(aknType.getAct().getContains().toString());
    return null;
  }

}
