package com.possible.demo.feature.search

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.possible.demo.R
import com.possible.demo.data.models.Repo
import com.possible.demo.data.models.User
import com.possible.demo.databinding.ItemUserBinding
import com.possible.demo.feature.shared.BindingViewHolder

/**
 * [androidx.recyclerview.widget.RecyclerView.Adapter] defining how to show a [Repo] model to the user
 * and via [userEvenListener] to listen to user interactions.
 */
class UserAdapter(private val userEvenListener: UserEvenListener) : ListAdapter<User, UserAdapter.UserHolder>(UserDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int = R.layout.item_user

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder =
            UserHolder(parent, userEvenListener)

    override fun onBindViewHolder(holder: UserHolder, position: Int) =
            holder.bind(getItem(position))

    interface UserEvenListener {
        fun onClicked(username: String)
    }

    class UserHolder(parent: ViewGroup, private val eventListener: UserEvenListener) : BindingViewHolder<ItemUserBinding>(parent, R.layout.item_user) {

        init {
            binding.root.setOnClickListener {
                binding.model?.name?.let(eventListener::onClicked)
            }
        }

        fun bind(user: User) {
            binding.model = user
        }
    }

    class UserDiffUtilCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
    }
}
