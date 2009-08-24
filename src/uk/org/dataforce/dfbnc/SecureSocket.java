/*
 * Copyright (c) 2006-2007 Shane Mc Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * SVN: $Id$
 */
package uk.org.dataforce.dfbnc;

import java.nio.channels.SocketChannel;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.KeyStoreException;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * This defines a Secure (ssl) Socket.
 */
public class SecureSocket extends SocketWrapper {
    /** SSLEngine used for this socket */
    private final SSLEngine sslEngine;
    /** SSLContext in use by ssl sockets */
    private static SSLContext sslContext = null;
    
    
    /**
     * Get (and create if needed) a copy of the SSLContext we are using.
     *
     * @return SSLContext to use for new SSLEngines
     * @throws IllegalArgumentException If there is a problem with the config settings related to ssl
     * @throws KeyStoreException If there is a problem with the keystore
     * @throws FileNotFoundException If the keystore does not exist
     * @throws NoSuchAlgorithmException If there is a problem getting the right algorithm for the SSLContext
     * @throws KeyManagementException If there is a problem with the keystore
     * @throws UnrecoverableKeyException  If there is a problem with the key in the keystore
     * @throws IOException If there is a problem opening the keystore
     * @throws CertificateException If there is a problem with the keystore
     */
    public static synchronized SSLContext getSSLContext() throws IllegalArgumentException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, IOException, CertificateException {
        if (sslContext == null) {
            String storePassword =  DFBnc.getBNC().getConfig().getOption("ssl", "storepass", "");
            String keyPassword =  DFBnc.getBNC().getConfig().getOption("ssl", "keypass", "");
            String keyStore =  DFBnc.getBNC().getConfig().getOption("ssl", "keystore", "");
            
            if (keyStore.isEmpty()) { throw new IllegalArgumentException("No keystore sepcified in config ('ssl.keystore')"); }
            else if (keyPassword.isEmpty()) { throw new IllegalArgumentException("No key password sepcified in config ('ssl.keypass')"); }
            else if (storePassword.isEmpty()) { throw new IllegalArgumentException("No keystore password sepcified in config ('ssl.storepass')"); }
            
            File keyFile = new File(keyStore);
            if (!keyFile.exists()) { throw new FileNotFoundException("Keystore '"+keyStore+"' does not exist."); }
            
            // Load the keystore
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keyFile), storePassword.toCharArray());
            
            // Load the keymanager
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyPassword.toCharArray());
            
            // Load the TrustManager
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            
            // Create an SSLContext
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        }

        return sslContext;
    }
    
    /**
     * Create a new SecureSocket
     *
     * @param channel Channel to Wrap.
     * @param owner ConnectedSocket that owns this.
     * @throws IOException If there is a problem creating and setting up the socket
     */
    public SecureSocket (final SocketChannel channel, final ConnectedSocket owner) throws IOException {
        super(channel, owner);
        
        try {
            sslEngine = getSSLContext().createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.beginHandshake();
        
            myByteChannel = new SSLByteChannel(channel, sslEngine);
        } catch (Exception e) {
            throw new IOException("Error setting up SSL Socket: "+e.getMessage(), e);
        }
    }
}