package multigear.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

/**
 * Audio
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class AudioManager implements SoundPool.OnLoadCompleteListener {
	
	// Final Private Variables
	final private multigear.mginterface.engine.Manager mManager;
	final private multigear.audio.Cache mCache;
	final private multigear.audio.PreCache mPreCache;
	final private List<Integer> mStreamsSeId;
	
	// Private Variables
	private SoundPool mSoundPool;
	private MediaPlayer mMediaPlayer;
	
	private multigear.audio.Listener mListener;
	private float mLeftVol = 1f, mRightVol = 1f, mRate = 1f;
	
	/*
	 * Construtor
	 */
	public AudioManager(final multigear.mginterface.engine.Manager manager) {
		mManager = manager;
		mSoundPool = new SoundPool(10, android.media.AudioManager.STREAM_MUSIC, 0);
		mSoundPool.setOnLoadCompleteListener(this);
		mCache = new multigear.audio.Cache();
		mPreCache = new multigear.audio.PreCache();
		mStreamsSeId = new ArrayList<Integer>();
	}
	
	/**
	 * Sound Pool Event.
	 *   - This event called by SoundPool System.
	 */
	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		Log.d("LogTest", "Loaded: " + sampleId + " " + status);
		final PreCache.PreArchive preArchive = mPreCache.getAndRemovePreArchive(sampleId);
		if(preArchive == null){
			multigear.general.utils.KernelUtils.error(mManager.getEngine().getActivity(), "AudioSupport: An unexpected error occurred loading an audio file.", 0x12);
			return;
		}
		mCache.archive(preArchive.getArchiveId(), preArchive.getArchiveLink());
		preArchive.getPostRunnable().run();
		final int resourceId = preArchive.getArchiveLink();
		if(mListener != null) {
			if(status == 0)
				mListener.onSampleLoadComplete(resourceId);
			else
				mListener.onSampleLoadError(resourceId);
		}
	}
	
	/**
	 * Set Listener used by this Support.
	 * 
	 */
	final public void setListener(final multigear.audio.Listener listener) {
		mListener = listener;
	}
	
	/**
	 * Pre setting for all future playback.
	 * 
	 * @param leftVol Left Volume
	 * @param rightVol Right Volume
	 * @param rate Rate
	 */
	final public void preConfig(final float leftVol, final float rightVol, final float rate) {
		mLeftVol = leftVol;
		mRightVol = rightVol;
		mRate = rate;
	}
	
	/*
	 * Carrega um arquivo de audio
	 */
	final public void loadEffx(final int resourceId, final Runnable postExecute) {
		final int audioId = mSoundPool.load(mManager.getEngine().getActivity(), resourceId, 1);
		mPreCache.preArchive(audioId, resourceId, postExecute);		
	}
	
	/*
	 * Carrega um arquivo de audio
	 */
	final public void loadEffx(final int resourceId) {
		final int audioId = mSoundPool.load(mManager.getEngine().getActivity(), resourceId, 1);
		mPreCache.preArchive(audioId, resourceId, new Runnable() {
			@Override
			public void run() {}
		});		
	}
	
	/**
	 * Play Se Audio.
	 * The Se audio is a simple audio, used for sound effects.
	 * 
	 * @param resourceId Resource Id
	 * @param loop Loop count. -1 = Infinity
	 */
	final public void playEffx(final int resourceId, final int loop) {
		final int audioId = mCache.getArchiveId(resourceId);
		if(audioId != -1)
			mStreamsSeId.add(mSoundPool.play(audioId, mLeftVol, mRightVol, 1, loop, mRate));
		else {
			// Load and Play Audio
			final float lastLeftVol = mLeftVol;
			final float lastRightVol = mRightVol;
			final float lastRate = mRate;
			loadEffx(resourceId, new Runnable() {
				
				/*
				 * Post Runnable Audio Execute
				 */
				@Override
				public void run() {
					
					final int audioId = mCache.getArchiveId(resourceId);
					Log.d("LogTest", "Playing: " + audioId);
					mStreamsSeId.add(mSoundPool.play(audioId, lastLeftVol, lastRightVol, 1, loop, lastRate));
				}
			});
		}
	}
	
	/**
	 * Stop Se Streams.
	 */
	final public void stopEffx() {
		Iterator<Integer> itr = mStreamsSeId.iterator();
		while(itr.hasNext()) {
			final Integer streamId = itr.next();
			mSoundPool.stop(streamId);
			itr.remove();
		}
	}
	
	/**
	 * Pause Audio Manager
	 */
	final public void pause() {
		stopEffx();
		mSoundPool.autoPause();
	}
	
	/**
	 * Resume Audio Manager
	 */
	final public void resume() {
		mSoundPool.autoResume();
	}
	
	/*
	 * Finish This Support
	 */
	final public void finish() {
		stopEffx();
		mSoundPool.release();
		mSoundPool = null;
	}
}
