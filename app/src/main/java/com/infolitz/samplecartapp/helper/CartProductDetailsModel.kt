package com.infolitz.samplecartapp.helper

data class CartProductDetailsModel (
    val productName: String,
    val productId: String,
    val price: Double,
    val quantityInCart: Int,
    val storeId: String,
)
