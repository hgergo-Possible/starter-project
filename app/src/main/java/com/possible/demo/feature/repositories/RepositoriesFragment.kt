package com.possible.demo.feature.repositories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.possible.demo.R
import com.possible.demo.databinding.FragmentUserRepositoriesBinding
import com.possible.demo.di.ViewModelFactory
import com.possible.demo.feature.shared.navArgs
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Detail screen of a [com.possible.demo.data.models.User] by showing their [repositories][com.possible.demo.data.models.Repo]
 *
 * It's controlled by [RepositoriesViewModel].
 */
class RepositoriesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val args by navArgs<RepositoriesFragmentArgs>()
    private lateinit var viewModel: RepositoriesViewModel
    private lateinit var binding: FragmentUserRepositoriesBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RepositoriesViewModel::class.java)
        viewModel.setUserName(args.username)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_repositories, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = RepositoriesAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
        viewModel.repositories.observe(viewLifecycleOwner, Observer {
            it?.let(adapter::submitList)
        })
    }
}