package com.buchi.cardinfofinder.data.repository

import com.buchi.cardinfofinder.presentation.MainViewState
import com.buchi.cardinfofinder.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun fetchCard(cardNumber: String?): Flow<ResultState<MainViewState>>
}