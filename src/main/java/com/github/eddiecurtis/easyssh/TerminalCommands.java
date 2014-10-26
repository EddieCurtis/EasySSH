package com.github.eddiecurtis.easyssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

class TerminalCommands {
    
    static Set<String> getMatchingFileList(Session session, String directory, String searchString) throws SSHException {
        
        Channel channel = null;
        BufferedReader br = null;
        try {
           channel = session.openChannel("exec");
           ((ChannelExec)channel).setCommand(String.format(Constants.FIND_MATCHING_FILES, directory, searchString));
           
           Set<String> filesToDownload = new HashSet<String>();
           channel.connect();
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
    
}
