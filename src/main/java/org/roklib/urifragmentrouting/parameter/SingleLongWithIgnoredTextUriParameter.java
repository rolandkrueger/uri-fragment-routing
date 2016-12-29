package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This URI parameter handles parameter values consisting of two parts: a numerical value (interpreted as a number of
 * type Long) and a textual suffix. This is useful if you want to add an id (e. g. the primary key of some item)
 * including a human-readable textual description of the referred item into a URI fragment. Consider, for example, a
 * blogging software where individual blog posts are referred in the URI fragment by their database id. In order to give
 * the users more context about the referred blog posts, a post's title can be added to the post id. For example:
 * <p>
 * <tt>http://www.example.com/blog#!posts/67234-my-first-blog-post</tt>
 * <p>
 * For these use cases you can use this parameter type. It converts the parameter's String value into an object of type
 * {@link IdWithText}. This object contains the number as a Long value and the remaining text as a String. In the
 * example above, the object would contain <tt>id=67234</tt> and <tt>text="-my-first-blog-post"</tt>.
 */
public class SingleLongWithIgnoredTextUriParameter extends AbstractSingleUriParameter<SingleLongWithIgnoredTextUriParameter.IdWithText> {
    private static final long serialVersionUID = 7990237721421647271L;

    public SingleLongWithIgnoredTextUriParameter(final String parameterName) {
        super(parameterName, IdWithTextParameterValueConverter.INSTANCE);
    }

    /**
     * Defines a data object which combines two values: a numerical ID together with a text. This is used as the domain
     * type for values of {@link SingleLongWithIgnoredTextUriParameter}.
     */
    public interface IdWithText {
        Long getId();

        String getText();

        void setId(Long id);

        void setText(String text);
    }

    /**
     * Implementation of interface {@link IdWithText}. Two objects of this class are considered equal if their ids
     * match. The text is ignored.
     */
    public static class IdWithTextImpl implements IdWithText {
        private Long id;
        private String text;

        public IdWithTextImpl() {
        }

        public IdWithTextImpl(final Long id, final String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void setId(final Long id) {
            this.id = id;
        }

        @Override
        public void setText(final String text) {
            this.text = text;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof IdWithTextImpl)) {
                return false;
            }
            final IdWithTextImpl other = (IdWithTextImpl) obj;
            return id != null && other.id.equals(id);
        }

        @Override
        public int hashCode() {
            return id == null ? 0 : id.hashCode();
        }
    }


    /**
     * Converter class for converting a String into an object of type {@link IdWithText}. The converter tries to match
     * an input String against the regular expression {@code ^(\d+)(.*)}. This will match any number with an arbitrary
     * textual suffix. An example for such a String would be
     * <p>
     * <tt>50923-my-first-blog-post</tt>
     */
    public static class IdWithTextParameterValueConverter implements ParameterValueConverter<IdWithText> {
        private final static Pattern PATTERN = Pattern.compile("^(\\d+)(.*)");
        public final static IdWithTextParameterValueConverter INSTANCE = new IdWithTextParameterValueConverter();

        @Override
        public String convertToString(final IdWithText value) {
            if (value == null || value.getId() == null) {
                return "";
            }

            return value.getId().toString() + (value.getText() == null ? "" : value.getText());
        }

        @Override
        public IdWithText convertToValue(final String valueAsString) throws ParameterValueConversionException {
            final Matcher m = PATTERN.matcher(valueAsString);
            final IdWithTextImpl result = new IdWithTextImpl();
            if (m.find()) {
                try {
                    result.setId(Long.valueOf(m.group(1)));
                } catch (final NumberFormatException e) {
                    throw new ParameterValueConversionException(e);
                }
                result.setText(m.group(2));
                return result;
            } else {
                throw new ParameterValueConversionException();
            }
        }
    }
}
