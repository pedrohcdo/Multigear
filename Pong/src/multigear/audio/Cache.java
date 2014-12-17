package multigear.audio;

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
public class Cache {
	
	/**
	 * Archive Info
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final private class Archive {
		
		// Final Private Variables
		final private int mArchiveId;
		final private int mArchiveLink;
		
		/*
		 * Construtor
		 */
		private Archive(final int archiveId, final int archiveLink) {
			mArchiveId = archiveId;
			mArchiveLink = archiveLink;
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
	}
	
	// Final Private Variables
	final private List<Archive> mArchivedList;
	
	/*
	 * Construtor
	 */
	protected Cache() {
		mArchivedList = new ArrayList<Archive>();
	}
	
	/**
	 * Return true if has Archived
	 */
	final protected boolean hasArchived(final int link) {
		final Iterator<Archive> itr = mArchivedList.iterator();
		while(itr.hasNext()) {
			if(itr.next().getArchiveLink() == link)
				return true;
		}
		return false;
	}
	
	/**
	 * Archive Info.
	 * 
	 * @param id Audio Id
	 * @param link Audio Resource Link
	 */
	final protected void archive(final int id, final int link) {
		if(hasArchived(link))
			return;
		mArchivedList.add(new Archive(id, link));
	}
	
	/**
	 * Return Archive Id
	 * 
	 * @param link Link of archive
	 * @return Return Archive Id
	 */
	final protected int getArchiveId(final int link) {
		final Iterator<Archive> itr = mArchivedList.iterator();
		while(itr.hasNext()) {
			final Archive archive = itr.next();
			if(archive.getArchiveLink() == link)
				return archive.getArchiveId();
		}
		return -1;
	}
}
