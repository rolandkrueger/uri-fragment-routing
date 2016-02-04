package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.parameter.SingleStringUriParameter;

/**
 * This action mapper is used to invariably interpret all URI tokens that are passed into this mapper during the URI
 * interpretation process. The value of this token can be obtained with {@link #getCurrentURIToken()}. As this action
 * mapper class is a particularly configured {@link RegexUriPathSegmentActionMapper}, all of the description of {@link
 * RegexUriPathSegmentActionMapper} also applies to this class.
 *
 * @author Roland Krüger
 * @since 1.0
 */
public class CatchAllUriPathSegmentActionMapper extends RegexUriPathSegmentActionMapper {
    private static final long serialVersionUID = -5033766191211958005L;

    public CatchAllUriPathSegmentActionMapper(String mapperName, String parameterId) {
        super(mapperName, "(.*)", new SingleStringUriParameter(parameterId));
    }

    /**
     * <p> {@inheritDoc} </p> <p> Invariably returns <code>true</code> for this {@link
     * CatchAllUriPathSegmentActionMapper}. </p>
     */
    @Override
    protected boolean isResponsibleForToken(String uriToken) {
        return true;
    }
}
