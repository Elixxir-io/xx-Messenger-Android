package io.xxlabs.messenger.requests.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.xxlabs.messenger.data.room.model.Contact
import io.xxlabs.messenger.data.room.model.Group
import io.xxlabs.messenger.databinding.FragmentRequestListBinding
import io.xxlabs.messenger.di.utils.Injectable
import io.xxlabs.messenger.requests.model.ContactRequest
import io.xxlabs.messenger.requests.model.GroupInvitation
import io.xxlabs.messenger.requests.ui.RequestsViewModel
import io.xxlabs.messenger.requests.ui.accepted.contact.RequestAcceptedDialog
import io.xxlabs.messenger.requests.ui.accepted.group.InvitationAcceptedDialog
import io.xxlabs.messenger.requests.ui.details.contact.RequestDetailsDialog
import io.xxlabs.messenger.requests.ui.details.group.InvitationDetailsDialog
import io.xxlabs.messenger.requests.ui.list.adapter.RequestItem
import io.xxlabs.messenger.requests.ui.list.adapter.RequestsAdapter
import io.xxlabs.messenger.support.extensions.toBase64String
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class RequestListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    protected val requestsViewModel: RequestsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { viewModelFactory }
    )

    private val requestsAdapter: RequestsAdapter by lazy {
        RequestsAdapter(requestsViewModel)
    }
    private lateinit var binding: FragmentRequestListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestListBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                getRequests().collect { requests ->
                    requestsAdapter.submitList(requests)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    private fun initComponents() {
        binding.requestsListRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = requestsAdapter
        }
    }

    abstract suspend fun getRequests(): Flow<List<RequestItem>>
}


class FailedRequestsFragment : RequestListFragment() {
    override suspend fun getRequests(): Flow<List<RequestItem>> =
        requestsViewModel.getFailedRequests()
}

class SentRequestsFragment : RequestListFragment() {
    override suspend fun getRequests(): Flow<List<RequestItem>> =
        requestsViewModel.getSentRequests()
}

class ReceivedRequestsFragment : RequestListFragment() {
    override suspend fun getRequests(): Flow<List<RequestItem>> =
        requestsViewModel.getReceivedRequests()
}