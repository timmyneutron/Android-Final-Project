package com.timmyneutron.finalproject

import android.location.Geocoder
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.timmyneutron.finalproject.model.Place
import com.timmyneutron.finalproject.ui.MainViewModel

class AddActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var geocoder: Geocoder
    private val categories: Array<String> by lazy {
        resources.getStringArray(R.array.categories)
    }
    private fun initSubmitButton() {
        val submitButton = findViewById<Button>(R.id.addSubmitButton)
        geocoder = Geocoder(this)
        submitButton.setOnClickListener {
            val titleET = findViewById<EditText>(R.id.titleET)
            if (titleET.text.isBlank()) {
                val toast = Toast.makeText(this, "Please enter a title!", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            val descriptionET = findViewById<EditText>(R.id.descriptionET)
            if (descriptionET.text.isBlank()) {
                val toast = Toast.makeText(this, "Please enter a description!", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            val addressET = findViewById<EditText>(R.id.addressET)
            if (addressET.text.isBlank()) {
                val toast = Toast.makeText(this, "Please enter an address!", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }
            val locations = geocoder.getFromLocationName(addressET.text.toString(), 1)
            if (locations.isEmpty()) {
                val toast = Toast.makeText(this, "No locations found for that address!", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }

            val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
            if (categorySpinner.selectedItemPosition == 0) {
                val toast = Toast.makeText(this, "Please select a category!", Toast.LENGTH_SHORT)
                toast.show()
                return@setOnClickListener
            }

            val place = Place()
            place.title = titleET.text.toString()
            place.description = descriptionET.text.toString()
            place.location = GeoPoint(locations[0].latitude, locations[0].longitude)
            place.voteScore = 0
            place.category = categories[categorySpinner.selectedItemPosition]
            place.creator = FirebaseAuth.getInstance().currentUser!!.displayName
            viewModel.postPlace(place)
            finish()
        }
    }

    private fun initCategorySpinner() {
        val categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories,
                android.R.layout.simple_spinner_item)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        categorySpinner.adapter = categoryAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val addToolbar: Toolbar = findViewById(R.id.addToolbar)
        setSupportActionBar(addToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initSubmitButton()
        initCategorySpinner()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}