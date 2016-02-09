package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.mapper.UriPathSegmentActionMapper;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConversionException;
import org.roklib.webapps.uridispatching.parameter.converter.ParameterValueConverter;
import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A URI parameter that takes a list of Strings as its value.
 *
 * @author Roland Krüger
 */
public class StringListUriParameter extends AbstractUriParameter<List<String>> {

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

    private static class StringListParameterValueConverter implements ParameterValueConverter<List<String>> {

        private static StringListParameterValueConverter INSTANCE = new StringListParameterValueConverter();

        private static Pattern encodedSemicolonPattern = Pattern.compile("%3[Bb]");
        private static Pattern encodedSlashPattern = Pattern.compile("%2[Ff]");
        private static Pattern semicolonPattern = Pattern.compile(";");
        private static Pattern slashPattern = Pattern.compile("/");

        @Override
        public String convertToString(List<String> value) {
            if (value == null) {
                return "";
            }
            StringJoiner joiner = new StringJoiner(";");
            value.stream().forEach(s -> {
                final String result = semicolonPattern.matcher(s).replaceAll("%3B");
                joiner.add(slashPattern.matcher(result).replaceAll("%2F"));
            });
            return joiner.toString();
        }

        @Override
        public List<String> convertToValue(String valueAsString) throws ParameterValueConversionException {
            if (valueAsString == null || "".equals(valueAsString)) {
                return new LinkedList<>();
            }

            return Arrays.stream(valueAsString.split(";")).map(s -> {
                        final String result = encodedSemicolonPattern.matcher(s).replaceAll(";");
                        return encodedSlashPattern.matcher(result).replaceAll("/");
                    }
            ).collect(Collectors.toList());
        }
    }

}
