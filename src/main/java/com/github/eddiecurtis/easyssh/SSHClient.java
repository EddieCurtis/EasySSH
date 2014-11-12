package com.github.eddiecurtis.easyssh;

import java.util.Set;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Session;

/**
 * SSHClient is a class which is used to connect to an SSH enabled server and perform file
 * operations such as scp
 *
 * @author Eddie Curtis
 * @date 7 Nov 2014
 */
public class SSHClient {
    
	private static final Logger LOG = Logger.getLogger(SSHClient.class);
	
    private static final String DEFAULT_DIRECTORY = "/";
    private static final boolean DEFAULT_RECURSIVE = false;
    private static final int DEFAULT_PORT = 22;
    
    private final String user;
    private final String password;
    private final String server;
    private final int port;
    
    /**
     * Create a new SSH client to connect to a remote server. This constructor uses the {@link #DEFAULT_PORT} value.
     * @param user - Username on the remote server
     * @param password - Password on the remote server
     * @param server - The server IP address or hostname
     */
    public SSHClient(String user, String password, String server) {
        this(user, password, server, DEFAULT_PORT);
    }
    
    /**
     * Create a new SSH client to connect to a remote server.
     * @param user - Username on the remote server
     * @param password - Password on the remote server
     * @param server - The server IP address or hostname
     * @param port - The port used to connect to the server
     */
    public SSHClient(String user, String password, String server, int port) {
        
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
        this.port = port;
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString, String localDirectory) throws SSHException {
        return downloadFilesMatchingString(searchString, localDirectory, DEFAULT_DIRECTORY);
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @param recursive - True if the search should be done recursively
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString, String localDirectory, boolean recursive) throws SSHException {
        return downloadFilesMatchingString(searchString, localDirectory, DEFAULT_DIRECTORY, recursive);
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @param remoteDirectory - The directory on the server to start searching in
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString, String localDirectory, String remoteDirectory) throws SSHException {
        return downloadFilesMatchingString(searchString, localDirectory, remoteDirectory, DEFAULT_RECURSIVE);
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @param remoteDirectory - The directory on the server to start searching in
     * @param recursive - True if the search should be done recursively
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString, String localDirectory, String remoteDirectory, boolean recursive) throws SSHException {        
        Session session = SessionFactory.createSession(user, password, server, port);
        Set<String> filesToDownload = TerminalCommands.getMatchingFileList(session, remoteDirectory, searchString);
        int downloaded = TerminalCommands.copyFiles(session, localDirectory, filesToDownload.toArray(new String[filesToDownload.size()]));
        int failures = filesToDownload.size() - downloaded;
        if (failures > 0) {
        	LOG.warn(String.format("Failed to download %d files", failures));
        }
        return downloaded;
    }
}
