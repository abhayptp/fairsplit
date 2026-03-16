package com.fairsplit.app.ui.addexpense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fairsplit.app.FairSplitApp
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.databinding.FragmentAddExpenseBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddExpenseFragment : Fragment() {
    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val args: AddExpenseFragmentArgs by navArgs()

    private val viewModel: AddExpenseViewModel by viewModels {
        val app = requireActivity().application as FairSplitApp
        AddExpenseViewModel.Factory(args.groupId, app.groupRepository, app.expenseRepository)
    }

    private var members: List<User> = emptyList()
    private val memberCheckboxes = mutableListOf<CheckBox>()
    private val customAmountInputs = mutableMapOf<Long, TextInputEditText>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        viewModel.members.observe(viewLifecycleOwner) { users ->
            members = users
            setupMemberCheckboxes(users)
            setupPaidBySpinner(users)
        }

        binding.splitTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            val isCustom = checkedId == binding.splitCustom.id
            binding.customAmountsContainer.visibility = if (isCustom) View.VISIBLE else View.GONE
        }

        binding.saveButton.setOnClickListener { saveExpense() }
    }

    private fun setupPaidBySpinner(users: List<User>) {
        val names = users.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.paidBySpinner.adapter = adapter
    }

    private fun setupMemberCheckboxes(users: List<User>) {
        binding.membersContainer.removeAllViews()
        binding.customAmountsContainer.removeAllViews()
        memberCheckboxes.clear()
        customAmountInputs.clear()

        users.forEach { user ->
            val cb = CheckBox(requireContext()).apply {
                text = user.name
                isChecked = true
                tag = user.id
            }
            memberCheckboxes.add(cb)
            binding.membersContainer.addView(cb)

            val inputLayout = TextInputLayout(requireContext()).apply {
                hint = "${user.name}'s share"
                setPadding(0, 0, 0, 8)
            }
            val input = TextInputEditText(requireContext()).apply {
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                tag = user.id
            }
            inputLayout.addView(input)
            customAmountInputs[user.id] = input
            binding.customAmountsContainer.addView(inputLayout)
        }
    }

    private fun saveExpense() {
        val description = binding.descriptionInput.text.toString()
        val amountStr = binding.amountInput.text.toString()
        val amount = amountStr.toDoubleOrNull()

        if (description.isBlank()) {
            Toast.makeText(requireContext(), "Enter a description", Toast.LENGTH_SHORT).show()
            return
        }
        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedMemberIds = memberCheckboxes.filter { it.isChecked }.map { it.tag as Long }
        if (selectedMemberIds.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one member", Toast.LENGTH_SHORT).show()
            return
        }

        val paidByIndex = binding.paidBySpinner.selectedItemPosition
        if (paidByIndex < 0 || paidByIndex >= members.size) {
            Toast.makeText(requireContext(), "Select who paid", Toast.LENGTH_SHORT).show()
            return
        }
        val paidByUserId = members[paidByIndex].id

        val customAmounts: Map<Long, Double>? = if (binding.splitCustom.isChecked) {
            val map = mutableMapOf<Long, Double>()
            for (userId in selectedMemberIds) {
                val amt = customAmountInputs[userId]?.text.toString().toDoubleOrNull() ?: 0.0
                map[userId] = amt
            }
            map
        } else null

        viewModel.addExpense(description, amount, paidByUserId, selectedMemberIds, customAmounts)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
