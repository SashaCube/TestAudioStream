package com.example.testaudiostream

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import java.io.IOException

class AudioRecordTest : AppCompatActivity() {

    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null
    private var playButton: PlayButton? = null
    private var player: MediaPlayer? = null
    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private val permissions =
        arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted =
                grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) {
        if (start) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun onPlay(start: Boolean) {
        if (start) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    private fun startPlaying() {
        player = MediaPlayer()
        try {
            player!!.setDataSource(fileName)
            player!!.prepareAsync()
            player!!.start()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    private fun stopPlaying() {
        player!!.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setOutputFile(fileName)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
        recorder!!.start()
    }

    private fun stopRecording() {
        recorder!!.stop()
        recorder!!.release()
        recorder = null
    }

    internal inner class RecordButton(ctx: Context?) :
        AppCompatButton(ctx) {
        var mStartRecording = true
        var clicker =
            OnClickListener {
                onRecord(mStartRecording)
                text = if (mStartRecording) {
                    "Stop recording"
                } else {
                    "Start recording"
                }
                mStartRecording = !mStartRecording
            }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context?) :
        AppCompatButton(ctx) {
        var mStartPlaying = true
        var clicker =
            OnClickListener {
                onPlay(mStartPlaying)
                text = if (mStartPlaying) {
                    "Stop playing"
                } else {
                    "Start playing"
                }
                mStartPlaying = !mStartPlaying
            }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        // Record to the external cache directory for visibility
        fileName = externalCacheDir!!.absolutePath
        fileName += "/audiorecordtest.3gp"
        ActivityCompat.requestPermissions(
            this,
            permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )
        val ll = LinearLayout(this)
        recordButton = RecordButton(this)
        ll.addView(
            recordButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0f
            )
        )
        playButton = PlayButton(this)
        ll.addView(
            playButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0f
            )
        )
        setContentView(ll)
    }

    public override fun onStop() {
        super.onStop()
        if (recorder != null) {
            recorder!!.release()
            recorder = null
        }
        if (player != null) {
            player!!.release()
            player = null
        }
    }

    companion object {
        private const val LOG_TAG = "AudioRecordTest"
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private var fileName: String? = null
    }
}