package org.roklib.webapps.uridispatching;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.roklib.webapps.uridispatching.mapper.AbstractURIPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.mapper.CatchAllURIPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.mapper.RegexURIPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.mapper.StartsWithURIPathSegmentActionMapperTest;
import org.roklib.webapps.uridispatching.parameter.URIParametersTests;

@RunWith(Suite.class)
@SuiteClasses({AbstractURIPathSegmentActionMapperTest.class,
    URIParametersTests.class,
    RegexURIPathSegmentActionMapperTest.class,
    StartsWithURIPathSegmentActionMapperTest.class,
    CatchAllURIPathSegmentActionMapperTest.class})
public class URIDispatchingTestSuite {
}
