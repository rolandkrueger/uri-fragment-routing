package org.roklib.urifragmentrouting.parameter.converter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This abstract converter class converts a String into a list of Strings by matching the input against a regular
 * expression that contains capturing groups. The resulting list contains the values of all capturing groups excluding
 * the first group (which corresponds to the whole input String).
 * <p>
 * Sub-classes have to implement the process that turns a list of Strings into a single String that matches the given
 * regular expression.
 *
 * @see Pattern
 * @see Matcher
 */
public abstract class AbstractRegexToStringListParameterValueConverter implements ParameterValueConverter<List<String>> {

    private Pattern pattern;

    /**
     * Creates a new converter for the given regular expression. The regex should contain at least one capturing group
     * since the converter would be useless otherwise (i. e. always convert into the empty list).
     *
     * @param regex regular expression which should contain at least one capturing group
     * @throws java.util.regex.PatternSyntaxException if the pattern could not be compiled
     * @throws IllegalArgumentException               if the pattern is the empty String or does only contain
     *                                                whitespace
     */
    public AbstractRegexToStringListParameterValueConverter(String regex) {
        if ("".equals(regex.trim())) {
            throw new IllegalArgumentException("regex must not be the empty string or all whitespaces");
        }
        pattern = Pattern.compile(regex);
    }

    /**
     * Tests if the given value matches against the pattern provided through the class constructor.
     *
     * @param value String input to test against the regular expression of this converter
     * @return <code>true</code> if the given value matches the regular expression of this converter
     */
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

    /**
     * Returns the regular expression set for this converter.
     *
     * @return the regular expression set for this converter.
     */
    public final String getRegex() {
        return pattern.pattern();
    }
}
