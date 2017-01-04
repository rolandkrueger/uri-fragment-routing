package org.roklib.urifragmentrouting;

/**
 * Interface to be implemented by action command classes which will be registered with an {@link
 * org.roklib.urifragmentrouting.mapper.AbstractUriPathSegmentActionMapper}. These classes implement the command design
 * pattern.
 * <p>
 * When a URI fragment is interpreted by the {@link UriActionMapperTree}, the interpretation process will eventually
 * reach the last path segment for the URI fragment on which a {@link UriActionCommand} class is registered. This action
 * command class will then be instantiated and executed. If the interpretation process reaches a path segment for which
 * no action command class is registered, the default action command for this {@link UriActionMapperTree} will be used
 * instead if any such is available.
 * <p>
 * Consider for example the following URI fragment:
 * <p>
 * <tt>/view/profile</tt>
 * <p>
 * When building the {@link UriActionMapperTree} responsible for this URI fragment, the class
 * <tt>ShowProfilePageActionCommand</tt> is registered on the path segment action mapper responsible for the
 * <tt>profile</tt> segment. When this URI fragment is interpreted by the {@link UriActionMapperTree}, this action
 * command class will instantiated, configured as needed, and finally executed by calling its {@link #run()} method.
 * <p>
 * Action command classes are required to have a public default constructor since they will later be created on demand
 * by reflection.
 * <p>
 * In addition to the {@link #run()} method inherited from {@link Runnable}, action command classes can provide a number
 * of annotated setter methods which will be used to pass context information, such as parameter values, into the action
 * command. These setter methods can be annotated with one of the following annotations: <ul> <li>{@link
 * org.roklib.urifragmentrouting.annotation.AllCapturedParameters}</li> <li>{@link
 * org.roklib.urifragmentrouting.annotation.CapturedParameter}</li> <li>{@link org.roklib.urifragmentrouting.annotation.CurrentUriFragment}</li>
 * <li>{@link org.roklib.urifragmentrouting.annotation.RoutingContext}</li> </ul> All methods annotated with one of
 * these annotations have to be {@code public}, must not be abstract and must have exactly one argument of the correct
 * type.
 * <p>
 * These methods may also be inherited from a superclass, so that you can create hierarchies of action command objects.
 * Following is a description of these options.
 * <p>
 * <h1>Set all captured parameters</h1> Using a setter method annotated with {@link
 * org.roklib.urifragmentrouting.annotation.AllCapturedParameters}, the action command object can receive an object of
 * type {@link org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues} which contains all URI parameter
 * values which have been captured from the currently interpreted URI fragment. This method must have exactly one
 * argument of type {@link org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues}.
 * <p>
 * <h1>Set an individual parameter value</h1> If not all captured parameter values are needed by an action command or if
 * the captured parameters shall be set individually with their own setter methods, you can annotate a method with
 * {@link org.roklib.urifragmentrouting.annotation.CapturedParameter} to receive only one parameter value. Such a method
 * must have exactly one argument of type {@link org.roklib.urifragmentrouting.parameter.value.ParameterValue}.
 * <p>
 * <h1>Set the current URI fragment</h1> A method annotated with {@link org.roklib.urifragmentrouting.annotation.CurrentUriFragment}
 * will receive the URI fragment which is currently being interpreted by the {@link UriActionMapperTree}. This method
 * must have exactly one argument of type String.
 * <p>
 * <h1>Set the routing context</h1> You can define a custom class which is used as the routing context for the {@link
 * UriActionMapperTree} object in use. An instance of this class can be passed along with the Uri fragment
 * interpretation process with {@link UriActionMapperTree#interpretFragment(String, Object)}. A setter method annotated
 * with {@link org.roklib.urifragmentrouting.annotation.RoutingContext} will receive this context object. This method
 * must have exactly one argument of the routing context's type.
 * <p>
 * <h1>Order of invocation for annotated setter methods</h1> When configuring an action class object, the setter methods
 * annotated with the annotations described above are invoked in the following order:
 * <p>
 * <ol> <li>Routing context</li> <li>Current URI fragment</li> <li>All captured parameters</li> <li>Individual
 * parameters</li> </ol>
 */
public interface UriActionCommand extends Runnable {

    /**
     * Executes the URI action command object. This method does no need to be invoked explicitly since the {@link
     * UriActionMapperTree} will take care of this.
     */
    @Override
    void run();
}
