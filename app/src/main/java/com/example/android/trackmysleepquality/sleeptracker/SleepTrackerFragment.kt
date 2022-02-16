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
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    // Object of ViewModel,viewModelFactory.... created
    private lateinit var sleepTrackerViewModel: SleepTrackerViewModel
    private lateinit var viewModelFactory: SleepTrackerViewModelFactory
    private lateinit var application: Application
    private lateinit var dataSource: SleepDatabaseDao

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false
        )
        //requireNotNull: Kotlin function  Wenn Wert null --> Exception
        // reference to the application context.
        application = requireNotNull(this.activity).application
        // To get a reference to the DAO of the database
        dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        // Create an instance of the viewModelFactory
        viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        // Get a reference to the SleepTrackerViewModel
        sleepTrackerViewModel = ViewModelProvider(this, viewModelFactory)
            .get(SleepTrackerViewModel::class.java)
        // Assign the sleepTrackerViewModel binding variable to the sleepTrackerViewModel.
        binding.sleepTrackerViewModel = sleepTrackerViewModel
        // Binding can observe LiveData Updates
        binding.lifecycleOwner = this

        // Observer setzen für Navigation Variable
        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner) { night ->
            night?.let {
                this.findNavController()
                    .navigate(
                        SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(
                            night.nightId
                        )
                    )
                sleepTrackerViewModel.doneNavigating()
            }
        }

        sleepTrackerViewModel.snackBarEvent.observe(viewLifecycleOwner) { snackbar ->
            if (snackbar == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                sleepTrackerViewModel.doneShowingSnackBar()
            }
        }

        /**
         * Gridlayout mit 3 Items pro Spalte
         * --> Muss in Kotlin geamcht wereden
         * Falls LinaerLayoutmanager pro Reihe 1 --> Dann in XML mit  app:layoutManager
         */
        val layoutManager = GridLayoutManager(activity, 3)
        binding.rcList.layoutManager = layoutManager

        // Create new Adapter with Click Listeners
        val adapter = SleepTrackerAdapter(SleepNightListeners { nightId ->
            Toast.makeText(context,"$nightId",Toast.LENGTH_SHORT).show()
        })
        // Bind RC
        binding.rcList.adapter = adapter


        sleepTrackerViewModel.nights.observe(viewLifecycleOwner) {
            it?.let {
                // submitList: Methode von DiffUtil, welches die Liste Updatet
                adapter.submitList(it)
            }
        }
        return binding.root
    }
}
