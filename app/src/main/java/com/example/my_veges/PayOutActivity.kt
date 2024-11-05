package com.example.my_veges

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_veges.databinding.ActivityPayOutBinding
import com.example.my_veges.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Set user data
        setUserData()

        // Retrieve intent extras
        val intent = intent
        foodItemName = intent.getStringArrayListExtra("FoodItemName") as? ArrayList<String> ?: ArrayList()
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") as? ArrayList<String> ?: ArrayList()
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") as? ArrayList<String> ?: ArrayList()
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantities") as? ArrayList<Int> ?: ArrayList()

        // Calculate total amount only if there are items in the list
        totalAmount = if (foodItemPrice.isNotEmpty() && foodItemQuantities.isNotEmpty()) {
            calculateTotalAmount().toString() + "$"
        } else {
            "0$"
        }

        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        binding.backButton.setOnClickListener{
            finish()
        }

        // Set 'Place My Order' button click event
        binding.PlaceMyOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            if (name.isBlank() || address.isBlank()) {
                Toast.makeText(this, "Please Enter All The Details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time: Long = System.currentTimeMillis()
        val itemPushKey: String? = databaseReference.child("OrderDetails").push().key

        // Create the OrderDetails object
        val orderDetails = OrderDetails(
            useruid = userId,
            userName = name,
            foodNames = foodItemName,
            foodImages = foodItemImage,
            foodPrices = foodItemPrice,
            foodQuantities = foodItemQuantities,
            address = address,
            totalPrice = totalAmount,
            orderAccepted = false,
            paymentReceived = false,
            itemPushKey = itemPushKey,
            currentTime = time
        )

        // Push the order to Firebase
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey ?: "")
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Congrats")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        val historyReference = databaseReference.child("user").child(userId).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
        historyReference.setValue(orderDetails).addOnSuccessListener {
            Toast.makeText(this, "Order added to history", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add order to history", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0

        // Loop through the food items
        for (i in foodItemPrice.indices) {
            val priceString: String = foodItemPrice[i]

            // Parse the price into an integer (removing non-digit characters)
            val priceIntValue: Int = priceString.filter { it.isDigit() }.toIntOrNull() ?: 0

            // Get the quantity for the current food item
            val quantity: Int = foodItemQuantities.getOrNull(i) ?: 1

            // Calculate total for the current item and add to the totalAmount
            totalAmount += priceIntValue * quantity
        }

        return totalAmount
    }

    private fun setUserData() {
        val user: FirebaseUser? = auth.currentUser
        if (user != null) {
            userId = user.uid
            val userReference = databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val names: String = snapshot.child("name").getValue(String::class.java) ?: ""
                        val addresses: String = snapshot.child("address").getValue(String::class.java) ?: ""
                        binding.name.setText(names)
                        binding.address.setText(addresses)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PayOutActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
