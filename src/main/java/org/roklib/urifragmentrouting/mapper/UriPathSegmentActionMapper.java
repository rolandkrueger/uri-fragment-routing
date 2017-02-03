package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.UriActionMapperTree;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface for defining path segment action mappers. These mappers are responsible for handling the individual tokens
 * of a URI fragment during the URI fragment interpretation process. Every path segment action mapper is responsible for
 * exactly one path segment of a URI fragment. For example, if the URI fragment <tt>/admin/profiles/users</tt> is to be
 * interpreted, three action mappers are needed which are responsible for the path segments <tt>admin</tt>,
 * <tt>profiles</tt>, and <tt>users</tt>, respectively.
 * <p>
 * A path segment action mapper may either pass the interpretation process on to its sub-mappers or directly map a path
 * segment on a {@link UriActionCommandFactory}. <h1>Mapper Name</h1> A URI path segment action mapper has a unique
 * mapper name. Using this name, a {@link UriPathSegmentActionMapper} instance can be uniquely identified in a {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree UriActionMapperTree}. This name is also used to identify parameter
 * values in a {@link CapturedParameterValues} object which belong to a particular action mapper.
 * <p>
 * A mapper name can only be assigned to one and only one action mapper in a URI action mapper tree. If the same mapper
 * name is used for more than one action mapper, an exception will be raised. However, every implementation class of
 * this interface provides additional constructors which allow to define a unique mapper name and an alternative path
 * segment name for the path segment the mapper is responsible for.
 * <p>
 * Typically, the mapper name is also used to specify the URI path segment for which the action mapper is responsible.
 * For example, for interpreting the following URI fragment
 * <p>
 * <tt>/user/profile</tt>
 * <p>
 * two action mappers with action names <tt>user</tt> and <tt>profile</tt> have to be added to the URI action mapper
 * tree. There are variants of path segment mappers available which allow to define a pattern for specifying the path
 * segments they are responsible for. <h1>URI Action Commands</h1> When a URI fragment is interpreted by the {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree UriActionMapperTree}, this interpretation process tries to resolve
 * the currently interpreted URI fragment to a {@link UriActionCommandFactory}. This factory will be used to create a
 * {@link UriActionCommand} object which will then be executed as the result of the interpretation process. The action
 * command factory will typically be provided by the path segment action mapper which is responsible for handling the
 * last URI token of a URI fragment. In the example above (URI fragment <tt>/user/profile</tt>), this will be the action
 * mapper responsible for the <tt>profile</tt> token. By that, each URI fragment can be mapped on one particular action
 * command factory.
 * <p>
 * Each URI path segment action mapper can have exactly one instance of a {@link UriActionCommandFactory} that will be
 * provided when this action mapper is the last in line when a URI fragment is interpreted.
 * <p>
 * Setting an action command factory for an action mapper is optional. If an action mapper is the last in line while
 * interpreting a URI fragment and it cannot provide an action command factory, the default action command factory for
 * the {@link org.roklib.urifragmentrouting.UriActionMapperTree UriActionMapperTree} is used if such has been defined.
 * <h1>URI Parameters</h1>Any number of {@link UriParameter}s can be registered on a path segment action mapper with
 * {@link #registerURIParameter(UriParameter)}. Using URI parameters, additional variable context-specific data can be
 * added to a URI fragment which can be used to parameterize the execution of the {@link UriActionCommand}.
 *
 * @see UriParameter
 * @see UriActionCommandFactory
 * @see UriActionCommand
 * @see DispatchingUriPathSegmentActionMapper
 * @see SimpleUriPathSegmentActionMapper
 */
public interface UriPathSegmentActionMapper extends Serializable {

    /**
     * Interprets the given list of URI fragment tokens to find an action command factory which is used to create an
     * action command object. This object will then be executed for the currently interpreted URI fragment at the end of
     * the interpretation process. The given list of {@code uriTokens} is generated at the beginning of the
     * interpretation process from the currently interpreted URI fragment using the active {@link
     * org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy UriTokenExtractionStrategy}. URI tokens are
     * interpreted using the <em>Chain of Responsibility</em> design pattern, i. e. each URI path segment action mapper
     * in the URI action mapper tree is responsible for handling exactly one of these URI tokens. The remaining tokens
     * from this list may identify sub-mappers of this action mapper and/or define some or all of the parameter values
     * for this action mapper.
     * <p>
     * Take for example the following URI fragment:
     * <p>
     * <tt> /users/profile/id/42 </tt>
     * <p>
     * Here, the path segments <tt>users</tt> and <tt>profile</tt> are the mapper names of two action mappers. The
     * mapper responsible for the <tt>users</tt> path segment is a {@link DispatchingUriPathSegmentActionMapper} while
     * the mapper for <tt>profile</tt> is a {@link SimpleUriPathSegmentActionMapper} with one registered parameter
     * <tt>id</tt>. When this URI fragment is interpreted, it is split by the {@link
     * org.roklib.urifragmentrouting.strategy.DirectoryStyleUriTokenExtractionStrategyImpl
     * DirectoryStyleUriTokenExtractionStrategyImpl} into the following list of URI tokens:
     * <p>
     * <tt> {"users", "profile", "id", "42"} </tt>
     * <p>
     * This list is passed as the argument {@code uriTokens} into this method. The URI fragment interpretation process
     * begins with the first responsible action mapper which is the one responsible for <tt>users</tt>. This action
     * mapper removes the <tt>users</tt> String from the URI token list and passes the interpretation process on to its
     * sub-mapper with mapper name <tt>profile</tt>. This sub-mapper in turn removes its name from the list and tries to
     * salvage a parameter value from the remaining tokens for its registered parameter <tt>id</tt>. It finds a
     * corresponding parameter name and value, converts these into a {@link org.roklib.urifragmentrouting.parameter.value.ParameterValue
     * ParameterValue} object, adds this to the {@code capturedParameterValues} and returns a {@link
     * UriActionCommandFactory} object since the token list is now empty.
     *
     * @param capturedParameterValues the current set of parameter values which have already been converted from their
     *                                String representations as salvaged from the current set of URI tokens. For all URI
     *                                parameters registered on this action mapper, this method tries to find parameter
     *                                values from the current set of {@code uriTokens} and {@code queryParameters}. Such
     *                                values are converted and added to the {@code capturedParameterValues}.
     * @param currentUriToken         the URI token which is currently being interpreted by this action mapper
     * @param uriTokens               the list of URI tokens which still have to be interpreted. Tokens which have
     *                                already been interpreted, either because they identify the current action mapper
     *                                or because they belong to one of the URI parameters registered with this action
     *                                mapper, have to be removed from this list, so that this list will be empty at the
     *                                end of the interpretation process.
     * @param queryParameters         map of parameter values which were appended to the currently interpreted URI
     *                                fragment in Query Parameter Mode. May be empty.
     * @param parameterMode           the {@link ParameterMode} to be used when capturing the URI parameters from the
     *                                URI token list and query parameter map
     *
     * @return an action command factory object which creates the URI action command for this action mapper or for one
     * of this mapper's sub-mappers. If no such factory could be found, {@code null} is returned.
     */
    UriActionCommandFactory interpretTokens(CapturedParameterValues capturedParameterValues,
                                            String currentUriToken,
                                            List<String> uriTokens,
                                            Map<String, String> queryParameters,
                                            ParameterMode parameterMode);

    /**
     * Returns the mapper name which has been defined for this action mapper. This must not be {@code null} or the empty
     * String.
     *
     * @return the mapper name
     */
    String getMapperName();

    /**
     * Set the action command factory object provided by this action mapper. This may be {@code null}.
     *
     * @param commandFactory the action command factory object for by this action mapper
     */
    void setActionCommandFactory(UriActionCommandFactory commandFactory);

    /**
     * Returns the action command factory for this mapper. This may be {@code null}.
     *
     * @return the action command factory for this mapper or {@code null} if no such object has been defined
     */
    UriActionCommandFactory getActionCommandFactory();

    /**
     * Register a URI parameter with this action mapper. Only URI parameters can be registered on an action mapper which
     * have a non-overlapping set of parameter names and don't have the same id.
     *
     * @param parameter the parameter to be registered on this action mapper
     *
     * @throws IllegalArgumentException if another parameter with the same parameter name is already registered on this
     *                                  action mapper or if another parameter is already registered on this action
     *                                  mapper which provides the same parameter name(s) as the specified parameter
     * @throws NullPointerException     if the parameter is {@code null}
     * @see UriParameter#getParameterNames()
     * @see UriParameter#getId()
     */
    void registerURIParameter(UriParameter<?> parameter);

    /**
     * Returns the action mapper to which this mapper has been added as a sub-mapper. The parent mapper is usually a
     * {@link DispatchingUriPathSegmentActionMapper}.
     *
     * @return the parent mapper for this action mapper
     */
    UriPathSegmentActionMapper getParentMapper();

    /**
     * Attach this action mapper to its parent mapper.
     *
     * @param parent the parent action mapper of this mapper
     */
    void setParentMapper(UriPathSegmentActionMapper parent);

    /**
     * Register the given mapper name in the set of mapper names used in the current action mapper tree. This method
     * simply passes the given sub-mapper name on to the same method of the parent mapper so that this value bubbles up
     * to the root of the action mapper tree where it is added into the set of all mapper names which are currently in
     * use.
     * <p>
     * An action mapper can only be added as a sub-mapper to one action mapper. In other words, an action mapper can
     * only have one parent.
     *
     * @param subMapperName name of a sub-mapper for this action mapper
     *
     * @throws IllegalArgumentException if the given mapper name is already in use by any other action mapper in the
     *                                  current action mapper tree
     */
    void registerSubMapperName(String subMapperName);

    /**
     * Assembles all relevant data from this action mapper for creating a parameterized URI fragment which will resolve
     * to this action mapper. This is the opposite operation to the interpretation process of a given URI fragment.
     * While this interpretation process tries to resolve a given URI fragment to a set of parameter values and a URI
     * action command factory, assembling a URI fragment for an action mapper goes the opposite direction. It
     * recursively creates a URI fragment together with a set of predefined parameter values. Such a URI fragment can
     * then be used to add a HTML link in a web application, for instance.
     * <p>
     * This assembly process is passed a set of {@link org.roklib.urifragmentrouting.parameter.value.ParameterValue
     * ParameterValue}s contained in a {@link CapturedParameterValues} object. These parameters have to be included into
     * the generated URI fragment.
     * <p>
     * Implementations of this method need to do two things: Firstly, they have to add their path segment name to the
     * list of {@code uriTokens}. Secondly, they have to add the parameter values contained in the given {@code
     * parameterValues} to the {@code uriTokens} list. The latter task is only necessary if the specified {@link
     * ParameterMode} is not {@link ParameterMode#QUERY}, since in query mode parameters are not contained in the path
     * segments.
     *
     * @param parameterValues The parameter values to be added to the generated URI fragment
     * @param uriTokens       The list of URI tokens to which this action mapper is supposed to add its path segment
     *                        name and URI parameters (the latter depends on the given {@code ParameterMode}
     * @param parameterMode   The {@link ParameterMode} to be used to append URI parameter values to the generated URI
     *                        fragment.
     */
    void assembleUriFragmentTokens(CapturedParameterValues parameterValues, List<String> uriTokens, ParameterMode parameterMode);

    /**
     * Check if this action mapper is responsible for the given token from the currently interpreted URI fragment. This
     * token is extracted from the currently interpreted URI fragment using the {@link
     * org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy UriTokenExtractionStrategy}. The action mapper
     * has to decide whether it is responsible for handling this token. If this is the case, the token interpretation
     * process will be passed on to this mapper.
     * <p>
     * In the simplest case, the given token is simply matched against this mapper's name. This is done by default. For
     * example, if this action mapper is responsible for handling path segments with name <tt>users</tt> then it is
     * responsible for handling the last URI token of the following URI fragment:
     * <p>
     * <tt> /admin/profiles/users </tt>
     * <p>
     * Classes implementing this interface may define more complex token matching methods, such as matching with a
     * regular expression (e. g. {@link RegexUriPathSegmentActionMapper}).
     *
     * @param uriToken one of the String tokens extracted from the currently interpreted URI fragment using the {@link
     *                 org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy UriTokenExtractionStrategy}
     *
     * @return {@code true} if this action mapper is responsible for handling the given token
     */
    boolean isResponsibleForToken(String uriToken);

    /**
     * Assemble an overview of this action mapper and its sub-mappers and add this to the list passed as an argument. If
     * this action mapper is a simple (non-dispatching) action mapper then the current mapper name, the path segment it
     * is responsible for, and an overview of all registered parameters has to be added to the given path String
     * argument. The result will then be added to the given String list.
     * <p>
     * If this action mapper is a dispatching action mapper, this process will be done recursively for all
     * sub-mappers.
     * <p>
     * This method is recursively called by {@link UriActionMapperTree#getMapperOverview()} in order to obtain an
     * overview of all action mappers and URI parameters available for this {@link UriActionMapperTree}. This is useful
     * for logging purposes.
     *
     * @param path               the path to which this action mapper has to add its own info. This path already
     *                           recursively contains information about all this mapper's parent action mappers
     * @param mapperOverviewList list to which the mapper overview of this action mapper is to be added
     *
     * @see UriActionMapperTree#getMapperOverview()
     */
    void getMapperOverview(String path, List<String> mapperOverviewList);

    /**
     * Returns an informational String about the path segment name of this action mapper. This is either the path
     * segment name itself or the mapper name added to the path segment name if these two differ. This method is used
     * for creating and logging an overview of the current {@link org.roklib.urifragmentrouting.UriActionMapperTree
     * UriActionMapperTree} with {@link UriActionMapperTree#getMapperOverview()}.
     *
     * @return an informational String about the path segment name of this action mapper for logging purposes
     */
    String getSegmentInfo();

    /**
     * Returns the complete path from the current mapper tree's root up to this action mapper. An exemplary return value
     * for some sub-mapper is "/admin/users/profile". This is mainly used for logging and debugging purposes.
     *
     * @return the complete path for this action mapper starting from the root
     */
    default String pathFromRoot() {
        final String rootPath = getParentMapper().pathFromRoot().equals("/") ? "" : getParentMapper().pathFromRoot() + "/";
        return rootPath + getSegmentInfo();
    }
}
