package com.buchi.cardinfofinder.data.network

import com.buchi.cardinfofinder.data.model.Card
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    /**
     * Get card information by returning a card from the api request for the query entry
     * @param queryEntry The card number to be queried
     */
    @GET("{cardNumber}")
    suspend fun card(
        @Path("cardNumber") queryEntry: String
    ): Card
}