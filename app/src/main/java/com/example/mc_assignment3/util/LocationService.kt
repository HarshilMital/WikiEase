package com.example.mc_assignment3.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service to handle location-related operations.
 */
class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    private val cancellationTokenSource = CancellationTokenSource()
    
    /**
     * Check if the app has location permissions
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get the current location as a suspending function
     */
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            suspendCancellableCoroutine { continuation ->
                val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
                
                fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                    .addOnSuccessListener { location ->
                        continuation.resume(location)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
                
                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }
        } catch (e: Exception) {
            Log.e("LocationService", "Error getting location", e)
            null
        }
    }
    
    /**
     * Get the address from coordinates using Geocoder
     */
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var address: Address? = null
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        address = addresses[0]
                    }
                }
                address
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()
            }
        } catch (e: IOException) {
            Log.e("LocationService", "Error getting address from location", e)
            null
        }
    }
    
    /**
     * Get keywords related to the current location
     */
    suspend fun getLocationRelatedKeywords(): List<String> {
        val location = getCurrentLocation() ?: return emptyList()
        val address = getAddressFromLocation(location.latitude, location.longitude) ?: return emptyList()
        
        val keywords = mutableListOf<String>()
        
        // Add different location attributes as potential keywords
        address.locality?.let { keywords.add(it) } // City
        address.adminArea?.let { keywords.add(it) } // State
        address.countryName?.let { keywords.add(it) } // Country
        address.featureName?.let { keywords.add(it) } // Feature name
        
        return keywords.distinct()
    }
    
    fun cleanup() {
        cancellationTokenSource.cancel()
    }
}