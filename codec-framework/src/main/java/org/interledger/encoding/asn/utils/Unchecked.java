package org.interledger.encoding.asn.utils;

public class Unchecked {
  @SuppressWarnings("unchecked")
  public static <T> T cast(Object value) {
    return (T) value;
  }
}
