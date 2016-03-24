package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;

import java.util.List;

/**
 * <p> URI action handler for matching all URI tokens which start with some particular character string. As this action
 * handler class is a particularly configured {@link RegexUriPathSegmentActionMapper}, all of the description of {@link
 * RegexUriPathSegmentActionMapper} also applies to this class. </p> <p> This action handler is initialized with some
 * prefix string which must not be all whitespaces or the empty string. By default, there is one capturing group in the
 * regular expression that underlies this class. This group captures any substring that comes after the given prefix
 * string in the currently evaluated URI token. </p>
 *
 * @author Roland Krüger
 * @see RegexUriPathSegmentActionMapper
 * @since 1.0
 */
public class StartsWithUriPathSegmentActionMapper extends RegexUriPathSegmentActionMapper {
    private static final long serialVersionUID = -8311620063509162064L;

    /**
     * Creates a new {@link StartsWithUriPathSegmentActionMapper} with the given prefix string.
     *
     * @param prefix prefix string to be used for interpreting URI tokens.
     * @throws IllegalArgumentException if the prefix is the empty string or all whitespaces
     */
    public StartsWithUriPathSegmentActionMapper(String mapperName, String prefix, String parameterId) {
        super(mapperName, parameterId, new StartsWithConverter(prefix));
        if ("".equals(prefix.trim())) {
            throw new IllegalArgumentException("prefix must not be the empty string or all whitespaces");
        }
    }

    private static class StartsWithConverter extends AbstractRegexToStringListParameterValueConverter {
        public StartsWithConverter(String prefix) {
            super(prefix + "(.*)");
        }

        @Override
        public String convertToString(List<String> value) {
            if (value == null || value.isEmpty()) {
                return "";
            }
            if (value.size() == 1) {
                return  value.get(0);
            }

            return value.get(0) + value.get(1);
        }
    }

}
