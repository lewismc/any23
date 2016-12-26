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
package org.apache.any23.extractor.ifc;

import java.io.IOException;
import java.io.InputStream;

import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractionParameters;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.Extractor;
import org.apache.any23.extractor.ExtractorDescription;

import be.ugent.IfcSpfReader;

/**
 * An extractor for <a href="http://www.ifc.org">Industry Foundation Classes</a> files.
 */
public class IFCExtractor implements Extractor.ContentExtractor {

    /**
     * Default constructor for this extractor.
     */
    public IFCExtractor() {
        //default constructor
    }

    /**
     * @see org.apache.any23.extractor.Extractor#run(org.apache.any23.extractor.ExtractionParameters, org.apache.any23.extractor.ExtractionContext, java.lang.Object, org.apache.any23.extractor.ExtractionResult)
     */
    @Override
    public void run(ExtractionParameters extractionParameters, ExtractionContext context, InputStream in,
            ExtractionResult out) throws IOException, ExtractionException {
        IfcSpfReader reader = new IfcSpfReader();
        reader.logToFile = false;
        reader.convert(in, "out.ttl", reader.DEFAULT_PATH);
    }

    /**
     * @see org.apache.any23.extractor.Extractor#getDescription()
     */
    @Override
    public ExtractorDescription getDescription() {
        return IFCExtractorFactory.getDescriptionInstance();
    }

    /** 
     * @see org.apache.any23.extractor.Extractor.ContentExtractor#setStopAtFirstError(boolean)
     */
    @Override
    public void setStopAtFirstError(boolean f) {
        //unsupported
    }

}
