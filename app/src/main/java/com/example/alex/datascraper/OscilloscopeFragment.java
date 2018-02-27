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
//        width = view.getMeasuredWidth();
////        centerY = view.getMeasuredHeight() / 2f;
//        centerY = 180;
//        width = 200;
        audio_path = this.getArguments().getString("filepath");
//        System.out.println(audio_path);
//        loadGraphView(view);
        final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        final File audio_file = new File(audio_path);
        final GLSurfaceView glSurfaceView = (GLSurfaceView) view.findViewById(R.id.gl_surface);

        mHandler = new Handler();

//        System.out.println(audio_file.isFile() + " is file");
//        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

//        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
//        handler = new Handler();

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do Something here
//                System.out.println("text");
//            }
//        }, 100)
        mSampleRate = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));

        final Horizon mHorizon = new Horizon(glSurfaceView, getResources().getColor(R.color.colorPrimary),
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_ENCODING_BIT);
        mHorizon.setMaxVolumeDb(MAX_DECIBELS);

//        final MediaPlayer mediaPlayer;
//
//        mediaPlayer = new MediaPlayer();
        final MediaPlayer mp;
        mp = new MediaPlayer();

        MediaPlayer mediaPlayer;

        setRecording(true);

         mTicker = new Runnable() {
            public void run() {



//                System.out.println(mediaPlayer.getCurrentPosition());
//                System.out.println(mediaPlayer.getTrackInfo());
//
//                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//                mmr.setDataSource(audio_file.getAbsolutePath());
//                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                int millSecond = Integer.parseInt(durationStr);
//                System.out.println(millSecond + " duration");


                FileInputStream reader = null;
                try {
                    reader = new FileInputStream(audio_file);

                } catch (FileNotFoundException e) {
                    System.out.println("Error " + e);
                }

                //fill up your buffer with data from the stream
                try {
                    int size = (int) audio_file.length();
                    byte[] bytes = new byte[size/ (RECORDER_ENCODING_BIT/8)];
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(audio_file));
                    buf.read(bytes, 0, bytes.length);
                    buf.close();

//                    bufferSize / bytePerSample


                    if( isRecording){
                        mHorizon.updateView(bytes);
                    }

                    String recording = Base64.encodeToString(bytes, 0);


                    // convert byte array to base64 string so it can be sent to the server
//                    String recording = Base64.encodeToString(bytes, 0);
//                    System.out.println(recording);

//                    double amplitude = 0;
//                    for (int i = 0; i < bytes.length/2; i++) {
//                        double y = (bytes[i*2] | bytes[i*2+1] << 8) / 32768.0;
//                        // depending on your endianness:
//                        // double y = (audioData[i*2]<<8 | audioData[i*2+1]) / 32768.0
//                        amplitude += Math.abs(y);
//                    }
//                    amplitude = amplitude / bytes.length / 2;
//
//                    System.out.println(amplitude);

//
//                    addDataPointToChart(amplitude);



//                    if (mHistoricalData == null)
//                        mHistoricalData = new LinkedList<>();
//                    LinkedList<float[]> temp = new LinkedList<>(mHistoricalData);
//
//                    // For efficiency, we are reusing the array of points.
//                    float[] waveformPoints;
//                    if (temp.size() == HISTORY_SIZE) {
//                        waveformPoints = temp.removeFirst();
//                    } else {
//                        waveformPoints = new float[width * 4];
//                    }
//
//                    drawRecordingWaveform(bytes, waveformPoints);
//                    temp.addLast(waveformPoints);
//                    mHistoricalData = temp;

//                    if (AudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
//                            && audioRecord.read(buffer, 0, buffer.length) != -1) {
//                    }




                } catch (IOException e){
                    System.out.println("Io Exception " + e);
                }

//                System.out.println("update");
//
//                BufferedReader br = new BufferedReader(new FileReader(audio));
//                File mFile = new File(filePath);
//                long fLength = mFile.length();
//                char[fLength - 44] buffer;
//                br.Read(buffer, 44, fLength - 44);
//                br.close();

                // Output the minimum and maximum value

                mHandler.postDelayed(this, 100);

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
        mHandler.removeCallbacks(mTicker);
    }

    public void start() {
        started = true;
        handler.postDelayed(mTicker, 10);
    }

    void drawRecordingWaveform(byte[] buffer, float[] waveformPoints) {
        float lastX = -1;
        float lastY = -1;
        int pointIndex = 0;
        float max = Short.MAX_VALUE;

        // For efficiency, we don't draw all of the samples in the buffer, but only the ones
        // that align with pixel boundaries.
        for (int x = 0; x < width; x++) {
            int index = (int) (((x * 1.0f) / width) * buffer.length);
            short sample = buffer[index];
            float y = centerY - ((sample / max) * centerY);


            if (lastX != -1) {
                waveformPoints[pointIndex++] = lastX;
                waveformPoints[pointIndex++] = lastY;
                waveformPoints[pointIndex++] = x;
                waveformPoints[pointIndex++] = y;
            }

            lastX = x;
            lastY = y;
        }

    }

    private void calculateAudioLength(byte[] mSamples) {
        if (mSamples == null || mSampleRate == 0 || mChannels == 0)
            return;

        mAudioLength = calculateAudioLength(mSamples.length, mSampleRate, mChannels);
    }

    public static int calculateAudioLength(int samplesCount, int sampleRate, int channelCount) {
        return ((samplesCount / channelCount) * 1000) / sampleRate;
    }

    private void loadGraphView(View view) {
        // init example series data
        chart = new LineChartView(this.getActivity());
        chart.setInteractive(false);

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        chart.setLineChartData(data);
//
//        try {
//            LinearLayout layout = (LinearLayout) view.findViewById(R.id.oscilloscope_graph);
//            layout.addView(chart);
//        } catch (NullPointerException e) {
//            // something to handle the NPE.
//        }
    }
}