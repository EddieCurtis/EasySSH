package com.github.eddiecurtis.easyssh;

public class SSHException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public SSHException(String message) {
        super(message);
    }
    
    public SSHException(String message, Exception cause) {
        super(message, cause);
    }
}
