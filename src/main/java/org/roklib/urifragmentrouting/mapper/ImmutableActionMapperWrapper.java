package org.roklib.urifragmentrouting.mapper;

import org.roklib.urifragmentrouting.UriActionCommandFactory;
import org.roklib.urifragmentrouting.helper.Preconditions;
import org.roklib.urifragmentrouting.parameter.ParameterMode;
import org.roklib.urifragmentrouting.parameter.UriParameter;
import org.roklib.urifragmentrouting.parameter.value.CapturedParameterValues;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Wrapper class which wraps a {@link UriPathSegmentActionMapper} and adds immutability to the wrapped action mapper.
 * That is, any method which changes the wrapped action mapper's configuration will throw an {@link
 * UnsupportedOperationException}. All other operations are delegated to the wrapped action mapper. Instances of this
 * class are passed to client code in two places: Any method annotated with {@link
 * org.roklib.urifragmentrouting.annotation.CurrentActionMapper @CurrentActionMapper} will receive the current action
 * mapper wrapped in an {@link ImmutableActionMapperWrapper} and the mappers passed to the {@link
 * java.util.function.Consumer Consumer} argument of {@link org.roklib.urifragmentrouting.UriActionMapperTree.SimpleMapperParameterBuilder#finishMapper(java.util.function.Consumer)
 * finishMapper(java.util.function.Consumer)} are wrapped in this class, too.
 * <p>
 * <h1>Equality</h1> Regarding the equality contract, an {@link ImmutableActionMapperWrapper} is equal to the {@link
 * UriPathSegmentActionMapper} it wraps and vice versa.
 */
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

    /**
     * Throws an {@link UnsupportedOperationException}.
     *
     * @param commandFactory the action command factory object for by this action mapper
     */
    @Override
    public void setActionCommandFactory(UriActionCommandFactory commandFactory) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    @Override
    public UriActionCommandFactory getActionCommandFactory() {
        return delegate.getActionCommandFactory();
    }

    /**
     * Throws an {@link UnsupportedOperationException}.
     *
     * @param parameter the parameter to be registered on this action mapper
     */
    @Override
    public void registerURIParameter(UriParameter<?> parameter) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    /**
     * Returns the parent mapper of the wrapped action mapper or {@code null} if the wrapped action mapper does not have
     * a parent mapper. The parent mapper itself will be wrapped in an {@link ImmutableActionMapperWrapper}.
     *
     * @return Returns the parent mapper of the wrapped action mapper
     */
    @Override
    public UriPathSegmentActionMapper getParentMapper() {
        UriPathSegmentActionMapper parentMapper = delegate.getParentMapper();
        return parentMapper == null ? null : new ImmutableActionMapperWrapper(parentMapper);
    }

    /**
     * Throws an {@link UnsupportedOperationException}.
     *
     * @param parent the parent action mapper of this mapper
     */
    @Override
    public void setParentMapper(UriPathSegmentActionMapper parent) {
        throw new UnsupportedOperationException("changing the configuration of this action mapper is disallowed");
    }

    /**
     * Throws an {@link UnsupportedOperationException}.
     *
     * @param subMapperName name of a sub-mapper for this action mapper
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UriPathSegmentActionMapper)) return false;
        UriPathSegmentActionMapper that = (UriPathSegmentActionMapper) o;
        return Objects.equals(delegate.getMapperName(), that.getMapperName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate.getMapperName());
    }
}
