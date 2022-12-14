package io.xxlabs.messenger.search

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import io.xxlabs.messenger.databinding.FragmentFactSearchBinding
import io.xxlabs.messenger.di.utils.Injectable
import io.xxlabs.messenger.requests.ui.RequestsViewModel
import io.xxlabs.messenger.requests.ui.list.adapter.RequestItem
import io.xxlabs.messenger.requests.ui.list.adapter.RequestsAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Superclass for Fragments that look up users by Fact (username, phone, etc.)
 */
abstract class FactSearchFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    protected val requestsViewModel: RequestsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { viewModelFactory }
    )
    protected val searchViewModel: UserSearchViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { viewModelFactory }
    )

    protected val resultsAdapter: RequestsAdapter by lazy {
        RequestsAdapter(requestsViewModel)
    }
    protected lateinit var binding: FragmentFactSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFactSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        binding.searchResultsRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = resultsAdapter
        }
        binding.searchTextInputEditText.apply {
            initKeyboardSearchButton()
            initFocusListener()
        }
        binding.ui = getSearchTabUi()
    }

    private fun TextInputEditText.initKeyboardSearchButton() {
        setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                try {
                    onSearchClicked(v.text.toString())
                    return@setOnEditorActionListener true
                } catch (e: Exception) {
                    return@setOnEditorActionListener false
                }

            } else {
                return@setOnEditorActionListener false
            }
        }
    }

    private fun TextInputEditText.initFocusListener() {
        setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) searchViewModel.onUserInput("")
        }
    }

    abstract suspend fun getResults(): Flow<List<RequestItem>>

    abstract fun onSearchClicked(query: String?)

    abstract fun getSearchTabUi(): FactSearchUi
}

class UsernameSearchFragment : FactSearchFragment() {
    override suspend fun getResults(): Flow<List<RequestItem>> =
        searchViewModel.usernameResults

    override fun onSearchClicked(query: String?) {
        lifecycleScope.launch {
            searchViewModel.onUsernameSearch(query).collect { results ->
                resultsAdapter.submitList(results)
            }
        }
    }

    override fun getSearchTabUi(): FactSearchUi = searchViewModel.usernameSearchUi

    override fun onResume() {
        super.onResume()
        searchViewModel.invitationFrom.observe(viewLifecycleOwner) { username ->
            username?.let {
                binding.searchTextInputEditText.setText(username)
                onSearchClicked(username)
                searchViewModel.onInvitationHandled()
            }
        }
    }
}

class EmailSearchFragment : FactSearchFragment() {
    override suspend fun getResults(): Flow<List<RequestItem>> =
        searchViewModel.emailResults

    override fun onSearchClicked(query: String?) {
        lifecycleScope.launch {
            searchViewModel.onEmailSearch(query).collect { results ->
                resultsAdapter.submitList(results)
            }
        }
    }

    override fun getSearchTabUi(): FactSearchUi = searchViewModel.emailSearchUi
}

class PhoneSearchFragment : FactSearchFragment() {
    override suspend fun getResults(): Flow<List<RequestItem>> =
        searchViewModel.phoneResults

    override fun onSearchClicked(query: String?) {
        lifecycleScope.launch {
            searchViewModel.onPhoneSearch(query).collect { results ->
                resultsAdapter.submitList(results)
            }
        }
    }

    override fun getSearchTabUi(): FactSearchUi = searchViewModel.phoneSearchUi
}

class QrSearchFragment : FactSearchFragment() {
    override suspend fun getResults(): Flow<List<RequestItem>> = flowOf()
    override fun onSearchClicked(query: String?) {}
    override fun getSearchTabUi(): FactSearchUi = searchViewModel.qrSearchUi
}