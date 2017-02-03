package org.roklib.urifragmentrouting.annotation;

import java.lang.annotation.*;

/**
 * A method annotated with this annotation will receive the currently interpreted URI fragment. This annotation can be
 * used on methods of classes implementing {@link org.roklib.urifragmentrouting.UriActionCommand UriActionCommand}.
 * <p>
 * The annotated method is required to have exactly one argument of type String.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CurrentUriFragment {
}
