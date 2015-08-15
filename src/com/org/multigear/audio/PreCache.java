package com.org.multigear.audio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Sound Support Cache
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class PreCache {
	
	/**
	 * Pre Archive Info
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final protected class PreArchive {
		
		// Final Private Variables
		final private int mArchiveId;
		final private int mArchiveLink;
		final private Runnable mPostExecute;
		
		/*
		 * Construtor
		 */
		private PreArchive(final int archiveId, final int archiveLink, final Runnable postExecute) {
			mArchiveId = archiveId;
			mArchiveLink = archiveLink;
			mPostExecute = postExecute;
		}
		
		/*
		 * Retorna o id do arquivo
		 */
		final protected int getArchiveId() {
			return mArchiveId;
		}
		
		/*
		 * Retorna o id do Link
		 */
		final protected int getArchiveLink() {
			return mArchiveLink;
		}
		
		/*
		 * Retorna o Runnable a ser executado posteriormente
		 */
		final protected Runnable getPostRunnable() {
			return mPostExecute;
		}
	}
	
	// Final Private Variables
	final private List<PreArchive> mPreArchiveList;
	
	/*
	 * Construtor
	 */
	protected PreCache() {
		mPreArchiveList = new ArrayList<PreArchive>();
	}
	
	/**
	 * Archive Info.
	 * 
	 * @param id Audio Id
	 * @param link Audio Resource Link
	 */
	final protected void preArchive(final int id, final int link, final Runnable postExecute) {
		mPreArchiveList.add(new PreArchive(id, link, postExecute));
	}
	
	/**
	 * Return Pre Archive
	 * 
	 * @param id Id of archive
	 * @return Return Archive Id
	 */
	final protected PreArchive getAndRemovePreArchive(final int id) {
		final Iterator<PreArchive> itr = mPreArchiveList.iterator();
		while(itr.hasNext()) {
			final PreArchive archive = itr.next();
			if(archive.getArchiveId() == id)
				return archive;
		}
		return null;
	}
}
