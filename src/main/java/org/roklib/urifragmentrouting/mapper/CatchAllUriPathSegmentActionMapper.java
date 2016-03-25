package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.parameter.AbstractSingleUriParameter;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameterError;
import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;
import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValuesImpl;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.List;
import java.util.Map;

/**
 * This action mapper is used to invariably interpret all URI tokens that are passed into this mapper during the URI
 * interpretation process. The value of this token can be obtained with {@link #getCurrentURIToken()}. As this action
 * mapper class is a particularly configured {@link RegexUriPathSegmentActionMapper}, all of the description of {@link
 * RegexUriPathSegmentActionMapper} also applies to this class.
 *
 * @author Roland Krüger
 * @since 1.0
 */
public class CatchAllUriPathSegmentActionMapper<V> extends RegexUriPathSegmentActionMapper {
    private static final long serialVersionUID = -5033766191211958005L;
    public static final String $INTERN = "$intern";

    private AbstractSingleUriParameter<V> parameter;
    private String internalParameterId;

    public CatchAllUriPathSegmentActionMapper(String mapperName, AbstractSingleUriParameter<V> parameter) {
        super(mapperName, parameter.getId() + $INTERN, new CatchAllConverter());
        this.parameter = parameter;
        internalParameterId = parameter.getId() + $INTERN;
    }

    /**
     * <p> {@inheritDoc} </p> <p> Invariably returns <code>true</code> for this {@link
     * CatchAllUriPathSegmentActionMapper}. </p>
     */
    @Override
    protected boolean isResponsibleForToken(String uriToken) {
        return true;
    }

    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValuesImpl capturedParameterValues,
                                                                    String currentMapperName,
                                                                    List<String> uriTokens,
                                                                    Map<String, String> parameters,
                                                                    ParameterMode parameterMode) {
        final Class<? extends UriActionCommand> actionClass =
                super.interpretTokensImpl(capturedParameterValues,
                        currentMapperName,
                        uriTokens,
                        parameters,
                        parameterMode);

        ParameterValue<V> parameterValue = null;
        if (capturedParameterValues.hasValueFor(mapperName, internalParameterId)) {
            final ParameterValue<List<String>> value = capturedParameterValues.getValueFor(mapperName, internalParameterId);
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
            capturedParameterValues.setValueFor(mapperName, parameter.getId(), parameterValue);
        }

        return actionClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String getMapperNameInstanceForAssembledUriFragment(CapturedParameterValues capturedParameterValues) {
        return parameter.getConverter().convertToString((V) capturedParameterValues.getValueFor(mapperName, parameter.getId()).getValue());
    }

    private static class CatchAllConverter extends AbstractRegexToStringListParameterValueConverter {
        public CatchAllConverter() {
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
