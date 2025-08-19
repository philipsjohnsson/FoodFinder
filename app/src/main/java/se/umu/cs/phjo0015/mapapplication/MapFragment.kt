package se.umu.cs.phjo0015.mapapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.NavHostFragment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = ComposeView(requireContext())
        view.setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FilledButtonExample(
                    onClick = { changeView() }
                )
            }
        }

        return view
        //inflater.inflate(R.layout.fragment_map, container, false)
    }

    fun changeView() {
        NavHostFragment.findNavController(this@MapFragment)
            .navigate(R.id.action_mapFragment_to_testFragment)
    }

    companion object {
        fun newInstance(): MapFragment {
            val fragment = MapFragment()
            return fragment
        }

    }
}