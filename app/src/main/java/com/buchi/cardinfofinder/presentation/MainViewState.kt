package com.buchi.cardinfofinder.presentation

import com.buchi.cardinfofinder.data.model.Card

/**
 * All possible data to view state. i.e views be rendering any of this parameters at one point or the other
 * In idle state or when no data is been processed they can be set to null.
 */
data class MainViewState(
    val cardDetail: Card? = null
)