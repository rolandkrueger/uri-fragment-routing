package org.roklib.urifragmentrouting.annotation;

import java.lang.annotation.*;

/**
 * A method annotated with this annotation will receive all URI parameter values that were captured from the currently
 * interpreted URI fragment. This annotation can be used on methods of classes implementing {@link
 * org.roklib.urifragmentrouting.UriActionCommand UriActionCommand}.
 * <p>
 * The annotated method is required to have exactly one argument of type {@link org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues
 * CapturedParameterValues}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AllCapturedParameters {
}
