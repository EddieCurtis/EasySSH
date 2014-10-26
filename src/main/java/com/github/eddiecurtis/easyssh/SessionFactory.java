package com.github.eddiecurtis.easyssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

class SessionFactory {
    
    static Session createSession(String user, String password, String location, int port) throws SSHException {
        try {
           JSch jsch = new JSch();
           Session session = jsch.getSession(user, location, port);
           session.setPassword(password);
           return session;
        } catch (Exception e) {
           throw new SSHException(String.format(Constants.CREATE_SESSION_ERROR, user, location, port));
        }
    }
}
