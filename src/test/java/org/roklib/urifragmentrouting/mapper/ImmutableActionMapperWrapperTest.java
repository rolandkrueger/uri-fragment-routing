package org.roklib.urifragmentrouting.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.SingleStringUriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImmutableActionMapperWrapperTest {

    private ImmutableActionMapperWrapper wrapper;
    @Mock
    private UriPathSegmentActionMapper delegateMock;
    @Mock
    private UriPathSegmentActionMapper parentMock;
    @Mock
    private UriActionCommandFactory factoryMock;

    @Before
    public void setUp() throws Exception {
        wrapper = new ImmutableActionMapperWrapper(delegateMock);

        when(delegateMock.getActionCommandFactory()).thenReturn(factoryMock);
        when(delegateMock.getMapperName()).thenReturn("string");
        when(delegateMock.getParentMapper()).thenReturn(parentMock);
        when(delegateMock.getSegmentInfo()).thenReturn("string");
        when(delegateMock.isResponsibleForToken(anyString())).thenReturn(true);
        when(delegateMock.pathFromRoot()).thenReturn("string");
        when(delegateMock.interpretTokens(any(CapturedParameterValues.class), anyString(), anyList(), anyMap(), any(ParameterMode.class))).thenReturn(factoryMock);
        when(parentMock.pathFromRoot()).thenReturn("string");
    }

    @Test(expected = NullPointerException.class)
    public void passing_null_to_constructor_disallowed() throws Exception {
        new ImmutableActionMapperWrapper(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void registerSubMapperName_is_unsupported() throws Exception {
        wrapper.registerSubMapperName("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void registerURIParameter_is_unsupported() throws Exception {
        wrapper.registerURIParameter(new SingleStringUriParameter("id"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setActionCommandFactory_is_unsupported() throws Exception {
        wrapper.setActionCommandFactory(() -> null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setParentMapper_is_unsupported() throws Exception {
        wrapper.setParentMapper(wrapper);
    }

    @Test
    public void interpretTokens_is_delegated() throws Exception {
        CapturedParameterValues values = new CapturedParameterValues();
        UriActionCommandFactory uriActionCommandFactory = wrapper.interpretTokens(values, "", Collections.emptyList(), Collections.emptyMap(), ParameterMode.QUERY);
        assertThat(uriActionCommandFactory, is(factoryMock));
        verify(delegateMock).interpretTokens(values, "", Collections.emptyList(), Collections.emptyMap(), ParameterMode.QUERY);
    }

    @Test
    public void getMapperName_is_delegated() throws Exception {
        String mapperName = wrapper.getMapperName();
        assertThat(mapperName, is("string"));
        verify(delegateMock).getMapperName();
    }

    @Test
    public void getActionCommandFactory_is_delegated() throws Exception {
        UriActionCommandFactory actionCommandFactory = wrapper.getActionCommandFactory();
        assertThat(actionCommandFactory, is(factoryMock));
        verify(delegateMock).getActionCommandFactory();
    }

    @Test
    public void getParentMapper_is_delegated() throws Exception {
        UriPathSegmentActionMapper parentMapper = wrapper.getParentMapper();
        assertTrue(parentMapper instanceof ImmutableActionMapperWrapper);
        verify(delegateMock).getParentMapper();
    }

    @Test
    public void getParentMapper_null_is_delegated() throws Exception {
        reset(delegateMock);
        when(delegateMock.getParentMapper()).thenReturn(null);
        UriPathSegmentActionMapper parentMapper = wrapper.getParentMapper();
        assertThat(parentMapper, is(nullValue()));
        verify(delegateMock).getParentMapper();
    }

    @Test
    public void assembleUriFragmentTokens_is_delegated() throws Exception {
        CapturedParameterValues values = new CapturedParameterValues();
        wrapper.assembleUriFragmentTokens(values, Collections.emptyList(), ParameterMode.QUERY);
        verify(delegateMock).assembleUriFragmentTokens(values, Collections.emptyList(), ParameterMode.QUERY);
    }

    @Test
    public void isResponsibleForToken_is_delegated() throws Exception {
        boolean responsibleForToken = wrapper.isResponsibleForToken("");
        assertThat(responsibleForToken, is(true));
        verify(delegateMock).isResponsibleForToken("");
    }

    @Test
    public void getMapperOverview_is_delegated() throws Exception {
        wrapper.getMapperOverview("", Collections.emptyList());
        verify(delegateMock).getMapperOverview("", Collections.emptyList());
    }

    @Test
    public void getSegmentInfo_is_delegated() throws Exception {
        String segmentInfo = wrapper.getSegmentInfo();
        assertThat(segmentInfo, is("string"));
        verify(delegateMock).getSegmentInfo();
    }

    @Test
    public void pathFromRoot_is_delegated() throws Exception {
        String pathFromRoot = wrapper.pathFromRoot();
        assertThat(pathFromRoot, is("string"));
        verify(delegateMock).pathFromRoot();
    }
}