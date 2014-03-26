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

/**
 * This class describes the report of the {@link AkomaNtosoParser}.
 * A generated report contains the detected ...
 *
 */
public class AkomaNtosoParserReport {

    private static final AkomaNtosoParserException[] NO_ERRORS = new AkomaNtosoParserException[0];

    private final ItemScope[] detectedItemScopes;

    private final AkomaNtosoParserException[] errors;

    public AkomaNtosoParserReport(ItemScope[] detectedItemScopes, AkomaNtosoParserException[] errors) {
        if(detectedItemScopes == null) {
            throw new NullPointerException("detected item scopes list cannot be null.");
        }
        this.detectedItemScopes = detectedItemScopes;
        this.errors = errors == null ? NO_ERRORS : errors;
    }

    public AkomaNtosoParserReport(ItemScope[] detectedItemScopes) {
        this(detectedItemScopes, null);
    }

    public ItemScope[] getDetectedItemScopes() {
        return detectedItemScopes;
    }

    public AkomaNtosoParserException[] getErrors() {
        return errors;
    }

}
