package multigear.communication.tcp.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Servers List
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class ServersList {
	
	/**
	 * Server Info
	 * 
	 * @author PedroH, RaphaelB
	 *
	 * Property Createlier.
	 */
	final public static class ServerInfo {
		
		// Final Public Variables
		final public String Name;
		final public String Address;
		
		/*
		 * Construtor
		 */
		public ServerInfo(final String name, final String address) {
			Name = name;
			Address = address;
		}
	}
	// Final Private VAriables
	final private List<ServerInfo> mList;
	
	/*
	 * Construtor
	 */
	public ServersList() {
		mList = new ArrayList<ServersList.ServerInfo>();
	}
	
	/*
	 * Adiciona uma nova informação de um servidor
	 */
	final protected void addServerInfo(final String name, final String address) {
		mList.add(new ServerInfo(name, address));
	}
	
	/*
	 * Retorna o tamanho da lista
	 */
	final public int size() {
		return mList.size();
	}
	
	/*
	 * Retorna a informação de um servidor
	 */
	final public ServerInfo getServerInfo(final int index) {
		return mList.get(index);
	}
	
	/**
	 * Return List of all Severs Info.
	 * @return
	 */
	final public List<ServerInfo> getList() {
		return mList;
	}
}
