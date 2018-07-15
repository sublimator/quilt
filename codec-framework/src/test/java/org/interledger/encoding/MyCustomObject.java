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
import org.interledger.encoding.asn.annotations.*;

import java.math.BigInteger;

public interface MyCustomObject {
  static MyCustomObjectBuilder builder() {
    return new MyCustomObjectBuilder();
  }

  @OerElement(0)
  @OerUtf8String()
  String getUtf8StringProperty();

  /**
   * Fixed size of 4 chars.
   */
  @OerElement(1)
  @OerUtf8String(fixedWidth = 4)
  String getFixedLengthUtf8StringProperty();

  @OerElement(2)
  @OerUint8
  int getUint8Property();

  @OerElement(3)
  @OerUint32
  long getUint32Property();

  @OerElement(4)
  @OerUint64
  BigInteger getUint64Property();

  @OerElement(5)
  @OerOctetString
  byte[] getOctetStringProperty();

  @OerElement(6)
  @OerUint
  BigInteger getUintProperty();

  /**
   * Fixed size of 32 bytes.
   */
  @OerElement(7)
  @OerOctetString(fixedWidth = 32)
  byte[] getFixedLengthOctetStringProperty();

  @OerElement(8)
  @OerRegistered()
  NestedObject custom();

  @Immutable
  abstract class AbstractMyCustomObject implements MyCustomObject {

  }

}
