package com.example.alex.datascraper;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yalantis.waves.util.Horizon;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by ddeisadze on 2/19/18.
 */

public class OscilloscopeFragment extends Fragment {
    String audio_path;
    LineChartView chart;

    List<PointValue> values = new ArrayList<PointValue>();
    private LinkedList<float[]> mHistoricalData;
    int domainCount = 0;
    private static final int HISTORY_SIZE = 6;

    Handler handler;
    int width;
    float centerY;
    int mSampleRate;
    int mChannels = 0;
    int mAudioLength;
    boolean started = false;


    private static final int RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS = 1;
    private static final int RECORDER_ENCODING_BIT = 16;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_8BIT;
    private static final int MAX_DECIBELS = 80;

    private boolean isRecording = false;
    Handler mHandler = null;
    Runnable mTicker = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oscilloscope,
                container, false);
        audio_path = this.getArguments().getString("filepath");

        final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        final File audio_file = new File(audio_path);
        final GLSurfaceView glSurfaceView = (GLSurfaceView) view.findViewById(R.id.gl_surface);

        mHandler = new Handler();
        mSampleRate = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));
        final Horizon mHorizon = new Horizon(glSurfaceView, getResources().getColor(R.color.colorPrimary),
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);
        mHorizon.setMaxVolumeDb(MAX_DECIBELS);

        final MediaPlayer mp;
        mp = new MediaPlayer();

        MediaPlayer mediaPlayer;

         mTicker = new Runnable() {
            public void run() {

                FileInputStream reader = null;
                try {
                    reader = new FileInputStream(audio_file);

                } catch (FileNotFoundException e) {
                    System.out.println("Error " + e);
                }

                //fill up your buffer with data from the stream
                try {
                    int size = (int) audio_file.length();
                    byte[] bytes = new byte[size / (RECORDER_ENCODING_BIT/8)];
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(audio_file));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();

                    if( isRecording){
                        System.out.println("recording");
                        mHorizon.updateView(bytes);
                    }

                } catch (IOException e){
                    System.out.println("Io Exception " + e);
                }

                mHandler.postDelayed(this, 500);


            }
        };

        mTicker.run();

        return view;
    }

    public void setRecording(boolean recording){

        this.isRecording = recording;

        System.out.println(isRecording);
    }

    private void addDataPointToChart(double amplitude){
        values.add(new PointValue(domainCount, (float) amplitude));
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        chart.setLineChartData(data);

        domainCount += 1;
    }

    public void stop() {
        started = false;
        setRecording(false);
        mHandler.removeCallbacks(mTicker);
    }

    public void start() {
        started = true;
        setRecording(true);
        handler.postDelayed(mTicker, 1000);

    }


}