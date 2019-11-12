package com.possible.demo.feature.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.possible.demo.databinding.FragmentSearchUsersBinding
import com.possible.demo.di.ViewModelFactory
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import android.view.KeyEvent
import com.possible.demo.R

/**
 * Main screen of the application, gives the user the ability to look up a [com.possible.demo.data.models.User]
 * and watch their [repositories][com.possible.demo.data.models.Repo] by taping on them.
 *
 * It's controlled by [SearchUserViewModel].
 */
class SearchUsersFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: SearchUserViewModel
    private lateinit var binding: FragmentSearchUsersBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchUserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_users, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.searchInputEditText.setOnEditorActionListener { textView, _, event ->
            when(event?.keyCode){
                KeyEvent.KEYCODE_SEARCH,
                KeyEvent.KEYCODE_ENTER -> viewModel.onSearchTextChanged(textView.text.toString())
            }
            false
        }

        val adapter = UserAdapter(object: UserAdapter.UserEvenListener{
            override fun onClicked(username: String) {
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.searchUsersFragment) {
                    binding.searchInputEditText.clearFocus()
                    findNavController().navigate(SearchUsersFragmentDirections.actionSearchUsersFragmentToRepositoriesFragment(username))
                }
            }
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(binding.recyclerView.context, 2)
        viewModel.users.observe(viewLifecycleOwner, Observer {
            it?.let(adapter::submitList)
        })
    }
}