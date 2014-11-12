package com.github.eddiecurtis.easyssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.github.eddiecurtis.easyssh.utils.CloseableUtils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

class TerminalCommands {
    
    static Set<String> getMatchingFileList(Session session, String directory, String searchString) throws SSHException {
        
        ChannelExec channel = null;
        BufferedReader br = null;
        try {
           channel = (ChannelExec) session.openChannel("exec");
           channel.setCommand(String.format(Constants.FIND_MATCHING_FILES, directory, searchString));
           channel.connect();
           
           Set<String> filesToDownload = new HashSet<String>();           
           br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
           String line;
           while ((line = br.readLine()) != null) {
               filesToDownload.add(line);
           }
           return filesToDownload;
        } catch (Exception e) {
            throw new SSHException("Error getting files list", e);
        } finally {
        	CloseableUtils.closeQuietly(br);
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
    
    static int copyFiles(Session session, String localDirectory, String... fileNames) throws SSHException {
        int filesDownloaded = 0;
        if (fileNames != null) {
            for (String fileName : fileNames) {
	            boolean success = runCopy(localDirectory, session, fileName);
	            if (success) {
	            	filesDownloaded++;
	            }
            }
        }
        return filesDownloaded;
    }
    
    private static boolean runCopy(String localDirectory, Session session, String fileLocation) throws SSHException {
        ChannelExec channel = null;
        FileOutputStream fileOut = null;
        InputStream channelIn = null;
        OutputStream channelOut = null;
        
        try {
           channel = (ChannelExec) session.openChannel("exec");
           channel.setCommand("scp -f " + fileLocation);
           channelIn = channel.getInputStream();
           channelOut = channel.getOutputStream();
           channel.connect();
           channelOut.write(new byte[] {0}, 0, 1);
           channelOut.flush();
           
           int status = channelIn.read();
           if (status == 0 || status == 'C') {
        	   byte[] buff = new byte[1024];
        	   
        	   // Read the rest of these characters as it's just header information
        	   channelIn.read(buff, 0, channelIn.available());
        	   
        	   // This write signifies we're ready to read the file contents
        	   channelOut.write(new byte[] {0}, 0, 1);
               channelOut.flush();
               
               String finalFileLocation = localDirectory + fileLocation;
               ensureDirectory(finalFileLocation);
               fileOut = new FileOutputStream(new File(finalFileLocation));
               int bytesRead = 0;
               buff = new byte[1024];
               while (channelIn.available() > 0 && (bytesRead = channelIn.read(buff)) != -1) {
            	   fileOut.write(buff, 0, bytesRead);
               }
               return true;
           }
           return false;
        } catch (Exception e) {
            throw new SSHException("Error downloading file: " + fileLocation, e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            CloseableUtils.closeQuietly(fileOut, channelIn, channelOut);
        }
    }
    
	private static void ensureDirectory(String finalFileLocation) {
		new File(finalFileLocation.substring(0, finalFileLocation.lastIndexOf('/'))).mkdirs();
    }
    
}
