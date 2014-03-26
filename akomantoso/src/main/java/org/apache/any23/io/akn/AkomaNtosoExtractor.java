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

import java.io.IOException;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.any23.extractor.IssueReport;
import org.apache.any23.extractor.microdata.ItemScope;
import org.apache.any23.rdf.RDFUtils;
import org.openrdf.model.URI;
import org.w3c.dom.Document;

/**
 * Extractor for the <a href="http://www.akomtantoso.org">Akoma Ntoso</a>
 * XML Format based on {@link org.apache.any23.extractor.Extractor.TagSoupDOMExtractor}
 * which handles {@link org.w3c.dom.Document} as input format. 
 * 
 * @author lewismc
 *
 */
public class AkomaNtosoExtractor implements Extractor.TagSoupDOMExtractor{

  private static final URI AKOMANTOSO_ITEM
  = RDFUtils.uri("http://docs.oasis-open.org/legaldocml/ns/akn/3.0/CSD08");

  private String documentLanguage;

  private boolean isStrict;

  private String defaultNamespace;

  @Override
  public void run(ExtractionParameters extractionParameters, ExtractionContext context, Document in,
      ExtractionResult out) throws IOException, ExtractionException {

    final AkomaNtosoParserReport parserReport = AkomaNtosoParser.getEmbeddedStructure(in);
    if (parserReport.getErrors().length > 0) {
      notifyError(parserReport.getErrors(), out);
    }
    final ItemScope[] itemScopes = parserReport.getDetectedItemScopes();
    if (itemScopes.length == 0) {
        return;
    }
  }

  private void notifyError(AkomaNtosoParserException[] errors, ExtractionResult out) {
    for(AkomaNtosoParserException anpe : errors) {
      out.notifyIssue(
          IssueReport.IssueLevel.Error,
          anpe.toJSON(),
          anpe.getErrorLocationBeginRow(),
          anpe.getErrorLocationBeginCol()
          );
    }
  }

  @Override
  public ExtractorDescription getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

}
