package org.roklib.urifragmentrouting.parameter.converter;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roland Krüger
 */
public abstract class AbstractRegexToStringListParameterValueConverter implements ParameterValueConverter<List<String>> {

    private Pattern pattern;

    public AbstractRegexToStringListParameterValueConverter(String regex) {
        if ("".equals(regex.trim())) {
            throw new IllegalArgumentException("regex must not be the empty string or all whitespaces");
        }
        pattern = Pattern.compile(regex);
    }

    public boolean matches(String value) {
        return pattern.matcher(value).matches();
    }

    @Override
    public List<String> convertToValue(String valueAsString) throws ParameterValueConversionException {
        Matcher matcher = pattern.matcher(valueAsString);
        List<String> result = new LinkedList<>();
        if (matcher.matches()) {
            for (int index = 1; index < matcher.groupCount() + 1; ++index) {
                result.add(matcher.group(index));
            }
        }
        return result;
    }
}
