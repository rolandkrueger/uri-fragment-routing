package org.roklib.webapps.uridispatching.parameter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({SingleBooleanUriParameterTest.class, SingleDoubleUriParameterTest.class,
    SingleFloatUriParameterTest.class, SingleIntegerUriParameterTest.class, SingleLongUriParameterTest.class,
    SingleStringUriParameterTest.class, Point2DUriParameterTest.class})
public class UriParametersTests {
}
