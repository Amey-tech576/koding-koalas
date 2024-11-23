package com.example.swiftcafe.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.swiftcafe.Model.UserModel
import com.example.swiftcafe.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setUserData()

        binding.apply {
            name.isEnabled = false
            email.isEnabled = false
            address.isEnabled = false
            phone.isEnabled = false

            editButton.setOnClickListener {
                name.isEnabled = !name.isEnabled
                email.isEnabled = !email.isEnabled
                address.isEnabled = !address.isEnabled
                phone.isEnabled = !phone.isEnabled
            }
        }

        binding.saveInfoButton.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val phone = binding.phone.text.toString().trim()

            // Validate the phone number
            if (phone.length == 10 && phone.all { it.isDigit() }) {
                updateUserData(name, email, address, phone)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Phone number is not valid! Please enter a 10-digit number. 😜",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone
            )

            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Profile updated successfully 😊",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Profile update failed 😒",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.email.setText(userProfile.email)
                            binding.phone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Log or handle database error if needed
                }
            })
        }
    }
}
