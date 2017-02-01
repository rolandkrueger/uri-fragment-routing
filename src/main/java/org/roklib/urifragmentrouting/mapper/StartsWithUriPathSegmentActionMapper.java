package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;

import java.util.List;
import java.util.stream.Stream;

/**
 * URI action handler for matching all path segments which start with some particular, pre-defined character string. As
 * this action handler class is a particularly configured {@link RegexUriPathSegmentActionMapper}, all of the
 * description of {@link RegexUriPathSegmentActionMapper} also applies to this class.
 * <p>
 * This action handler is initialized with some prefix String which must not be all whitespace or the empty String. Note
 * that this prefix String must not contain any unescaped characters reserved in a regular expression as they will be
 * used as matching operators otherwise.
 * <p>
 * The regular expression used by this mapper is constituted as follows:
 * <p>
 * <code>prefix(.*)</code>
 * <p>
 * By default, there is one capturing group in the regular expression that is used by this mapper type. This group
 * captures any substring that comes after the given prefix string in the currently evaluated URI token. This captured
 * String can be obtained through the {@link org.roklib.urifragmentrouting.parameter.StringListUriParameter
 * StringListUriParameter} registered with this action mapper with the id specified through the constructor.
 *
 * @see RegexUriPathSegmentActionMapper
 */
public class StartsWithUriPathSegmentActionMapper extends RegexUriPathSegmentActionMapper {
    private static final long serialVersionUID = -8311620063509162064L;

    /**
     * Creates a new {@link StartsWithUriPathSegmentActionMapper} with the given mapper name, prefix string, and URI
     * parameter id. This action mapper will only be responsible for path segments that start with the specified
     * prefix (see {@link #isResponsibleForToken(String)}).
     *
     * @param mapperName  the name of this mapper
     * @param prefix      prefix string to be used for interpreting URI tokens. Note that if the prefix contains any
     *                    reserved characters of a regular expression (see class {@link java.util.regex.Pattern
     *                    Pattern}) these characters have to be escaped.
     * @param parameterId id of the {@link org.roklib.urifragmentrouting.parameter.UriParameter UriParameter} to be used
     *                    to capture the path segment name of the interpreted path segment without the prefix
     *
     * @throws IllegalArgumentException if the prefix is the empty string or only consists of whitespaces
     */
    public StartsWithUriPathSegmentActionMapper(final String mapperName, final String prefix, final String parameterId) {
        super(mapperName, parameterId, new StartsWithConverter(prefix));
        if ("".equals(prefix.trim())) {
            throw new IllegalArgumentException("prefix must not be the empty string or all whitespaces");
        }
    }

    /**
     * Regex parameter value converter which uses the following regex: <tt>prefix(.*)</tt>. If the prefix contains any
     * characters which have a special meaning in regular expression, then these characters will be escaped.
     */
    private static class StartsWithConverter extends AbstractRegexToStringListParameterValueConverter {
        private final String prefix;

        StartsWithConverter(final String prefix) {
            super(escapeSpecialChars(prefix) + "(.*)");
            this.prefix = prefix;
        }

        private static String escapeSpecialChars(String prefix) {
            String[] cache = new String[]{prefix};
            Stream.of("\\", ".", "^", "$", "|", "?", "*", "+", "(", ")", "{", "[")
                    .forEach(metaCharacter -> cache[0] = cache[0].replace(metaCharacter, "\\" + metaCharacter));
            return cache[0];
        }

        @Override
        public String convertToString(final List<String> value) {
            if (value == null || value.isEmpty()) {
                return "";
            }
            if (value.size() == 1) {
                return prefix + value.get(0);
            } else {
                throw new IllegalArgumentException("value list has more than one element: only single valued lists are allowed for " + StartsWithUriPathSegmentActionMapper.class.getName());
            }
        }
    }

}
