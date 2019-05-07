package com.easing.commons.android.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.easing.commons.android.manager.ServiceUtil;

import lombok.SneakyThrows;

public class MediaUtil {

    public static final String ASSET_COMING_MESSAGE = "mp3/coming_msg.mp3";

    //播放
    @SneakyThrows
    public static void play(MediaPlayer player, String path) {
        player.setDataSource(path);
        player.prepare();
        player.start();
    }

    //停止
    @SneakyThrows
    public static void stop(MediaPlayer player) {
        if (player.isPlaying())
            player.stop();
    }

    //获取音视频时长
    @SneakyThrows
    public static int getMediaDuration(String path) {
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(path);
        player.prepare();
        Integer duration = player.getDuration();
        player.release();
        return duration;
    }

    //播放本地文件
    @SneakyThrows
    public static void playFile(String path) {
        MediaPlayer player = new MediaPlayer();
        player.setDataSource(path);
        player.prepare();
        player.start();
    }

    //播放Asset资源
    @SneakyThrows
    public static void playAsset(Context context, String name) {
        MediaPlayer player = new MediaPlayer();
        AssetManager manager = context.getAssets();
        AssetFileDescriptor descriptor = manager.openFd(name);
        player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getStartOffset());
        player.prepare();
        player.start();
    }

    //开启外放
    public static void useSpeaker(Context context) {
        AudioManager audioManager = ServiceUtil.getAudioManager(context);
        audioManager.setSpeakerphoneOn(true);
    }

    //开启听筒
    public static void useHeadphone(Context context) {
        AudioManager audioManager = ServiceUtil.getAudioManager(context);
        audioManager.setSpeakerphoneOn(false);
    }

    //开启麦克风
    public static void openMicrophone(Context context) {
        AudioManager audioManager = ServiceUtil.getAudioManager(context);
        audioManager.setMicrophoneMute(false);
    }

    //禁用麦克风
    public static void closeMicrophone(Context context) {
        AudioManager audioManager = ServiceUtil.getAudioManager(context);
        audioManager.setMicrophoneMute(true);
    }

    //静音
    public static void mute(Context context, boolean willMute) {
        AudioManager audioManager = ServiceUtil.getAudioManager(context);
        audioManager.setRingerMode(willMute ? AudioManager.RINGER_MODE_SILENT : AudioManager.RINGER_MODE_NORMAL);
        audioManager.getStreamVolume(AudioManager.STREAM_RING);
    }
}
