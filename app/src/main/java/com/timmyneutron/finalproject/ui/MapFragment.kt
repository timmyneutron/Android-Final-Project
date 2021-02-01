package com.timmyneutron.finalproject.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.timmyneutron.finalproject.R
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.timmyneutron.finalproject.DetailActivity

class MapFragment : Fragment(),
    OnMapReadyCallback {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var map: GoogleMap
    var markers = mutableListOf<Marker>()

    override fun onResume() {
        super.onResume()
        viewModel.getPlaces()
    }

    private fun clearMarkers() {
        markers.forEach { marker ->
            marker.remove()
        }
        markers = mutableListOf<Marker>()
    }

    private fun initObserver() {
        viewModel.observePlaces().observe(viewLifecycleOwner,
            Observer { placeList ->
                clearMarkers()
                placeList.forEach { place ->
                        var latLong = LatLng(place.location!!.latitude, place.location!!.longitude)
                        val marker = map.addMarker(MarkerOptions()
                            .position(latLong)
                            .title(place.title)
                        )
                    markers.add(marker)
                }
            })
    }

    private fun initClickListeners() {
        map.setOnInfoWindowClickListener { marker ->
            val idx = markers.indexOf(marker)
            val place = viewModel.getPlace(idx)
            place?.let {
                viewModel.doDetail(this.requireContext(), it)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val NYC = LatLng(40.74468096617014, -73.94727720666731)
        val zoomLevel = 11.0F

        googleMap?.let { gm ->
            gm.moveCamera(CameraUpdateFactory.newLatLngZoom(NYC, zoomLevel))
            map = gm
        }
        initObserver()
        initClickListeners()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        val mapView = childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        mapView.getMapAsync(this)
        return root
    }
}