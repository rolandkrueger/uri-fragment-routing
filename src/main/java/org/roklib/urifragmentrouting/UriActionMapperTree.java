package org.roklib.urifragmentrouting;

import org.roklib.urifragmentrouting.helper.ActionCommandConfigurer;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.mapper.*;
import org.roklib.urifragmentrouting.parameter.AbstractSingleUriParameter;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.SingleValuedParameterFactory;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.strategy.DirectoryStyleUriTokenExtractionStrategyImpl;
import org.roklib.urifragmentrouting.strategy.QueryParameterExtractionStrategy;
import org.roklib.urifragmentrouting.strategy.StandardQueryNotationQueryParameterExtractionStrategyImpl;
import org.roklib.urifragmentrouting.strategy.UriTokenExtractionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class is the central entry point into the URI fragment routing framework. It represents and manages the complete
 * set of URI fragments which are available for a web application. This class is chiefly responsible for the following
 * two tasks: <ul> <li>It interprets a URI fragment visited by a user, extracts parameter values from this URI fragment,
 * and resolves it into a URI action command (see {@link UriActionCommand}) which is eventually executed.</li> <li>It
 * creates fully parameterized URI fragments for any of the {@link UriPathSegmentActionMapper}s contained in this tree
 * which can be used for defining link targets in a web application.</li> </ul>
 * <p>
 * When a URI fragment is to be interpreted by this URI action mapper tree, that fragment has to be passed to method
 * {@link #interpretFragment(String)} or {@link #interpretFragment(String, Object, boolean)} (the latter method is used
 * when an application-specific context object is to be passed along to the URI action command to be found for the URI
 * fragment). The URI fragment is then split into a token list to be recursively interpreted by the action mappers
 * registered on this action mapper tree. Each action mapper is responsible for handling one of the URI tokens which
 * represent the individual path segments of the currently interpreted URI fragment.The strategy for splitting a URI
 * fragment into a URI token list is defined by the {@link UriTokenExtractionStrategy} set for this mapper tree. For
 * example, if the following URI is to be interpreted
 * <pre>
 * http://www.example.com/myapp#!/user/home/messages
 * </pre>
 * for a web application running under context <code>http://www.example.com/myapp/</code>, the URI fragment to be
 * interpreted by the action mapper tree is <code>/user/home/messages</code>. This is split into three individual URI
 * tokens <code>user</code>, <code>home</code>, and <code>messages</code>, in that order. Each URI token stands for one
 * individual path segment of the URI fragment. To interpret these tokens, the URI action mapper tree passes this token
 * list to the sub-mapper which is responsible for handling the first path segment <code>user</code>. This action mapper
 * in turn passes the remaining tokens to one of its own sub-mapper which is responsible for handling the
 * <code>home</code> path segment. The URI token list is thus interpreted recursively by the sub-mappers of the mapper
 * tree until eventually the mapper responsible for the final URI token is reached. This action mapper will return its
 * action command factory which will then be used to create an action command object. This command is subsequently
 * executed as the result of interpreting the full URI fragment.
 * <p>
 * If no final mapper could be found for the remaining URI tokens in the list, either nothing is done or the default
 * action command factory is used. <h1>Default action command factory</h1> A default action command factory can be
 * specified with {@link #setDefaultActionCommandFactory(UriActionCommandFactory)}. This default factory is used when
 * the current URI fragment could not successfully be interpreted. It will be used when the interpretation process of
 * some URI fragment does not yield any action command factory. Such a default factory could be used to show a Page Not
 * Found error page to the user, for example.
 * <p>
 * <h1>URI parameters</h1> Besides specifying a path structure of URI fragments which point to individual action command
 * classes, parameter values can be added to each path segment of a URI fragment. By that, it is possible to
 * parameterize the action command objects which are executed as the last step of interpreting a URI fragment. For
 * example, to parameterize the URI fragment shown above, one could add a user ID to the <code>user</code> path
 * segment:
 * <pre>
 *     /user/id/42/home/messages
 * </pre>
 * The two path segments <code>id</code> and <code>42</code> together form a URI parameter value. URI parameters can be
 * registered on {@link UriPathSegmentActionMapper} instances with method {@link UriPathSegmentActionMapper#registerURIParameter(UriParameter)}.
 * In the example, the action mapper responsible for the <code>user</code> path segment has one registered parameter
 * <code>id</code>. During the interpretation process of a URI fragment, all parameter values found in the fragment are
 * extracted and converted into their respective data type using a {@link org.roklib.urifragmentrouting.parameter.converter.ParameterValueConverter
 * ParameterValueConverter}. URI parameters are represented by a class implementing the {@link UriParameter} interface.
 * A URI parameter does always belong to a {@link UriPathSegmentActionMapper}, i. e. it is possible to register the same
 * parameter on more than one action mapper. <h1>Parameter mode</h1> A {@link ParameterMode} can be set for a {@link
 * UriActionMapperTree} with {@link #setParameterMode(ParameterMode)}. This mode defines the way how a URI parameter is
 * contained in a URI fragment. There are three different modes available. The following list shows the example from
 * above using the three different parameter modes: <ul> <li>{@link ParameterMode#DIRECTORY_WITH_NAMES}:
 * <code>/user/id/42/home/messages</code></li> <li>{@link ParameterMode#DIRECTORY}:
 * <code>/user/42/home/messages</code></li> <li>{@link ParameterMode#QUERY}: <code>/user/home/messages?id=42</code></li>
 * </ul> (Note that the current {@link UriTokenExtractionStrategy} and {@link QueryParameterExtractionStrategy}
 * determine the syntax of the URI fragment path structure and query parameters. See below for details.)
 * <p>
 * By default, if not specified differently, the {@link ParameterMode} used by the {@link UriActionMapperTree} is {@link
 * ParameterMode#DIRECTORY_WITH_NAMES}. <h1>URI token and query parameter extraction strategy</h1> There are two
 * strategy interfaces which define how a URI fragment is disassembled into its constituents. Interface {@link
 * UriTokenExtractionStrategy} specifies the algorithm that splits a URI fragment into a list of URI tokens. Interface
 * {@link QueryParameterExtractionStrategy} specifies the algorithm that extracts URI parameters from the URI fragment
 * in query mode. By default, the {@link UriActionMapperTree} uses the two standard implementations of these interfaces.
 * These are {@link DirectoryStyleUriTokenExtractionStrategyImpl} and {@link StandardQueryNotationQueryParameterExtractionStrategyImpl},
 * respectively. <h1>Routing context</h1> When a URI fragment is interpreted by the {@link UriActionMapperTree}, a
 * custom routing context object can be passed along that interpretation process. This context object can be passed to
 * the URI action command which will be executed if this context is required by the action command (see annotation
 * {@link org.roklib.urifragmentrouting.annotation.RoutingContext RoutingContext}). Using this routing context object,
 * an application can pass application- and user-specific data to the URI action command. For example, a reference to
 * the current user session could be passed along with the routing context.
 * <p>
 * The routing context object can be specified with {@link #interpretFragment(String, Object, boolean)}. <h1>Thread
 * safety</h1> The URI fragment routing framework is thread-safe. This means that you typically have one
 * application-scoped instance of a {@link UriActionMapperTree} which contains all available URI fragments handled by a
 * single application. In other words, it is not necessary to store an instance of {@link UriActionMapperTree} in the
 * user session. <h1>Constructing a URI action mapper tree with a builder</h1>There are two options to construct a
 * {@link UriActionMapperTree}: First, you can instantiate all action mapper objects yourself, stick them together and
 * add all root action mappers to a {@link UriActionMapperTree} with <code>getRootActionMapper().addSubMapper(UriPathSegmentActionMapper)</code>.
 * The second option is to use the {@link UriActionMapperTree.UriActionMapperTreeBuilder} to build a URI action mapper
 * tree with a fluent API. To start building a URI action mapper tree, you start with the following code:
 * <pre>
 * MapperTreeBuilder builder = UriActionMapperTree.create().buildMapperTree();
 * </pre>
 * Use the {@link MapperTreeBuilder} to construct the complete URI action mapper tree. Building a mapper tree is a
 * depth-first approach. That is, you first create one of the root mappers, then add one of its sub-mappers, then in
 * turn add one of the sub-mappers's sub-mapper and so forth until you reach a leaf action mapper. By calling one of the
 * builders' <code>finishMapper()</code> methods, you can move one step up the mapping tree and start building the
 * siblings of the mapper you just finished.
 */
public class UriActionMapperTree {

    private static final Logger LOG = LoggerFactory.getLogger(UriActionMapperTree.class);
    /**
     * Action mapper name for the <em>root action mapper</em>. This is the action mapper at the root of this action
     * mapper tree. This action mapper is always available and is responsible for the root URI fragment '/'. If you want
     * to provide URI fragment parameter values to this mapper, you have to reference this root mapper using this
     * constant.
     * <p>
     * You can register a {@link UriParameter} on the root mapper either with <code>
     * uriActionMapperTree.getRootActionMapper().registerURIParameter(parameter); </code> or with method {@link
     * UriActionMapperTreeBuilder#registerRootActionMapperParameter(UriParameter)}.
     * <p>
     * Setting a parameter value for this root mapper could be done with a {@link CapturedParameterValues} object like
     * so: <code>capturedParameterValues.setValueFor(UriActionMapperTree.ROOT_MAPPER, parameterId, value)</code>
     * <p>
     * Receiving a parameter value for the root mapper in an annotated {@link UriActionCommand} method could be done
     * like so:
     * <pre>
     *    {@literal @}CapturedParameter(mapperName = ROOT_MAPPER, parameterName = "parameter")
     *     public void setRootMapperParameter(ParameterValue&lt;String&gt; value) {
     *         // ...
     *     }
     * </pre>
     */
    public static final String ROOT_MAPPER = "$rootMapper$";

    private ParameterMode parameterMode = ParameterMode.DIRECTORY_WITH_NAMES;
    private QueryParameterExtractionStrategy queryParameterExtractionStrategy;
    private UriTokenExtractionStrategy uriTokenExtractionStrategy;
    private UriActionCommandFactory defaultActionCommandFactory;

    /**
     * Base dispatching mapper that contains all root action mappers.
     */
    private final DispatchingUriPathSegmentActionMapper rootMapper;
    /**
     * Set comprising the mapper names of all action mappers contained in this URI action mapper tree.
     */
    private final Set<String> mapperNamesInUse;

    private UriActionMapperTree() {
        queryParameterExtractionStrategy = new StandardQueryNotationQueryParameterExtractionStrategyImpl();
        uriTokenExtractionStrategy = new DirectoryStyleUriTokenExtractionStrategyImpl();
        rootMapper = new DispatchingUriPathSegmentActionMapper(ROOT_MAPPER, "") {
            @Override
            public String toString() {
                return "[Root Dispatching Action Mapper]";
            }
        };
        rootMapper.setParentMapper(new AbstractUriPathSegmentActionMapper("") {
            private static final long serialVersionUID = 3744506992900879054L;

            protected UriActionCommandFactory interpretTokensImpl(final CapturedParameterValues capturedParameterValues,
                                                                  final String currentUriToken,
                                                                  final List<String> uriTokens,
                                                                  final Map<String, String> queryParameters,
                                                                  final ParameterMode parameterMode) {
                return null;
            }

            @Override
            public boolean isResponsibleForToken(final String uriToken) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void getMapperOverview(final String path, final List<String> mapperOverviewList) {
            }

            @Override
            public String pathFromRoot() {
                return "/";
            }

            @Override
            public void registerSubMapperName(final String subMapperName) {
                if (isMapperNameInUse(subMapperName)) {
                    throw new IllegalArgumentException("Mapper name '" + subMapperName + "' is already in use");
                }
                addUsedMapperName(subMapperName);
            }
        });
        mapperNamesInUse = new HashSet<>();
    }

    /**
     * Interpret the given fragment without using a context object. See {@link #interpretFragment(String, Object,
     * boolean)} for details.
     *
     * @param uriFragment the URI fragment to be interpreted
     *
     * @return the command object responsible for the given {@code uriFragment} or {@code null} if the fragment could
     * not be resolved to any action command factory
     * @see #interpretFragment(String, Object, boolean)
     */
    public UriActionCommand interpretFragment(final String uriFragment) {
        return interpretFragment(uriFragment, null);
    }

    /**
     * Interpret the given fragment using the specified context object. This method will execute any {@link
     * UriActionCommand} found for the given {@code uriFragment} right away. See {@link #interpretFragment(String,
     * Object, boolean)} for details.
     *
     * @param uriFragment the URI fragment to be interpreted
     * @param context     a custom defined context object which is passed to the action command object via a method
     *                    annotated with {@link org.roklib.urifragmentrouting.annotation.RoutingContext
     *                    RoutingContext}.
     * @param <C>         class of the context object
     *
     * @return the command object responsible for the given {@code uriFragment} or {@code null} if the fragment could
     * not be resolved to any action command factory. Note that this command object has already been executed by this
     * method.
     * @see #interpretFragment(String, Object, boolean)
     */
    public <C> UriActionCommand interpretFragment(final String uriFragment, final C context) {
        return interpretFragment(uriFragment, context, true);
    }

    /**
     * Interpret the given fragment using the specified context object. Interpreting a URI means extracting all URI
     * parameter values from the fragment and resolving the fragment to one distinct {@link UriActionCommandFactory}. If
     * such a command factory could be resolved, is is used to create an instance of an action command object which is
     * executed subsequently. Eventually, this command instance is returned from this method as a result.
     * <p>
     * For example, when the URI fragment {@code /admin/users/id/4711/profile} is interpreted by this method, the action
     * command factory defined for the action mapper responsible for the path segment 'profile' is used to create the
     * action command to be executed. All URI parameter values found while interpreting this fragment (in this case
     * {@code id=4711}) are collected in an object of type {@link CapturedParameterValues} and passed to the action
     * command object via methods annotated with {@link org.roklib.urifragmentrouting.annotation.AllCapturedParameters
     * AllCapturedParameters} or {@link org.roklib.urifragmentrouting.annotation.CapturedParameter CapturedParameter}.
     * <p>
     * The context object specified as the second parameter is passed to the action command object via a method
     * annotated with {@link org.roklib.urifragmentrouting.annotation.RoutingContext RoutingContext}. This may be an
     * arbitrary, application-defined object, so no restriction is imposed on this object.
     *
     * @param uriFragment    the URI fragment to be interpreted
     * @param context        a custom defined context object which is passed to the action command object via a method
     *                       annotated with {@link org.roklib.urifragmentrouting.annotation.RoutingContext
     *                       RoutingContext}.
     * @param executeCommand if {@code true}, the {@link UriActionCommand} found for the given URI fragment (if any)
     *                       will be executed right away. If {@code false}, the command object will not be executed but
     *                       only be returned by this method. In this case, the external caller is responsible for
     *                       executing this command.
     * @param <C>            type of the context object
     *
     * @return the command object responsible for the given {@code uriFragment} or {@code null} if the fragment could
     * not be resolved to any command factory. Depending on the given value for parameter {@code executeCommand}, this
     * command object will be executed by this method.
     */
    public <C> UriActionCommand interpretFragment(final String uriFragment, final C context, final boolean executeCommand) {
        final UUID uuid = UUID.randomUUID();
        LOG.info("[{}] interpretFragment() - INTERPRET - [ {} ] - CONTEXT={}", uuid, uriFragment, context == null ? "[]" : context);
        LOG.debug("[{}] interpreting fragment [ {} ] - PARAMETER_MODE={}", uuid, uriFragment, parameterMode);
        final CapturedParameterValues capturedParameterValues = new CapturedParameterValues();
        final UriActionCommandFactory actionCommandFactory =
                getActionCommandFactoryForUriFragment(capturedParameterValues,
                        uriFragment,
                        uriTokenExtractionStrategy.extractUriTokens(queryParameterExtractionStrategy.stripQueryParametersFromUriFragment(uriFragment)),
                        queryParameterExtractionStrategy.extractQueryParameters(uriFragment),
                        parameterMode,
                        uuid);

        if (actionCommandFactory != null) {
            final UriActionCommand actionCommandObject = createAndConfigureUriActionCommand(uriFragment, context, capturedParameterValues, actionCommandFactory);
            if (executeCommand) {
                LOG.debug("[{}] interpretFragment() - Running action command object {}", uuid, actionCommandObject);
                actionCommandObject.run();
            }
            return actionCommandObject;
        }
        LOG.debug("[{}] interpretFragment() - No action command class found for fragment '{}'", uuid, uriFragment);
        return null;
    }

    private <C> UriActionCommand createAndConfigureUriActionCommand(final String currentUriFragment,
                                                                    final C routingContext,
                                                                    final CapturedParameterValues capturedParameterValues,
                                                                    final UriActionCommandFactory uriActionCommandFactory) {
        ActionCommandConfigurer configurer = uriActionCommandFactory instanceof ActionCommandConfigurer ?
                (ActionCommandConfigurer) uriActionCommandFactory :
                new ActionCommandConfigurer(uriActionCommandFactory);

        configurer.passUriPathSegmentActionMapper();
        if (routingContext != null) {
            configurer.passRoutingContext(routingContext);
        }
        if (capturedParameterValues != null) {
            configurer.passAllCapturedParameters(capturedParameterValues);
            configurer.passCapturedParameters(capturedParameterValues);
        }
        if (currentUriFragment != null) {
            configurer.passUriFragment(currentUriFragment);
        }
        return configurer.createUriActionCommand();
    }

    /**
     * Set the parameter mode to be used for interpreting the visited URIs.
     *
     * @param parameterMode {@link ParameterMode} which will be used by {@link #interpretFragment(String, Object,
     *                      boolean)}
     */
    private void setParameterMode(final ParameterMode parameterMode) {
        this.parameterMode = parameterMode;
    }

    private void setQueryParameterExtractionStrategy(final QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
        Preconditions.checkNotNull(queryParameterExtractionStrategy);
        this.queryParameterExtractionStrategy = queryParameterExtractionStrategy;
    }

    private void setUriTokenExtractionStrategy(final UriTokenExtractionStrategy uriTokenExtractionStrategy) {
        Preconditions.checkNotNull(uriTokenExtractionStrategy);
        this.uriTokenExtractionStrategy = uriTokenExtractionStrategy;
    }

    /**
     * Creates a new {@link UriActionMapperTreeBuilder} which can be used to construct a complete URI action mapper
     * tree. The returned builder provides a fluent API which simplifies the building process for the user a lot since
     * the builders allow calling only those methods which make sense in the current context.
     *
     * @return a builder for constructing a {@link UriActionMapperTree}
     */
    public static UriActionMapperTreeBuilder create() {
        return new UriActionMapperTreeBuilder();
    }

    /**
     * Needed by unit tests.
     */
    Collection<UriPathSegmentActionMapper> getFirstLevelActionMappers() {
        return getRootActionMapper().getSubMapperMap().values();
    }

    /**
     * Assembles a URI fragment for the given action mapper which resolves to this action mapper's command factory when
     * interpreted by the mapper tree. If there are any action mappers in the path from the mapper tree's root to the
     * specified action mapper which have registered URI parameters, no values will be set for these parameters.
     * <p>
     * If you want to add concrete parameter values to the assembled URI fragment, you can pass these values along with
     * method {@link #assembleUriFragment(CapturedParameterValues, UriPathSegmentActionMapper)}.
     * <p>
     * For example, if you have a {@link SimpleUriPathSegmentActionMapper}, which is responsible for the path segment
     * 'profiles' and which has a parent {@link DispatchingUriPathSegmentActionMapper} responsible for the path segment
     * 'admin' then passing this {@link SimpleUriPathSegmentActionMapper} to this method would yield the URI fragment
     * {@code /admin/profiles}.
     *
     * @param forMapper action mapper for which an interpretable URI fragment is desired
     *
     * @return the URI fragment for the given action mapper
     */
    public String assembleUriFragment(final UriPathSegmentActionMapper forMapper) {
        return assembleUriFragment(new CapturedParameterValues(), forMapper);
    }

    /**
     * Assembles a URI fragment for the given action mapper which resolves to this action mapper's command factory when
     * interpreted by the mapper tree.
     * <p>
     * For example, if you have a {@link SimpleUriPathSegmentActionMapper}, which is responsible for the path segment
     * 'profiles' and which has a parent {@link DispatchingUriPathSegmentActionMapper} responsible for the path segment
     * 'admin' then passing this {@link SimpleUriPathSegmentActionMapper} to this method would yield the URI fragment
     * {@code /admin/profiles}.
     * <p>
     * If the specified action mapper or one of its parent action mappers has one or more registered URI parameters, the
     * concrete values for these parameters can be provided with the first parameter of type {@link
     * CapturedParameterValues}. E. g., if in the example above the action mapper responsible for 'profiles' has a
     * Integer-typed URI parameter registered with parameter ID 'id', a value for this parameter can be provided as
     * follows:
     * <pre>
     * CapturedParameterValues values = new CapturedParameterValues();
     * values.setValueFor("profiles", "id", ParameterValue.forValue("4711"));
     * mapperTree.assembleUriFragment(values, mapper);
     * </pre>
     * Depending on the {@link ParameterMode} used, the resulting URI fragment could be as follows: {@code
     * /admin/profiles/id/4711}
     * <p>
     * Note that all parameter values have to be provided with the {@link CapturedParameterValues} object. Any parameter
     * value which is not given in the {@link CapturedParameterValues} object will simply be left out from the URI
     * fragment.
     *
     * @param capturedParameterValues parameter values to be used for the registered URI parameters of the given action
     *                                mapper and all its parent mappers
     * @param forMapper               action mapper for which an interpretable URI fragment is desired
     *
     * @return the parameterized URI fragment for the given action mapper
     */
    public String assembleUriFragment(final CapturedParameterValues capturedParameterValues, final UriPathSegmentActionMapper forMapper) {
        Preconditions.checkNotNull(forMapper);
        final Stack<UriPathSegmentActionMapper> mapperStack = buildMapperStack(forMapper);

        final List<String> uriTokens = new LinkedList<>();
        while (!mapperStack.isEmpty()) {
            final UriPathSegmentActionMapper mapper = mapperStack.pop();
            mapper.assembleUriFragmentTokens(capturedParameterValues, uriTokens, parameterMode);
        }

        String queryParamSection = "";
        if (parameterMode == ParameterMode.QUERY) {
            queryParamSection = queryParameterExtractionStrategy.assembleQueryParameterSectionForUriFragment(capturedParameterValues.asQueryParameterMap());
        }

        return uriTokenExtractionStrategy.assembleUriFragmentFromTokens(uriTokens) + queryParamSection;
    }

    /**
     * Constructs a stack of action mappers where the first element on the stack is the specified action mapper itself,
     * with all its parent action mappers stacked upon it. The specified mapper's parent action mapper which is furthest
     * up the tree will be the uppermost element on the stack.
     *
     * @param forMapper action mapper for which a mapper stack is to be build
     *
     * @return a stack of action mappers where the first element on the stack is the specified mapper and the uppermost
     * element is the parent action mapper for this mapper which is furthest up the mapper tree.
     */
    private Stack<UriPathSegmentActionMapper> buildMapperStack(final UriPathSegmentActionMapper forMapper) {
        final Stack<UriPathSegmentActionMapper> stack = new Stack<>();

        UriPathSegmentActionMapper currentMapper = forMapper;
        do {
            stack.push(currentMapper);
            currentMapper = currentMapper.getParentMapper();
            if (currentMapper == null) {
                throw new IllegalArgumentException("given mapper instance is not part of the mapper tree");
            }
        } while (!getRootActionMapper().equals(currentMapper));

        stack.push(getRootActionMapper());
        return stack;
    }

    /**
     * Returns the {@link DispatchingUriPathSegmentActionMapper} which is the root action mapper of this URI action
     * mapper tree. This is a special action mapper since the path segment name it is responsible for is the empty
     * String. Thus, when the current URI fragment is to be interpreted by this URI action mapper tree, this URI
     * fragment is first passed to that root dispatching mapper. All URI action mappers which are responsible for the
     * first directory level of a URI fragment will have to be added to this root mapper as sub-mappers.
     *
     * @return the root dispatching mapper for this URI action mapper tree
     */
    public DispatchingUriPathSegmentActionMapper getRootActionMapper() {
        return rootMapper;
    }

    /**
     * Sets the default action command to be executed each time when no responsible action mapper could be found for
     * some URI fragment. If set to {@code null} no particular action is performed when no URI action command could be
     * found for the currently interpreted URI fragment.
     *
     * @param defaultActionCommandFactory default command to be executed when no URI action command could be found for
     *                                    the currently interpreted URI fragment. May be {@code null}.
     */
    public void setDefaultActionCommandFactory(final UriActionCommandFactory defaultActionCommandFactory) {
        this.defaultActionCommandFactory = defaultActionCommandFactory;
    }

    private UriActionCommandFactory getActionCommandFactoryForUriFragment(final CapturedParameterValues capturedParameterValues,
                                                                          final String uriFragment,
                                                                          final List<String> uriTokens,
                                                                          final Map<String, String> extractedQueryParameters,
                                                                          final ParameterMode parameterMode,
                                                                          final UUID uuid) {

        final UriActionCommandFactory commandFactory = rootMapper.interpretTokens(capturedParameterValues, null, uriTokens, extractedQueryParameters, parameterMode);

        if (commandFactory == null) {
            LOG.info("[{}] getActionCommandFactoryForUriFragment() - NOT_FOUND - No registered URI action mapper found or action factory for fragment: {}", uuid, uriFragment);
            if (defaultActionCommandFactory != null) {
                if (LOG.isInfoEnabled()) {
                    UriActionCommand command = defaultActionCommandFactory.createUriActionCommand();
                    LOG.info("[{}] getActionCommandFactoryForUriFragment() - NOT_FOUND - Using default action command: {}",
                            uuid, command == null ? "null" : command.getClass().getName());
                }
                return defaultActionCommandFactory;
            } else {
                return null;
            }
        }
        return commandFactory;
    }

    private boolean isMapperNameInUse(final String mapperName) {
        return mapperNamesInUse.contains(mapperName);
    }

    private void addUsedMapperName(final String mapperName) {
        mapperNamesInUse.add(mapperName);
    }

    /**
     * Assembles a list with the String representations of all URI action mappers which are either the leaves of this
     * tree or can provide an action command factory. These String representations also contain all relevant information
     * about the URI parameters registered on the action mappers and the action command classes created by the
     * registered action command factories. Such a list is useful for logging and debugging purposes. With this list,
     * the whole tree can be printed to the log.
     * <p>
     * An example for this is the following:
     * <pre>
     * /admin/users -&gt; org.roklib.urifragmentrouting.AssembleUriFragmentForMapperTest$SomeActionClass
     * /location[{Point2DUriParameter: id='coord', xParam='x', yParam='y'}] -&gt; SomeActionClass
     * /login -&gt; SomeActionClass
     * /profiles[{SingleStringUriParameter: id='type'}]/customer[{SingleIntegerUriParameter: id='id'},
     * {SingleStringUriParameter: id='lang'}] -&gt; SomeActionClass
     * </pre>
     *
     * @return an overview of all action mappers at the leaves of this mapper tree including information about all
     * registered URI parameters and the action command classes created by the registered action command factories
     */
    public List<String> getMapperOverview() {
        final List<String> result = new LinkedList<>();
        getRootActionMapper()
                .getSubMapperMap()
                .values()
                .forEach(mapper -> mapper.getMapperOverview("", result));
        return result;
    }

    public static class UriActionMapperTreeBuilder {
        final UriActionMapperTree uriActionMapperTree;

        private UriActionMapperTreeBuilder() {
            uriActionMapperTree = new UriActionMapperTree();
        }

        /**
         * Start building a URI action mapper tree. This method returns a builder object for constructing action mappers
         * which will be added to the root sub-tree action mapper.
         *
         * @return the root {@link MapperTreeBuilder} for building the action mapper objects on the first level of the
         * URI action mapper tree
         */
        public MapperTreeBuilder buildMapperTree() {
            LOG.debug("buildMapperTree() - Starting to build a mapper tree");
            return new MapperTreeBuilder(uriActionMapperTree, uriActionMapperTree.getRootActionMapper());
        }

        /**
         * Specify the {@link UriTokenExtractionStrategy} the constructed URI action mapper tree shall use.
         *
         * @param uriTokenExtractionStrategy the concrete {@link UriTokenExtractionStrategy} to be used
         *
         * @return this builder object
         * @see #setUriTokenExtractionStrategy(UriTokenExtractionStrategy)
         */
        public UriActionMapperTreeBuilder useUriTokenExtractionStrategy(final UriTokenExtractionStrategy uriTokenExtractionStrategy) {
            uriActionMapperTree.setUriTokenExtractionStrategy(uriTokenExtractionStrategy);
            return this;
        }

        /**
         * Specify the {@link QueryParameterExtractionStrategy} the constructed URI action mapper tree shall use.
         *
         * @param queryParameterExtractionStrategy the concrete {@link QueryParameterExtractionStrategy} to be used
         *
         * @return this builder object
         * @see #setQueryParameterExtractionStrategy(QueryParameterExtractionStrategy)
         */
        public UriActionMapperTreeBuilder useQueryParameterExtractionStrategy(final QueryParameterExtractionStrategy queryParameterExtractionStrategy) {
            uriActionMapperTree.setQueryParameterExtractionStrategy(queryParameterExtractionStrategy);
            return this;
        }

        /**
         * Specify the {@link ParameterMode} to be employed by the constructed URI action mapper tree.
         *
         * @param parameterMode the {@link ParameterMode} to be used
         *
         * @return this builder object
         * @see #setParameterMode(ParameterMode)
         */
        public UriActionMapperTreeBuilder useParameterMode(final ParameterMode parameterMode) {
            uriActionMapperTree.setParameterMode(parameterMode);
            return this;
        }

        /**
         * Specify the default {@link UriActionCommandFactory}  to be used by the constructed URI action mapper tree.
         *
         * @param defaultActionCommandClass the default {@link UriActionCommandFactory}
         *
         * @return this builder object
         * @see #setDefaultActionCommandFactory(UriActionCommandFactory)
         */
        public UriActionMapperTreeBuilder useDefaultActionCommandFactory(final UriActionCommandFactory defaultActionCommandClass) {
            uriActionMapperTree.setDefaultActionCommandFactory(defaultActionCommandClass);
            return this;
        }

        /**
         * Sets the action command factory to be used by the root mapper. The action command provided by this factory
         * will be executed when an empty URI fragment or the fragment "/" is interpreted.
         *
         * @param rootActionCommandFactory the action command factory to be used by the root mapper
         *
         * @return this builder object
         */
        public UriActionMapperTreeBuilder setRootActionCommandFactory(final UriActionCommandFactory rootActionCommandFactory) {
            uriActionMapperTree.getRootActionMapper().setActionCommandFactory(rootActionCommandFactory);
            return this;
        }

        /**
         * Registers a URI fragment parameter on the root action mapper. This is the action mapper at the root of this
         * action mapper tree. This action mapper is always available and is responsible for the root URI fragment '/'.
         * <p>
         * If you, for instance, register a String-typed parameter with name {@code lang} on the root mapper then this
         * parameter will be available for every URI fragment of your application as in the following examples:
         * {@code /lang/de/home}, {@code /lang/en/profile/user/john.doe}, or {@code /lang/sv/admin}
         *
         * @param parameter URI fragment parameter to be registered
         *
         * @return this builder object
         */
        public UriActionMapperTreeBuilder registerRootActionMapperParameter(UriParameter<?> parameter) {
            uriActionMapperTree.getRootActionMapper().registerURIParameter(parameter);
            return this;
        }
    }

    public static class MapperTreeBuilder {
        private final UriActionMapperTree uriActionMapperTree;
        private final DispatchingUriPathSegmentActionMapper currentDispatchingMapper;
        private MapperTreeBuilder parentBuilder;

        private MapperTreeBuilder(final UriActionMapperTree uriActionMapperTree,
                                  final DispatchingUriPathSegmentActionMapper currentDispatchingMapper,
                                  final MapperTreeBuilder parentBuilder) {
            this(uriActionMapperTree, currentDispatchingMapper);
            this.parentBuilder = parentBuilder;
        }

        private MapperTreeBuilder(final UriActionMapperTree uriActionMapperTree, final DispatchingUriPathSegmentActionMapper currentDispatchingMapper) {
            this.uriActionMapperTree = uriActionMapperTree;
            this.currentDispatchingMapper = currentDispatchingMapper;
        }

        /**
         * Finalize and build the URI action mapper tree.
         *
         * @return the fully constructed {@link UriActionMapperTree} ready to be used
         */
        public UriActionMapperTree build() {
            return uriActionMapperTree;
        }

        /**
         * Finishes the construction of the currently built URI action mapper. After calling this method, a sibling
         * action mapper can be constructed for the action mapper which has just been completed. For each action mapper
         * which is constructed starting with the {@link #map(String)} or {@link #mapSubtree(String)} method (or any of
         * the overloaded variants), This method has to be called exactly once to finalize the construction
         * of this mapper.
         *
         * @return a builder object
         */
        public MapperTreeBuilder finishMapper() {
            LOG.debug("finishMapper() - Finishing mapper {}. Path: {}", currentDispatchingMapper, currentDispatchingMapper.pathFromRoot());
            return parentBuilder == null ? this : parentBuilder;
        }

        /**
         * Adds a pre-built URI action mapper as sub-mapper to the currently built dispatching mapper.
         *
         * @param mapper the action mapper to be added
         *
         * @return this builder object
         */
        public MapperTreeBuilder addMapper(final UriPathSegmentActionMapper mapper) {
            currentDispatchingMapper.addSubMapper(mapper);
            return this;
        }

        /**
         * Start building a {@link SimpleUriPathSegmentActionMapper} with the given mapper name. When this action mapper
         * has completely been constructed, it will be added to its parent sub-tree action mapper by calling method
         * {@link SimpleMapperParameterBuilder#finishMapper()}. the parent sub-tree action mapper is specified by the
         * builder object on which this method is invoked.
         *
         * @param mapperName the mapper name for which the action mapper is responsible
         *
         * @return a builder object for constructing a {@link SimpleUriPathSegmentActionMapper}
         */
        public MapperBuilder map(final String mapperName) {
            return new MapperBuilder(this, currentDispatchingMapper, mapperName);
        }

        /**
         * Start building a sub-tree mapper using the specified mapper name (see {@link
         * DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)}).
         * <p>
         * Take for example the following URI action mapper tree:
         * <p>
         * <pre>
         * /admin/users/profile
         * /admin/settings
         * </pre>
         * <p>
         * In this tree, the action mappers responsible for the path segments 'admin' and 'users' are dispatching action
         * mappers and can be constructed with the builder returned by this method.
         * <p>
         * The {@link DispatchingUriPathSegmentActionMapper} constructed by this method is passed to the specified
         * {@link Consumer} object. This consumer can then further process the action mapper object.
         * <p>
         * One common use case for this is to add the action mapper to some hash map so that it can later be accessed by
         * an application. Access to the action mappers is necessary for the configuration and assembly of URI fragments
         * to be used in hyperlinks (see {@link #assembleUriFragment(CapturedParameterValues,
         * UriPathSegmentActionMapper)}).
         *
         * @param mapperName  the mapper name for this dispatching action mapper (see {@link
         *                    DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String,
         *                    String)}). Using this method, both the mapper and the segment name for the constructed
         *                    sub-tree mapper are the same.
         * @param segmentName the segment name for this dispatching action mapper (see {@link
         *                    DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String,
         *                    String)}). In the example above, this may be 'admin' or 'users'.
         * @param consumer    a {@link Consumer} to which the finished {@link DispatchingUriPathSegmentActionMapper}
         *                    object is passed for further processing
         *
         * @return a builder object for further configuring the currently constructed {@link
         * DispatchingUriPathSegmentActionMapper}.
         * @see DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String, String)
         */
        public SubtreeMapperBuilder mapSubtree(final String mapperName, final String segmentName, final Consumer<UriPathSegmentActionMapper> consumer) {
            final SubtreeMapperBuilder subtreeBuilder = mapSubtree(mapperName, segmentName);
            consumer.accept(new ImmutableActionMapperWrapper(subtreeBuilder.dispatchingMapper));
            return subtreeBuilder;
        }

        /**
         * Start building a sub-tree mapper using the specified mapper name (see {@link
         * DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)}).
         * <p>
         * Take for example the following URI action mapper tree:
         * <p>
         * <pre>
         * /admin/users/profile
         * /admin/settings
         * </pre>
         * <p>
         * In this tree, the action mappers responsible for the path segments 'admin' and 'users' are dispatching action
         * mappers and can be constructed with the builder returned by this method.
         * <p>
         * The {@link DispatchingUriPathSegmentActionMapper} constructed by this method is passed to the specified
         * {@link Consumer} object. This consumer can then further process the action mapper object.
         * <p>
         * One common use case for this is to add the action mapper to some hash map so that it can later be accessed by
         * an application. Access to the action mappers is necessary for the configuration and assembly of URI fragments
         * to be used in hyperlinks (see {@link #assembleUriFragment(CapturedParameterValues,
         * UriPathSegmentActionMapper)}).
         *
         * @param mapperName the mapper name for this dispatching action mapper (see {@link
         *                   DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)}).
         *                   Using this method, both the mapper and the segment name for the constructed sub-tree mapper
         *                   are the same.
         * @param consumer   a {@link Consumer} to which the finished {@link DispatchingUriPathSegmentActionMapper}
         *                   object is passed for further processing
         *
         * @return a builder object for further configuring the currently constructed {@link
         * DispatchingUriPathSegmentActionMapper}.
         * @see DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)
         */
        public SubtreeMapperBuilder mapSubtree(final String mapperName, final Consumer<UriPathSegmentActionMapper> consumer) {
            return mapSubtree(mapperName, mapperName, consumer);
        }

        /**
         * Start building a sub-tree mapper using the specified mapper and segment name (see {@link
         * DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String, String)}).
         * <p>
         * Take for example the following URI action mapper tree:
         * <p>
         * <pre>
         * /admin/users/profile
         * /admin/settings
         * </pre>
         * <p>
         * In this tree, the action mappers responsible for the path segments 'admin' and 'users' are dispatching action
         * mappers and can be constructed with the builder returned by this method.
         *
         * @param mapperName  the mapper name for this dispatching action mapper (see {@link
         *                    DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String,
         *                    String)})
         * @param segmentName the segment name for this dispatching action mapper (see {@link
         *                    DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String,
         *                    String)}). In the example above, this may be 'admin' or 'users'.
         *
         * @return a builder object for further configuring the currently constructed {@link
         * DispatchingUriPathSegmentActionMapper}.
         * @see DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String, String)
         */
        public SubtreeMapperBuilder mapSubtree(final String mapperName, final String segmentName) {
            LOG.debug("mapSubtree() - Building a subtree for mapper name '{}', segment name '{}' on tree {}", mapperName, segmentName, currentDispatchingMapper);
            final DispatchingUriPathSegmentActionMapper dispatchingMapper = new DispatchingUriPathSegmentActionMapper(mapperName, segmentName);
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }

        /**
         * Start building a sub-tree mapper using the specified mapper name (see {@link
         * DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)}).
         * <p>
         * Take for example the following URI action mapper tree:
         * <p>
         * <pre>
         * /admin/users/profile
         * /admin/settings
         * </pre>
         * <p>
         * In this tree, the action mappers responsible for the path segments 'admin' and 'users' are dispatching action
         * mappers and can be constructed with the builder returned by this method.
         *
         * @param mapperName the mapper name for this dispatching action mapper (see {@link
         *                   DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)}).
         *                   Using this method, both the mapper and the segment name for the constructed sub-tree mapper
         *                   are the same.
         *
         * @return a builder object for further configuring the currently constructed {@link
         * DispatchingUriPathSegmentActionMapper}.
         * @see DispatchingUriPathSegmentActionMapper#DispatchingUriPathSegmentActionMapper(String)
         */
        public SubtreeMapperBuilder mapSubtree(final String mapperName) {
            return mapSubtree(mapperName, mapperName);
        }

        /**
         * Add the given preconfigured {@link DispatchingUriPathSegmentActionMapper} to the currently constructed
         * dispatching action mapper. This method can be used to add custom-built dispatching action mappers to the
         * current action mapper tree. This is useful in cases when the builder objects available from this API are not
         * flexible enough or when own sub-classes of {@link DispatchingUriPathSegmentActionMapper} shall be used (e. g.
         * a {@link org.roklib.urifragmentrouting.mapper.RegexUriPathSegmentActionMapper
         * RegexUriPathSegmentActionMapper}). The given dispatching action mapper does not necessarily need to have all
         * sub-tree mappers readily configured and added. All required sub-tree mappers to be added to the given
         * dispatching action mapper can be constructed using the returned sub-tree mapper builder object.
         *
         * @param dispatchingMapper a dispatching action mapper which has been constructed without using the builders
         *                          provided by this API
         *
         * @return a sub-tree mapper builder which uses the given dispatching action mapper as the current parent action
         * mapper. Using this builder, further sub-tree mappers can be constructed and added to the given dispatching
         * action mapper.
         */
        public SubtreeMapperBuilder mapSubtree(final DispatchingUriPathSegmentActionMapper dispatchingMapper) {
            currentDispatchingMapper.addSubMapper(dispatchingMapper);
            return new SubtreeMapperBuilder(uriActionMapperTree, dispatchingMapper, this);
        }
    }

    public static class MapperBuilder {
        private final MapperTreeBuilder parentMapperTreeBuilder;
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private final String mapperName;
        private String pathSegment;

        private MapperBuilder(final MapperTreeBuilder parentMapperTreeBuilder,
                              final DispatchingUriPathSegmentActionMapper dispatchingMapper,
                              final String mapperName) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            this.mapperName = mapperName;
        }

        /**
         * Define the action command factory to be used for the currently constructed {@link
         * SimpleUriPathSegmentActionMapper}. As a result, a builder object is returned for defining and adding URI
         * parameter objects to this action mapper. If no URI parameters need to be defined for this action mapper, the
         * construction process can be finalized with {@link SimpleMapperParameterBuilder#finishMapper()}.
         *
         * @param actionCommandFactory the action command factory to be used for the currently constructed {@link
         *                             SimpleUriPathSegmentActionMapper} (see {@link SimpleUriPathSegmentActionMapper#SimpleUriPathSegmentActionMapper(String,
         *                             String, UriActionCommandFactory)}.
         *
         * @return builder object for defining the URI parameters for the currently constructed {@link
         * SimpleUriPathSegmentActionMapper}.
         */
        public SimpleMapperParameterBuilder onActionFactory(UriActionCommandFactory actionCommandFactory) {
            Preconditions.checkNotNull(actionCommandFactory);
            if (pathSegment == null) {
                LOG.debug("onAction() - Adding mapper for path segment '{}' on action factory {}", mapperName, actionCommandFactory);
            } else {
                LOG.debug("onAction() - Adding mapper with name '{}' using path segment '{}' on action factory {}", mapperName, pathSegment, actionCommandFactory);
            }
            final SimpleUriPathSegmentActionMapper mapper = new SimpleUriPathSegmentActionMapper(mapperName, pathSegment, actionCommandFactory);
            return new SimpleMapperParameterBuilder(parentMapperTreeBuilder, dispatchingMapper, mapper);
        }

        /**
         * Define the path segment name for the currently constructed {@link SimpleUriPathSegmentActionMapper}.
         *
         * @param pathSegment the name of the path segment this action mapper is responsible for (see {@link
         *                    SimpleUriPathSegmentActionMapper#SimpleUriPathSegmentActionMapper(String, String,
         *                    UriActionCommandFactory)})
         *
         * @return this builder object
         * @see SimpleUriPathSegmentActionMapper
         */
        public MapperBuilder onPathSegment(final String pathSegment) {
            this.pathSegment = pathSegment;
            return this;
        }
    }

    public static class SimpleMapperParameterBuilder {
        private final MapperTreeBuilder parentMapperTreeBuilder;
        private final SimpleUriPathSegmentActionMapper targetMapper;
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;

        private SimpleMapperParameterBuilder(final MapperTreeBuilder parentMapperTreeBuilder,
                                             final DispatchingUriPathSegmentActionMapper dispatchingMapper,
                                             final SimpleUriPathSegmentActionMapper targetMapper) {
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
            this.dispatchingMapper = dispatchingMapper;
            this.targetMapper = targetMapper;
        }

        /**
         * Start building a single-valued URI parameter for a particular supported data type (e.g. String, Integer,
         * Float, Date, etc.) with the given parameter id.
         *
         * @param id id to be used for the parameter (see {@link UriParameter#getId()})
         *
         * @return a builder object for building the single-valued URI parameter
         */
        public SingleValuedParameterBuilder<SimpleMapperParameterBuilder> withSingleValuedParameter(final String id) {
            return new SingleValuedParameterBuilder<>(this, id, targetMapper);
        }

        /**
         * Register the given preconfigured URI parameter object on the action mapper currently under construction by
         * this builder.
         *
         * @param parameter preconfigured {@link UriParameter} object to be registered on the currently built action
         *                  mapper.
         *
         * @return this builder object
         */
        public SimpleMapperParameterBuilder withParameter(final UriParameter<?> parameter) {
            if (parameter.isOptional()) {
                LOG.debug("withParameter() - Registering preconfigured parameter {} on mapper {} with default value '{}'", parameter, targetMapper,
                        parameter.getDefaultValue());
            } else {
                LOG.debug("withParameter() - Registering preconfigured parameter {} on mapper {} with no default value", parameter, targetMapper,
                        parameter.getDefaultValue());
            }
            targetMapper.registerURIParameter(parameter);
            return this;
        }

        /**
         * Completes the construction and configuration of the currently built {@link SimpleUriPathSegmentActionMapper}.
         * This method will add the {@link SimpleUriPathSegmentActionMapper} to its parent sub-tree mapper and return
         * the builder object for this parent action mapper. Using this builder, further sibling action mapper objects
         * for the currently constructed {@link SimpleUriPathSegmentActionMapper} can be configured and added to the
         * parent sub-tree action mapper.
         *
         * @return a builder object for the parent sub-tree mapper of the currently constructed {@link
         * SimpleUriPathSegmentActionMapper}
         */
        public MapperTreeBuilder finishMapper() {
            dispatchingMapper.addSubMapper(targetMapper);
            LOG.debug("finishMapper() - Finishing mapper {}. Path: {}", targetMapper, targetMapper.pathFromRoot());
            return parentMapperTreeBuilder;
        }

        /**
         * Completes the construction and configuration of the currently built {@link SimpleUriPathSegmentActionMapper}
         * (see {@link #finishMapper()} for details).
         * <p>
         * The completed {@link SimpleUriPathSegmentActionMapper} is then passed to the specified {@link Consumer}
         * object. This consumer can then further process the action mapper object.
         * <p>
         * One common use case for this is to add the action mapper to some hash map so that it can later be accessed by
         * an application. Access to the action mappers is necessary for the configuration and assembly of URI fragments
         * to be used in hyperlinks (see {@link #assembleUriFragment(CapturedParameterValues,
         * UriPathSegmentActionMapper)}).
         *
         * @param consumer a {@link Consumer} to which the finished {@link SimpleUriPathSegmentActionMapper} object is
         *                 passed for further processing
         *
         * @return a builder object for the parent sub-tree mapper of the currently constructed {@link
         * SimpleUriPathSegmentActionMapper}
         */
        public MapperTreeBuilder finishMapper(final Consumer<UriPathSegmentActionMapper> consumer) {
            consumer.accept(new ImmutableActionMapperWrapper(targetMapper));
            return finishMapper();
        }
    }

    public static class SingleValuedParameterBuilder<B> {
        private final B parentBuilder;
        private final String id;
        private final UriPathSegmentActionMapper targetMapper;

        private SingleValuedParameterBuilder(final B parentBuilder, final String id,
                                             final UriPathSegmentActionMapper targetMapper) {
            this.parentBuilder = parentBuilder;
            this.id = id;
            this.targetMapper = targetMapper;
        }

        /**
         * Define the desired data type for the single-valued parameter. The parameter object itself will be created by
         * a factory class which is able to construct the corresponding parameter objects for a particular data type.
         *
         * @param forType class object defining the desired data type
         * @param <T>     data type of the single-valued parameter
         *
         * @return a builder object
         * @throws IllegalArgumentException if the given data type is not supported by the underlying {@link
         *                                  SingleValuedParameterFactory} which is responsible for creating the
         *                                  parameter object
         * @see SingleValuedParameterFactory
         */
        public <T> SingleValueParameterWithDefaultValueBuilder<T, B> forType(final Class<T> forType) {
            return new SingleValueParameterWithDefaultValueBuilder<>(parentBuilder, id, forType, targetMapper);
        }

        public static class SingleValueParameterWithDefaultValueBuilder<T, B> {
            private final AbstractSingleUriParameter parameter;
            private final B parentBuilder;
            private final UriPathSegmentActionMapper targetMapper;

            private SingleValueParameterWithDefaultValueBuilder(final B parentBuilder,
                                                                final String id,
                                                                final Class<T> forType,
                                                                final UriPathSegmentActionMapper targetMapper) {
                this.parentBuilder = parentBuilder;
                this.targetMapper = targetMapper;
                parameter = SingleValuedParameterFactory.createUriParameter(id, forType);
            }

            /**
             * Provide a default value for the single-valued parameter and finish building the parameter.
             *
             * @param defaultValue the default value to use for the new parameter
             *
             * @return the parent builder object
             */
            @SuppressWarnings("unchecked")
            public B usingDefaultValue(final T defaultValue) {
                LOG.debug("usingDefaultValue() - Registering parameter {} on mapper {} with default value='{}'", parameter, targetMapper, defaultValue);
                parameter.setOptional(defaultValue);
                return noDefault();
            }

            /**
             * Finish building the single-valued parameter without setting a default value.
             *
             * @return the parent builder object
             */
            public B noDefault() {
                if (!parameter.isOptional()) {
                    LOG.debug("noDefault() - Registering parameter {} on mapper {} with no default value", parameter, targetMapper);
                }
                targetMapper.registerURIParameter(parameter);
                return parentBuilder;
            }
        }
    }

    public static class SubtreeMapperBuilder {
        private final DispatchingUriPathSegmentActionMapper dispatchingMapper;
        private final UriActionMapperTree uriActionMapperTree;
        private final MapperTreeBuilder parentMapperTreeBuilder;

        private SubtreeMapperBuilder(final UriActionMapperTree uriActionMapperTree,
                                     final DispatchingUriPathSegmentActionMapper dispatchingMapper,
                                     final MapperTreeBuilder parentMapperTreeBuilder) {
            this.uriActionMapperTree = uriActionMapperTree;
            this.dispatchingMapper = dispatchingMapper;
            this.parentMapperTreeBuilder = parentMapperTreeBuilder;
        }

        /**
         * Start building a single-valued URI parameter for a particular supported data type (e.g. String, Integer,
         * Float, Date, etc.) with the given parameter id.
         *
         * @param id id to be used for the parameter (see {@link UriParameter#getId()})
         *
         * @return a builder object for building the single-valued URI parameter
         */
        public SingleValuedParameterBuilder<SubtreeMapperBuilder> withSingleValuedParameter(final String id) {
            return new SingleValuedParameterBuilder<>(this, id, dispatchingMapper);
        }

        /**
         * Register the given preconfigured URI parameter object on the sub-tree action mapper currently under
         * construction by this builder.
         *
         * @param parameter preconfigured {@link UriParameter} object to be registered on the currently built sub-tree
         *                  action mapper.
         *
         * @return this builder object
         */
        public SubtreeMapperBuilder withParameter(final UriParameter<?> parameter) {
            if (parameter.isOptional()) {
                LOG.debug("withParameter() - Registering preconfigured parameter {} on mapper {} with default value '{}'", parameter, dispatchingMapper,
                        parameter.getDefaultValue());
            } else {
                LOG.debug("withParameter() - Registering preconfigured parameter {} on mapper {} with no default value", parameter, dispatchingMapper,
                        parameter.getDefaultValue());
            }
            dispatchingMapper.registerURIParameter(parameter);
            return this;
        }

        /**
         * Define the action command factory to be used for the currently constructed {@link
         * DispatchingUriPathSegmentActionMapper}.
         *
         * @param actionCommandFactory the action command factory to be used for the currently constructed {@link
         *                             DispatchingUriPathSegmentActionMapper} (see {@link DispatchingUriPathSegmentActionMapper#setActionCommandFactory(UriActionCommandFactory)}).
         *
         * @return this builder object for building sub-tree mappers
         */
        public SubtreeMapperBuilder onActionFactory(UriActionCommandFactory actionCommandFactory) {
            dispatchingMapper.setActionCommandFactory(actionCommandFactory);
            return this;
        }

        /**
         * Start configuring the sub-tree.
         *
         * @return a builder object which adds new sub-tree action mappers to the currently constructed {@link
         * DispatchingUriPathSegmentActionMapper}.
         */
        public MapperTreeBuilder onSubtree() {
            return new MapperTreeBuilder(uriActionMapperTree, dispatchingMapper, parentMapperTreeBuilder);
        }
    }
}
