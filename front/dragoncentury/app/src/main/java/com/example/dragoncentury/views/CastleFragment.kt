package com.example.dragoncentury.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dragoncentury.R
import com.example.dragoncentury.adapters.GtnCocheAdapter
import com.example.dragoncentury.models.CocheModel
import com.example.dragoncentury.viewmodel.CocheViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CastleFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_castle, container, false)
    }

    companion object {
        @JvmStatic fun newInstance(param1: String, param2: String) =
                CastleFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private lateinit var rvGtnCoches: RecyclerView
    private var gtnCochesList: List<CocheModel> = listOf()
    private val cocheViewModel : CocheViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListCoches(view)
        //initDataInRecycleView(view)
    }
    private fun initDataInRecycleView(view: View) {
        rvGtnCoches = view.findViewById(R.id.rvGtnCoches)
        rvGtnCoches.layoutManager = LinearLayoutManager(context)
        rvGtnCoches.adapter = GtnCocheAdapter(gtnCochesList, {onCocheSelected(it)})
    }

    private fun onCocheSelected(coche: CocheModel) {
        Toast.makeText(requireContext(), coche.nameCoche, Toast.LENGTH_LONG).show()
    }

    private fun getListCoches(view: View) {
        cocheViewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            gtnCochesList = it
            initDataInRecycleView(view)
        })
        cocheViewModel.getCoches(requireContext())
    }


}