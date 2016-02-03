package org.roklib.webapps.uridispatching.mapper;

import org.roklib.webapps.uridispatching.parameter.SingleStringURIParameter;

/**
 * This action mapper is used to invariably interpret all URI tokens that are passed into this mapper during the URI
 * interpretation process. The value of this token can be obtained with {@link #getCurrentURIToken()}. As this action
 * mapper class is a particularly configured {@link RegexURIPathSegmentActionMapper}, all of the description of {@link
 * RegexURIPathSegmentActionMapper} also applies to this class.
 *
 * @author Roland Krüger
 * @since 1.0
 */
public class CatchAllURIPathSegmentActionMapper extends RegexURIPathSegmentActionMapper {
    private static final long serialVersionUID = -5033766191211958005L;

    public CatchAllURIPathSegmentActionMapper(String parameterId) {
        super("(.*)", new SingleStringURIParameter(parameterId));
    }

    /**
     * <p> {@inheritDoc} </p> <p> Invariably returns <code>true</code> for this {@link
     * CatchAllURIPathSegmentActionMapper}. </p>
     */
    @Override
    protected boolean isResponsibleForToken(String uriToken) {
        return true;
    }
}
