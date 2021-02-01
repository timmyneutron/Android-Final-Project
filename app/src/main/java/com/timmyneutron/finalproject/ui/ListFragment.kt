package com.timmyneutron.finalproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timmyneutron.finalproject.R

class ListFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        viewModel.getPlaces()
    }

    private fun initAdapter(root: View) {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        rv?.layoutManager = LinearLayoutManager(context)

        val adapter = PlaceListAdapter(viewModel)
        rv?.adapter = adapter

        viewModel.observePlaces().observe(viewLifecycleOwner,
        Observer { placeList ->
            adapter.submitList(placeList)
            adapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        initAdapter(view.rootView)
        return view
    }
}