package com.cheroliv.jobdone.fragments.list

import android.R.id.home
import android.app.AlertDialog.Builder
import android.os.Bundle
import android.util.Log.d
import android.view.*
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.cheroliv.jobdone.R.id.*
import com.cheroliv.jobdone.R.menu.list_fragment_menu
import com.cheroliv.jobdone.data.models.ToDoData
import com.cheroliv.jobdone.data.viewmodel.ToDoViewModel
import com.cheroliv.jobdone.databinding.FragmentListBinding
import com.cheroliv.jobdone.databinding.FragmentListBinding.inflate
import com.cheroliv.jobdone.fragments.SharedViewModel
import com.cheroliv.jobdone.fragments.list.adapter.ListAdapter
import com.cheroliv.jobdone.utils.hideKeyboard
import com.cheroliv.jobdone.utils.observeOnce
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar.make

class ListFragment : Fragment(), OnQueryTextListener {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Data binding
        _binding = inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        // Setup RecyclerView
        setupRecyclerview()

        // Observe LiveData
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
            binding.recyclerView.scheduleLayoutAnimation()
        }

        // Hide soft keyboard
        hideKeyboard(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
                menuInflater.inflate(list_fragment_menu, menu.apply {
                    (menu.findItem(menu_search).actionView as? SearchView)?.apply {
                        isSubmitButtonEnabled = true
                        setOnQueryTextListener(this@ListFragment)
                    }
                })

            override fun onMenuItemSelected(menuItem: MenuItem) = true.apply {
                when (menuItem.itemId) {

                    menu_delete_all -> confirmRemoval()

                    menu_priority_high -> mToDoViewModel
                        .sortByHighPriority
                        .observe(viewLifecycleOwner) { adapter.setData(it) }

                    menu_priority_low -> mToDoViewModel
                        .sortByLowPriority
                        .observe(viewLifecycleOwner) { adapter.setData(it) }

                    home -> requireActivity().onBackPressed()

                }
            }

        }, viewLifecycleOwner, RESUMED)

    }

    private fun setupRecyclerview() = binding.recyclerView.run {
        adapter = this@ListFragment.adapter
        layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
        // Swipe to Delete
        swipeToDelete(this)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper(object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.dataList[viewHolder.adapterPosition].run {
                    // Delete Item
                    mToDoViewModel.deleteItem(this)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    // Restore Deleted Item
                    restoreDeletedData(viewHolder.itemView, this)
                }
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) = make(
        view,
        "Deleted '${deletedItem.title}'",
        LENGTH_LONG
    ).setAction("Undo") { mToDoViewModel.insertData(deletedItem) }.show()


    override fun onQueryTextSubmit(query: String?) = true.apply {
        if (query != null) searchThroughDatabase(query)
    }

    override fun onQueryTextChange(query: String?) = true.apply {
        if (query != null) searchThroughDatabase(query)
    }


    private fun searchThroughDatabase(query: String) = mToDoViewModel
        .searchDatabase("%$query%")
        .observeOnce(viewLifecycleOwner) { list ->
            d("ListFragment", "searchThroughDatabase")
            adapter.setData(list)
        }

    // Show AlertDialog to Confirm Removal of All Items from Database Table
    private fun confirmRemoval() {
        Builder(requireContext()).run {
            setPositiveButton("Yes") { _, _ ->
                mToDoViewModel.deleteAll()
                makeText(
                    requireContext(),
                    "Successfully Removed Everything!",
                    LENGTH_SHORT
                ).show()
            }
            setNegativeButton("No") { _, _ -> }
            setTitle("Delete everything?")
            setMessage("Are you sure you want to remove everything?")
            create().show()
        }
    }

    override fun onDestroyView() = super.onDestroyView().run { _binding = null }

}