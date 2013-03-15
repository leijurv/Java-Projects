/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLOutputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXResult;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXResult;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class JDBC4MysqlSQLXML
/*     */   implements SQLXML
/*     */ {
/*     */   private XMLInputFactory inputFactory;
/*     */   private XMLOutputFactory outputFactory;
/*     */   private String stringRep;
/*     */   private ResultSetInternalMethods owningResultSet;
/*     */   private int columnIndexOfXml;
/*     */   private boolean fromResultSet;
/*  89 */   private boolean isClosed = false;
/*     */   private boolean workingWithResult;
/*     */   private DOMResult asDOMResult;
/*     */   private SAXResult asSAXResult;
/*     */   private SimpleSaxToReader saxToReaderConverter;
/*     */   private StringWriter asStringWriter;
/*     */   private ByteArrayOutputStream asByteArrayOutputStream;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */ 
/*     */   protected JDBC4MysqlSQLXML(ResultSetInternalMethods owner, int index, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/* 106 */     this.owningResultSet = owner;
/* 107 */     this.columnIndexOfXml = index;
/* 108 */     this.fromResultSet = true;
/* 109 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   protected JDBC4MysqlSQLXML(ExceptionInterceptor exceptionInterceptor) {
/* 113 */     this.fromResultSet = false;
/* 114 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */ 
/*     */   public synchronized void free() throws SQLException {
/* 118 */     this.stringRep = null;
/* 119 */     this.asDOMResult = null;
/* 120 */     this.asSAXResult = null;
/* 121 */     this.inputFactory = null;
/* 122 */     this.outputFactory = null;
/* 123 */     this.owningResultSet = null;
/* 124 */     this.workingWithResult = false;
/* 125 */     this.isClosed = true;
/*     */   }
/*     */ 
/*     */   public synchronized String getString() throws SQLException
/*     */   {
/* 130 */     checkClosed();
/* 131 */     checkWorkingWithResult();
/*     */ 
/* 133 */     if (this.fromResultSet) {
/* 134 */       return this.owningResultSet.getString(this.columnIndexOfXml);
/*     */     }
/*     */ 
/* 137 */     return this.stringRep;
/*     */   }
/*     */ 
/*     */   private synchronized void checkClosed() throws SQLException {
/* 141 */     if (this.isClosed)
/* 142 */       throw SQLError.createSQLException("SQLXMLInstance has been free()d", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   private synchronized void checkWorkingWithResult()
/*     */     throws SQLException
/*     */   {
/* 148 */     if (this.workingWithResult)
/* 149 */       throw SQLError.createSQLException("Can't perform requested operation after getResult() has been called to write XML data", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public synchronized void setString(String str)
/*     */     throws SQLException
/*     */   {
/* 183 */     checkClosed();
/* 184 */     checkWorkingWithResult();
/*     */ 
/* 186 */     this.stringRep = str;
/* 187 */     this.fromResultSet = false;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isEmpty() throws SQLException {
/* 191 */     checkClosed();
/* 192 */     checkWorkingWithResult();
/*     */ 
/* 194 */     if (!this.fromResultSet) {
/* 195 */       return (this.stringRep == null) || (this.stringRep.length() == 0);
/*     */     }
/*     */ 
/* 198 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized InputStream getBinaryStream() throws SQLException {
/* 202 */     checkClosed();
/* 203 */     checkWorkingWithResult();
/*     */ 
/* 205 */     return this.owningResultSet.getBinaryStream(this.columnIndexOfXml);
/*     */   }
/*     */ 
/*     */   public synchronized Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/* 234 */     checkClosed();
/* 235 */     checkWorkingWithResult();
/*     */ 
/* 237 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */   }
/*     */ 
/*     */   public synchronized Source getSource(Class clazz)
/*     */     throws SQLException
/*     */   {
/* 289 */     checkClosed();
/* 290 */     checkWorkingWithResult();
/*     */ 
/* 296 */     if ((clazz == null) || (clazz.equals(SAXSource.class)))
/*     */     {
/* 298 */       InputSource inputSource = null;
/*     */ 
/* 300 */       if (this.fromResultSet) {
/* 301 */         inputSource = new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml));
/*     */       }
/*     */       else {
/* 304 */         inputSource = new InputSource(new StringReader(this.stringRep));
/*     */       }
/*     */ 
/* 307 */       return new SAXSource(inputSource);
/* 308 */     }if (clazz.equals(DOMSource.class)) {
/*     */       try {
/* 310 */         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 312 */         builderFactory.setNamespaceAware(true);
/* 313 */         DocumentBuilder builder = builderFactory.newDocumentBuilder();
/*     */ 
/* 315 */         InputSource inputSource = null;
/*     */ 
/* 317 */         if (this.fromResultSet) {
/* 318 */           inputSource = new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml));
/*     */         }
/*     */         else {
/* 321 */           inputSource = new InputSource(new StringReader(this.stringRep));
/*     */         }
/*     */ 
/* 325 */         return new DOMSource(builder.parse(inputSource));
/*     */       } catch (Throwable t) {
/* 327 */         SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", this.exceptionInterceptor);
/*     */ 
/* 329 */         sqlEx.initCause(t);
/*     */ 
/* 331 */         throw sqlEx;
/*     */       }
/*     */     }
/* 334 */     if (clazz.equals(StreamSource.class)) {
/* 335 */       Reader reader = null;
/*     */ 
/* 337 */       if (this.fromResultSet) {
/* 338 */         reader = this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */       }
/*     */       else {
/* 341 */         reader = new StringReader(this.stringRep);
/*     */       }
/*     */ 
/* 344 */       return new StreamSource(reader);
/* 345 */     }if (clazz.equals(StAXSource.class)) {
/*     */       try {
/* 347 */         Reader reader = null;
/*     */ 
/* 349 */         if (this.fromResultSet) {
/* 350 */           reader = this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */         }
/*     */         else {
/* 353 */           reader = new StringReader(this.stringRep);
/*     */         }
/*     */ 
/* 356 */         return new StAXSource(this.inputFactory.createXMLStreamReader(reader));
/*     */       }
/*     */       catch (XMLStreamException ex) {
/* 359 */         SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", this.exceptionInterceptor);
/*     */ 
/* 361 */         sqlEx.initCause(ex);
/*     */ 
/* 363 */         throw sqlEx;
/*     */       }
/*     */     }
/* 366 */     throw SQLError.createSQLException("XML Source of type \"" + clazz.toString() + "\" Not supported.", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   public synchronized OutputStream setBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 392 */     checkClosed();
/* 393 */     checkWorkingWithResult();
/*     */ 
/* 395 */     this.workingWithResult = true;
/*     */ 
/* 397 */     return setBinaryStreamInternal();
/*     */   }
/*     */ 
/*     */   private synchronized OutputStream setBinaryStreamInternal() throws SQLException
/*     */   {
/* 402 */     this.asByteArrayOutputStream = new ByteArrayOutputStream();
/*     */ 
/* 404 */     return this.asByteArrayOutputStream;
/*     */   }
/*     */ 
/*     */   public synchronized Writer setCharacterStream()
/*     */     throws SQLException
/*     */   {
/* 433 */     checkClosed();
/* 434 */     checkWorkingWithResult();
/*     */ 
/* 436 */     this.workingWithResult = true;
/*     */ 
/* 438 */     return setCharacterStreamInternal();
/*     */   }
/*     */ 
/*     */   private synchronized Writer setCharacterStreamInternal() throws SQLException
/*     */   {
/* 443 */     this.asStringWriter = new StringWriter();
/*     */ 
/* 445 */     return this.asStringWriter;
/*     */   }
/*     */ 
/*     */   public synchronized Result setResult(Class clazz)
/*     */     throws SQLException
/*     */   {
/* 494 */     checkClosed();
/* 495 */     checkWorkingWithResult();
/*     */ 
/* 497 */     this.workingWithResult = true;
/* 498 */     this.asDOMResult = null;
/* 499 */     this.asSAXResult = null;
/* 500 */     this.saxToReaderConverter = null;
/* 501 */     this.stringRep = null;
/* 502 */     this.asStringWriter = null;
/* 503 */     this.asByteArrayOutputStream = null;
/*     */ 
/* 505 */     if ((clazz == null) || (clazz.equals(SAXResult.class))) {
/* 506 */       this.saxToReaderConverter = new SimpleSaxToReader();
/*     */ 
/* 508 */       this.asSAXResult = new SAXResult(this.saxToReaderConverter);
/*     */ 
/* 510 */       return this.asSAXResult;
/* 511 */     }if (clazz.equals(DOMResult.class))
/*     */     {
/* 513 */       this.asDOMResult = new DOMResult();
/* 514 */       return this.asDOMResult;
/*     */     }
/* 516 */     if (clazz.equals(StreamResult.class))
/* 517 */       return new StreamResult(setCharacterStreamInternal());
/* 518 */     if (clazz.equals(StAXResult.class)) {
/*     */       try {
/* 520 */         if (this.outputFactory == null) {
/* 521 */           this.outputFactory = XMLOutputFactory.newInstance();
/*     */         }
/*     */ 
/* 524 */         return new StAXResult(this.outputFactory.createXMLEventWriter(setCharacterStreamInternal()));
/*     */       }
/*     */       catch (XMLStreamException ex) {
/* 527 */         SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", this.exceptionInterceptor);
/*     */ 
/* 529 */         sqlEx.initCause(ex);
/*     */ 
/* 531 */         throw sqlEx;
/*     */       }
/*     */     }
/* 534 */     throw SQLError.createSQLException("XML Result of type \"" + clazz.toString() + "\" Not supported.", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */ 
/*     */   private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out)
/*     */   {
/*     */     try
/*     */     {
/* 549 */       String encoding = "UTF-8";
/*     */       try
/*     */       {
/* 552 */         ByteArrayInputStream bIn = new ByteArrayInputStream(out.toByteArray());
/*     */ 
/* 554 */         XMLStreamReader reader = this.inputFactory.createXMLStreamReader(bIn);
/*     */ 
/* 557 */         int eventType = 0;
/*     */ 
/* 559 */         while ((eventType = reader.next()) != 8) {
/* 560 */           if (eventType == 7) {
/* 561 */             String possibleEncoding = reader.getEncoding();
/*     */ 
/* 563 */             if (possibleEncoding == null) break;
/* 564 */             encoding = possibleEncoding;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */ 
/* 575 */       return new StringReader(new String(out.toByteArray(), encoding)); } catch (UnsupportedEncodingException badEnc) {
/*     */     }
/* 577 */     throw new RuntimeException(badEnc);
/*     */   }
/*     */ 
/*     */   protected String readerToString(Reader reader) throws SQLException
/*     */   {
/* 582 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 584 */     int charsRead = 0;
/*     */ 
/* 586 */     char[] charBuf = new char[512];
/*     */     try
/*     */     {
/* 589 */       while ((charsRead = reader.read(charBuf)) != -1)
/* 590 */         buf.append(charBuf, 0, charsRead);
/*     */     }
/*     */     catch (IOException ioEx) {
/* 593 */       SQLException sqlEx = SQLError.createSQLException(ioEx.getMessage(), "S1009", this.exceptionInterceptor);
/*     */ 
/* 595 */       sqlEx.initCause(ioEx);
/*     */ 
/* 597 */       throw sqlEx;
/*     */     }
/*     */ 
/* 600 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   protected synchronized Reader serializeAsCharacterStream() throws SQLException
/*     */   {
/* 605 */     checkClosed();
/* 606 */     if (this.workingWithResult)
/*     */     {
/* 608 */       if (this.stringRep != null) {
/* 609 */         return new StringReader(this.stringRep);
/*     */       }
/*     */ 
/* 612 */       if (this.asDOMResult != null) {
/* 613 */         return new StringReader(domSourceToString());
/*     */       }
/*     */ 
/* 616 */       if (this.asStringWriter != null) {
/* 617 */         return new StringReader(this.asStringWriter.toString());
/*     */       }
/*     */ 
/* 620 */       if (this.asSAXResult != null) {
/* 621 */         return this.saxToReaderConverter.toReader();
/*     */       }
/*     */ 
/* 624 */       if (this.asByteArrayOutputStream != null) {
/* 625 */         return binaryInputStreamStreamToReader(this.asByteArrayOutputStream);
/*     */       }
/*     */     }
/*     */ 
/* 629 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */   }
/*     */   protected String domSourceToString() throws SQLException {
/*     */     SQLException sqlEx;
/*     */     try { DOMSource source = new DOMSource(this.asDOMResult.getNode());
/* 635 */       Transformer identity = TransformerFactory.newInstance().newTransformer();
/*     */ 
/* 637 */       StringWriter stringOut = new StringWriter();
/* 638 */       Result result = new StreamResult(stringOut);
/* 639 */       identity.transform(source, result);
/*     */ 
/* 641 */       return stringOut.toString();
/*     */     } catch (Throwable t) {
/* 643 */       sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", this.exceptionInterceptor);
/*     */ 
/* 645 */       sqlEx.initCause(t);
/*     */     }
/* 647 */     throw sqlEx;
/*     */   }
/*     */ 
/*     */   protected synchronized String serializeAsString() throws SQLException
/*     */   {
/* 652 */     checkClosed();
/* 653 */     if (this.workingWithResult)
/*     */     {
/* 655 */       if (this.stringRep != null) {
/* 656 */         return this.stringRep;
/*     */       }
/*     */ 
/* 659 */       if (this.asDOMResult != null) {
/* 660 */         return domSourceToString();
/*     */       }
/*     */ 
/* 663 */       if (this.asStringWriter != null) {
/* 664 */         return this.asStringWriter.toString();
/*     */       }
/*     */ 
/* 667 */       if (this.asSAXResult != null) {
/* 668 */         return readerToString(this.saxToReaderConverter.toReader());
/*     */       }
/*     */ 
/* 671 */       if (this.asByteArrayOutputStream != null) {
/* 672 */         return readerToString(binaryInputStreamStreamToReader(this.asByteArrayOutputStream));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 677 */     return this.owningResultSet.getString(this.columnIndexOfXml);
/*     */   }
/*     */ 
/*     */   class SimpleSaxToReader extends DefaultHandler
/*     */   {
/* 702 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 744 */     private boolean inCDATA = false;
/*     */ 
/*     */     SimpleSaxToReader()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void startDocument()
/*     */       throws SAXException
/*     */     {
/* 705 */       this.buf.append("<?xml version='1.0' encoding='UTF-8'?>");
/*     */     }
/*     */ 
/*     */     public void endDocument()
/*     */       throws SAXException
/*     */     {
/*     */     }
/*     */ 
/*     */     public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException
/*     */     {
/* 715 */       this.buf.append("<");
/* 716 */       this.buf.append(qName);
/*     */ 
/* 718 */       if (attrs != null) {
/* 719 */         for (int i = 0; i < attrs.getLength(); i++) {
/* 720 */           this.buf.append(" ");
/* 721 */           this.buf.append(attrs.getQName(i)).append("=\"");
/* 722 */           escapeCharsForXml(attrs.getValue(i), true);
/* 723 */           this.buf.append("\"");
/*     */         }
/*     */       }
/*     */ 
/* 727 */       this.buf.append(">");
/*     */     }
/*     */ 
/*     */     public void characters(char[] buf, int offset, int len) throws SAXException
/*     */     {
/* 732 */       if (!this.inCDATA)
/* 733 */         escapeCharsForXml(buf, offset, len, false);
/*     */       else
/* 735 */         this.buf.append(buf, offset, len);
/*     */     }
/*     */ 
/*     */     public void ignorableWhitespace(char[] ch, int start, int length)
/*     */       throws SAXException
/*     */     {
/* 741 */       characters(ch, start, length);
/*     */     }
/*     */ 
/*     */     public void startCDATA()
/*     */       throws SAXException
/*     */     {
/* 747 */       this.buf.append("<![CDATA[");
/* 748 */       this.inCDATA = true;
/*     */     }
/*     */ 
/*     */     public void endCDATA() throws SAXException {
/* 752 */       this.inCDATA = false;
/* 753 */       this.buf.append("]]>");
/*     */     }
/*     */ 
/*     */     public void comment(char[] ch, int start, int length)
/*     */       throws SAXException
/*     */     {
/* 759 */       this.buf.append("<!--");
/* 760 */       for (int i = 0; i < length; i++) {
/* 761 */         this.buf.append(ch[(start + i)]);
/*     */       }
/* 763 */       this.buf.append("-->");
/*     */     }
/*     */ 
/*     */     Reader toReader()
/*     */     {
/* 768 */       return new StringReader(this.buf.toString());
/*     */     }
/*     */ 
/*     */     private void escapeCharsForXml(String str, boolean isAttributeData) {
/* 772 */       if (str == null) {
/* 773 */         return;
/*     */       }
/*     */ 
/* 776 */       int strLen = str.length();
/*     */ 
/* 778 */       for (int i = 0; i < strLen; i++)
/* 779 */         escapeCharsForXml(str.charAt(i), isAttributeData);
/*     */     }
/*     */ 
/*     */     private void escapeCharsForXml(char[] buf, int offset, int len, boolean isAttributeData)
/*     */     {
/* 786 */       if (buf == null) {
/* 787 */         return;
/*     */       }
/*     */ 
/* 790 */       for (int i = 0; i < len; i++)
/* 791 */         escapeCharsForXml(buf[(offset + i)], isAttributeData);
/*     */     }
/*     */ 
/*     */     private void escapeCharsForXml(char c, boolean isAttributeData)
/*     */     {
/* 796 */       switch (c) {
/*     */       case '<':
/* 798 */         this.buf.append("&lt;");
/* 799 */         break;
/*     */       case '>':
/* 802 */         this.buf.append("&gt;");
/* 803 */         break;
/*     */       case '&':
/* 806 */         this.buf.append("&amp;");
/* 807 */         break;
/*     */       case '"':
/* 811 */         if (!isAttributeData) {
/* 812 */           this.buf.append("\"");
/*     */         }
/*     */         else {
/* 815 */           this.buf.append("&quot;");
/*     */         }
/*     */ 
/* 818 */         break;
/*     */       case '\r':
/* 821 */         this.buf.append("&#xD;");
/* 822 */         break;
/*     */       default:
/* 827 */         if (((c >= '\001') && (c <= '\037') && (c != '\t') && (c != '\n')) || ((c >= '') && (c <= '')) || (c == ' ') || ((isAttributeData) && ((c == '\t') || (c == '\n'))))
/*     */         {
/* 830 */           this.buf.append("&#x");
/* 831 */           this.buf.append(Integer.toHexString(c).toUpperCase());
/* 832 */           this.buf.append(";");
/*     */         }
/*     */         else {
/* 835 */           this.buf.append(c);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/leijurv/Downloads/STiki_exec_2012_07_18/STiki_2012_07_18.jar
 * Qualified Name:     com.mysql.jdbc.JDBC4MysqlSQLXML
 * JD-Core Version:    0.6.0
 */