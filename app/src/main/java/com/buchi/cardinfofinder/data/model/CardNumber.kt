package com.buchi.cardinfofinder.data.model

data class CardNumber(
    val length: Int? = null,
    val luhn: Boolean = true
)