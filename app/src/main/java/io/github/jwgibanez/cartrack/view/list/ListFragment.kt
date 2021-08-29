package io.github.jwgibanez.cartrack.view.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.jwgibanez.cartrack.viewmodel.LoginViewModel

import android.view.MenuInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.data.model.User
import io.github.jwgibanez.cartrack.databinding.FragmentListBinding
import java.lang.Exception

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUsers()

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val adapter = ListAdapter({ user -> onItemClick(user) }, ListAdapter.Diff())
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                // Scroll to newly added item
                layoutManager.scrollToPositionWithOffset(positionStart, 0)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                // Do nothing
            }
        })

        viewModel.users.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                adapter.submitList(users)
                binding.itemList.visibility = VISIBLE
                binding.emptyListMessage.visibility = GONE
            } else {
                binding.itemList.visibility = GONE
                binding.emptyListMessage.visibility = VISIBLE
            }
        }

        binding.itemList.adapter = adapter
        binding.itemList.layoutManager = layoutManager

        val decoration = DividerItemDecoration(binding.itemList.context, DividerItemDecoration.VERTICAL)
        binding.itemList.addItemDecoration(decoration)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                findNavController().popBackStack()
                true
            }
            R.id.action_refresh -> {
                fetchUsers()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchUsers() {
        viewModel.fetchUsers(
            requireActivity(),
            { binding.loading.visibility = VISIBLE },
            { onFetchError(it) },
            { binding.loading.visibility = GONE })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(user: User) {
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment(user)
        findNavController().navigate(action)
    }

    private fun onFetchError(exception: Exception?) {
        exception?.let {
            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
        }
    }
}