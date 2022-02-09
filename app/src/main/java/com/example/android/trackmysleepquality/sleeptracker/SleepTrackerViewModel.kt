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
import android.provider.SyncStateContract.Helpers.insert
import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 * val database: SleepDatabaseDao: Zugriff auf Datenbank durch Instance von DAO ermöglichen
 * application: Application: Um Ressources, Strings etc Zugriff zu haben
 * Diese beiden werden hineingegeben
 *
 *
 */
class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    // hold current night --> Mit Livedata kann man observe und Mutable noch ändern
    private var tonight = MutableLiveData<SleepNight?>()

    // Abfrage von allen Nächten
    private val nights = database.getAllNights()

    val nightsToString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)

    }

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean>
        get() = _isRunning


    // To initialize the tonight
    init {
        initializeTonight()
        _isRunning.value = false
    }


    // viewModelScope.launch{} start a coroutine in the ViewModelScope
    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    // coroutine get tonight from the database
    // Start time ungleich EndTime --> return null, sonst night
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    fun onStartTracking() {
        if (_isRunning.value == false) {
            viewModelScope.launch {
                // Create new SleepNight
                val newNight = SleepNight()
                // In DB speichern
                insert(newNight)
                tonight.value = getTonightFromDatabase()
            }
            _isRunning.value = true
        }
    }

    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    // ClickListener Aufruf in XML
    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch

            oldNight.endTimeMilli = System.currentTimeMillis()

            update(oldNight)
            _isRunning.value = false
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    // ClickListener Aufruf in XML
    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
            _isRunning.value = false
        }

    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }


}

