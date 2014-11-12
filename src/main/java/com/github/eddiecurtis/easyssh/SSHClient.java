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
    private final String localDirectory;
    private final int port;
    
    /**
     * Create a new SSH client to connect to a remote server. This constructor uses the {@link #DEFAULT_PORT} value.
     * @param user - Username on the remote server
     * @param password - Password on the remote server
     * @param server - The server IP address or hostname
     * @param localDirectory - TODO: this will be removed soon and added to individual fileDownload methods
     */
    public SSHClient(String user, String password, String server, String localDirectory) {
        this(user, password, server, localDirectory, DEFAULT_PORT);
    }
    
    /**
     * Create a new SSH client to connect to a remote server.
     * @param user - Username on the remote server
     * @param password - Password on the remote server
     * @param server - The server IP address or hostname
     * @param localDirectory - TODO: this will be removed soon and added to individual fileDownload methods
     * @param port - The port used to connect to the server
     */
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
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString) throws SSHException {
        return downloadFilesMatchingString(searchString, DEFAULT_DIRECTORY);
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @param recursive - True if the search should be done recursively
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString, boolean recursive) throws SSHException {
        return downloadFilesMatchingString(searchString, DEFAULT_DIRECTORY, recursive);
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @param directory - The directory on the server to start searching in
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
    public int downloadFilesMatchingString(String searchString, String directory) throws SSHException {
        return downloadFilesMatchingString(searchString, directory, DEFAULT_RECURSIVE);
    }
    
    /**
     * Downloads any files matching the specified search string from the server this client is connected to
     * @param searchString - The String to search for
     * @param directory - The directory on the server to start searching in
     * @param recursive - True if the search should be done recursively
     * @return Returns the number of files that were downloaded from the server
     * @throws SSHException If there was a problem connecting to the server
     */
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
