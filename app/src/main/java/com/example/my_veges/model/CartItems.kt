package com.example.my_veges.model

data class CartItems(
    var foodName: String? = null,
    var foodPrice: String? = null,
    var foodDescription: String? = null,
    var foodImageVal: String? = null,  // Corrected the name
    var foodQuantity: Int? = null,

    var foodIngredint:String?=null
)
