package io.xxlabs.messenger.ui.intro.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.elixxir.xxmessengerclient.Messenger
import io.reactivex.disposables.CompositeDisposable
import io.xxlabs.messenger.application.SchedulerProvider
import io.xxlabs.messenger.application.XxMessengerApplication
import io.xxlabs.messenger.bindings.wrapper.bindings.BindingsWrapperBindings
import io.xxlabs.messenger.repository.PreferencesRepository
import io.xxlabs.messenger.repository.base.BaseRepository
import io.xxlabs.messenger.support.ioThread
import io.xxlabs.messenger.support.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashScreenViewModel @Inject constructor(
    val messenger: Messenger,
    val repo: BaseRepository,
    val preferences: PreferencesRepository,
    val schedulers: SchedulerProvider
) : ViewModel() {
    //Rx
    var subscriptions = CompositeDisposable()

    val appDataCleared: LiveData<Boolean> by ::_appDataCleared
    private val _appDataCleared = MutableLiveData(false)

    val navigateNext: LiveData<Boolean> by ::_navigateNext
    private val _navigateNext = MutableLiveData(false)

    init {
//        fetchCommonErrors()
        getKronosTime()
        validateSession()
    }

    fun fetchCommonErrors() {
        subscriptions.add(
            BindingsWrapperBindings.downloadCommonErrors()
                .timeout(1, TimeUnit.SECONDS)
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .doOnError { Timber.v("[SplashScreenViewModel] Error retrieving common errors!") }
                .doOnSuccess { BindingsWrapperBindings.updateCommonErrors(it) }
                .subscribe()
        )
    }

    private fun getKronosTime() {
        val clock = XxMessengerApplication.kronosClock
        ioThread {
            val time = clock.getCurrentNtpTimeMs()
            time?.let {
                onKronosSuccess(it)
            } ?: onKronosError()
        }
    }

    private fun onKronosSuccess(time: Long) {
        Timber.v("Refreshed with success!")
        Timber.v("Kronos time retrieved: $time")
        Timber.v("Kronos time Now (ms): ${XxMessengerApplication.kronosClock.getCurrentNtpTimeMs()}")
        Timber.v("Java time: ${Utils.getCurrentTimeStamp()}")
        BindingsWrapperBindings.setTimeSource(XxMessengerApplication.getKronosTime())
    }

    private fun onKronosError() {
        Timber.v("Kronos time is null")
    }

    private fun validateSession() {
        if (preferences.name.isNotEmpty()) {
            Timber.v("User session is already created")
            _navigateNext.value = true
        } else {
            Timber.v("User session not present, creating new...")
            clearAppData()
        }
    }

    private fun clearAppData() {
        viewModelScope.launch(Dispatchers.IO) {
            messenger.destroy()
            _appDataCleared.postValue(true)
        }

    }

    override fun onCleared() {
        subscriptions.clear()
        super.onCleared()
    }
}