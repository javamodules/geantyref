/*
 * License: Apache License, Version 2.0
 * See the LICENSE file in the root directory or at <a href="http://www.apache.org/licenses/LICENSE-2">apache.org</a>.
 */

package io.leangen.geantyref;

import junit.framework.TestCase;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import static io.leangen.geantyref.Annotations.A1;
import static io.leangen.geantyref.Annotations.A2;
import static io.leangen.geantyref.Annotations.A3;
import static io.leangen.geantyref.Annotations.A4;
import static io.leangen.geantyref.Annotations.A5;
import static org.junit.Assert.assertArrayEquals;

/**
 * <a href="https://github.com/leangen/geantyref/issues/20">Issue 20</a>
 */
public class Issue20Test extends TestCase {

    static class TestClass {

        public UnitX<@A1 ?> returnType() {
            return null;
        }
    }

    public void testRecursiveTypes() throws Exception {
        AnnotatedType type = GenericTypeReflector.reduceBounded(
                GenericTypeReflector.getExactReturnType(TestClass.class.getMethod("returnType"),
                        GenericTypeReflector.annotate(TestClass.class)));

        assertNotNull(type);
        assertTrue(type instanceof AnnotatedParameterizedType);
        AnnotatedParameterizedType result = (AnnotatedParameterizedType) type;

        assertSame(((ParameterizedType) result.getType()).getRawType(), UnitX.class);
        AnnotatedType param = result.getAnnotatedActualTypeArguments()[0];
        assertSame(param.getType(), QuantityX.class);
        Class<?>[] expected = new Class[] { A1.class, A2.class, A3.class, A4.class, A5.class };
        assertArrayEquals(expected, Arrays.stream(param.getAnnotations()).map(Annotation::annotationType).toArray(Class[]::new));
    }

    @SuppressWarnings("unused")
    public interface StevenSpecial<X extends OkType, Q extends QuantityY<Q>> {

        Q getUnitQ();

        X getOkType();
    }

    @SuppressWarnings("unused")
    public interface QuantityY<Q extends QuantityY<Q>> {

        Q getQuantityQ();
    }

    @SuppressWarnings("unused")
    public interface OkType {
        String getXxx();
    }

    static class OkClass implements OkType {
        public String getXxx() {
            return "";
        }
    }

    static class TestClass2 {

        public StevenSpecial<OkClass, ?> returnType() {
            return null;
        }
    }

    public void testRecursiveTypes2() throws Exception {
        AnnotatedType type = GenericTypeReflector.reduceBounded(
                GenericTypeReflector.getExactReturnType(TestClass2.class.getMethod("returnType"),
                        GenericTypeReflector.annotate(TestClass2.class)));

        assertNotNull(type);
        assertTrue(type instanceof AnnotatedParameterizedType);
        AnnotatedParameterizedType result = (AnnotatedParameterizedType) type;

        assertSame(((ParameterizedType) result.getType()).getRawType(), StevenSpecial.class);
        assertSame(result.getAnnotatedActualTypeArguments()[0].getType(), OkClass.class);
        assertSame(result.getAnnotatedActualTypeArguments()[1].getType(), QuantityY.class);
    }

    @SuppressWarnings("unused")
    public interface DoubleParameters<X extends OkType, Q extends DoubleParameters<X, Q>> {

        Q getUnitQ();

        X getOkType();
    }

    static class TestClass3 {

        public DoubleParameters<OkClass, ?> returnType() {
            return null;
        }
    }

    public void testRecursiveTypes3() throws Exception {
        AnnotatedType type = GenericTypeReflector.reduceBounded(GenericTypeReflector.annotate(
                GenericTypeReflector.getExactReturnType(TestClass3.class.getMethod("returnType"),
                        TestClass3.class)));

        assertNotNull(type);
        assertTrue(type instanceof AnnotatedParameterizedType);
        AnnotatedParameterizedType result = (AnnotatedParameterizedType) type;

        assertSame(((ParameterizedType) result.getType()).getRawType(), DoubleParameters.class);
        assertSame(result.getAnnotatedActualTypeArguments()[0].getType(), OkClass.class);
        assertSame(result.getAnnotatedActualTypeArguments()[1].getType(), DoubleParameters.class);
    }

    public static void main(String[] args) {

    }
}
