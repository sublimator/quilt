package org.interledger.encoding.asn.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@OerAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface OerUtf8String {
  int fixedWidth() default -1;
}
