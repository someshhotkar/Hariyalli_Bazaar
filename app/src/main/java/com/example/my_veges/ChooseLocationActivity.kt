package com.example.my_veges

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.my_veges.databinding.ActivityChooseLocationBinding
import android.widget.ArrayAdapter

class ChooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize the list of locations
        val locationList: Array<String> = arrayOf("Jaipur", "Odisha", "Bundi", "Sikar")

        // Create an ArrayAdapter to convert the location list into a dropdown menu
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)

        // Set the adapter to the AutoCompleteTextView
        binding.listOfLocation.setAdapter(adapter)
    }
}
