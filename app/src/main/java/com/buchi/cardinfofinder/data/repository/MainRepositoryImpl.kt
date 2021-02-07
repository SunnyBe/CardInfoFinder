package com.buchi.cardinfofinder.data.repository

import android.content.Context
import com.buchi.cardinfofinder.data.network.ApiService
import com.buchi.cardinfofinder.presentation.MainViewState
import com.buchi.cardinfofinder.utils.ResultState
import com.buchi.cardinfofinder.utils.ReusableFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val context: Context,
    private val networkClient: ApiService,
) : MainRepository {

    private val reusableFlow: ReusableFlow<MainViewState> by lazy {
        ReusableFlow(context)
    }

    override fun fetchCard(cardNumber: String?): Flow<ResultState<MainViewState>> {
        return reusableFlow.internetCheckProcessFlow {
            val card = networkClient.card(cardNumber?:"")
            MainViewState(cardDetail = card)
        }.flowOn(Dispatchers.IO)
    }

}