package org.roklib.webapps.uridispatching.parameter.value;

import org.roklib.webapps.uridispatching.URIActionCommand;
import org.roklib.webapps.uridispatching.exception.InvalidActionCommandClassException;
import org.roklib.webapps.uridispatching.exception.InvalidMethodSignatureException;
import org.roklib.webapps.uridispatching.helper.Preconditions;
import org.roklib.webapps.uridispatching.parameter.URIParameter;
import org.roklib.webapps.uridispatching.parameter.annotation.CurrentUriFragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Contains the set of parameter values which have been collected during the interpretation process for a concrete URI
 * fragment.
 *
 * @author Roland Krüger
 */
public class CapturedParameterValues {

    private Map<String, Map<String, ParameterValue<?>>> values;

    public CapturedParameterValues() {
        values = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <V> Optional<ParameterValue<V>> getValueFor(String mapperName, String parameterId) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameterId);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((ParameterValue<V>) parameterValues.get(parameterId));
    }

    public <V> void setValueFor(String mapperName, URIParameter<V> parameter, ParameterValue<?> value) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameter);
        if (value == null) {
            return;
        }

        final Map<String, ParameterValue<?>> mapperValues = values.computeIfAbsent(mapperName, k -> new HashMap<>());
        mapperValues.put(parameter.getId(), value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public <V> boolean hasValueFor(String mapperName, URIParameter<V> parameter) {
        Preconditions.checkNotNull(mapperName);
        Preconditions.checkNotNull(parameter);

        final Map<String, ParameterValue<?>> parameterValues = values.get(mapperName);
        if (parameterValues == null) {
            return false;
        }
        ParameterValue<?> parameterValue = parameterValues.get(parameter.getId());
        return parameterValue != null && parameterValue.hasValue();
    }

    public URIActionCommand passParametersToActionCommand(String currentUriFragment, Class<? extends URIActionCommand> commandClass) {
        URIActionCommand uriActionCommand = null;
        try {
            uriActionCommand = commandClass.newInstance();
        } catch (InstantiationException e) {
            throw new InvalidActionCommandClassException("Unable to create new instance of action command class "
                    + commandClass.getName() + ". Make sure this class has a default constructor.");
        } catch (IllegalAccessException e) {
            throw new InvalidActionCommandClassException("Unable to create new instance of action command class "
                    + commandClass.getName() + ". Make sure this class has public visibility.");
        }

        if (currentUriFragment != null) {
            final Optional<Method> currentUriFragmentSetter = Arrays.stream(commandClass.getDeclaredMethods())
                    .filter(this::hasCurrentUriFragmentAnnotation)
                    .findFirst();
            if (currentUriFragmentSetter.isPresent()) {
                try {
                    currentUriFragmentSetter.get().invoke(uriActionCommand, currentUriFragment);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new InvalidMethodSignatureException("Unable to invoke method annotated with @"
                            + CurrentUriFragment.class.getName() + " in class " + commandClass.getName()
                            + ". Make sure this method is public.");
                }
            }
        }

        return uriActionCommand;
    }

    private boolean hasCurrentUriFragmentAnnotation(Method method) {
        final Annotation[] declaredAnnotations = method.getDeclaredAnnotations();

        return isAnnotatedWith(declaredAnnotations, CurrentUriFragment.class)
                && !hasExactlyOneParameter(method)
                && isParameterTypeEqualTo(method.getParameterTypes()[0], String.class);

    }

    private boolean isAnnotatedWith(Annotation[] declaredAnnotations, java.lang.Class<? extends Annotation> annotationType) {
        return Arrays.stream(declaredAnnotations)
                .filter(annotation -> annotation.annotationType() == annotationType)
                .count() > 0;
    }

    private boolean isParameterTypeEqualTo(Class<?> parameterType, Class<?> expectedClass) {
        return parameterType.isAssignableFrom(expectedClass);
    }

    private boolean hasExactlyOneParameter(Method method) {
        return method.getParameterCount() != 1;
    }

}
