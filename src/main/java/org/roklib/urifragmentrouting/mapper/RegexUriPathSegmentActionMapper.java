package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValuesImpl;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;
import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionDispatcher;
import org.roklib.urifragmentrouting.parameter.StringListUriParameter;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> Special dispatching URI action handler which is not only responsible for handling one particular URI token but
 * handles all tokens matching a predefined regular expression. So instead of only handling URI tokens such as
 * <code>user</code> it could handle all tokens matching the regex <code>user_(\d+)</code>, i. e. user_1, user_17,
 * user_23, and so on. </p> <p> A {@link RegexUriPathSegmentActionMapper} is itself a {@link
 * DispatchingUriPathSegmentActionMapper}, that is, it can have its own
 * sub-handlers to which the responsibility to interpret part of a URI can be passed. To set the action command for this
 * {@link RegexUriPathSegmentActionMapper} in case there are no more URI tokens to be passed to sub-handlers (i. e. the
 * currently interpreted URI directly points to this handler), you use method {@link
 * #setActionCommandClass(Class)}. </p> <h1>Capturing Groups</h1> <p> The regular expression for
 * this action handler can contain capturing groups in order to capture parts or all of the currently interpreted URI
 * token. The captured values for these capturing groups can be obtained through a {@link StringListUriParameter}
 * whose parameter ID is specified as constructor parameter. The
 * set of matched token fragments is updated after each call to {@link #isResponsibleForToken(String)} by the parent
 * handler. This usually happens while in the process of interpreting a visited URI by the {@link UriActionDispatcher}.
 * Note that the array of matched token fragments does not contain {@link Matcher}'s first capturing group holding the
 * entire pattern. </p> <h1>Generating Parameterized Action URIs</h1> <p> When you are generating parameterized action
 * URIs with the {@link #getParameterizedActionURI(boolean)} methods, you have to provide a value for the URI token used
 * to represent this {@link RegexUriPathSegmentActionMapper}. This is done with {@link #setURIToken(String)}. The token
 * set with this method must be able to be successfully matched against the regular expression of this handler.
 * Otherwise, an exception is thrown. If you generate a parameterized action URI without setting the URI token first,
 * the regular expression pattern itself is used as the token verbatim. </p> <p> For example, if you have defined the
 * following pattern to be used by this handler: <code>user_(\d+)</code> and this handler is registered as a sub-handler
 * for another action handler with the action name <code>profile</code>. Calling {@link
 * #getParameterizedActionURI(boolean)} without setting the URI token first will then yield the following URI
 * <code>http://www.example.com/profile/user_(\d+)</code>. Setting this handler's URI token to <code>user_123</code>
 * will instead yield the URI <code>http://www.example.com/profile/user_123</code>. The URI token has to be defined for
 * every {@link RegexUriPathSegmentActionMapper} that is found on the path from the action handler tree's root to some
 * action handler for which a parameterized action URI is to be generated. </p>
 *
 * @author Roland Krüger
 * @since 1.0
 */
public class RegexUriPathSegmentActionMapper extends DispatchingUriPathSegmentActionMapper {
    private static final long serialVersionUID = 4435578380164414638L;

    private String parameterId;
    private AbstractRegexToStringListParameterValueConverter valueListConverter;

    /**
     * Creates a new {@link RegexUriPathSegmentActionMapper} with the provided regular expression. This regex will be
     * applied to the URI tokens passed in to {@link #isResponsibleForToken(String)} to determine if this object is
     * responsible for handling the given token.
     *
     * @param parameterId id for the {@link StringListUriParameter} which will contain the values captured by the
     *                    regular expression's capturing groups
     * @throws IllegalArgumentException               when the regular exception is the empty String or consists of only
     *                                                whitespaces
     * @throws java.util.regex.PatternSyntaxException when the regular exception could not be compiled
     */
    public RegexUriPathSegmentActionMapper(String mapperName, String parameterId, AbstractRegexToStringListParameterValueConverter valueListConverter) {
        this(mapperName, new StringListUriParameter(parameterId, valueListConverter), valueListConverter);
    }

    protected RegexUriPathSegmentActionMapper(String mapperName,
                                              UriParameter<List<String>> parameter,
                                              AbstractRegexToStringListParameterValueConverter valueListConverter) {
        super(mapperName);
        Preconditions.checkNotNull(valueListConverter);

        registerURIParameter(parameter);
        parameter.setConverter(valueListConverter);
        this.parameterId = parameter.getId();
        this.valueListConverter = valueListConverter;
    }

    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValuesImpl capturedParameterValues,
                                                                    String currentMapperName,
                                                                    List<String> uriTokens,
                                                                    Map<String, String> parameters,
                                                                    ParameterMode parameterMode) {
        Map<String, String> capturedValues = new HashMap<>();
        capturedValues.put(parameterId, currentMapperName);
        ParameterInterpreter interpreter = new ParameterInterpreter(getMapperName());
        interpreter.interpretQueryParameters(getUriParameters(), capturedParameterValues, capturedValues);

        return super.interpretTokensImpl(capturedParameterValues, currentMapperName, uriTokens, parameters, parameterMode);
    }

    /**
     * Checks if this {@link RegexUriPathSegmentActionMapper} is responsible for handling the given URI token. It does
     * so by checking whether the token matches the assigned regular expression. If that is the case <code>true</code>
     * is returned.
     *
     * @return <code>true</code> if the given URI token will be handled by this action handler
     */
    @Override
    protected boolean isResponsibleForToken(String uriToken) {
        return valueListConverter.matches(uriToken);
    }

    @Override
    protected String getMapperNameInstanceForAssembledUriFragment(CapturedParameterValues capturedParameterValues) {
        final ParameterValue<List<String>> value = capturedParameterValues.getValueFor(mapperName, parameterId);
        if (value != null && value.hasValue()) {
            return valueListConverter.convertToString(value.getValue());
        } else throw new IllegalArgumentException("The value set for this mapper is invalid. Mapper: " + toString());
    }
}
