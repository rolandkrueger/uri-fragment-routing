package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractSingleUriParameter<V> extends AbstractUriParameter<V> {
    private static final long serialVersionUID = -4048110873045678896L;

    protected AbstractSingleUriParameter(String parameterName, ParameterValueConverter<V> converter) {
        super(parameterName, converter);
        Preconditions.checkNotNull(converter);
    }

    public int getSingleValueCount() {
        return 1;
    }

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
            if (getConverter() != null) {
                uriTokens.add(getConverter().convertToString((V) value.getValue()));
            } else {
                uriTokens.add(value.getValue().toString());
            }
        }
    }

    protected final ParameterValue<V> consumeParametersImpl(Map<String, String> parameters) {
        String value = parameters.get(getId());
        if (!(value == null)) {
            try {
                return ParameterValue.forValue(getConverter().convertToValue(value));
            } catch (ParameterValueConversionException e) {
                return ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "{" + getClass().getSimpleName() + ": " + getId() + "}";
    }
}