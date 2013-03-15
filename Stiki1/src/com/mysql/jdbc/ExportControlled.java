/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.sql.SQLException;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class ExportControlled
/*     */ {
/*     */   private static final String SQL_STATE_BAD_SSL_PARAMS = "08000";
/*     */ 
/*     */   protected static boolean enabled()
/*     */   {
/*  64 */     return true;
/*     */   }
/*     */ 
/*     */   protected static void transformSocketToSSLSocket(MysqlIO mysqlIO)
/*     */     throws SQLException
/*     */   {
/*  82 */     SSLSocketFactory sslFact = getSSLSocketFactoryDefaultOrConfigured(mysqlIO);
/*     */     try
/*     */     {
/*  85 */       mysqlIO.mysqlConnection = sslFact.createSocket(mysqlIO.mysqlConnection, mysqlIO.host, mysqlIO.port, true);
/*     */ 
/*  90 */       ((SSLSocket)mysqlIO.mysqlConnection).setEnabledProtocols(new String[] { "TLSv1" });
/*     */ 
/*  92 */       ((SSLSocket)mysqlIO.mysqlConnection).startHandshake();
/*     */ 
/*  95 */       if (mysqlIO.connection.getUseUnbufferedInput())
/*  96 */         mysqlIO.mysqlInput = mysqlIO.mysqlConnection.getInputStream();
/*     */       else {
/*  98 */         mysqlIO.mysqlInput = new BufferedInputStream(mysqlIO.mysqlConnection.getInputStream(), 16384);
/*     */       }
/*     */ 
/* 102 */       mysqlIO.mysqlOutput = new BufferedOutputStream(mysqlIO.mysqlConnection.getOutputStream(), 16384);
/*     */ 
/* 105 */       mysqlIO.mysqlOutput.flush();
/*     */     } catch (IOException ioEx) {
/* 107 */       throw SQLError.createCommunicationsException(mysqlIO.connection, mysqlIO.getLastPacketSentTimeMs(), mysqlIO.getLastPacketReceivedTimeMs(), ioEx, mysqlIO.getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static SSLSocketFactory getSSLSocketFactoryDefaultOrConfigured(MysqlIO mysqlIO)
/*     */     throws SQLException
/*     */   {
/* 118 */     String clientCertificateKeyStoreUrl = mysqlIO.connection.getClientCertificateKeyStoreUrl();
/*     */ 
/* 120 */     String trustCertificateKeyStoreUrl = mysqlIO.connection.getTrustCertificateKeyStoreUrl();
/*     */ 
/* 122 */     String clientCertificateKeyStoreType = mysqlIO.connection.getClientCertificateKeyStoreType();
/*     */ 
/* 124 */     String clientCertificateKeyStorePassword = mysqlIO.connection.getClientCertificateKeyStorePassword();
/*     */ 
/* 126 */     String trustCertificateKeyStoreType = mysqlIO.connection.getTrustCertificateKeyStoreType();
/*     */ 
/* 128 */     String trustCertificateKeyStorePassword = mysqlIO.connection.getTrustCertificateKeyStorePassword();
/*     */ 
/* 131 */     if ((StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) && (StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl)))
/*     */     {
/* 133 */       if (mysqlIO.connection.getVerifyServerCertificate()) {
/* 134 */         return (SSLSocketFactory)SSLSocketFactory.getDefault();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 139 */     TrustManagerFactory tmf = null;
/* 140 */     KeyManagerFactory kmf = null;
/*     */     try
/*     */     {
/* 143 */       tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/*     */ 
/* 145 */       kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/*     */     }
/*     */     catch (NoSuchAlgorithmException nsae) {
/* 148 */       throw SQLError.createSQLException("Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */     }
/*     */ 
/* 154 */     if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) {
/*     */       try {
/* 156 */         if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreType)) {
/* 157 */           KeyStore clientKeyStore = KeyStore.getInstance(clientCertificateKeyStoreType);
/*     */ 
/* 159 */           URL ksURL = new URL(clientCertificateKeyStoreUrl);
/* 160 */           char[] password = clientCertificateKeyStorePassword == null ? new char[0] : clientCertificateKeyStorePassword.toCharArray();
/*     */ 
/* 162 */           clientKeyStore.load(ksURL.openStream(), password);
/* 163 */           kmf.init(clientKeyStore, password);
/*     */         }
/*     */       } catch (UnrecoverableKeyException uke) {
/* 166 */         throw SQLError.createSQLException("Could not recover keys from client keystore.  Check password?", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (NoSuchAlgorithmException nsae)
/*     */       {
/* 171 */         throw SQLError.createSQLException("Unsupported keystore algorithm [" + nsae.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (KeyStoreException kse)
/*     */       {
/* 175 */         throw SQLError.createSQLException("Could not create KeyStore instance [" + kse.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (CertificateException nsae)
/*     */       {
/* 179 */         throw SQLError.createSQLException("Could not load client" + clientCertificateKeyStoreType + " keystore from " + clientCertificateKeyStoreUrl, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (MalformedURLException mue)
/*     */       {
/* 183 */         throw SQLError.createSQLException(clientCertificateKeyStoreUrl + " does not appear to be a valid URL.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 187 */         SQLException sqlEx = SQLError.createSQLException("Cannot open " + clientCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */ 
/* 190 */         sqlEx.initCause(ioe);
/*     */ 
/* 192 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */ 
/* 196 */     if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl)) {
/*     */       try
/*     */       {
/* 199 */         if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreType)) {
/* 200 */           KeyStore trustKeyStore = KeyStore.getInstance(trustCertificateKeyStoreType);
/*     */ 
/* 202 */           URL ksURL = new URL(trustCertificateKeyStoreUrl);
/*     */ 
/* 204 */           char[] password = trustCertificateKeyStorePassword == null ? new char[0] : trustCertificateKeyStorePassword.toCharArray();
/*     */ 
/* 206 */           trustKeyStore.load(ksURL.openStream(), password);
/* 207 */           tmf.init(trustKeyStore);
/*     */         }
/*     */       } catch (NoSuchAlgorithmException nsae) {
/* 210 */         throw SQLError.createSQLException("Unsupported keystore algorithm [" + nsae.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (KeyStoreException kse)
/*     */       {
/* 214 */         throw SQLError.createSQLException("Could not create KeyStore instance [" + kse.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (CertificateException nsae)
/*     */       {
/* 218 */         throw SQLError.createSQLException("Could not load trust" + trustCertificateKeyStoreType + " keystore from " + trustCertificateKeyStoreUrl, "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (MalformedURLException mue)
/*     */       {
/* 222 */         throw SQLError.createSQLException(trustCertificateKeyStoreUrl + " does not appear to be a valid URL.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 226 */         SQLException sqlEx = SQLError.createSQLException("Cannot open " + trustCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */ 
/* 230 */         sqlEx.initCause(ioe);
/*     */ 
/* 232 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */ 
/* 236 */     SSLContext sslContext = null;
/*     */     try
/*     */     {
/* 239 */       sslContext = SSLContext.getInstance("TLS");
/* 240 */       sslContext.init(StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl) ? null : kmf.getKeyManagers(), new X509TrustManager[] { mysqlIO.connection.getVerifyServerCertificate() ? tmf.getTrustManagers() : new X509TrustManager()
/*     */       {
/*     */         public void checkClientTrusted(X509Certificate[] chain, String authType)
/*     */         {
/*     */         }
/*     */ 
/*     */         public void checkServerTrusted(X509Certificate[] chain, String authType)
/*     */           throws CertificateException
/*     */         {
/*     */         }
/*     */ 
/*     */         public X509Certificate[] getAcceptedIssuers()
/*     */         {
/* 254 */           return null;
/*     */         }
/*     */       }
/*     */        }, null);
/*     */ 
/* 258 */       return sslContext.getSocketFactory();
/*     */     } catch (NoSuchAlgorithmException nsae) {
/* 260 */       throw SQLError.createSQLException("TLS is not a valid SSL protocol.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */     }
/*     */     catch (KeyManagementException kme) {
/*     */     }
/* 264 */     throw SQLError.createSQLException("KeyManagementException: " + kme.getMessage(), "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.ExportControlled
 * JD-Core Version:    0.6.0
 */