package com.pekyurek.emircan.voicemessaging.presentation.ui.userlist

import androidx.annotation.StringRes
import com.pekyurek.emircan.voicemessaging.R
import com.pekyurek.emircan.voicemessaging.extensions.toMd5

enum class SearchType(@StringRes val titleResId: Int, @StringRes val warningTextId: Int, val searchKey : String?) {
    ALL_USERS(R.string.title_all_users, R.string.user_not_found, "all___user___list".toMd5()),
    CHAT_USERS(R.string.title_chat_users, R.string.chat_list_is_empty, ""),
    FILTER_USERS(R.string.title_filtered_users, R.string.user_not_found, null)
}