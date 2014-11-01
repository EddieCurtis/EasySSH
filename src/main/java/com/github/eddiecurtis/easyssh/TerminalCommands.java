package com.github.eddiecurtis.easyssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

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
    
    static boolean copyFiles(Session session, String... fileNames) throws SSHException {
        boolean success = false;
        if (fileNames != null) {
            String fileList = concatStrings(fileNames);
            // TODO: I'd like to get it working this way, but only had success scp'ing files individually so far
            String command = "scp -f \\{" + fileList + "\\} .";
            System.out.println("running: " + command);
            success = runCopy(session, command);
        }
        return success;
    }
    
    private static boolean runCopy(Session session, String command) throws SSHException {
        ChannelExec channel = null;
        try {
           channel = (ChannelExec) session.openChannel("exec");
           channel.setCommand("command");
           channel.connect();
           
           OutputStream out=channel.getOutputStream();
           out.write(new byte[] {0});
           out.flush();
           
           InputStream is = channel.getInputStream();
           int status = is.read();
           //TODO: check if the file needs writing manually or if the SCP command alone is enough
           if (status == 0 || status == 'C') {
               return true;
           }
           throw new SSHException("Copy command returned status code: " + status);
        } catch (Exception e) {
            throw new SSHException("Error copying files", e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
    
    private static String concatStrings(String[] strings) {
       String result = null;
       StringBuilder sb = new StringBuilder();
       for (String file : strings) {
           if (file != null) {
               sb.append(file + ",");
           }
       }
       if (sb.length() > 0) {
           // Remove the last comma from the StringBuilder before returning
           result = sb.subSequence(0, sb.length() - 1).toString();
       }
       return result;
    }
    
}
