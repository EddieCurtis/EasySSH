package com.github.eddiecurtis.easyssh;

import java.util.Set;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Session;

public class SSHClient {
    
	private static final Logger LOG = Logger.getLogger(SSHClient.class);
	
    private static final String DEFAULT_DIRECTORY = "/";
    private static final boolean DEFAULT_RECURSIVE = false;
    private static final int DEFAULT_PORT = 22;
    
    private final String user;
    private final String password;
    private final String server;
    private final String localDirectory;
    private final int port;
    
    public SSHClient(String user, String password, String server, String localDirectory) {
        this(user, password, server, localDirectory, DEFAULT_PORT);
    }
    
    public SSHClient(String user, String password, String server, String localDirectory, int port) {
        
        if (user == null) {
            throw new NullPointerException("Username must not be null");
        }
        if (password == null) {
            throw new NullPointerException("Password must not be null");
        }
        if (server == null) {
            throw new NullPointerException("Server must not be null");
        }
        
        this.user = user;
        this.password = password;
        this.server = server;
        this.localDirectory = localDirectory;
        this.port = port;
    }
    
    public int downloadFilesMatchingString(String searchString) throws SSHException {
        return downloadFilesMatchingString(searchString, DEFAULT_DIRECTORY);
    }
    
    public int downloadFilesMatchingString(String searchString, boolean recursive) throws SSHException {
        return downloadFilesMatchingString(searchString, DEFAULT_DIRECTORY, recursive);
    }
    
    public int downloadFilesMatchingString(String searchString, String directory) throws SSHException {
        return downloadFilesMatchingString(searchString, directory, DEFAULT_RECURSIVE);
    }
    
    public int downloadFilesMatchingString(String searchString, String directory, boolean recursive) throws SSHException {        
        Session session = SessionFactory.createSession(user, password, server, port);
        Set<String> filesToDownload = TerminalCommands.getMatchingFileList(session, directory, searchString);
        int downloaded = TerminalCommands.copyFiles(session, localDirectory, filesToDownload.toArray(new String[filesToDownload.size()]));
        int failures = filesToDownload.size() - downloaded;
        if (failures > 0) {
        	LOG.warn(String.format("Failed to download %d files", failures));
        }
        return downloaded;
    }
}
