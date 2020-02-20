package com.example.testaudiostream

import android.media.*
import android.util.Log

object AudioUtils {

    fun findAudioTrack(): AudioTrack {
        lateinit var track: AudioTrack

        val myBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_FREQUENCY,
            TRACK_CHANNELS,
            AUDIO_ENCODING
        )
        if (myBufferSize != AudioTrack.ERROR_BAD_VALUE) {
            track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_FREQUENCY,
                TRACK_CHANNELS,
                AUDIO_ENCODING,
                myBufferSize,
                AudioTrack.MODE_STREAM
            )
            track.playbackRate = SAMPLE_FREQUENCY
        }
        return track
    }

    fun findAudioRecord() = AudioRecord(
        MediaRecorder.AudioSource.DEFAULT,
        SAMPLE_FREQUENCY,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        getMinBufferSize()
    )

    fun getMinBufferSize(): Int {
        return AudioRecord.getMinBufferSize(
            SAMPLE_FREQUENCY,
            RECORDER_CHANNELS,
            AUDIO_ENCODING
        )
    }

    const val SAMPLE_FREQUENCY = 44100
    const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    const val TRACK_CHANNELS = AudioFormat.CHANNEL_OUT_MONO
    const val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

    const val TAG = "AudioUtils"
}

fun AudioTrack.isInitialized(): Boolean {
    return if (state == AudioTrack.STATE_UNINITIALIZED) {
        Log.e(AudioUtils.TAG, "AudioTrack Uninitialized")
        false
    } else {
        Log.i(AudioUtils.TAG, "AudioTrack Initialized")
        true
    }
}

fun AudioRecord.isInitialized(): Boolean {
    return if (state == AudioRecord.STATE_UNINITIALIZED) {
        Log.e(AudioUtils.TAG, "AudioRecord Uninitialized")
        false
    } else {
        Log.i(AudioUtils.TAG, "AudioRecord Initialized")
        true
    }
}