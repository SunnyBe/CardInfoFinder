package com.buchi.cardinfofinder.data.model

data class Card(
    val scheme: String? = null,
    val type: String? = null,
    val brand: String? = null,
    val prepaid: Boolean = false,
    val bank: Bank? = null,
    val country: Country? = null,
    val number: CardNumber? = null
)