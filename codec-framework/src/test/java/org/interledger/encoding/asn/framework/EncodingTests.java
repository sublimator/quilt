package org.interledger.encoding.asn.framework;

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

import org.interledger.encoding.MyCustomObject;
import org.interledger.encoding.asn.AsnMyCustomObjectCodec;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EncodingTests {
  private CodecContext context;

  private CodecContext customContext() {
    return CodecContextFactory.getContext(CodecContextFactory.OCTET_ENCODING_RULES)
      .register(MyCustomObject.class, AsnMyCustomObjectCodec::new);
  }

  @Before
  public void setUp() {
    context = customContext();
  }

  private MyCustomObject roundTripChecked(MyCustomObject obj) throws IOException {
    ByteArrayOutputStream baOs = new ByteArrayOutputStream();
    context.write(obj, baOs);
    byte[] dehydrated = baOs.toByteArray();

    ByteArrayInputStream baIs = new ByteArrayInputStream(dehydrated);
    MyCustomObject revived = context.read(MyCustomObject.class, baIs);
    assertThat(revived, is(obj));

    return revived;
  }

  @Test
  public void test1() throws Exception {
    MyCustomObject obj = MyCustomObject.builder()
      .utf8StringProperty("Hello")
      .fixedLengthUtf8StringProperty("1234")
      .uint8Property(255)
      .uint32Property(1234567L)
      .uint64Property(BigInteger.ZERO)
      .octetStringProperty()
      .fixedLengthOctetStringProperty(
        new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
          0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
          0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
          0, 1,})
      .uintProperty(BigInteger.TEN)
      .build();

    MyCustomObject copy = roundTripChecked(obj);

    assertThat(copy.toString(), is(
      "MyCustomObject{utf8StringProperty=Hello, " +
        "fixedLengthUtf8StringProperty=1234, " +
        "uint8Property=255, " +
        "uint32Property=1234567, " +
        "uint64Property=0, " +
        "octetStringProperty=[], " +
        "uintProperty=10, " +
        "fixedLengthOctetStringProperty=[" +
        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, " +
        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, " +
        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, " +
        "0, 1]}"));

  }


  @Test
  public void test2() throws Exception {
    MyCustomObject obj = MyCustomObject.builder()
      .utf8StringProperty("World")
      .fixedLengthUtf8StringProperty("ABCD")
      .uint8Property(1)
      .uint32Property(1234567L)
      .uint64Property(BigInteger.ZERO)
      .octetStringProperty(new byte[]{0, 1, 2, 4})
      .fixedLengthOctetStringProperty(
        new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
          0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
          0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
          0, 1,})
      .uintProperty(BigInteger.ZERO)
      .build();

    MyCustomObject copy = roundTripChecked(obj);

    assertThat(copy.toString(), is(
      "MyCustomObject{utf8StringProperty=World, " +
        "fixedLengthUtf8StringProperty=ABCD, " +
        "uint8Property=1, " +
        "uint32Property=1234567, " +
        "uint64Property=0, " +
        "octetStringProperty=[0, 1, 2, 4], " +
        "uintProperty=0, " +
        "fixedLengthOctetStringProperty=[" +
        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, " +
        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, " +
        "0, 1, 2, 3, 4, 5, 6, 7, 8, 9, " +
        "0, 1]}"));
  }

}
