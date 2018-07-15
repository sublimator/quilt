package org.interledger.encoding;

/*-
 * ========================LICENSE_START=================================
 * Interledger Codec Framework
 * %%
 * Copyright (C) 2017 - 2018 Hyperledger and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import org.interledger.annotations.Immutable;
import org.interledger.encoding.asn.annotations.OerElement;
import org.interledger.encoding.asn.annotations.OerUint32;
import org.interledger.encoding.asn.annotations.OerUint8;
import org.interledger.encoding.asn.annotations.OerUtf8String;

public interface NestedObject {
  static NestedObjectBuilder builder() {
    return new NestedObjectBuilder();
  }

  @OerElement(0)
  @OerUint32()
  Long id();

  @OerElement(1)
  @OerUtf8String()
  String name();

  @OerElement(2)
  @OerUint8
  int code();

  @Immutable
  abstract class AbstractNestedObject implements NestedObject {

  }
}
