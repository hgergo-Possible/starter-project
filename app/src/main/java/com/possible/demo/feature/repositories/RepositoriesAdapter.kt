package com.possible.demo.feature.repositories

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.possible.demo.R
import com.possible.demo.data.models.Repo
import com.possible.demo.databinding.ItemRepositoryBinding
import com.possible.demo.feature.shared.BindingViewHolder

/**
 * [androidx.recyclerview.widget.RecyclerView.Adapter] defining how to show a [Repo] model to the user.
 */
class RepositoriesAdapter : ListAdapter<Repo, RepositoriesAdapter.RepoViewHolder>(RepoDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int = R.layout.item_repository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder =
            RepoViewHolder(parent)

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) =
            holder.bind(getItem(position))

    class RepoViewHolder(parent: ViewGroup) : BindingViewHolder<ItemRepositoryBinding>(parent, R.layout.item_repository) {

        fun bind(repoResponse: Repo) {
            binding.model = repoResponse
        }
    }

    class RepoDiffUtilCallback : DiffUtil.ItemCallback<Repo>() {

        override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem == newItem
    }
}
