package io.github.jwgibanez.cartrack.view.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.jwgibanez.cartrack.viewmodel.LoginViewModel

import android.view.MenuInflater
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.data.model.User
import io.github.jwgibanez.cartrack.databinding.FragmentListBinding

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

        viewModel.users.observe(viewLifecycleOwner) { facts ->
            //binding.emptyMessage.setVisibility(if (facts.size() > 0) GONE else VISIBLE)
            adapter.submitList(facts)
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
                viewModel.fetchUsers(requireActivity())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClick(user: User) {
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment()
        findNavController().navigate(action)
    }
}