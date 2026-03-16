package com.fairsplit.app.ui.groupdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairsplit.app.FairSplitApp
import com.fairsplit.app.data.db.entity.User
import com.fairsplit.app.data.repository.BalanceEntry
import com.fairsplit.app.databinding.FragmentGroupDetailBinding

class GroupDetailFragment : Fragment() {
    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!
    private val args: GroupDetailFragmentArgs by navArgs()

    private val viewModel: GroupDetailViewModel by viewModels {
        val app = requireActivity().application as FairSplitApp
        GroupDetailViewModel.Factory(
            args.groupId,
            app.groupRepository,
            app.expenseRepository,
            app.settlementRepository
        )
    }

    private lateinit var expensesAdapter: ExpensesAdapter
    private lateinit var balancesAdapter: BalancesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.title = args.groupName
        binding.toolbar.setNavigationIcon(android.R.drawable.ic_media_previous)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        expensesAdapter = ExpensesAdapter(onDelete = { expense ->
            AlertDialog.Builder(requireContext())
                .setMessage("Delete \"${expense.description}\"?")
                .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteExpense(expense) }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        })
        binding.expensesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.expensesRecycler.adapter = expensesAdapter

        balancesAdapter = BalancesAdapter(onSettle = { entry -> confirmSettle(entry) })
        binding.balancesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.balancesRecycler.adapter = balancesAdapter

        viewModel.members.observe(viewLifecycleOwner) { members ->
            expensesAdapter.setUsers(members)
        }

        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            expensesAdapter.submitList(expenses)
            binding.emptyText.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
            viewModel.refreshBalances()
        }

        viewModel.balances.observe(viewLifecycleOwner) { balances ->
            balancesAdapter.submitList(balances)
            binding.allSettledText.visibility = if (balances.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fab.setOnClickListener {
            val action = GroupDetailFragmentDirections.actionGroupDetailToAddExpense(args.groupId)
            findNavController().navigate(action)
        }

        viewModel.refreshBalances()
    }

    private fun confirmSettle(entry: BalanceEntry) {
        AlertDialog.Builder(requireContext())
            .setMessage("Mark ${entry.fromUser.name} as settled with ${entry.toUser.name} for $${"%.2f".format(entry.amount)}?")
            .setPositiveButton("Settle") { _, _ -> viewModel.settle(entry.fromUser, entry.toUser, entry.amount) }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
