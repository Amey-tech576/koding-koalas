package com.example.swiftcafe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.example.swiftcafe.fragment.CongratsBottomSheet
import com.example.swiftcafe.databinding.ActivityPayOutBinding
import com.example.swiftcafe.Model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    lateinit var binding: ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemDescription: ArrayList<String>
    private lateinit var foodItemIngredient: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var gpay: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase and User details
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        // Set user data
        setUserData()

        // Retrieve intent data
        val intent = intent
        foodItemName = intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("FoodItemDescription") as ArrayList<String>
        foodItemIngredient = intent.getStringArrayListExtra("FoodItemIngredient") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantities") as ArrayList<Int>

        totalAmount = calculateTotalAmount().toString() + "₹"
        binding.totalAmount.setText(totalAmount)

        binding.backeButton.setOnClickListener {
            finish()
        }

        binding.PlaceMyOrder.setOnClickListener {
            // Retrieve data from text fields
            gpay = findViewById(R.id.radioButtonGPay)
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            // Validation checks
            when {
                name.isBlank() || address.isBlank() || phone.isBlank() -> {
                    Toast.makeText(this, "Please Enter All The Details 😜", Toast.LENGTH_SHORT).show()
                }
                phone.length < 10 -> {
                    Toast.makeText(this, "Phone number must be at least 10 digits! 😜", Toast.LENGTH_SHORT).show()
                }
                gpay.isChecked -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val url = Uri.parse("https://upilinks.in/payment-link/upi728579088")
                    intent.data = url
                    startActivity(intent)
                }
                else -> {
                    placeOrder()
                }
            }
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(
            userId, name, foodItemName, foodItemPrice, foodItemImage, foodItemQuantities,
            address, totalAmount, phone, time, itemPushKey, false, false
        )
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to order 😒", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userId).child("BuyHistory").child(orderDetails.itemPushKey!!).setValue(orderDetails)
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("CartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (i in foodItemPrice.indices) {
            val price = foodItemPrice[i]
            val priceValue = if (price.last() == '₹') price.dropLast(1).toInt() else price.toInt()
            val quantity = foodItemQuantities[i]
            totalAmount += priceValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userReference = databaseReference.child("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val names = snapshot.child("name").getValue(String::class.java) ?: ""
                        val addresses = snapshot.child("address").getValue(String::class.java) ?: ""
                        val phones = snapshot.child("phone").getValue(String::class.java) ?: ""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresses)
                            phone.setText(phones)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
