package com.infolitz.cartitinfo.modals

data class ProductViewModal(
    val productName: String,
    val productId: String,
    val description: String,
    val price: Double,
    val stockCount: Int,
    val storeId: String,
    val proImgUrl: String,
    val productOldPrice: Double
)
