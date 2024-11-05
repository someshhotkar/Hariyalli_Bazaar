package com.example.my_veges.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.my_veges.databinding.FragmentProfileBinding
import com.example.my_veges.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// A simple [Fragment] subclass.
class ProfileFragment : Fragment() {

    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Binding instance
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Set user data when the fragment is created
        setUserData()

        binding.apply {
            // Disable fields initially
            name.isEnabled = false
            email.isEnabled = false
            address.isEnabled = false
            phone.isEnabled = false

            // Toggle edit mode when clicking the edit button
            editButton.setOnClickListener {
                name.isEnabled = !name.isEnabled
                email.isEnabled = !email.isEnabled
                address.isEnabled = !address.isEnabled
                phone.isEnabled = !phone.isEnabled
            }
        }

        // Set up the save button click listener
        binding.saveInfoButton.setOnClickListener {
            val name: String = binding.name.text.toString()
            val email: String = binding.email.text.toString()
            val address: String = binding.address.text.toString()
            val phone: String = binding.phone.text.toString()

            // Call method to update user data
            updateUserData(name, email, address, phone)
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId: String? = auth.currentUser?.uid
        if (userId != null) {
            val userReference: DatabaseReference = database.getReference("user").child(userId)
            val userData: HashMap<String, String> = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )

            // Update user data in Firebase and handle success/failure
            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Profile update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to set user data
    private fun setUserData() {
        val userId: String? = auth.currentUser?.uid
        if (userId != null) {
            val userReference: DatabaseReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile: UserModel? = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            // Set the user data to the corresponding views
                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.email.setText(userProfile.email)
                            binding.phone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors here
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks by setting binding to null
    }
}
