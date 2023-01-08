package com.cheroliv.jobdone.fragments

import android.view.View
import android.view.View.*
import android.widget.Spinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.cheroliv.jobdone.R.*
import com.cheroliv.jobdone.R.color.*
import com.cheroliv.jobdone.R.id.action_listFragment_to_addFragment
import com.cheroliv.jobdone.data.models.Priority
import com.cheroliv.jobdone.data.models.Priority.*
import com.cheroliv.jobdone.data.models.ToDoData
import com.cheroliv.jobdone.fragments.list.ListFragmentDirections.Companion.actionListFragmentToUpdateFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

private fun Priority.toInt(): Int = when (this) {
    HIGH -> 0
    MEDIUM -> 1
    LOW -> 2
}

private fun View.emptyDatabase(value: Boolean) = when (value) {
    true -> visibility = VISIBLE
    false -> visibility = INVISIBLE
}

class BindingAdapters {

    companion object {

        @BindingAdapter("android:navigateToAddFragment")
        @JvmStatic
        fun navigateToAddFragment(view: FloatingActionButton, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) view.findNavController()
                    .navigate(action_listFragment_to_addFragment)
            }
        }

        @BindingAdapter("android:emptyDatabase")
        @JvmStatic
        fun emptyDatabase(view: View, emptyDatabase: MutableLiveData<Boolean>) =
            view.emptyDatabase(emptyDatabase.value ?: false)

        @BindingAdapter("android:parsePriorityToInt")
        @JvmStatic
        fun parsePriorityToInt(view: Spinner, priority: Priority) =
            view.setSelection(priority.toInt())

        @BindingAdapter("android:parsePriorityColor")
        @JvmStatic
        fun parsePriorityColor(cardView: CardView, priority: Priority) =
            cardView.setCardBackgroundColor(
                cardView.context.getColor(
                    when (priority) {
                        HIGH -> red
                        MEDIUM -> yellow
                        LOW -> green
                    }
                )
            )

        @BindingAdapter("android:sendDataToUpdateFragment")
        @JvmStatic
        fun sendDataToUpdateFragment(view: ConstraintLayout, currentItem: ToDoData) {
            view.setOnClickListener {
                view.findNavController().navigate(actionListFragmentToUpdateFragment(currentItem))
            }
        }

    }

}


