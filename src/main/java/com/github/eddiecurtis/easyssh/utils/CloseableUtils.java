package com.github.eddiecurtis.easyssh.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * A class used to perform operations on {@link Closable}s
 * @author Eddie Curtis
 * @date 12 Nov 2014
 */
public class CloseableUtils {

	/**
	 * Closes multiple {@link Closeable} objects without throwing any exceptions if there was an error
	 * @param closeables - The objects to be closed
	 */
	public static void closeQuietly(Closeable... closeables) {
		if (closeables != null) {
			for (Closeable closeable : closeables) {
				closeQuietly(closeable);
			}
		}
	}
	
	/**
	 * Closes a {@link Closeable} object without throwing any exceptions if there was an error
	 * @param closeables - The objects to be closed
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
		        closeable.close();
	        } catch (IOException e) {
	        	// Do nothing
	        }
		}
	}
}
