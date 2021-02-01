package com.timmyneutron.finalproject

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.timmyneutron.finalproject.model.Comment
import com.timmyneutron.finalproject.ui.CommentListAdapter
import com.timmyneutron.finalproject.ui.MainViewModel
import java.util.*


class DetailActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var title: String
    private lateinit var creator: String
    private lateinit var description: String
    private var voteScore = 0
    private var id: String? = null

    fun hideKeyboard() {
        val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    fun showKeyboard(view: View) {
        val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setInfo() {
        val activityThatCalled = intent
        val callingBundle = activityThatCalled.extras
        callingBundle?.let { bundle ->
            bundle.getString("title")?.let {
                viewModel.getHeadlines(it)
            }
            title = bundle.getString("title") ?: "<no title>"
            description = bundle.getString("description") ?: "<no description>"
            creator = bundle.getString("creator") ?: "<anonymous>"
            voteScore = bundle.getInt("voteScore")
            id = bundle.getString("id")
        }
    }

    private fun displayInfo() {
        val detailTitle = findViewById<TextView>(R.id.detailTitle)
        val detailDescription = findViewById<TextView>(R.id.detailDescription)
        val detailCreator = findViewById<TextView>(R.id.detailCreator)
        val placeVoteScore = findViewById<TextView>(R.id.placeVoteScore)
        detailTitle.text = title
        detailCreator.text = "by ${creator}"
        detailDescription.text = description
        placeVoteScore.text = voteScore.toString()
    }

    private fun getComments() {
        val activityThatCalled = intent
        val callingBundle = activityThatCalled.extras
        callingBundle?.let { bundle ->
            viewModel.getComments(id)
        }
    }

    private fun initCommentAdapter() {
        val rv = findViewById<RecyclerView>(R.id.commentRecyclerView)
        rv?.layoutManager = LinearLayoutManager(this)
        val adapter = CommentListAdapter(viewModel)
        rv.adapter = adapter

        viewModel.observeComments().observe(this,
                Observer { commentList ->
                    adapter.submitList(commentList)
                    adapter.notifyDataSetChanged()
                })
    }

    private fun initVoteArrows() {
        val detailUpVoteArrow = findViewById<ImageButton>(R.id.detailUpVoteArrow)
        val detailDownVoteArrow = findViewById<ImageButton>(R.id.detailDownVoteArrow)
        val placeVoteScore = findViewById<TextView>(R.id.placeVoteScore)

        detailUpVoteArrow.setOnClickListener {
            voteScore += 1
            placeVoteScore.text = voteScore.toString()
            viewModel.upVotePlace(id)
        }
        detailDownVoteArrow.setOnClickListener {
            voteScore -= 1
            placeVoteScore.text = voteScore.toString()
            viewModel.downVotePlace(id)
        }
    }

    private fun showDeleteAndEditButtons() {
        val deleteAndEditButtons = findViewById<LinearLayout>(R.id.editDeleteButtons)
        FirebaseAuth.getInstance().currentUser?.let { user ->
            if (creator == user.displayName) {
                deleteAndEditButtons.visibility = View.VISIBLE
            }
        }
    }

    private fun initDeleteButton() {
        val deleteButton = findViewById<Button>(R.id.deletePlaceButton)
        deleteButton.setOnClickListener {
            viewModel.deletePlace(id)
            finish()
        }
    }
    
    private fun initToggleCommentButton() {
        val addCommentButton = findViewById<FloatingActionButton>(R.id.addCommentButton)
        val addCommentET = findViewById<EditText>(R.id.addCommentET)
        val submitCommentButton = findViewById<Button>(R.id.submitCommentButton)

        addCommentButton.setOnClickListener {
            if (addCommentET.visibility == View.VISIBLE) {
                addCommentET.text.clear()
                addCommentET.visibility = View.GONE
                submitCommentButton.visibility = View.GONE
                addCommentButton.setImageResource(R.drawable.ic_add)
                hideKeyboard()
            } else {
                addCommentET.visibility = View.VISIBLE
                submitCommentButton.visibility = View.VISIBLE
                addCommentButton.setImageResource(R.drawable.ic_x)
                addCommentET.requestFocus()
                showKeyboard(addCommentET)
            }
        }
    }

    private fun initSubmitCommentButton() {
        val submitCommentButton = findViewById<Button>(R.id.submitCommentButton)
        val addCommentButton = findViewById<FloatingActionButton>(R.id.addCommentButton)
        val addCommentET = findViewById<EditText>(R.id.addCommentET)

        submitCommentButton.setOnClickListener {
            if (addCommentET.text.isNotBlank()) {
                var comment = Comment()
                comment.body = addCommentET.text.toString()
                comment.commentor = FirebaseAuth.getInstance().currentUser!!.displayName.toString()
                comment.voteScore = 0
                comment.placeID = id
                viewModel.postComment(id, comment)
                addCommentET.text.clear()
                addCommentButton.setImageResource(R.drawable.ic_add)
                addCommentET.clearFocus()
                addCommentET.visibility = View.GONE
                submitCommentButton.visibility = View.GONE
                hideKeyboard()
            } else {
                val toast = Toast.makeText(this, "Please enter a comment!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun initNewsObserver() {
        viewModel.observeHeadlines().observe(this,
            Observer { urlheadlines ->

                if (urlheadlines.size > 0) {
                    val nyTimesText = findViewById<TextView>(R.id.nyTimesText)
                    nyTimesText.visibility = View.VISIBLE
                    val nytimesLink1 = findViewById<TextView>(R.id.nytimesLink1)
                    val text1 = "<a href=\"${urlheadlines[0].url}\">${urlheadlines[0].headline}</a>"
                    nytimesLink1.text = Html.fromHtml(text1, 0)
                    nytimesLink1.movementMethod = LinkMovementMethod.getInstance()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val detailToolbar: Toolbar = findViewById(R.id.detailToolbar)
        setSupportActionBar(detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setInfo()
        displayInfo()
        initCommentAdapter()
        getComments()
        initToggleCommentButton()
        initSubmitCommentButton()
        initVoteArrows()
        initDeleteButton()
        showDeleteAndEditButtons()
        initNewsObserver()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }
}