package io.xxlabs.messenger.requests.ui.list.adapter

import android.graphics.Bitmap
import androidx.annotation.IdRes
import io.xxlabs.messenger.R
import io.xxlabs.messenger.data.datatype.RequestStatus.*
import io.xxlabs.messenger.data.room.model.ContactData
import io.xxlabs.messenger.data.room.model.formattedEmail
import io.xxlabs.messenger.data.room.model.formattedPhone
import io.xxlabs.messenger.requests.model.ContactRequest
import io.xxlabs.messenger.requests.model.GroupInvitation
import io.xxlabs.messenger.requests.model.NullRequest
import io.xxlabs.messenger.requests.model.Request
import io.xxlabs.messenger.support.appContext

sealed class RequestItem(val request: Request) : ItemThumbnail {
    open val id: ByteArray = request.requestId
    open val title: String = request.name
    open val timestamp: Long = request.createdAt

    abstract val subtitle: String?
    abstract val details: String?

    open val actionLabel: String? =
        when (request.requestStatus) {
            VERIFYING -> appContext().getString(R.string.request_item_action_verifying)
            VERIFICATION_FAIL -> appContext().getString(R.string.request_item_action_failed_verification)
            RESET_FAIL, RESET_SENT, SEND_FAIL, SENT -> appContext().getString(R.string.request_item_action_retry)
            RESENT -> appContext().getString(R.string.request_item_action_resent)
            else -> null
        }

    open val actionIcon: Int? =
        when (request.requestStatus) {
            VERIFICATION_FAIL -> R.drawable.ic_info_outline_24dp
            RESET_FAIL, RESET_SENT, SEND_FAIL, SENT -> R.drawable.ic_retry
            RESENT -> R.drawable.ic_check_green
            else -> null
        }

    open val actionIconColor: Int? =
        when (request.requestStatus) {
            VERIFICATION_FAIL -> R.color.accent_danger
            RESET_FAIL, RESET_SENT, SEND_FAIL, SENT-> R.color.brand_default
            RESENT -> R.color.accent_success
            else -> null
        }

    @IdRes
    open val actionTextStyle: Int? =
        when (request.requestStatus) {
            VERIFYING -> R.style.request_item_verifying
            VERIFICATION_FAIL -> R.style.request_item_error
            RESET_FAIL, RESET_SENT, SEND_FAIL, SENT -> R.style.request_item_retry
            RESENT -> R.style.request_item_resent
            else -> null
        }
}

data class ContactRequestItem(
    val contactRequest: ContactRequest,
    val photo: Bitmap? = null,
) : RequestItem(contactRequest) {
    override val subtitle: String? = null
    override val details: String? = contactRequest.getContactInfo()
    override val itemPhoto: Bitmap? = photo
    override val itemInitials: String = contactRequest.model.initials
    override val itemIconRes: Int? = null
}

private fun ContactRequest.getContactInfo(): String? =
    with ("${model.formattedEmail() ?: ""}\n${model.formattedPhone() ?: ""}") {
        when {
            isNullOrBlank() -> null
            else -> trim()
        }
    }

data class ContactRequestSearchResultItem(
    val contactRequest: ContactRequest,
    val photo: Bitmap? = null,
    val statusText: String = "Request pending",
    val statusTextColor: Int = R.color.neutral_weak,
    val actionVisible: Boolean = true,
    override val actionIcon: Int = R.drawable.ic_retry,
    override val actionIconColor: Int = R.color.brand_default,
    override val actionTextStyle: Int = R.style.request_item_retry,
    override val actionLabel: String = if (actionVisible) appContext().getString(R.string.request_item_action_retry) else ""
) : RequestItem(contactRequest) {
    override val subtitle: String = statusText
    override val details: String? = null
    override val itemPhoto: Bitmap? = photo
    override val itemInitials: String = contactRequest.model.initials
    override val itemIconRes: Int? = null
}

data class GroupInviteItem(
    val invite: GroupInvitation,
//    val membersList: List<MemberItem>,
    private val groupCreator: String,
) : RequestItem(invite) {
    override val subtitle: String = groupCreator // TODO: membersList.first()
    override val details: String? = null
    override val itemPhoto: Bitmap? = null
    override val itemInitials: String? = null
    override val itemIconRes: Int = R.drawable.ic_group_chat
}

data class EmptyPlaceholderItem(
    val placeholder: NullRequest = NullRequest(),
    val text: String = ""
) : RequestItem(placeholder) {
    override val title = text
    override val itemPhoto: Bitmap? = null
    override val itemIconRes: Int? = null
    override val itemInitials: String? = null
    override val subtitle: String? = null
    override val details: String? = null
}

data class HiddenRequestToggleItem(
    val placeholder: NullRequest = NullRequest(),
) : RequestItem(placeholder) {
    override val itemPhoto: Bitmap? = null
    override val itemIconRes: Int? = null
    override val itemInitials: String? = null
    override val subtitle: String? = null
    override val details: String? = null
}

data class AcceptedConnectionItem(
    val contactRequest: ContactRequest,
    val photo: Bitmap? = null
) : RequestItem(contactRequest) {
    override val subtitle: String? = null
    override val details: String? = contactRequest.getContactInfo()
    override val itemPhoto: Bitmap? = photo
    override val itemInitials: String = contactRequest.model.initials
    override val itemIconRes: Int? = null
}

data class SearchResultItem(
    val contactRequest: ContactRequest,
    val photo: Bitmap? = null
) : RequestItem(contactRequest) {
    override val subtitle: String? = null
    override val details: String? = contactRequest.getContactInfo()
    override val itemPhoto: Bitmap? = photo
    override val itemInitials: String = contactRequest.model.initials
    override val itemIconRes: Int? = null
}

data class ConnectionsDividerItem(
    val placeholder: NullRequest = NullRequest(),
    val text: String = "Local results"
) : RequestItem(placeholder) {
    override val title = text
    override val itemPhoto: Bitmap? = null
    override val itemIconRes: Int? = null
    override val itemInitials: String? = null
    override val subtitle: String? = null
    override val details: String? = null
}