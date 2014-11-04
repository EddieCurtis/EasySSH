import com.github.eddiecurtis.easyssh.SSHClient;
import com.github.eddiecurtis.easyssh.SSHException;

import java.util.Scanner;

public class TempTest {
    
   // Test class only, not to be shipped in releases
    
   public static void main(String[] args) throws SSHException {
       
       Scanner scanner = new Scanner(System.in);
       
       System.out.print("Username: ");
       String username = scanner.next();
       System.out.print("Password: ");
       String password = scanner.next();
       System.out.print("Server: ");
       String server = scanner.next();
       System.out.print("Port: ");
       int port = Integer.parseInt(scanner.next());       
       
       SSHClient client = new SSHClient(username, password, server, port);
       
       int filesDownloaded = client.downloadFilesMatchingString("test", "/home/codenvy/");
       if(filesDownloaded > 0) {
           System.out.println("Successfully downloaded %d files");
       } else {
           System.out.println("No matching files found on server");
       }
   }
}
