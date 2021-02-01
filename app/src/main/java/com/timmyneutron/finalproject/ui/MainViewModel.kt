package com.timmyneutron.finalproject.ui

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.timmyneutron.finalproject.model.Place
import com.timmyneutron.finalproject.model.Comment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.*
import com.google.firebase.firestore.FieldValue
import com.timmyneutron.finalproject.DetailActivity
import com.timmyneutron.finalproject.api.NYTimesAPI
import com.timmyneutron.finalproject.api.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var places = MutableLiveData<List<Place>>()
    private var searchPlaces = MediatorLiveData<List<Place>>()
    private var comments = MutableLiveData<List<Comment>>()
    private var searchTerm = MutableLiveData<String>()
    private val nyTimesAPI = NYTimesAPI.create()
    private val NYTimesRepository = Repository(nyTimesAPI)
    private val urlsAndHeadlines = MutableLiveData<List<NYTimesAPI.urlHeadline>>()

    init {
        searchPlaces.addSource(places) { newPosts ->
            searchTerm.value?.let { searchTerm ->
                searchPlaces.postValue(filterPlaces(newPosts, searchTerm))
            } ?: run {
                searchPlaces.postValue(newPosts)
            }
        }

        searchPlaces.addSource(searchTerm) { newSearchTerm ->
            places.value?.let { places ->
                searchPlaces.postValue(filterPlaces(places, newSearchTerm))
            }
        }
    }

    fun filterPlaces(places: List<Place>, searchTerm: String) : List<Place> {
        return places.filter { place ->
            place.searchFor(searchTerm)
        }
    }

    fun getHeadlines(q: String) = viewModelScope.launch(
            context = viewModelScope.coroutineContext
                    + Dispatchers.IO) {
        urlsAndHeadlines.postValue(NYTimesRepository.fetchHeadlines(q))
    }

    fun getPlaces() {
        db.collection("places")
                .limit(100)
                .orderBy("voteScore")
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        Log.d("XXX", "Error getting places database")
                        return@addSnapshotListener
                    }
                    val localPlaces = querySnapshot!!.documents.mapNotNull {
                        it.toObject(Place::class.java)
                    }.toMutableList()
                    localPlaces.reverse()
                    places.postValue(localPlaces)
                }
    }

    fun getComments(placeID: String?) {
        if (placeID == null)
            return
        db.collection("places/$placeID/comments")
                .limit(20)
                .orderBy("voteScore")
                .addSnapshotListener { querySnapshot, error ->
                    if (error != null) {
                        Log.d("XXX", "Error getting places database")
                        return@addSnapshotListener
                    }
                    val localComments = querySnapshot!!.documents.mapNotNull {
                        it.toObject(Comment::class.java)
                    }.toMutableList()
                    localComments.reverse()
                    comments.postValue(localComments)
                }
    }

    fun postPlace(place: Place) {
        place.id = db.collection("places").document().id
        db.collection("places")
            .document(place.id)
            .set(place)
    }

    fun postComment(placeID: String?, comment: Comment) {
        if (placeID == null)
            return
        comment.id = db.collection("places/$placeID/comments").document().id
        db.collection("places/$placeID/comments")
            .document(comment.id)
            .set(comment)
    }

    fun upVotePlace(placeID: String?) {
        if (placeID == null)
            return
        db.collection("places")
                .document(placeID)
                .update("voteScore", FieldValue.increment(1))
    }

    fun downVotePlace(placeID: String?) {
        if (placeID == null)
            return
        db.collection("places")
                .document(placeID)
                .update("voteScore", FieldValue.increment(-1))
    }

    fun upVoteComment(comment: Comment) {
        db.collection("places/${comment.placeID}/comments")
                .document(comment.id)
                .update("voteScore", FieldValue.increment(1))

    }

    fun downVoteComment(comment: Comment) {
        db.collection("places/${comment.placeID}/comments")
                .document(comment.id)
                .update("voteScore", FieldValue.increment(-1))
    }

    fun deleteComment(comment: Comment) {
        db.collection("places/${comment.placeID}/comments")
                .document(comment.id)
                .delete()
    }

    fun observePlaces(): LiveData<List<Place>> {
        return searchPlaces
    }

    fun observeComments(): LiveData<List<Comment>> {
        return comments
    }

    fun getPlace(idx: Int) : Place? {
        searchPlaces.value?.let {
            if (idx >=0 && idx < it.size) {
                return it[idx]
            }
        }
        return null
    }

    fun deletePlace(placeID: String?) {
        if (placeID == null)
            return
        db.collection("places")
                .document(placeID)
                .delete()
    }

    fun setSearchTerm(newSearchTerm: String) {
        searchTerm.postValue(newSearchTerm)
        places.value?.let {
            val filtered = it.filter {
                it.searchFor(newSearchTerm)
            }
        }
    }

    fun observeHeadlines() : LiveData<List<NYTimesAPI.urlHeadline>> {
        return urlsAndHeadlines
    }

    fun doDetail(context: Context, place: Place) {
        val detailIntent = Intent(context, DetailActivity::class.java)
        val extras = Bundle()
        extras.putString("title", place.title.toString())
        extras.putString("creator", place.creator.toString())
        extras.putString("description", place.description.toString())
        extras.putFloatArray("location", floatArrayOf(place.location!!.latitude.toFloat(), place.location!!.longitude.toFloat()))
        extras.putString("category", place.category)
        extras.putInt("voteScore", place.voteScore)
        extras.putString("id", place.id)
        detailIntent.putExtras(extras)
        context.startActivity(detailIntent, extras)
    }
}