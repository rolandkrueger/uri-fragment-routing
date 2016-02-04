package org.roklib.webapps.uridispatching;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.roklib.webapps.uridispatching.mapper.AbstractUriPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.mapper.CatchAllUriPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.mapper.RegexUriPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.mapper.StartsWithUriPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.parameter.UriParametersTests;

@RunWith(Suite.class)
@SuiteClasses({AbstractUriPathSegmentActionMapperTest.class,
    UriParametersTests.class,
    RegexUriPathSegmentActionMapperTest.class,
    StartsWithUriPathSegmentActionMapperTest.class,
    CatchAllUriPathSegmentActionMapperTest.class})
public class UriDispatchingTestSuite {
}
