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

import org.apache.any23.io.akn.AkomaNtosoParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test case for {@link AkomaNtosoParser}.
 */
public class AkomaNtosoParserTest {

    //private static final Logger logger = LoggerFactory.getLogger(AkomaNtosoParser.class);

    private AkomaNtosoParser parser;

    @Before
    public void setUp() {;
        
    }

    @After
    public void tearDown() {
        parser = null;
    }

    /**
     * Tests the correct behavior with incomplete input.
     *
     */
    @Ignore
    @Test
    public void testIncompleteParsing() {
    }

    /**
     * Tests parsing of empty lines and comments.
     *
     */
    @Ignore
    @Test
    public void testParseEmptyLinesAndComments() {
    }

    /**
     * Tests basic AkomaNtoso parsing.
     *
     */
    @Ignore
    @Test
    public void testParseBasic() {
    }

    /**
     * Tests basic AkomaNtoso parsing with blank node.
     */
    @Ignore
    @Test
    public void testParseBasicBNode() {
    }

}