package com.example.my_veges.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class OrderDetails(
    var useruid: String? = null,
    var userName: String? = null,
    var foodNames: MutableList<String>? = null,
    var foodImages: MutableList<String>? = null,
    var foodPrices: MutableList<String>? = null,
    var foodQuantities: MutableList<Int>? = null,
    var address: String? = null,
    var totalPrice: String? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false, // Changed to Boolean
    var paymentReceived: Boolean = false, // Changed to Boolean
    var itemPushKey: String? = null,
    var currentTime: Long = 0 // Changed to Long
) : Parcelable {

    // Constructor to read from Parcel
    constructor(parcel: Parcel) : this(
        useruid = parcel.readString(),
        userName = parcel.readString(),
        foodNames = parcel.createStringArrayList(),
        foodImages = parcel.createStringArrayList(),
        foodPrices = parcel.createStringArrayList(),
        foodQuantities = mutableListOf<Int>().apply {
            parcel.readList(this, Int::class.java.classLoader)
        },
        address = parcel.readString(),
        totalPrice = parcel.readString(),
        phoneNumber = parcel.readString(),
        orderAccepted = parcel.readByte() != 0.toByte(),
        paymentReceived = parcel.readByte() != 0.toByte(),
        itemPushKey = parcel.readString(),
        currentTime = parcel.readLong()
    )

    // Writing to Parcel
  override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(useruid)
        parcel.writeString(userName)
        parcel.writeStringList(foodNames)
        parcel.writeStringList(foodImages)
        parcel.writeStringList(foodPrices)
        parcel.writeList(foodQuantities)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0) // Correctly write Boolean as Byte
        parcel.writeByte(if (paymentReceived) 1 else 0) // Correctly write Boolean as Byte
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

     override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<OrderDetails> {
        override fun createFromParcel(parcel: Parcel): OrderDetails {
            return OrderDetails(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetails?> {
            return arrayOfNulls(size)
        }
    }
}
