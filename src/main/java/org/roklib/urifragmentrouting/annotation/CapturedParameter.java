package org.roklib.urifragmentrouting.annotation;

import java.lang.annotation.*;

/**
 * A method annotated with this annotation will receive one of the URI parameter values captured from the currently
 * interpreted URI fragment. This annotation can be used on methods of classes implementing {@link
 * org.roklib.urifragmentrouting.UriActionCommand UriActionCommand}.
 * <p>
 * The annotated method is required to have exactly one argument of type {@link org.roklib.urifragmentrouting.parameter.value.ParameterValue
 * ParameterValue} with its class type set to the type of the requested URI parameter. E. g. if you're using a URI
 * fragment parameter of type {@link org.roklib.urifragmentrouting.parameter.SingleDateUriParameter
 * SingleDateUriParameter} with parameter name <tt>startDate</tt> on a mapper with name <tt>list</tt> then the method
 * annotated with this annotation could be annotated as follows:
 * <p>
 * <pre>
 *    {@literal @}CapturedParameter(mapperName="list", parameterName="startDate")
 *     public void setStartDate(ParameterValue&lt;Date&gt; startDate) {
 *         // ...
 *     }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CapturedParameter {
    /**
     * Name of the URI path segment mapper on which the required URI parameter is registered.
     */
    String mapperName();

    /**
     * Name of the required URI parameter.
     */
    String parameterName();
}
