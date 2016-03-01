package org.roklib.urifragmentrouting.parameter.converter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Roland Krüger
 */
public class StringListParameterValueConverter implements ParameterValueConverter<List<String>> {

    public static StringListParameterValueConverter INSTANCE = new StringListParameterValueConverter();

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
