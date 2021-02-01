package com.timmyneutron.finalproject.model
import com.google.firebase.firestore.GeoPoint

data class Place(
    var creator: String? = null,
    var description: String? = null,
    var location: GeoPoint? = null,
    var title: String? = null,
    var category: String? = null,
    var voteScore: Int = 0,
    var id: String = ""
) {
    fun searchFor(searchTerm: String) : Boolean {
        if (title == null || category == null)
            return false
        if (title!!.indexOf(searchTerm, ignoreCase = true) == -1
                && category!!.indexOf(searchTerm, ignoreCase = true) == -1)
            return false
        return true
    }
}