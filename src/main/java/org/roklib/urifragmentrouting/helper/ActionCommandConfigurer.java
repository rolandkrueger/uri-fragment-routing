package org.roklib.urifragmentrouting.helper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.annotation.*;
import org.roklib.urifragmentrouting.exception.InvalidMethodSignatureException;
import org.roklib.urifragmentrouting.mapper.ImmutableActionMapperWrapper;
import org.roklib.urifragmentrouting.mapper.UriPathSegmentActionMapper;
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
 * Configurer class for URI action command objects. This class is used internally by {@link
 * org.roklib.urifragmentrouting.UriActionMapperTree UriActionMapperTree}. It is used to pass required data to
 * accordingly annotated methods of the action command object passed in through the constructor. Action command objects
 * can request data from the currently interpreted URI fragment using the following annotations: <ul> <li>{@link
 * CurrentUriFragment}</li> <li>{@link CapturedParameter}</li> <li>{@link RoutingContext}</li> <li>{@link
 * AllCapturedParameters}</li> </ul>
 */
public class ActionCommandConfigurer implements UriActionCommandFactory {

    private UriActionCommand uriActionCommand;
    private UriActionCommandFactory uriActionCommandFactory;
    private UriPathSegmentActionMapper actionMapper;

    /**
     * Create a new configurer object for the given action command factory.
     *
     * @param uriActionCommandFactory factory which creates the action command object to be configured. Must not be
     *                                {@code null}.
     */
    public ActionCommandConfigurer(UriActionCommandFactory uriActionCommandFactory) {
        this.uriActionCommandFactory = uriActionCommandFactory;
    }

    public ActionCommandConfigurer(UriActionCommandFactory uriActionCommandFactory, UriPathSegmentActionMapper actionMapper) {
        this.uriActionCommandFactory = uriActionCommandFactory;
        this.actionMapper = new ImmutableActionMapperWrapper(actionMapper);
    }

    @Override
    public UriActionCommand createUriActionCommand() {
        if (uriActionCommand == null) {
            uriActionCommand = uriActionCommandFactory.createUriActionCommand();
        }
        return uriActionCommand;
    }

    private Class<? extends UriActionCommand> getCommandClass() {
        return createUriActionCommand().getClass();
    }

    /**
     * Passes the {@link UriPathSegmentActionMapper} object given through the constructor to the method of the given
     * action command annotated with {@link CurrentActionMapper}. If there is no such method, this method does nothing.
     */
    public void passUriPathSegmentActionMapper() {
        if (actionMapper == null) {
            return;
        }
        final List<Method> actionMapperSetters = findSetterMethodsFor(getCommandClass(),
                method -> hasAnnotation(method, CurrentActionMapper.class, UriPathSegmentActionMapper.class));
        for (Method method : actionMapperSetters) {
            try {
                method.invoke(createUriActionCommand(), actionMapper);
            } catch (IllegalAccessException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + CurrentActionMapper.class.getName() + " in class " + getCommandClass().getName()
                        + ". Make sure this method is public and has exactly one parameter of the correct argument type.", e);
            } catch (InvocationTargetException itExc) {
                throw new RuntimeException("An exception occurred while calling method annotated with @" +
                        CurrentActionMapper.class.getName() + " in class " + getCommandClass().getName()
                        + ".", itExc.getCause());
            }
        }
    }

    /**
     * Passes the currently interpreted URI fragment to the method of the given action command annotated with
     * {@link CurrentUriFragment}. If there is no such method, this method does nothing.
     *
     * @param uriFragment the currently interpreted URI fragment
     *
     * @throws InvalidMethodSignatureException if the method annotated with {@link CurrentUriFragment} cannot be
     *                                         accessed or does not have exactly one argument of type String
     */
    public void passUriFragment(final String uriFragment) {
        final List<Method> currentUriFragmentSetters = findSetterMethodsFor(getCommandClass(),
                method -> hasAnnotation(method, CurrentUriFragment.class, String.class));
        for (final Method method : currentUriFragmentSetters) {
            try {
                method.invoke(createUriActionCommand(), uriFragment);
            } catch (IllegalAccessException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + CurrentUriFragment.class.getName() + " in class " + getCommandClass().getName()
                        + ". Make sure this method is public and has exactly one parameter of the correct argument type.", e);
            } catch (InvocationTargetException itExc) {
                throw new RuntimeException("An exception occurred while calling method annotated with @" +
                        CurrentUriFragment.class.getName() + " in class " + getCommandClass().getName()
                        + ".", itExc.getCause());
            }
        }
    }

    /**
     * Passes all URI parameter values captured from the currently interpreted URI fragment to the method of the given
     * action command annotated with {@link AllCapturedParameters}. If there is no such method, this method does
     * nothing.
     *
     * @param capturedParameterValues all captured parameter values to be passed to the action command object
     *
     * @throws InvalidMethodSignatureException if the method annotated with {@link AllCapturedParameters} cannot be
     *                                         accessed or does not have exactly one argument of type {@link
     *                                         CapturedParameterValues}
     */
    public void passAllCapturedParameters(final CapturedParameterValues capturedParameterValues) {
        final List<Method> allCapturedParametersSetters = findSetterMethodsFor(getCommandClass(),
                method -> hasAnnotation(method, AllCapturedParameters.class, CapturedParameterValues.class));
        for (final Method method : allCapturedParametersSetters) {
            try {
                method.invoke(createUriActionCommand(), capturedParameterValues);
            } catch (IllegalAccessException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + AllCapturedParameters.class.getName() + " in class " + getCommandClass().getName()
                        + ". Make sure this method is public and has exactly one parameter of the correct argument type.", e);
            } catch (InvocationTargetException itExc) {
                throw new RuntimeException("An exception occurred while calling method annotated with @" +
                        AllCapturedParameters.class.getName() + " in class " + getCommandClass().getName()
                        + ".", itExc.getCause());
            }
        }
    }

    /**
     * Passes single URI parameter values captured from the currently interpreted URI fragment to the methods of the
     * given action command which are annotated with {@link CapturedParameter}. If there are no such methods, this
     * method does nothing.
     *
     * @param capturedParameterValues all captured parameter values to be passed to the action command object
     *
     * @throws InvalidMethodSignatureException if one of the methods annotated with {@link CapturedParameter} is not
     *                                         accessible or does not have exactly one parameter of the correct type.
     */
    public void passCapturedParameters(final CapturedParameterValues capturedParameterValues) {
        final List<Method> parameterSetters = findSetterMethodsFor(getCommandClass(),
                method -> hasAnnotation(method, CapturedParameter.class, ParameterValue.class));
        parameterSetters
                .forEach(method -> {
                    final CapturedParameter annotation = method.getDeclaredAnnotation(CapturedParameter.class);
                    try {
                        method.invoke(createUriActionCommand(), capturedParameterValues.getValueFor(annotation.mapperName(), annotation.parameterName()));
                    } catch (IllegalAccessException e) {
                        throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                                + CapturedParameter.class.getName() + " in class " + getCommandClass().getName()
                                + ". Make sure this method is public and has exactly one parameter of the correct argument type.", e);
                    } catch (InvocationTargetException itExc) {
                        throw new RuntimeException("An exception occurred while calling method annotated with @" +
                                CapturedParameter.class.getName() + " in class " + getCommandClass().getName()
                                + ".", itExc.getCause());
                    }
                });
    }

    /**
     * Passes the current routing context object to the method of the given action command annotated with {@link
     * RoutingContext}. If no such method exists, this method does nothing.
     *
     * @param context the current routing context object
     *
     * @throws InvalidMethodSignatureException if the method annotated with {@link RoutingContext} is not accessible or
     *                                         does not have exactly one argument of the correct type.
     */
    public void passRoutingContext(final Object context) {
        if (context == null) {
            return;
        }
        final List<Method> contextSetters = findSetterMethodsFor(getCommandClass(), method -> hasAnnotation(method, RoutingContext.class, context.getClass()));
        contextSetters
                .forEach(method -> {
                    try {
                        method.invoke(createUriActionCommand(), context);
                    } catch (IllegalAccessException e) {
                        throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                                + RoutingContext.class.getName() + " in class " + getCommandClass().getName()
                                + ". Make sure this method is public and has exactly one parameter of the correct argument type.", e);
                    } catch (InvocationTargetException itExc) {
                        throw new RuntimeException("An exception occurred while calling method annotated with @" +
                                RoutingContext.class.getName() + " in class " + getCommandClass().getName()
                                + ".", itExc.getCause());
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
