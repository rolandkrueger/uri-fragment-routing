package org.roklib.urifragmentrouting.annotation;

import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;

import java.lang.annotation.*;

/**
 * A method annotated with this annotation will receive the action mapper which handled the current URI fragment and
 * returned the current {@link org.roklib.urifragmentrouting.UriActionCommandFactory UriActionCommandFactory}. Receiving
 * the current action mapper is useful if you need to create a new URI fragment for this mapper, in other words if you
 * need to change the current URI fragment in some way (e. g. adapt one of the URI fragment parameters). For this, you
 * need the corresponding {@link org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper
 * UriPathSegmentActionMapper} in order to invoke method {@link org.roklib.urifragmentrouting.UriActionMapperTree#assembleUriFragment(UriPathSegmentActionMapper)
 * UriActionMapperTree#assembleUriFragment(UriPathSegmentActionMapper)}.
 * <p>
 * This annotation can be used on methods of classes implementing {@link org.roklib.urifragmentrouting.UriActionCommand
 * UriActionCommand}. The annotated method is required to have exactly one argument of type {@link
 * org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper UriPathSegmentActionMapper}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface CurrentActionMapper {
}
