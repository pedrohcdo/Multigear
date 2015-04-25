package multigear.communication.tcp.client;

import java.util.Locale;

/**
 * Utils
 * 
 * @author PedroH, RaphaelB
 *
 * Property Createlier.
 */
final public class Utils {
	
	/*
	 * Converte para Byte IP
	 */
	final protected static int[] intToIpVInt(final int ip) {
		final int[] i2v = new int[4];
		i2v[0] = ip & 0xff;
		i2v[1] = ip >> 8 & 0xff;
		i2v[2] = ip >> 16 & 0xff;
		i2v[3] = ip >> 24 & 0xff;
		return i2v;
	}
	
	/*
	 * Converte para String IP
	 */
	final protected static String intToIpString(final int ip) {
		return String.format(Locale.US, "%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
	}
	
	/*
	 * Converte para String IP
	 */
	final protected static String vIntToIpString(final int[] ip) {
		return String.format(Locale.US, "%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
	}
	
	/*
	 * Cria uma lista de conexão
	 */
	final protected static String[] createConnectionsList(final int[] ipAddress, final String dns, int connectionAttempts) {
		// Copy Ip Address
		final int[] virtual = new int[4];
		virtual[0] = ipAddress[0];
		virtual[1] = ipAddress[1];
		virtual[2] = ipAddress[2];
		virtual[3] = ipAddress[3];
		// Correct Connection Attempts
		if(connectionAttempts % 2 != 0)
			connectionAttempts++;
		// Create Host List
		final String[] list = new String[connectionAttempts + 2];
		// Set List Index
		int index = 0;
		// Add DNS
		list[index++] = "192.168.43.1";
		list[index++] = dns;
		
		// Get Sides
		final int baseAddress = ipAddress[3];
		int leftSide = baseAddress - 1;
		int rightSide = baseAddress + 1;
		// Add Sides Host
		int times = connectionAttempts;
		while(times > 0) {
			// Add Left Side
			if(leftSide >= 0) {
				times--;
				virtual[3] = leftSide--;
				list[index++] = vIntToIpString(virtual);
			}
			// Add Right Side
			if(rightSide < 254) {
				times--;
				virtual[3] = rightSide++;
				list[index++] = vIntToIpString(virtual);
			}
		}
		// Return list
		return list;
	}
}
