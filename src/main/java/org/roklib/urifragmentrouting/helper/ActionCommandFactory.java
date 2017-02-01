package org.roklib.urifragmentrouting.helper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.annotation.CapturedParameter;
import org.roklib.urifragmentrouting.annotation.CurrentUriFragment;
import org.roklib.urifragmentrouting.annotation.RoutingContext;
import org.roklib.urifragmentrouting.exception.InvalidActionCommandClassException;
import org.roklib.urifragmentrouting.exception.InvalidMethodSignatureException;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;
import org.roklib.urifragmentrouting.parameter.value.ParameterValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Factory class for URI action command objects. This class is used internally by {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree UriActionMapperTree}. It creates new instances of URI action
 * command objects and passes required data to their annotated methods. Action command objects can request data from the
 * currently interpreted URI fragment using the following annotations: <ul> <li>{@link CurrentUriFragment}</li>
 * <li>{@link CapturedParameter}</li> <li>{@link RoutingContext}</li> <li>{@link AllCapturedParameters}</li> </ul>
 */
public class ActionCommandFactory implements UriActionCommandFactory {

    private Class<? extends UriActionCommand> commandClass;
    private UriActionCommand uriActionCommand;

    /**
     * Create a new action command factory which creates new instances of the specified URI action command class.
     *
     * @param commandClass class implementing {@link UriActionCommand} which is to be created by this factory
     */
    public ActionCommandFactory(final Class<? extends UriActionCommand> commandClass) {
        Preconditions.checkNotNull(commandClass);
        this.commandClass = commandClass;
    }

    public ActionCommandFactory(UriActionCommand uriActionCommand) {
        this(uriActionCommand.getClass());
        this.uriActionCommand = uriActionCommand;
    }

    /**
     * Instantiates a new object of the {@link UriActionCommand} class specified in the constructor.
     *
     * @return a new action command object object
     * @throws InvalidActionCommandClassException if the action command class could not be instantiated. This can happen
     *                                            if the class does not have a default constructor or is abstract or an
     *                                            interface.
     */
    @Override
    public UriActionCommand createUriActionCommand() {
        if (uriActionCommand != null) {
            return uriActionCommand;
        }
        try {
            uriActionCommand = commandClass.newInstance();
        } catch (final InstantiationException e) {
            throw new InvalidActionCommandClassException("Unable to create new instance of action command class "
                    + commandClass.getName() + ". Make sure this class has a default constructor.");
        } catch (final IllegalAccessException e) {
            throw new InvalidActionCommandClassException("Unable to create new instance of action command class "
                    + commandClass.getName() + ". Make sure this class has public visibility.");
        }

        return uriActionCommand;
    }

    /**
     * Passes the currently interpreted URI fragment to the method of the given action command class annotated with
     * {@link CurrentUriFragment}. If there is no such method this method does nothing.
     *
     * @param uriFragment      the currently interpreted URI fragment
     * @param uriActionCommand URI action command object to which the URI fragment is passed
     *
     * @throws InvalidMethodSignatureException if the method annotated with {@link CurrentUriFragment} cannot be
     *                                         accessed or does not have exactly one argument of type String
     */
    public void passUriFragment(final String uriFragment, final UriActionCommand uriActionCommand) {
        final List<Method> currentUriFragmentSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, CurrentUriFragment.class, String.class));
        for (final Method method : currentUriFragmentSetters) {
            try {
                method.invoke(uriActionCommand, uriFragment);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + CurrentUriFragment.class.getName() + " in class " + commandClass.getName()
                        + ". Make sure this method is public.", e);
            }
        }
    }

    /**
     * Passes all URI parameter values captured from the currently interpreted URI fragment to the method of the given
     * action command class annotated with {@link AllCapturedParameters}. If there is no such method this method does
     * nothing.
     *
     * @param capturedParameterValues all captured parameter values to be passed to the action command object
     * @param uriActionCommand        URI action command object to which the captured parameter values are passed
     *
     * @throws InvalidMethodSignatureException if the method annotated with {@link AllCapturedParameters} cannot be
     *                                         accessed or does not have exactly one argument of type {@link
     *                                         CapturedParameterValues}
     */
    public void passAllCapturedParameters(final CapturedParameterValues capturedParameterValues, final UriActionCommand uriActionCommand) {
        final List<Method> allCapturedParametersSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, AllCapturedParameters.class, CapturedParameterValues.class));
        for (final Method method : allCapturedParametersSetters) {
            try {
                method.invoke(uriActionCommand, capturedParameterValues);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + AllCapturedParameters.class.getName() + " in class " + commandClass.getName()
                        + ". Make sure this method is public, has only one parameter, and has the correct argument type.", e);
            }
        }
    }

    /**
     * Passes single URI parameter values captured from the currently interpreted URI fragment to the methods of the
     * given action command class which are annotated with {@link CapturedParameter}. If there are no such method this
     * method does nothing.
     *
     * @param capturedParameterValues all captured parameter values to be passed to the action command object
     * @param uriActionCommand        URI action command object to which the captured parameter values are passed
     *
     * @throws InvalidMethodSignatureException if one of the methods annotated with {@link CapturedParameter} is not
     *                                         accessible or does not have exactly one parameter of the correct type.
     */
    public void passCapturedParameters(final CapturedParameterValues capturedParameterValues, final UriActionCommand uriActionCommand) {
        final List<Method> parameterSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, CapturedParameter.class, ParameterValue.class));
        parameterSetters
                .forEach(method -> {
                    final CapturedParameter annotation = method.getDeclaredAnnotation(CapturedParameter.class);
                    try {
                        method.invoke(uriActionCommand, capturedParameterValues.getValueFor(annotation.mapperName(), annotation.parameterName()));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                                + CapturedParameter.class.getName() + " in class " + commandClass.getName()
                                + ". Make sure this method is public, has only one parameter, and has the correct argument type.", e);
                    }
                });
    }

    /**
     * Passes the current routing context object to the method of the given action command class annotated with {@link
     * RoutingContext}. If no such method exists this method does nothing.
     *
     * @param context          the current routing context object
     * @param uriActionCommand URI action command object to which the captured parameter values are passed
     *
     * @throws InvalidMethodSignatureException if the method annotated with {@link RoutingContext} is not accessible or
     *                                         does not have exactly one argument of the correct type.
     */
    public void passRoutingContext(final Object context, final UriActionCommand uriActionCommand) {
        if (context == null) {
            return;
        }
        final List<Method> contextSetters = findSetterMethodsFor(commandClass, method -> hasAnnotation(method, RoutingContext.class, context.getClass()));
        contextSetters
                .forEach(method -> {
                    try {
                        method.invoke(uriActionCommand, context);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                                + RoutingContext.class.getName() + " in class " + commandClass.getName()
                                + ". Make sure this method is public, has only one parameter, and has the correct argument type.", e);
                    }
                });
    }

    private List<Method> findSetterMethodsFor(final Class<?> clazz, final Predicate<? super Method> predicate) {
        if (clazz.equals(Object.class)) {
            return Collections.emptyList();
        }
        final List<Method> result = new LinkedList<>();
        result.addAll(Arrays.stream(clazz.getDeclaredMethods())
                .filter(predicate)
                .collect(Collectors.toList()));
        result.addAll(findSetterMethodsFor(clazz.getSuperclass(), predicate));
        return result;
    }

    private boolean isAnnotatedWith(final Annotation[] declaredAnnotations, final java.lang.Class<? extends Annotation> annotationType) {
        return Arrays.stream(declaredAnnotations)
                .filter(annotation -> annotation.annotationType() == annotationType)
                .count() > 0;
    }

    private boolean hasAnnotation(final Method method, final java.lang.Class<? extends Annotation> annotationType, final Class<?> expectedClass) {
        final Annotation[] declaredAnnotations = method.getDeclaredAnnotations();

        if (isAnnotatedWith(declaredAnnotations, annotationType)) {
            if (!hasExactlyOneParameter(method)) {
                throw new InvalidMethodSignatureException("Method " + method + " does not have exactly one parameter.");
            }
            if (!isParameterTypeEqualTo(method.getParameterTypes()[0], expectedClass)) {
                throw new InvalidMethodSignatureException("Parameter of method " + method +
                        " does not have the expected type " + expectedClass);
            }
            return true;
        }
        return false;
    }

    private boolean isParameterTypeEqualTo(final Class<?> parameterType, final Class<?> expectedClass) {
        return parameterType.isAssignableFrom(expectedClass);
    }

    private boolean hasExactlyOneParameter(final Method method) {
        return method.getParameterCount() == 1;
    }
}
