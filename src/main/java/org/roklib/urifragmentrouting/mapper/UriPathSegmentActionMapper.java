package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommand;
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
 * segment on a {@link UriActionCommand}. <h1>Mapper Name</h1> A URI path segment action mapper has a unique mapper
 * name. Using this name, a {@link UriPathSegmentActionMapper} instance can be uniquely identified in a {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree}. This name is also used to identify parameter values in a {@link
 * CapturedParameterValues} object which belong to a particular action mapper.
 * <p>
 * A mapper name can only be assigned to one and only one action mapper in a URI action mapper tree. If the same mapper
 * name is used for more than one action mapper, an exception will be thrown. However, every implementation class of
 * this interface provides additional constructors which allow to define a unique mapper name and an alternative path
 * segment name or a pattern for matching path segment names for the path segment the mapper is responsible for.
 * <p>
 * Typically, the mapper name is also used to specify the URI path segment for which the action mapper is responsible.
 * For example, for interpreting the following URI fragment
 * <p>
 * <tt>/user/profile</tt>
 * <p>
 * two action mappers with action names <tt>user</tt> and <tt>profile</tt> have to be added to the URI action mapper
 * tree. There are variants of path segment mappers available which allow to define a pattern for specifying the path
 * segments they are responsible for. <h1>URI Action Commands</h1> When a URI fragment is interpreted by the {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree}, this interpretation process tries to resolve the currently
 * interpreted URI fragment to a {@link UriActionCommand} which will then be executed as the result of this process.
 * This action command will typically be provided by the path segment action mapper which is responsible for handling
 * the last URI token of a URI fragment. In the example above (URI fragment <tt>/user/profile</tt>), this will be the
 * action mapper responsible for the <tt>profile</tt> token. By that, each URI fragment can be mapped on one particular
 * action command class.
 * <p>
 * Each URI path segment action mapper can have exactly one instance of a {@link UriActionCommand} class that will be
 * provided when this action mapper is the last in line while interpreting a URI fragment. Action commands are provided
 * as {@link Class} objects. The {@link org.roklib.urifragmentrouting.UriActionMapperTree} will instantiate and execute
 * them at the end of the URI fragment interpretation process.
 * <p>
 * Defining an action command class is optional. If an action mapper is the last in line while interpreting a URI
 * fragment and it cannot provide an action command class the default action command for the {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree} is executed if such has been defined. <h1>URI Parameters</h1>You
 * may register any number of {@link UriParameter}s on a path segment action mapper with {@link
 * #registerURIParameter(UriParameter)}. Using URI parameters, additional variable context-specific data can be added to
 * a URI fragment which can be used to parameterize the execution of the {@link UriActionCommand}.
 *
 * @see UriParameter
 * @see UriActionCommand
 * @see DispatchingUriPathSegmentActionMapper
 * @see SimpleUriPathSegmentActionMapper
 */
public interface UriPathSegmentActionMapper extends Serializable {
    Class<? extends UriActionCommand> interpretTokens(CapturedParameterValues capturedParameterValues,
                                                      String currentMapperName, List<String> uriTokens,
                                                      Map<String, String> queryParameters,
                                                      ParameterMode parameterMode);

    /**
     * Returns the mapper name which has been defined for this action mapper. This must not be null or the empty
     * String.
     *
     * @return the mapper name
     */
    String getMapperName();

    /**
     * Set the action command class to be provided by this action command. This class may be null.
     *
     * @param command the action command class for this action mapper
     */
    void setActionCommandClass(Class<? extends UriActionCommand> command);

    /**
     * Returns the action command class for this mapper. This method may return null.
     *
     * @return the action command class for this mapper or null if no such class has been defined
     */
    Class<? extends UriActionCommand> getActionCommand();

    /**
     * Register a URI parameter with this action mapper. Only URI parameters can be registered on an action mapper which
     * have a non-overlapping set of parameter names and don't have the same id.
     *
     * @param parameter the parameter to be registered on this action mapper
     * @throws IllegalArgumentException if another parameter with the same parameter id is already registered on this
     *                                  action mapper or if another parameter is already registered on this action
     *                                  mapper which provides the same parameter name(s) as the specified parameter
     * @throws NullPointerException     if the parameter is null
     * @see UriParameter#getParameterNames()
     * @see UriParameter#getId()
     */
    void registerURIParameter(UriParameter<?> parameter);

    /**
     * Returns the action mapper to which this mapper has been added as a sub-mapper. The parent mapper is typically a
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
     * simply passes on the given sub-mapper name to the same method of the parent mapper so that this value bubbles up
     * to the root of the action mapper tree where it is added into the set of all mapper names currently in use.
     *
     * @param subMapperName name of a sub-mapper for this action mapper
     * @throws IllegalArgumentException if the given mapper name is already in use by any other action mapper in the
     *                                  current action mapper tree
     */
    void registerSubMapperName(String subMapperName);

    void assembleUriFragmentTokens(CapturedParameterValues capturedParameterValues, List<String> tokens, ParameterMode parameterMode);

    /**
     * Check if this action mapper is responsible for the given token from the currently interpreted URI fragment. This
     * token is extracted from the currently interpreted URI fragment using the {@link
     * org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy}. This action mapper has to decide whether it
     * is responsible for this token. If this is the case the token interpretation process will be passed on to this
     * mapper.
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
     *                 org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy}
     * @return true if this action mapper is responsible for handling the given token
     */
    boolean isResponsibleForToken(String uriToken);

    /**
     * Assemble an overview of this action mapper and its sub-mappers and add this to the specified list. If this action
     * mapper is a simple (non-dispatching) action mapper then the current mapper name, the path segment it is
     * responsible for, and an overview of all registered parameters has to be added to the given path String argument.
     * The result will then be added to the given String list.
     * <p>
     * If this action mapper is a dispatching action mapper, this process has to be done recursively for all
     * sub-mappers.
     * <p>
     * This method is recursively called by {@link UriActionMapperTree#getMapperOverview()} in order to print out an
     * overview of all action mappers and URI parameters available for this {@link UriActionMapperTree}. This is useful
     * for logging purposes.
     *
     * @param path               the path to which this action mapper has to add its own info. This path already
     *                           recursively contains information about all this mapper's parent action mappers
     * @param mapperOverviewList list to which the mapper overview of this action mapper is to be added
     * @see UriActionMapperTree#getMapperOverview()
     */
    void getMapperOverview(String path, List<String> mapperOverviewList);
}
