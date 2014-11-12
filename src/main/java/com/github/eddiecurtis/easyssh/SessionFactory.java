package com.github.eddiecurtis.easyssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * A factory class used to create new JSch sessions
 * @author Eddie Curtis
 * @date 7 Nov 2014
 */
class SessionFactory {
    
	/**
	 * Create and connect to a new {@link Session}
	 * @param user - The username for the server
	 * @param password - The password of this user
	 * @param location - The server location, e.g. oracle.com
	 * @param port - The port to connect to
	 * @return Returns a new {@link Session} with the provided details
	 * @throws SSHException If the session was unable to be connected to
	 */
    static Session createSession(String user, String password, String location, int port) throws SSHException {
        try {
           //TODO: remove this setConfig as it's not secure. Used only to be able to test from the cloud
           JSch.setConfig("StrictHostKeyChecking", "no");
           JSch jsch = new JSch();           
           Session session = jsch.getSession(user, location, port);
           session.setPassword(password);
           session.connect();
           session.sendKeepAliveMsg();
           return session;
        } catch (Exception e) {
           throw new SSHException(String.format(Constants.CREATE_SESSION_ERROR, user, location, port), e);
        }
    }
}
