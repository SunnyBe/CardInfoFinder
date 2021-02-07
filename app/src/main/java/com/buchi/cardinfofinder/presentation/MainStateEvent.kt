package com.buchi.cardinfofinder.presentation

/**
 * All possible events that can be triggered from main viewModel
 */
sealed class MainStateEvent {
    // Events to fetch users is routed through this class
    class FetchCard(val cardNumber: String?) : MainStateEvent()

    // Idle state of the Main View, No processing here.
    class Idle(): MainStateEvent()
}