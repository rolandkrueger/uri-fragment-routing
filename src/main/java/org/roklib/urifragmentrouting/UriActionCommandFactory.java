package org.roklib.urifragmentrouting;

/**
 * Factory interface for creating {@link UriActionCommand} objects. A {@link UriActionCommandFactory} can be set on a
 * {@link org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper UriPathSegmentActionMapper} object instead of
 * a {@link UriActionCommand} class. If this is done, the action command objects executed at the end of the URI fragment
 * interpretation process will not be created by instantiating a given action command class but by invoking method
 * {@link #createUriActionCommand()}.
 * <p>
 * This approach makes creating action command objects more flexible. If an action command class is defined as the
 * target action for an action mapper, the creating of instances of this class cannot be configured any further. Using
 * an action class object, the concrete action objects can then only be created using their default constructor. When
 * you provide an action command factory, you can configure this factory which in turn can pass this configuration on to
 * the action command objects it provides.
 * <p>
 * Take, for example, the following action command factory.
 * <pre>
 *     public class NavigationCommandFactory implements UriActionCommandFactory {
 *
 *         private String target;
 *
 *         public NavigationCommandFactory(String target) {
 *             this.target = target;
 *         }
 *
 *        {@literal @}Override
 *         public UriActionCommand createUriActionCommand() {
 *             return new NavigationCommand(target);
 *         }
 *     }
 * </pre>
 * This factory is constructed using a String argument {@code target}. This value is passed on to the constructor of the
 * action command object created by this factory. By that, you can easily create action command objects which are
 * configured differently. In this example, the factory can create navigation commands which can be used to navigate in
 * an application to different screens.
 * <p>
 * This interface is a functional interface, i. e. it can be implemented by a lambda expression.
 */
@FunctionalInterface
public interface UriActionCommandFactory {

    /**
     * Creates a new action command object.
     *
     * @return a new instance of an action command class.
     */
    UriActionCommand createUriActionCommand();

}
