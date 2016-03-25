package org.roklib.urifragmentrouting.parameter;

import org.roklib.urifragmentrouting.exception.ParameterValueConversionException;
import org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleLongWithIgnoredTextUriParameter extends AbstractSingleUriParameter<SingleLongWithIgnoredTextUriParameter.IdWithText> {
    private static final long serialVersionUID = 7990237721421647271L;

    private static final Pattern PATTERN = Pattern.compile("^(\\d+)(.*)");

    public SingleLongWithIgnoredTextUriParameter(String parameterName) {
        super(parameterName, IdWithTextParameterValueConverter.INSTANCE);
    }

    public interface IdWithText {
        Long getId();

        String getText();

        void setId(Long id);

        void setText(String text);
    }

    public static class IdWithTextImpl implements IdWithText {
        private Long id;
        private String text;

        public IdWithTextImpl() {
        }

        public IdWithTextImpl(Long id, String text) {
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
        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public void setText(String text) {
            this.text = text;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof IdWithTextImpl)) {
                return false;
            }
            IdWithTextImpl other = (IdWithTextImpl) obj;
            if (id == null) {
                return false;
            }
            return other.id.equals(id);
        }

        @Override
        public int hashCode() {
            return id == null ? 0 : id.hashCode();
        }
    }


    public static class IdWithTextParameterValueConverter implements ParameterValueConverter<IdWithText> {

        public final static IdWithTextParameterValueConverter INSTANCE = new IdWithTextParameterValueConverter();

        @Override
        public String convertToString(IdWithText value) {
            if (value == null || value.getId() == null) {
                return "";
            }

            return value.getId().toString() + (value.getText() == null ? "" : value.getText());
        }

        @Override
        public IdWithText convertToValue(String valueAsString) throws ParameterValueConversionException {
            Matcher m = PATTERN.matcher(valueAsString);
            IdWithTextImpl result = new IdWithTextImpl();
            if (m.find()) {
                try {
                    result.setId(Long.valueOf(m.group(1)));
                } catch (NumberFormatException e) {
                    throw new ParameterValueConversionException(e);
                }
                result.setText(m.group(2));

            }
            return result;
        }
    }
}
