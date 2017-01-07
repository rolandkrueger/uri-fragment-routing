package org.roklib.urifragmentrouting.annotation;

import java.lang.annotation.*;

/**
 * A method annotated with this annotation will receive the routing context object passed to the URI fragment
 * interpretation pipeline with {@link org.roklib.urifragmentrouting.UriActionMapperTree#interpretFragment(String,
 * Object, boolean)}. The routing context is a custom defined object which can be passed along with the interpretation
 * process of a URI fragment. This context could be used, for instance, to give the action commands executed at the end
 * of the interpretation process access to application services, such as an event bus. This annotation can be used on a
 * method in classes implementing {@link org.roklib.urifragmentrouting.UriActionCommand}.
 * <p>
 * The annotated method is required to have exactly one argument of the same type or a supertype of the context object
 * passed into the URI fragment interpretation pipeline with {@link org.roklib.urifragmentrouting.UriActionMapperTree#interpretFragment(String,
 * Object, boolean)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RoutingContext {
}
