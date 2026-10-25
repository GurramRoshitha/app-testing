package com.company.tripplanner.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.tripplanner.data.services.WeatherInfo
import com.company.tripplanner.data.services.WeatherService
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherService = WeatherService()
    
    private val _localWeather = mutableStateOf<WeatherInfo?>(null)
    val localWeather: State<WeatherInfo?> = _localWeather

    private val _tripWeather = mutableStateOf<WeatherInfo?>(null)
    val tripWeather: State<WeatherInfo?> = _tripWeather

    private val _isLocalLoading = mutableStateOf(false)
    val isLocalLoading: State<Boolean> = _isLocalLoading

    private val _isTripLoading = mutableStateOf(false)
    val isTripLoading: State<Boolean> = _isTripLoading

    // Legacy support for single weather screen
    val weatherState: State<WeatherInfo?> = _tripWeather
    val isLoading: State<Boolean> = _isTripLoading

    fun loadLocalWeather(city: String) {
        viewModelScope.launch {
            _isLocalLoading.value = true
            _localWeather.value = weatherService.getWeather(city)
            _isLocalLoading.value = false
        }
    }

    fun loadTripWeather(city: String) {
        viewModelScope.launch {
            _isTripLoading.value = true
            _tripWeather.value = weatherService.getWeather(city)
            _isTripLoading.value = false
        }
    }
    
    // For single screen compatibility
    fun loadWeather(destination: String) = loadTripWeather(destination)
}
