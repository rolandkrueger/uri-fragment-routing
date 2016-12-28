package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.StringListUriParameter;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.converter.AbstractRegexToStringListParameterValueConverter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * This URI path segment action mapper is a special type of a {@link DispatchingUriPathSegmentActionMapper} which
 * decides whether or not is responsible for handling a particular URI token by trying to match it with a given regular
 * expression. If the regex matches the URI token given to method {@link #isResponsibleForToken(String)}, this action
 * mapper will be responsible for handling this token.
 * <p>
 * Since this class is a subclass of {@link DispatchingUriPathSegmentActionMapper}, it can have its own sub-mappers to
 * which the responsibility for interpreting the remaining URI tokens can be passed. <h1>Capturing groups</h1> The
 * regular expression for this action handler may contain capturing groups in order to capture parts or all of the
 * currently interpreted path segment. The captured values for these capturing groups can be obtained through a {@link
 * StringListUriParameter} whose parameter ID is specified as a constructor parameter. The set of matched token
 * fragments is updated after each call to {@link #isResponsibleForToken(String)} by the parent handler. This usually
 * happens while in the process of interpreting the current URI fragment by the {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree}. Note that the first capturing group of a {@link Matcher} for the
 * current URI token will not be contained in the {@link StringListUriParameter}, since this contains the entire match
 * which corresponds to the matched URI token itself. <h1>Generating parameterized URI fragments</h1> When you are
 * generating parameterized URI fragments with {@link org.roklib.urifragmentrouting.UriActionMapperTree#assembleUriFragment(CapturedParameterValues,
 * UriPathSegmentActionMapper)}, you have to provide a value for the URI token used to represent this {@link
 * RegexUriPathSegmentActionMapper}. This is done by defining the contents of the {@link StringListUriParameter} whose
 * ID is specified by the constructor. The concrete algorithm to assemble a valid URI token, which can be successfully
 * handled by this action mapper, is defined by the {@link AbstractRegexToStringListParameterValueConverter} that is
 * passed to the constructor of this class. A subclass of this converter has to be provided when constructing a {@link
 * RegexUriPathSegmentActionMapper}. This converter specifies the regular expression to be used for this action mapper
 * and the algorithm to convert a URI token into a list of Strings using this regex and the opposite operation of
 * assembling a URI token from a list of Strings which will later match the regex.
 */
public class RegexUriPathSegmentActionMapper extends DispatchingUriPathSegmentActionMapper {
    private static final long serialVersionUID = 4435578380164414638L;

    private final String parameterId;
    private final AbstractRegexToStringListParameterValueConverter valueListConverter;

    /**
     * Creates a new {@link RegexUriPathSegmentActionMapper}. The regex to be applied for this action mapper is defined
     * through the {@link AbstractRegexToStringListParameterValueConverter} that has to be passed into this constructor.
     * This regex will be applied to the URI tokens passed in to {@link #isResponsibleForToken(String)} to determine if
     * this action mapper is responsible for handling the given token.
     *
     * @param mapperName         the name of this mapper
     * @param parameterId        id of the {@link StringListUriParameter} which will contain the values captured by the
     *                           regular expression's capturing groups
     * @param valueListConverter the String list converter to be used
     * @throws NullPointerException when the {@code valueListConverter} is {@code null}
     */
    public RegexUriPathSegmentActionMapper(String mapperName, String parameterId,
                                           AbstractRegexToStringListParameterValueConverter valueListConverter) {
        super(mapperName);
        Preconditions.checkNotNull(valueListConverter);

        UriParameter<List<String>> parameter = new StringListUriParameter(parameterId, valueListConverter);
        registerURIParameter(parameter);
        parameter.setConverter(valueListConverter);
        this.parameterId = parameter.getId();
        this.valueListConverter = valueListConverter;
    }

    @Override
    protected Class<? extends UriActionCommand> interpretTokensImpl(CapturedParameterValues capturedParameterValues,
                                                                    String currentUriToken,
                                                                    List<String> uriTokens,
                                                                    Map<String, String> queryParameters,
                                                                    ParameterMode parameterMode) {
        Map<String, String> capturedValues = new HashMap<>();
        capturedValues.put(parameterId, currentUriToken);
        ParameterInterpreter interpreter = new ParameterInterpreter(getMapperName());
        interpreter.interpretParameters(getUriParameters(), capturedParameterValues, capturedValues);

        return super.interpretTokensImpl(capturedParameterValues, currentUriToken, uriTokens, queryParameters, parameterMode);
    }

    /**
     * Checks if this {@link RegexUriPathSegmentActionMapper} is responsible for handling the given URI token. It does
     * so by checking whether the token matches the assigned regular expression. If that is the case <code>true</code>
     * is returned.
     *
     * @return <code>true</code> if the given URI token will be handled by this action handler, i.e. if the given URI
     * token matches the regular expression of the {@link AbstractRegexToStringListParameterValueConverter} specified in
     * the constructor.
     */
    @Override
    public boolean isResponsibleForToken(String uriToken) {
        return valueListConverter.matches(uriToken);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This action mapper expects a {@link ParameterValue} of type {@code List<String>} to be provided in the {@code
     * capturedParameterValues}. This list of Strings is fed into the {@link AbstractRegexToStringListParameterValueConverter}
     * specified in the constructor to create the concrete path segment name for this action mapper.
     *
     * @param capturedParameterValues the set of {@link ParameterValue}s to be used to parameterize the generated URI
     *                                fragment
     * @return the path segment name for this action mapper to be used to assemble a URI fragment. This path segment
     * name is assembled from a list of Strings conveyed through a {@link ParameterValue} identified by this action
     * mapper's parameter ID. The {@link ParameterValue}'s list of Strings correspond to the capturing groups of the
     * regular expression defined for this mapper.
     * @throws IllegalArgumentException if there is a {@link ParameterValue} present for this action mapper in the given
     *                                  {@code capturedParameterValues} but this {@link ParameterValue} object has no
     *                                  value.
     */
    @Override
    protected String getPathSegmentNameForAssemblingUriFragment(CapturedParameterValues capturedParameterValues) {
        final ParameterValue<List<String>> value = capturedParameterValues.removeValueFor(getMapperName(), parameterId);
        if (value != null && value.hasValue()) {
            return valueListConverter.convertToString(value.getValue());
        } else {
            throw new IllegalArgumentException("The value set for this mapper is invalid. Mapper: " + toString());
        }
    }

    @Override
    public String getSegmentInfo() {
        return String.format("<%s[regex: '%s']>", getMapperName(), valueListConverter.getRegex());
    }
}
