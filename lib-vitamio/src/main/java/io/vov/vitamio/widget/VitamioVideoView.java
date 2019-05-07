package io.vov.vitamio.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.MediaPlayer.TrackInfo;
import io.vov.vitamio.utils.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class VitamioVideoView extends SurfaceView implements MediaController.MediaPlayerControl {

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_SUSPEND = 6;
    public static final int STATE_RESUME = 7;
    public static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private Uri mUri;
    private long mDuration;
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    private float mVideoAspectRatio;
    private int mVideoChroma = MediaPlayer.VIDEOCHROMA_RGBA;
    private boolean mUseHardwareDecode = false;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private MediaController mMediaController;
    private View mMediaBufferingIndicator;
    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnTimedTextListener mOnTimedTextListener;
    private OnInfoListener mOnInfoListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private long mTargetSeek;
    private Context mContext;
    private Map<String, String> mHeaders;
    private int mBufSize;

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.d("onPrepared");
            mCurrentState = STATE_PREPARED;

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mMediaPlayer);
            if (mMediaController != null)
                mMediaController.setEnabled(true);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoAspectRatio = mp.getVideoAspectRatio();

            if (mTargetSeek != 0)
                seekTo(mTargetSeek);

            start();
        }
    };

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mTargetSeek != 0)
                    seekTo(mTargetSeek);
                start();
                if (mMediaController != null) {
                    if (mMediaController.isShowing())
                        mMediaController.hide();
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
                resume();
            } else {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();
            release(false);
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            Log.d("onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };
    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.d("Error: %d, %d", framework_err, impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null)
                mMediaController.hide();

            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err))
                    return true;
            }

            if (getWindowToken() != null) {
                int message = framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? getResources().getIdentifier("VideoView_error_text_invalid_progressive_playback", "string", mContext.getPackageName()) : getResources().getIdentifier("VideoView_error_text_unknown", "string", mContext.getPackageName());

                new AlertDialog.Builder(mContext).setTitle(getResources().getIdentifier("VideoView_error_title", "string", mContext.getPackageName())).setMessage(message).setPositiveButton(getResources().getIdentifier("VideoView_error_button", "string", mContext.getPackageName()), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mOnCompletionListener != null)
                            mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }).setCancelable(false).show();
            }
            return true;
        }
    };
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    };
    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.d("onInfo: (%d, %d)", what, extra);

            if (MediaPlayer.MEDIA_INFO_UNKNOW_TYPE == what) {
                Log.e(" VITAMIO--TYPE_CHECK  stype  not include  onInfo mediaplayer unknow type ");
            }

            if (MediaPlayer.MEDIA_INFO_FILE_OPEN_OK == what) {
                long buffersize = mMediaPlayer.audioTrackInit();
                mMediaPlayer.audioInitedOk(buffersize);
            }

            Log.d("onInfo: (%d, %d)", what, extra);

            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            } else if (mMediaPlayer != null) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    mMediaPlayer.pause();
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(View.VISIBLE);
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mMediaPlayer.start();
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(View.GONE);
                }
            }
            return true;
        }
    };
    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Log.d("onSeekComplete");
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
        }
    };
    private OnTimedTextListener mTimedTextListener = new OnTimedTextListener() {
        @Override
        public void onTimedTextUpdate(byte[] pixels, int width, int height) {
            Log.i("onSubtitleUpdate: bitmap subtitle, %dx%d", width, height);
            if (mOnTimedTextListener != null)
                mOnTimedTextListener.onTimedTextUpdate(pixels, width, height);
        }

        @Override
        public void onTimedText(String text) {
            Log.i("onSubtitleUpdate: %s", text);
            if (mOnTimedTextListener != null)
                mOnTimedTextListener.onTimedText(text);
        }
    };

    public VitamioVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView(context);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @SuppressWarnings("deprecation")
    private void initVideoView(Context ctx) {
        mContext = ctx;
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().setFormat(PixelFormat.RGBA_8888); // PixelFormat.RGB_565
        getHolder().addCallback(mSHCallback);
        // this value only use Hardware decoder before Android 2.3
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && mUseHardwareDecode) {
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (ctx instanceof Activity)
            ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public boolean isPlayValid() {
        return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
    }

    //播放媒体文件
    public void play(String path) {
        play(Uri.parse(path));
    }

    //播放媒体文件
    public void play(Uri uri) {
        play(uri, null);
    }

    public void play(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mTargetSeek = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    private void openVideo() {
//    if (mUri == null || mSurfaceHolder == null || !Vitamio.isInitialized(mContext))
//      return;
        if (mUri == null || mSurfaceHolder == null)
            return;
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release(false);

        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            mMediaPlayer = new MediaPlayer(mContext, mUseHardwareDecode);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);

            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setBufferSize(mBufSize);
            mMediaPlayer.setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ? MediaPlayer.VIDEOCHROMA_RGB565 : MediaPlayer.VIDEOCHROMA_RGBA);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            Log.e("Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.e("Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }

    public void setMediaController(MediaController controller) {
        if (mMediaController != null)
            mMediaController.hide();
        mMediaController = controller;
        attachMediaController();
    }

    public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
        if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.GONE);
        mMediaBufferingIndicator = mediaBufferingIndicator;
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(optional());

            if (mUri != null) {
                List<String> paths = mUri.getPathSegments();
                String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
                mMediaController.setFileName(name);
            }
        }
    }

    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnTimedTextListener(OnTimedTextListener l) {
        mOnTimedTextListener = l;
    }

    public void setOnInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }

    public void release(boolean resetTargetState) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (resetTargetState)
                mTargetState = STATE_IDLE;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (optional() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (optional() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (optional() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    public void start() {
        if (optional()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    public void pause() {
        if (optional()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        if (optional()) {
            release(false);
            mCurrentState = STATE_SUSPEND_UNSUPPORTED;
            Log.d("Unable to suspend video. Release MediaPlayer.");
        }
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
        }
    }

    public long getDuration() {
        if (optional()) {
            if (mDuration > 0)
                return mDuration;
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public long getCurrentPosition() {
        if (optional())
            return mMediaPlayer.getCurrentPosition();
        return 0;
    }

    public void seekTo(long msec) {
        if (optional()) {
            mMediaPlayer.seekTo(msec);
            mTargetSeek = 0;
        } else {
            mTargetSeek = msec;
        }
    }

    public boolean isPlaying() {
        return optional() && mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public float getVideoAspectRatio() {
        return mVideoAspectRatio;
    }

    /**
     * Must set before {@link #play}
     *
     * @param chroma
     */
    public void setVideoChroma(int chroma) {
        getHolder().setFormat(chroma == MediaPlayer.VIDEOCHROMA_RGB565 ? PixelFormat.RGB_565 : PixelFormat.RGBA_8888); // PixelFormat.RGB_565
        mVideoChroma = chroma;
    }

    public void setVideoQuality(int quality) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVideoQuality(quality);
    }

    public void setBufferSize(int bufSize) {
        mBufSize = bufSize;
    }

    public boolean isBuffering() {
        if (mMediaPlayer != null)
            return mMediaPlayer.isBuffering();
        return false;
    }

    public String getMetaEncoding() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getMetaEncoding();
        return null;
    }

    public void setMetaEncoding(String encoding) {
        if (mMediaPlayer != null)
            mMediaPlayer.setMetaEncoding(encoding);
    }

    public SparseArray<MediaFormat> getAudioTrackMap(String encoding) {
        if (mMediaPlayer != null)
            return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_AUDIO, mMediaPlayer.getTrackInfo(encoding));
        return null;
    }

    public int getAudioTrack() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getAudioTrack();
        return -1;
    }

    public void setAudioTrack(int audioIndex) {
        if (mMediaPlayer != null)
            mMediaPlayer.selectTrack(audioIndex);
    }

    public void setTimedTextShown(boolean shown) {
        if (mMediaPlayer != null)
            mMediaPlayer.setTimedTextShown(shown);
    }

    public void setTimedTextEncoding(String encoding) {
        if (mMediaPlayer != null)
            mMediaPlayer.setTimedTextEncoding(encoding);
    }

    public int getTimedTextLocation() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getTimedTextLocation();
        return -1;
    }

    public void addTimedTextSource(String subPath) {
        if (mMediaPlayer != null)
            mMediaPlayer.addTimedTextSource(subPath);
    }

    public String getTimedTextPath() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getTimedTextPath();
        return null;
    }

    public void setSubTrack(int trackId) {
        if (mMediaPlayer != null)
            mMediaPlayer.selectTrack(trackId);
    }

    public int getTimedTextTrack() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getTimedTextTrack();
        return -1;
    }

    public SparseArray<MediaFormat> getSubTrackMap(String encoding) {
        if (mMediaPlayer != null)
            return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mMediaPlayer.getTrackInfo(encoding));
        return null;
    }

    /**
     *
     *
     *
     *
     *
     *
     */

    // get the player that is bound to video view
    public MediaPlayer mediaPlayer() {
        return mMediaPlayer;
    }

    // speed in [0.5f, 1.0f, 1.5f, 2.0f]
    public void speed(float speed) {
        if (mMediaPlayer != null)
            mMediaPlayer.setPlaybackSpeed(speed);
    }

    // get the position that player has played to
    public long currentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    // get play state
    public int state() {
        return mCurrentState;
    }

    // update play state
    public void state(int state) {
        mCurrentState = state;
    }

    // auto seek to target position, when player is prepared
    public void targetSeek(Long targetSeek) {
        this.mTargetSeek = targetSeek;
    }

    // auto set to target state, when player is prepared
    public void targetState(int targetState) {
        this.mTargetState = targetState;
    }

    // set the resource that will be played
    public void targetUri(String path) {
        this.mUri = Uri.parse(path);
    }

    // judge that whether player is operational
    // when player is in PREPARED, PLAYING, PAUSE or RESUME state, indicate that player can be player right now
    public boolean optional() {
        return mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING;
    }

    // use hardware decode
    public void hardwareDecode(boolean useHardwareDecode) {
        mUseHardwareDecode = useHardwareDecode;
    }
}