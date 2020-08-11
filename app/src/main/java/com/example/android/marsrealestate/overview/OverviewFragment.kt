/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsApiFilter

/**
 * This fragment shows the the status of the Mars real-estate web services transaction.
 */
class OverviewFragment : Fragment() {

    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //for fragment_overview.xml
        val binding = FragmentOverviewBinding.inflate(inflater)

        //for grid_view_item.xml when it used viewModel data variable
        //val binding = GridViewItemBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        //Initialize the recyclerview photogridadapter in binding.photosGrid
        //binding.photosGrid.adapter = PhotoGridAdapter()
        //add photogridadapter onclicklistener object to photogridadapter constructor
        //that calls displayPropertyDetails with passed in marsproperty to trigger livedata
        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
        })

        //observer of navigatetoselectedproperty
        viewModel.navigateToSelectedProperty.observe(this, Observer {
            //when marsproperty is not null
            if ( null != it ) {
                //navigate using overviewfragmentdirections.actionshowdetail method
                this.findNavController().navigate(OverviewFragmentDirections.actionShowDetail(it))
                viewModel.displayPropertyDetailsComplete() //reset livedata to null state
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //figure out which item in menu was clicked and pass the corresponding
        //filter vaue
        viewModel.updateFilter(
                when (item?.itemId) {
                    R.id.show_rent_menu -> MarsApiFilter.SHOW_RENT
                    R.id.show_buy_menu -> MarsApiFilter.SHOW_BUY
                    else -> MarsApiFilter.SHOW_ALL
                }
        )
        return true  //return true since we handled the menu item
    }
}
