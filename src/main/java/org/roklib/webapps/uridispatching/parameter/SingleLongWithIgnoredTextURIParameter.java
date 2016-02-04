package org.roklib.webapps.uridispatching.parameter;

import org.roklib.webapps.uridispatching.parameter.value.ParameterValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleLongWithIgnoredTextUriParameter extends SingleLongUriParameter {
    private static final long serialVersionUID = 7990237721421647271L;

    private static final Pattern PATTERN = Pattern.compile("^(\\d+).*?");

    public SingleLongWithIgnoredTextUriParameter(String parameterName) {
        super(parameterName);
    }

    private String convertValue(String value) {
        Matcher m = PATTERN.matcher(value);
        if (m.find()) {
            return m.group(1);
        }
        return value;
    }

    @Override
    protected ParameterValue<Long> consumeParametersImpl(String value) {
        return super.consumeParametersImpl(convertValue(value));
    }

}
