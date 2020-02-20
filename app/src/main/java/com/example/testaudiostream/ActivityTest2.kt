package com.example.testaudiostream

import android.content.Context
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.example.testaudiostream.AudioUtils.findAudioRecord
import com.example.testaudiostream.AudioUtils.getMinBufferSize
import kotlinx.android.synthetic.main.activity_main.*

class ActivityTest2 : AppCompatActivity() {

    private lateinit var recorder: AudioRecord
    private lateinit var track: AudioTrack

    private var thread: Thread? = null

    private val bufferRec by lazy { ShortArray(minBufferSizeRec / 2) }

    private val minBufferSizeRec by lazy { getMinBufferSize() }

    private val manager: AudioManager by lazy {
        this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private var isAudioRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initListeners()
        initAudio()
    }

    private fun initView() {
        btnStartRecording.isEnabled = true
        btnStopRecording.isEnabled = false
    }

    private fun initListeners() {
        btnStartRecording.setOnClickListener {
            setAudioState(true)

            btnStartRecording.isEnabled = false
            btnStopRecording.isEnabled = true
        }

        btnStopRecording.setOnClickListener {
            setAudioState(false)

            btnStartRecording.isEnabled = true
            btnStopRecording.isEnabled = false
        }
    }

    private fun setAudioState(state: Boolean) {
        isAudioRunning = state

        thread = when (state) {
            false -> Thread { stopAudioWork() }
            true -> Thread { startAudioWork() }
        }

        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        thread?.start()
    }

    private fun initAudio() {
        manager.mode = AudioManager.MODE_NORMAL
        volumeControlStream = AudioManager.STREAM_VOICE_CALL
    }

    private fun startAudioWork() {
        recorder = findAudioRecord()
        if (!recorder.isInitialized()) return

        track = AudioUtils.findAudioTrack()
        if (!track.isInitialized()) return

        var data = ShortArray(minBufferSizeRec / 2)
        recorder.startRecording()
        track.play()

        while (isAudioRunning) {
            recorder.read(bufferRec, 0, minBufferSizeRec / 2)
            for (i in data.indices) {
                data[i] = bufferRec[i]
            }
            track.write(data, 0, data.size)
            data = ShortArray(minBufferSizeRec / 2)
        }

        thread?.interrupt()
    }

    private fun stopAudioWork() {
        stopRecorder()
        stopPlayer()

        thread?.interrupt()
    }

    private fun stopRecorder() {
        if (recorder.isInitialized()) {
            recorder.stop()
            recorder.release()
        }
    }

    private fun stopPlayer() {
        if (track.isInitialized()) {
            if (track.playState != AudioTrack.PLAYSTATE_STOPPED) {
                try {
                    track.stop()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            track.release()
            manager.mode = AudioManager.MODE_NORMAL
        }
    }
}