/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.index;

import java.io.IOException;
import java.util.Objects;
import org.apache.lucene.util.BytesRef;

/** Delegates all methods to a wrapped {@link VectorValues}. */
public abstract class FilterVectorValues extends VectorValues {

  /** Wrapped values */
  protected final VectorValues in;

  /** Sole constructor */
  protected FilterVectorValues(VectorValues in) {
    Objects.requireNonNull(in);
    this.in = in;
  }

  @Override
  public int docID() {
    return in.docID();
  }

  @Override
  public int nextDoc() throws IOException {
    return in.nextDoc();
  }

  @Override
  public int advance(int target) throws IOException {
    return in.advance(target);
  }

  @Override
  public long cost() {
    return in.cost();
  }

  @Override
  public int dimension() {
    return in.dimension();
  }

  @Override
  public int size() {
    return in.size();
  }

  @Override
  public float[] vectorValue() throws IOException {
    return in.vectorValue();
  }

  @Override
  public BytesRef binaryValue() throws IOException {
    return in.binaryValue();
  }
}
