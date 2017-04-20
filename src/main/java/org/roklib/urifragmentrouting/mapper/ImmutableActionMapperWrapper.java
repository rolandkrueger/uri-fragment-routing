package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.util.List;
import java.util.Map;

public class ImmutableActionMapperWrapper implements UriPathSegmentActionMapper {

    private UriPathSegmentActionMapper delegate;

    public ImmutableActionMapperWrapper(UriPathSegmentActionMapper delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public UriActionCommandFactory interpretTokens(CapturedParameterValues capturedParameterValues, String currentUriToken, List<String> uriTokens, Map<String, String> queryParameters, ParameterMode parameterMode) {
        return delegate.interpretTokens(capturedParameterValues, currentUriToken, uriTokens, queryParameters, parameterMode);
    }

    @Override
    public String getMapperName() {
        return delegate.getMapperName();
    }

    @Override
    public void setActionCommandFactory(UriActionCommandFactory commandFactory) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    @Override
    public UriActionCommandFactory getActionCommandFactory() {
        return delegate.getActionCommandFactory();
    }

    @Override
    public void registerURIParameter(UriParameter<?> parameter) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    @Override
    public UriPathSegmentActionMapper getParentMapper() {
        UriPathSegmentActionMapper parentMapper = delegate.getParentMapper();
        return parentMapper == null ? null : new ImmutableActionMapperWrapper(parentMapper);
    }

    @Override
    public void setParentMapper(UriPathSegmentActionMapper parent) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    @Override
    public void registerSubMapperName(String subMapperName) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    @Override
    public void assembleUriFragmentTokens(CapturedParameterValues parameterValues, List<String> uriTokens, ParameterMode parameterMode) {
        delegate.assembleUriFragmentTokens(parameterValues, uriTokens, parameterMode);
    }

    @Override
    public boolean isResponsibleForToken(String uriToken) {
        return delegate.isResponsibleForToken(uriToken);
    }

    @Override
    public void getMapperOverview(String path, List<String> mapperOverviewList) {
        delegate.getMapperOverview(path, mapperOverviewList);
    }

    @Override
    public String getSegmentInfo() {
        return delegate.getSegmentInfo();
    }

    @Override
    public String pathFromRoot() {
        return delegate.pathFromRoot();
    }
}
