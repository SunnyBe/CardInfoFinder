package com.buchi.cardinfofinder.data.model

data class Card(
    val scheme: String? = null,
    val type: String? = null,
    val brand: String? = null,
    val prepaid: Boolean = false,
    val bank: Bank? = null,
    val country: Country? = null,
    val number: CardNumber? = null
) {
    companion object {
        fun testCard(number: String?) = Card(
            scheme = "visa",
            type = "debit",
            brand = "Visa/Dankort",
            prepaid = false,
            bank = Bank(
                name = "Jske Bank",
                url = "www.jyskebank.dk",
                phone = "+4589893300",
                city = "Hjørring"
            ),
            country = Country(
                numeric = "208",
                alpha = "DK",
                name = "Denmark",
                emoji = "\uD83C\uDDE9\uD83C\uDDF0",
                currency = "DKK",
                latitude = 56,
                longitude = 10
            ),
            number = CardNumber(
                length = number?.length,
                luhn = true
            )
        )
    }
}