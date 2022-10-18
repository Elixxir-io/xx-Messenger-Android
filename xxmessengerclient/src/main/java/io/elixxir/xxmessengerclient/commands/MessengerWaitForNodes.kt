package io.elixxir.xxmessengerclient.commands

import io.elixxir.xxmessengerclient.MessengerEnvironment
import io.elixxir.xxmessengerclient.utils.MessengerException

class MessengerWaitForNodes(private val env: MessengerEnvironment) {

    operator fun invoke(
        retries: Int = 10,
        retryIntervalMs: Long,
        targetRatio: Float = 0.75F,
    ) {
        val cMix = env.cMix ?: throw MessengerException.NotLoaded("CMix")
        var report = cMix.getNodeRegistrationStatus()
        var _retries = retries

        while (report.ratio < targetRatio && _retries > 0) {
            env.sleep(retryIntervalMs)
            report = cMix.getNodeRegistrationStatus()
            _retries--
        }

        if (report.ratio < targetRatio) throw MessengerException.TimedOut()
    }
}