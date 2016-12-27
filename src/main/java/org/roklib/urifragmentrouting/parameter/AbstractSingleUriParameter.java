package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Abstract superclass for URI parameters for single values. For such a parameter, the parameter id is used as the name
 * of the parameter, so depending on the {@link ParameterMode} used, the parameter's id is visible in a URI fragment.
 *
 * @param <V> domain type of the parameter value
 */
public abstract class AbstractSingleUriParameter<V> extends AbstractUriParameter<V> {
    private static final long serialVersionUID = -4048110873045678896L;

    /**
     * Creates a new single-valued parameter. The parameter name is used as the parameter id which will make the id
     * appear in a URI fragment (depending on the {@link ParameterMode} used). For example, in the URI fragment {@code
     * /view/productId/42}, there is an Integer-typed parameter used with name and id <tt>productId</tt>.
     *
     * @param parameterName the name of the parameter which is used to identify the parameter's value in a URI fragment
     * @param converter     a converter for the parameter value's domain type
     */
    protected AbstractSingleUriParameter(String parameterName, ParameterValueConverter<V> converter) {
        super(parameterName, converter);
        Preconditions.checkNotNull(converter);
    }

    /**
     * Returns the number of values for this parameter which is exactly 1.
     */
    @Override
    public int getSingleValueCount() {
        return 1;
    }

    /**
     * Returns the name to be used to identify the parameter's value. This is the parameter's id which coincides with
     * the parameter name for single-valued URI parameters.
     *
     * @return a list containing exactly one element which is the id of the parameter
     */
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
            if (getConverter() != null) {
                uriTokens.add(getConverter().convertToString((V) value.getValue()));
            } else {
                uriTokens.add(value.getValue().toString());
            }
        }
    }

    @Override
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
