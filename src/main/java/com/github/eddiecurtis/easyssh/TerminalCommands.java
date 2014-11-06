package com.github.eddiecurtis.easyssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

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
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {};
            }
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
        FileOutputStream out = null;
        InputStream is = null;
        OutputStream os = null;
        
        try {
           channel = (ChannelExec) session.openChannel("exec");
           channel.setCommand("scp -f " + fileLocation);
           is = channel.getInputStream();
           os = channel.getOutputStream();
           channel.connect();
           os.write(new byte[] {0}, 0, 1);
           os.flush();
           
           int status = is.read();
           if (status == 0 || status == 'C') {
        	   
        	   //TODO create directory if it doesn't already exist
        	   out = new FileOutputStream(new File(localDirectory + fileLocation));
        	   byte[] bytes = new byte[1024];
        	   // Files start with '0644 '
        	   is.read(bytes, 0, 5);
        	   int read = 0;
        	   while ((read = is.read(bytes)) > -1) {
        		   out.write(bytes, 0, read);
        	   }
        	   
        	   //TODO this currently seems to only download information about the file.
        	   //Need to actually download the contents too, possibly by sending a '0' again.
               return true;
           }
           return false;
        } catch (Exception e) {
            throw new SSHException("Error downloading file: " + fileLocation, e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (out != null) {
            	try {
	                out.close();
                } catch (IOException e) {}
            }
            if (is != null) {
            	try {
	                is.close();
                } catch (IOException e) {}
            }
            if (os != null) {
            	try {
            		os.close();
            	} catch (IOException e) {}
            }
        }
    }
    
}
