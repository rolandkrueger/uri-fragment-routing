package org.roklib.webapps.uridispatching.mapper;

/**
 * This action handler is used to invariably interpret all URI tokens that are passed into this handler during the URI
 * interpretation process. The value of this token can be obtained with {@link #getCurrentURIToken()}. As this action
 * handler class is a particularly configured {@link RegexURIPathSegmentActionMapper}, all of the description of {@link
 * RegexURIPathSegmentActionMapper} also applies to this class.
 *
 * @author Roland Krüger
 * @since 1.1.0
 */
public class CatchAllURIPathSegmentActionMapper extends RegexURIPathSegmentActionMapper {
    private static final long serialVersionUID = -5033766191211958005L;

    public CatchAllURIPathSegmentActionMapper() {
        super("(.*)");
    }

    /**
     * Returns the value of the URI token that has been interpreted by this action handler.
     *
     * @return the URI token captured by this action handler
     */
    public String getCurrentURIToken() {
        String[] matchedTokenFragments = getMatchedTokenFragments();
        if (matchedTokenFragments != null) {
            return matchedTokenFragments[0];
        }
        return null;
    }

    /**
     * <p> {@inheritDoc} </p> <p> Invariably returns <code>true</code> for this {@link
     * CatchAllURIPathSegmentActionMapper}. </p>
     */
    @Override
    protected boolean isResponsibleForToken(String uriToken) {
        matchedTokenFragments = new String[]{uriToken};
        return true;
    }
}