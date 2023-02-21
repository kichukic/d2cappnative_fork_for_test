package com.infolitz.cartitinfo.modals

data class CartProductDetailsModel (
    val productName: String,
    val productId: String,
    val price: Double,
    val quantityInCart: Int,
    val storeId: String,
)
