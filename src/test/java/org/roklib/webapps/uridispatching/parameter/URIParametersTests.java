package org.roklib.webapps.uridispatching.parameter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({SingleBooleanURIParameterTest.class, SingleDoubleURIParameterTest.class,
    SingleFloatURIParameterTest.class, SingleIntegerURIParameterTest.class, SingleLongURIParameterTest.class,
    SingleStringURIParameterTest.class, Point2DURIParameterTest.class})
public class URIParametersTests {
}
