package com.fairsplit.app.ui.balances

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fairsplit.app.FairSplitApp
import com.fairsplit.app.data.repository.BalanceEntry
import com.fairsplit.app.databinding.FragmentBalancesBinding
import com.fairsplit.app.databinding.ItemBalanceBinding

class BalancesFragment : Fragment() {
    private var _binding: FragmentBalancesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BalancesViewModel by viewModels {
        BalancesViewModel.Factory((requireActivity().application as FairSplitApp).groupRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalancesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = BalanceListAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.balances.observe(viewLifecycleOwner) { balances ->
            adapter.submitList(balances)
            binding.emptyText.visibility = if (balances.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class BalanceListAdapter : ListAdapter<BalanceEntry, BalanceListAdapter.VH>(DIFF) {
    inner class VH(private val b: ItemBalanceBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(e: BalanceEntry) {
            b.balanceText.text = "${e.fromUser.name} owes ${e.toUser.name} $${"%.2f".format(e.amount)}"
            b.settleButton.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<BalanceEntry>() {
            override fun areItemsTheSame(a: BalanceEntry, b: BalanceEntry) =
                a.fromUser.id == b.fromUser.id && a.toUser.id == b.toUser.id
            override fun areContentsTheSame(a: BalanceEntry, b: BalanceEntry) = a == b
        }
    }
}
