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

import org.apache.any23.extractor.html.DomUtils;
import org.w3c.dom.Node;

/**
 * Defines an exception occurring while parsing
 * <i>AkomaNtoso</i>.
 *
 * @see AkomaNtosoParser
 */
public class AkomaNtosoParserException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String errorPath;
  private int[]  errorLocation;

  public AkomaNtosoParserException(String message, Node errorNode) {
    super(message);
    setErrorNode(errorNode);
  }

  public AkomaNtosoParserException(String message, Throwable cause, Node errorNode) {
    super(message, cause);
    setErrorNode(errorNode);
  }

  public String getErrorPath() {
    return errorPath;
  }

  public int getErrorLocationBeginRow() {
    return errorLocation == null ? -1 : errorLocation[0];
  }

  public int getErrorLocationBeginCol() {
    return errorLocation == null ? -1 : errorLocation[1];
  }

  public int getErrorLocationEndRow() {
    return errorLocation == null ? -1 : errorLocation[2];
  }

  public int getErrorLocationEndCol() {
    return errorLocation == null ? -1 : errorLocation[3];
  }

  public String toJSON() {
    return String.format(
        "{ \"message\" : \"%s\", " +
            "\"path\" : \"%s\", " +
            "\"begin_row\" : %d, \"begin_col\" : %d, " +
            "\"end_row\" : %d, \"end_col\" : %d }",
            getMessage().replaceAll("\"", ""),
            getErrorPath(),
            getErrorLocationBeginRow(),
            getErrorLocationBeginCol(),
            getErrorLocationEndRow(),
            getErrorLocationEndCol()
        );
  }

  @Override
  public String toString() {
    return toJSON();
  }

  protected void setErrorNode(Node n) {
    if(n == null) {
      errorPath     = null;
      errorLocation = null;
      return;
    }

    errorPath     = DomUtils.getXPathForNode(n);
    errorLocation = DomUtils.getNodeLocation(n);
  }

}
