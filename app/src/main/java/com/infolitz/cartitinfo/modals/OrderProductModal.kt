package com.infolitz.cartitinfo.modals

data class OrderProductModal(
    val productName: String,
    val productId: String,
    val totalPrice: Double,
    val totalItems: String,
    val orderStatus: String,
    val orderPlacedDate:String,
    val addressCust:String,
    val mobileCustomer:String,
)
