package org.interledger.encoding.asn.annotations;

import org.interledger.encoding.asn.codecs.*;
import org.interledger.encoding.asn.framework.AsnObjectCodec;
import org.interledger.encoding.asn.framework.CodecContext;
import org.interledger.encoding.asn.utils.Unchecked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class OerAnnotated {
  public static <T> void registerImmutable(CodecContext context, Class<T> intf) {
    try {
      Method[] annotated = elementOrderedMethods(intf.getMethods());
      Method builder = intf.getMethod("builder");
      Object gotBuilder = builder.invoke(null);
      Class<?> builderClass = gotBuilder.getClass();
      Method from = builderClass.getMethod("from", intf);
      Method build = builderClass.getMethod("build");

      ArrayList<Supplier<AsnObjectCodec>> fields = new ArrayList<>();
      for (Method method : annotated)
        Stream.of(method.getAnnotations())
          .filter(OerAnnotated::isOer)
          .filter(annotation -> !(annotation instanceof OerElement))
          .forEach((annotation -> {
            if (annotation instanceof OerIA5String) {
              int fixedWidth = ((OerIA5String) annotation).fixedWidth();
              AsnSizeConstraint unconstrained = constraint(fixedWidth);
              fields.add(() -> new AsnIA5StringCodec(unconstrained));
            } else if (annotation instanceof OerOctetString) {
              int fixedWidth = ((OerOctetString) annotation).fixedWidth();
              fields.add(() -> new AsnOctetStringCodec(constraint(fixedWidth)));
            } else if (annotation instanceof OerUtf8String) {
              int fixedWidth = ((OerUtf8String) annotation).fixedWidth();
              fields.add(() -> new AsnUtf8StringCodec(constraint(fixedWidth)));
            } else if (annotation instanceof OerUint8) {
              fields.add(AsnUint8Codec::new);
            } else if (annotation instanceof OerUint32) {
              fields.add(AsnUint32Codec::new);
            } else if (annotation instanceof OerUint64) {
              fields.add(AsnUint64Codec::new);
            } else if (annotation instanceof OerUint) {
              fields.add(AsnUintCodec::new);
            } else if (annotation instanceof OerRegistered) {
              // Cache this for performance
              // TODO: circular references ?
              AsnObjectCodec<?> asnObjectCodec =
                context.codecForType(method.getReturnType());
              fields.add(() -> asnObjectCodec);
            }
          }));

      context.register(
        intf,
        () ->
        {
          AsnObjectCodec[] array = new AsnObjectCodec[fields.size()];
          for (int i = 0; i < array.length; i++) {
            array[i] = fields.get(i).get();
          }
          return new PrivateSequenceCodec<>(
            array,
            annotated,
            intf)
            .setBuilderMethods(from, build, builder);
        });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  private static AsnSizeConstraint constraint(int fixedWidth) {
    return fixedWidth == -1 ?
      AsnSizeConstraint.UNCONSTRAINED :
      new AsnSizeConstraint(fixedWidth);
  }

  private static Method[] elementOrderedMethods(Method[] methods) {
    TreeMap<Integer, Method> annotated = new TreeMap<>();

    for (Method method : methods) {
      Annotation[] annotations = method.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof OerElement) {
          OerElement elementAnnotation = (OerElement) annotation;
          annotated.put(elementAnnotation.value(), method);
        }
      }
    }
    return annotated.values().toArray(new Method[0]);
  }

  private static boolean isOer(Annotation annotation) {
    return annotation.annotationType()
      .isAnnotationPresent(OerAnnotation.class);
  }

}

class PrivateSequenceCodec<T> extends AsnSequenceCodec<T> {
  private Method[] methods;
  private Class<T> kls;

  private Method builderFrom;
  private Method builderBuild;
  private Method builderFactory;

  PrivateSequenceCodec<T> setBuilderMethods(Method builderFrom, Method builderBuild, Method builder) {
    this.builderFactory = builder;
    this.builderBuild = builderBuild;
    this.builderFrom = builderFrom;
    return this;
  }

  PrivateSequenceCodec(AsnObjectCodec[] fields, Method[] methods, Class<T> kls) {
    super(fields);
    this.methods = methods;
    this.kls = kls;
  }

  @Override
  public T decode() {
    HashMap<Method, Object> hash = new HashMap<>();
    for (int i = 0; i < methods.length; i++) {
      Object valueAt = getValueAt(i);
      hash.put(methods[i], valueAt);
    }
    T t = Unchecked.cast(Proxy.newProxyInstance(
      ClassLoader.getSystemClassLoader(), new Class[]{
        kls
      }, (proxy, method, args) -> hash.get(method)));

    try {
      Object builderObject = builderFactory.invoke(null);
      Object built = builderFrom.invoke(builderObject, t);
      return Unchecked.cast(builderBuild.invoke(built));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void encode(T value) {
    int i = 0;
    for (Method method : methods) {
      try {
        Object val = method.invoke(value);
        setValueAt(i, val);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      i++;
    }
  }
}
