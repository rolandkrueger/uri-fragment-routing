package org.roklib.urifragmentrouting.parameter.converter;

import java.io.Serializable;

/**
 * Interface for defining parameter value converters. These converters are responsible for converting parameter values
 * extracted from a URI fragment into their respective domain types. When a URI fragment is interpreted, all parameter
 * values found in this fragment are extracted as a String value. Using a parameter value converter, these Strings can
 * then be converted into the correct type.
 * <p>
 * An implementation of this interface has to care ensure that the two conversion methods of this interface match, i. e.
 * any value that has been converted into a String can be unambiguously restored from that String representation. In
 * pseudocode, the following constraint has to hold:
 * <pre>
 *     T value;
 *     T valueAfterConversion = convertToValue(convertToString(value));
 *     value == valueAfterConversion;
 * </pre>
 * <p>
 * If a String cannot be converted into the domain representation of a value, a {@link
 * ParameterValueConversionException} must be thrown.
 *
 * @param <T> target type into which and from which textual parameter values are converted, the parameter domain type
 * @author Roland Krüger
 */
public interface ParameterValueConverter<T> extends Serializable {

    /**
     * Converts a parameter value into its String representation.
     *
     * @param value the parameter value to be converted
     * @return a String representation of the given value
     */
    String convertToString(T value);

    /**
     * Converts a String back into the domain type of the value.
     *
     * @param valueAsString String representation of a value
     * @return the value converted into its domain type
     * @throws ParameterValueConversionException if the String could not be successfully converted
     */
    T convertToValue(String valueAsString) throws ParameterValueConversionException;
}
