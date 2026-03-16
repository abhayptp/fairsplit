package com.fairsplit.app.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairsplit.app.FairSplitApp
import com.fairsplit.app.databinding.FragmentUsersBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class UsersFragment : Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsersViewModel by viewModels {
        UsersViewModel.Factory((requireActivity().application as FairSplitApp).userRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = UsersAdapter(onDelete = { user ->
            AlertDialog.Builder(requireContext())
                .setMessage("Remove ${user.name}?")
                .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteUser(user) }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
            binding.emptyText.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fab.setOnClickListener { showAddUserDialog() }
    }

    private fun showAddUserDialog() {
        val inputLayout = TextInputLayout(requireContext()).apply {
            hint = "Friend's name"
            setPadding(48, 16, 48, 0)
        }
        val input = TextInputEditText(requireContext())
        inputLayout.addView(input)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Friend")
            .setView(inputLayout)
            .setPositiveButton("Add") { _, _ ->
                viewModel.addUser(input.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
