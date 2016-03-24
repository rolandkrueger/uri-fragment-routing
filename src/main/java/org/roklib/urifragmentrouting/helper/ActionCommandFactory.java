package org.roklib.urifragmentrouting.helper;

import org.roklib.urifragmentrouting.UriActionCommand;
import org.roklib.urifragmentrouting.exception.InvalidActionCommandClassException;
import org.roklib.urifragmentrouting.exception.InvalidMethodSignatureException;
import org.roklib.urifragmentrouting.annotation.AllCapturedParameters;
import org.roklib.urifragmentrouting.annotation.CapturedParameter;
import org.roklib.urifragmentrouting.annotation.CurrentUriFragment;
import org.roklib.urifragmentrouting.annotation.RoutingContext;
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
 * @author Roland Krüger
 */
public class ActionCommandFactory<C> {

    private Class<? extends UriActionCommand> commandClass;

    public ActionCommandFactory(Class<? extends UriActionCommand> commandClass) {
        this.commandClass = commandClass;
    }

    public UriActionCommand createCommand() {
        UriActionCommand uriActionCommand;
        try {
            uriActionCommand = commandClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidActionCommandClassException("Unable to create new instance of action command class "
                    + commandClass.getName() + ". Make sure this class has a default constructor.");
        } catch (IllegalAccessException e) {
            throw new InvalidActionCommandClassException("Unable to create new instance of action command class "
                    + commandClass.getName() + ". Make sure this class has public visibility.");
        }
        return uriActionCommand;
    }

    public void passUriFragment(String uriFragment, Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        final List<Method> currentUriFragmentSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, CurrentUriFragment.class, String.class));
        for (Method method : currentUriFragmentSetters) {
            try {
                method.invoke(uriActionCommand, uriFragment);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + CurrentUriFragment.class.getName() + " in class " + commandClass.getName()
                        + ". Make sure this method is public.", e);
            }
        }
    }

    public void passAllCapturedParameters(CapturedParameterValues capturedParameterValues, Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        final List<Method> allCapturedParametersSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, AllCapturedParameters.class, CapturedParameterValues.class));
        for (Method method : allCapturedParametersSetters) {
            try {
                method.invoke(uriActionCommand, capturedParameterValues);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + AllCapturedParameters.class.getName() + " in class " + commandClass.getName()
                        + ". Make sure this method is public, has only one parameter, and has the correct argument type.", e);
            }
        }
    }

    public void passCapturedParameters(CapturedParameterValues capturedParameterValues, Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        List<Method> parameterSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, CapturedParameter.class, ParameterValue.class));
        parameterSetters.stream()
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

    public void passRoutingContext(C context, Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        if (context == null) {
            return;
        }
        List<Method> contextSetters = findSetterMethodsFor(commandClass, method -> hasAnnotation(method, RoutingContext.class, context.getClass()));
        contextSetters.stream()
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

    private List<Method> findSetterMethodsFor(Class<?> clazz, Predicate<? super Method> predicate) {
        if (clazz.equals(Object.class)) {
            return Collections.emptyList();
        }
        List<Method> result = new LinkedList<>();
        result.addAll(Arrays.stream(clazz.getDeclaredMethods())
                .filter(predicate)
                .collect(Collectors.toList()));
        result.addAll(findSetterMethodsFor(clazz.getSuperclass(), predicate));
        return result;
    }

    private boolean isAnnotatedWith(Annotation[] declaredAnnotations, java.lang.Class<? extends Annotation> annotationType) {
        return Arrays.stream(declaredAnnotations)
                .filter(annotation -> annotation.annotationType() == annotationType)
                .count() > 0;
    }

    private boolean hasAnnotation(Method method, java.lang.Class<? extends Annotation> annotationType, Class<?> expectedClass) {
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

    private boolean isParameterTypeEqualTo(Class<?> parameterType, Class<?> expectedClass) {
        return parameterType.isAssignableFrom(expectedClass);
    }

    private boolean hasExactlyOneParameter(Method method) {
        return method.getParameterCount() == 1;
    }
}
