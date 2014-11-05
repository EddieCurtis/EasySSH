package com.github.eddiecurtis.easyssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    private static boolean runCopy(String localDirectory, Session session, String fileName) throws SSHException {
        ChannelExec channel = null;
        FileOutputStream out = null;
        InputStream is = null;
        
        try {
           channel = (ChannelExec) session.openChannel("exec");
           //TODO replace this with filename once tested
           channel.setCommand("scp -f /home/codenvy/test1.txt");
           channel.connect();
           
           is = channel.getInputStream();
           int status = is.read();
           if (status == 0 || status == 'C') {
        	   out = new FileOutputStream(new File(localDirectory + fileName));
        	   byte[] bytes = new byte[1024];
        	   int read = 0;
        	   while ((read = is.read(bytes)) > -1) {
        		   out.write(bytes, 0, read);
        	   }
               return true;
           }
           return false;
        } catch (Exception e) {
            throw new SSHException("Error downloading file: " + fileName, e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (out != null) {
            	try {
	                out.close();
                } catch (IOException e) {
	                e.printStackTrace();
                }
            }
            if (is != null) {
            	try {
	                is.close();
                } catch (IOException e) {
	                e.printStackTrace();
                }
            }
        }
    }
    
}
