package com.timmyneutron.finalproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.timmyneutron.finalproject.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0);
    }

    private fun initActionBar(actionBar: ActionBar) {
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        val customView: View = layoutInflater.inflate(R.layout.action_bar, null)
        actionBar.customView = customView
    }

    private fun setAddButton() {
        val addButton = findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener {
            val AddActivityIntent = Intent(this, AddActivity::class.java)
            val result = 1
            startActivityForResult(AddActivityIntent, result)
        }
    }

    private fun setSignOutButton() {
        val signOutButton = findViewById<ImageButton>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val authInitIntent = Intent(this, AuthInitActivity::class.java)
            startActivity(authInitIntent)
        }
    }

    private fun actionSearch() {
        findViewById<EditText>(R.id.actionSearch)?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.isEmpty()) {
                    hideKeyboard()
                }
                viewModel.setSearchTerm(s.toString())
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }
        navView.setupWithNavController(navController)
        setAddButton()
        setSignOutButton()
        actionSearch()
        val authInitIntent = Intent(this, AuthInitActivity::class.java)
        startActivity(authInitIntent)
    }
}