package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.converter.StringListParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A URI parameter that takes a list of Strings as its value. The String list is converted by a {@link
 * StringListParameterValueConverter}.
 *
 * @see StringListParameterValueConverter
 */
public class StringListUriParameter extends AbstractUriParameter<List<String>> {

    /**
     * Creates a new {@link StringListUriParameter} with the given id and a custom implementation of {@link
     * ParameterValueConverter} for String lists.
     *
     * @param id        the identifier for this parameter
     * @param converter a custom converter for String lists
     */
    public StringListUriParameter(String id, ParameterValueConverter<List<String>> converter) {
        super(id, converter);
    }

    /**
     * Creates a new {@link StringListUriParameter} with the given id. The URI parameter uses an instance of {@link
     * StringListParameterValueConverter} to convert a list of Strings into a String and vice versa.
     *
     * @param id the identifier for this parameter
     */
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
    public void toUriTokenList(ParameterValue<?> value, List<String> uriTokens, ParameterMode parameterMode) {
        if (value.hasValue()) {
            if (parameterMode == ParameterMode.DIRECTORY_WITH_NAMES) {
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
