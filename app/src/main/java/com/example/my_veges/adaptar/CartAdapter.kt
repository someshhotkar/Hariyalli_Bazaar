package com.example.my_veges.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_veges.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private val cartImages: MutableList<String>,
    private val cartDescriptions: MutableList<String>,
    private val cartQuantities: MutableList<Int>, // Changed to Int
    private val cartIngredient: MutableList<String> // Assuming ingredients are Strings
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var cartItemsReference: DatabaseReference
    private var itemQuantities: MutableList<Int> = cartQuantities.toMutableList()

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val userId: String = auth.currentUser?.uid ?: ""
        cartItemsReference = database.reference.child("user").child(userId).child("CartItems")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    fun getUpdatedItemQuantities(): MutableList<Int> {
        return itemQuantities.toMutableList() // Return a copy of itemQuantities
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                val quantity: Int = itemQuantities[position]
                cartFoodName.text = cartItems[position]
                cartItemPrice.text = cartItemPrices[position]

                // Load image using Glide
                val uri: Uri = Uri.parse(cartImages[position])
                Glide.with(binding.root.context).load(uri).into(cartImage)

                cartItemQuantity.text = quantity.toString()

                // Decrease quantity button listener
                minusbutton.setOnClickListener {
                    decreaseQuantity(position)
                }

                // Increase quantity button listener
                plusbutton.setOnClickListener {
                    increaseQuantity(position)
                }

                // Delete button listener
                deleteButton.setOnClickListener {
                    deleteItem(position)
                }
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            getUniqueKeyAtPosition(position) { uniqueKey ->
                uniqueKey?.let {
                    removeItem(position, it)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                // Remove the item from the lists
                cartItems.removeAt(position)
                cartImages.removeAt(position)
                cartDescriptions.removeAt(position)
                cartQuantities.removeAt(position)
                cartItemPrices.removeAt(position)
                cartIngredient.removeAt(position)

                Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()

                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show()
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String? = null
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == positionRetrieve) {
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(null)
                }
            })
        }
    }
}
