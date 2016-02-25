package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConversionException;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.converter.StringListParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A URI parameter that takes a list of Strings as its value.
 *
 * @author Roland Krüger
 */
public class StringListUriParameter extends AbstractUriParameter<List<String>> {

    public StringListUriParameter(String id, ParameterValueConverter<List<String>> converter) {
        super(id, converter);
    }

    public StringListUriParameter(String id) {
        super(id, StringListParameterValueConverter.INSTANCE);
    }

    @Override
    protected ParameterValue<List<String>> consumeParametersImpl(Map<String, String> parameters) {
        if (parameters.containsKey(getId())) {
            try {
                return ParameterValue.forValue(getConverter().convertToValue(parameters.get(getId())));
            } catch (ParameterValueConversionException e) {
                return ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
            }
        } else {
            return null;
        }
    }

    @Override
    public int getSingleValueCount() {
        return 1;
    }

    @Override
    public List<String> getParameterNames() {
        return Collections.singletonList(getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void toUriTokenList(ParameterValue<?> value, List<String> uriTokens, UriPathSegmentActionMapper.ParameterMode parameterMode) {
        if (value.hasValue()) {
            if (parameterMode == UriPathSegmentActionMapper.ParameterMode.DIRECTORY_WITH_NAMES) {
                uriTokens.add(getId());
            }
            uriTokens.add(getConverter().convertToString((List<String>) value.getValue()));
        }
    }

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + ": " + getId() + "}";
    }
}
