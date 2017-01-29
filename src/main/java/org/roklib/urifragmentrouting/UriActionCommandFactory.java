package org.roklib.urifragmentrouting;

import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

@FunctionalInterface
public interface UriActionCommandFactory<C> {

    UriActionCommand createUriActionCommand(String currentUriFragment, CapturedParameterValues parameterValues, C routingContext);

}
