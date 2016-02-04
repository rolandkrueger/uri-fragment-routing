package org.roklib.webapps.uridispatching.parameter.value;

import org.roklib.webapps.uridispatching.UriActionCommand;
import org.roklib.webapps.uridispatching.exception.InvalidActionCommandClassException;
import org.roklib.webapps.uridispatching.exception.InvalidMethodSignatureException;
import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.UriParameter;
import org.roklib.webapps.uridispatching.parameter.annotation.AllCapturedParameters;
import org.roklib.webapps.uridispatching.parameter.annotation.CapturedParameter;
import org.roklib.webapps.uridispatching.parameter.annotation.CurrentUriFragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contains the set of parameter values which have been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class CapturedParameterValuesImpl implements CapturedParameterValues {

    private Map<String, Map<String, ParameterValue<?>>> values;

    public CapturedParameterValuesImpl() {
        values = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> ParameterValue<V> getValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return null;
        }

        return (ParameterValue<V>) parameterValues.get(parameterId);
    }

    public <V> void setValueFor(String mapperName, UriParameter<V> parameter, ParameterValue<?> value) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameter);
        if (value == null) {
            return;
        }

        final Map<String, ParameterValue<?>> mapperValues = values.computeIfAbsent(mapperName, k -> new HashMap<>());
        mapperValues.put(parameter.getId(), value);
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean hasValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return false;
        }
        ParameterValue<?> parameterValue = parameterValues.get(parameterId);
        return parameterValue != null && parameterValue.hasValue();
    }

    public UriActionCommand createActionCommandAndPassParameters(String currentUriFragment, Class<? extends UriActionCommand> commandClass) {
        final UriActionCommand uriActionCommand = createNewActionCommandInstance(commandClass);

        if (currentUriFragment != null) {
            passCurrentUriFragment(currentUriFragment, commandClass, uriActionCommand);
        }

        passAllCapturedParameters(commandClass, uriActionCommand);
        passCapturedParameters(commandClass, uriActionCommand);

        return uriActionCommand;
    }

    private void passCapturedParameters(Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        List<Method> parameterSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, CapturedParameter.class, ParameterValue.class));
        parameterSetters.stream()
                .forEach(method -> {
                    final CapturedParameter annotation = method.getDeclaredAnnotation(CapturedParameter.class);
                    try {
                        method.invoke(uriActionCommand, this.getValueFor(annotation.mapperName(), annotation.parameterName()));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                                + CapturedParameter.class.getName() + " in class " + commandClass.getName()
                                + ". Make sure this method is public.");
                    }
                });
    }

    private void passAllCapturedParameters(Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        final List<Method> allCapturedParametersSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, AllCapturedParameters.class, CapturedParameterValues.class));
        for (Method method : allCapturedParametersSetters) {
            try {
                method.invoke(uriActionCommand, this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + AllCapturedParameters.class.getName() + " in class " + commandClass.getName()
                        + ". Make sure this method is public.");
            }
        }
    }

    private void passCurrentUriFragment(String currentUriFragment, Class<? extends UriActionCommand> commandClass, UriActionCommand uriActionCommand) {
        final List<Method> currentUriFragmentSetters = findSetterMethodsFor(commandClass,
                method -> hasAnnotation(method, CurrentUriFragment.class, String.class));
        for (Method method : currentUriFragmentSetters) {
            try {
                method.invoke(uriActionCommand, currentUriFragment);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                        + CurrentUriFragment.class.getName() + " in class " + commandClass.getName()
                        + ". Make sure this method is public.");
            }
        }
    }

    private UriActionCommand createNewActionCommandInstance(Class<? extends UriActionCommand> commandClass) {
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
