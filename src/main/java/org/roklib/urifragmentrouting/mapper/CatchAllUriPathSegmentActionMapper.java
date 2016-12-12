package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.parameter.AbstractSingleUriParameter;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameterError;
import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;
import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.List;
import java.util.Map;

/**
 * This action mapper will handle all URI tokens which are passed to it during the URI fragment interpretation process.
 * As this action mapper class is a particularly configured {@link RegexUriPathSegmentActionMapper}, all of the
 * description of {@link RegexUriPathSegmentActionMapper} also applies to this class. The regex used by this mapper is
 * the catch-all pattern {@code (.*)}.
 * <p>
 * In order to obtain the value of a URI fragment token this mapper has handled, a specific {@link
 * org.roklib.urifragmentrouting.parameter.UriParameter} is used. The data type of this parameter is specified by the
 * type parameter {@code V} of this class. By specifying any other type than {@link String}, the range of valid values
 * expected from the URI fragment token handled by this action mapper can be narrowed down. The URI parameter instance
 * to be used for capturing the value of the path segment handled by this action mapper is specified with the class
 * constructor {@link #CatchAllUriPathSegmentActionMapper(String, AbstractSingleUriParameter)}.
 * <p>
 * During the process of interpreting a URI fragment, a {@link CatchAllUriPathSegmentActionMapper} will always be asked
 * last to interpret the current URI token, so that other, more specific action mappers have a chance to interpret the
 * token in preference to the catch-all mapper.
 *
 * @param <V> Type of the {@link ParameterValue} used internally to capture a path segment value
 * @see RegexUriPathSegmentActionMapper
 */
public class CatchAllUriPathSegmentActionMapper<V> extends RegexUriPathSegmentActionMapper {
    private static final long serialVersionUID = -5033766191211958005L;
    private static final String $INTERN = "$intern";

    private final AbstractSingleUriParameter<V> parameter;
    private final String internalParameterId;

    /**
     * Creates a new {@link CatchAllUriPathSegmentActionMapper} for the given mapper name and {@link
     * org.roklib.urifragmentrouting.parameter.UriParameter}. The URI parameter is used to capture and define the path
     * segment name handled by this action mapper.
     *
     * @param mapperName name of this action mapper
     * @param parameter  URI parameter to capture the value of the handled path segment
     */
    public CatchAllUriPathSegmentActionMapper(String mapperName, AbstractSingleUriParameter<V> parameter) {
        super(mapperName, parameter.getId() + $INTERN, new CatchAllConverter());
        this.parameter = parameter;
        internalParameterId = parameter.getId() + $INTERN;
    }

    /**
     * <p> {@inheritDoc} </p> <p> Invariably returns <code>true</code> for this {@link
     * CatchAllUriPathSegmentActionMapper}.
     *
     * @return <code>true</code> for all URI tokens
     */
    @Override
    public boolean isResponsibleForToken(String uriToken) {
        return true;
    }

    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                    String currentUriToken,
                                                                    List<String> uriTokens,
                                                                    Map<String, String> queryParameters,
                                                                    ParameterMode parameterMode) {
        final Class<? extends UriActionCommand> actionClass =
                super.interpretTokensImpl(capturedParameterValues,
                        currentUriToken,
                        uriTokens,
                        queryParameters,
                        parameterMode);

        ParameterValue<V> parameterValue = null;
        if (capturedParameterValues.hasValueFor(getMapperName(), internalParameterId)) {
            final ParameterValue<List<String>> value = capturedParameterValues.getValueFor(getMapperName(), internalParameterId);
            if (value.hasError()) {
                parameterValue = ParameterValue.forError(value.getError());
            } else {
                try {
                    parameterValue = ParameterValue.forValue(parameter.getConverter().convertToValue(value.getValue().get(0)));
                } catch (ParameterValueConversionException e) {
                    parameterValue = ParameterValue.forError(UriParameterError.CONVERSION_ERROR);
                }
            }
        } else if (parameter.isOptional()) {
            parameterValue = ParameterValue.forDefaultValue(parameter.getDefaultValue());
        }

        if (parameterValue != null) {
            capturedParameterValues.setValueFor(getMapperName(), parameter.getId(), parameterValue);
        }

        return actionClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String getPathSegmentNameForAssemblingUriFragment(CapturedParameterValues capturedParameterValues) {
        return parameter.getConverter().convertToString((V) capturedParameterValues.getValueFor(getMapperName(), parameter.getId()).getValue());
    }

    /**
     * Converter class which converts the whole input String into a singleton list and in turn converts a list of
     * Strings into a single String by using the unaltered first list element as result. It uses the following regex:
     * <tt>(.*)</tt>.
     */
    private static class CatchAllConverter extends AbstractRegexToStringListParameterValueConverter {
        CatchAllConverter() {
            super("(.*)");
        }

        @Override
        public String convertToString(List<String> value) {
            if (value == null || value.isEmpty()) {
                return "";
            }

            return value.get(0);
        }
    }

}
