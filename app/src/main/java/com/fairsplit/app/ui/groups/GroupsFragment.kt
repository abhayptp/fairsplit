package com.fairsplit.app.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairsplit.app.FairSplitApp
import com.fairsplit.app.R
import com.fairsplit.app.data.db.entity.Group
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.databinding.FragmentGroupsBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GroupsFragment : Fragment() {
    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupsViewModel by viewModels {
        val app = requireActivity().application as FairSplitApp
        GroupsViewModel.Factory(app.groupRepository, app.userRepository)
    }

    private lateinit var adapter: GroupsAdapter
    private var allUsers: List<User> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = GroupsAdapter(
            onClick = { group -> navigateToGroup(group) },
            onDelete = { group ->
                AlertDialog.Builder(requireContext())
                    .setMessage("Delete group \"${group.name}\"?")
                    .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteGroup(group.id) }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner) { users -> allUsers = users }

        viewModel.groups.observe(viewLifecycleOwner) { groups ->
            // We need member counts — for simplicity show 0 as placeholder, updated when members load
            val items = groups.map { GroupWithCount(it, 0) }
            adapter.submitList(items)
            binding.emptyText.visibility = if (groups.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fab.setOnClickListener { showCreateGroupDialog() }
    }

    private fun navigateToGroup(group: Group) {
        val action = GroupsFragmentDirections.actionGroupsToGroupDetail(
            groupId = group.id,
            groupName = group.name
        )
        findNavController().navigate(action)
    }

    private fun showCreateGroupDialog() {
        if (allUsers.isEmpty()) {
            AlertDialog.Builder(requireContext())
                .setMessage("Add some friends first before creating a group.")
                .setPositiveButton(android.R.string.ok, null)
                .show()
            return
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
        }

        val nameLayout = TextInputLayout(requireContext()).apply { hint = "Group name" }
        val nameInput = TextInputEditText(requireContext())
        nameLayout.addView(nameInput)
        container.addView(nameLayout)

        val checkboxes = allUsers.map { user ->
            CheckBox(requireContext()).apply {
                text = user.name
                tag = user.id
            }
        }
        checkboxes.forEach { container.addView(it) }

        AlertDialog.Builder(requireContext())
            .setTitle("Create Group")
            .setView(container)
            .setPositiveButton("Create") { _, _ ->
                val name = nameInput.text.toString()
                val selectedIds = checkboxes.filter { it.isChecked }.map { it.tag as Long }
                viewModel.createGroup(name, selectedIds)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
