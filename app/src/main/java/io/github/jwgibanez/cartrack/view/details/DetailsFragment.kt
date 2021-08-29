package io.github.jwgibanez.cartrack.view.details

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(), GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var userLatLng: LatLng
    private val args: DetailsFragmentArgs by navArgs()

    private val permRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> {
                    enableMyLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showMissingPermissionError1()
                }
                else -> {
                    showMissingPermissionError2()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        // User data binding
        binding.user = args.user
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize map
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)
    }

    private fun showMissingPermissionError1() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.error_permission)
            .setMessage(R.string.location_permission_required1)
            .setPositiveButton(R.string.allow) { _, _ ->
                askPermission()
            }
            .setNegativeButton(R.string.close) { _, _ ->
                // User cancelled the dialog, go back to user list
                findNavController().popBackStack()
            }
        builder.create().show()
    }

    private fun showMissingPermissionError2() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.error_permission)
            .setMessage(R.string.location_permission_required2)
            .setNegativeButton(R.string.close) { _, _ ->
                // User cancelled the dialog, go back to user list
                findNavController().popBackStack()
            }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_find_user -> {
                map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val user = args.user
        user.address?.geo?.apply {
            // Pin user's location on map
            userLatLng = LatLng(lat?.toDouble() ?: 0.0, lng?.toDouble() ?: 0.0)
            val options =
                MarkerOptions()
                    .position(userLatLng)
                    .title(
                        if (user.name?.isNotEmpty() == true)
                            getString(R.string.user_details_location, user.username)
                        else getString(R.string.user_details_location_default)
                    )
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
            map.addMarker(options)?.apply { showInfoWindow() }
            map.moveCamera(CameraUpdateFactory.newLatLng(userLatLng))
        }

        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        googleMap.uiSettings.isZoomControlsEnabled = true
        enableMyLocation()
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
        // Do nothing
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            askPermission()
        }
    }

    private fun askPermission() {
        permRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}