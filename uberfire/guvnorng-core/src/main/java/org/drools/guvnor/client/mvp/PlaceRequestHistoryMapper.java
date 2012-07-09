package org.drools.guvnor.client.mvp;

public interface PlaceRequestHistoryMapper {

    /**
     * Returns the {@link PlaceRequest} associated with the given token.
     * 
     * @param token
     *            a String token
     * @return a {@link PlaceRequest} instance
     */
    IPlaceRequest getPlaceRequest(String token);

    /**
     * Returns the String identifier associated with the given {@link PlaceRequest}.
     * 
     * @param placeRequest
     *            a {@link PlaceRequest} instance
     * @return a String identifier
     */
    String getIdentifier(IPlaceRequest placeRequest);
}