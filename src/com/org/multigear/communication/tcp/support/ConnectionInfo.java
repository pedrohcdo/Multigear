package com.org.multigear.communication.tcp.support;

/**
 * Connection Info
 * 
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
public class ConnectionInfo {
	
	// Final Public Variables
	final public String Name;
	final public String Address;
	
	/*
	 * Construtor
	 */
	public ConnectionInfo(final String name, final String address) {
		Name = name;
		Address = address;
	}
}
