/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javamail;

import java.util.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class JavaMail {
    public static void main1(String[] args){
        MailReader.main(args);
    }
    public static void main(String[] args) {
        String to = "leijurv@nuevaschool.org";
        String from = "speckles@a.cat";
        String host = "mail.nuevaschool.org";
 
        // Create properties, get Session
        Properties props = new Properties();
 
        // If using static Transport.send(),
        // need to specify which host to send it to
        props.put("mail.smtp.host", host);
        // To see what is going on behind the scene
        props.put("mail.debug", "true");
        Session session = Session.getInstance(props);

        try {
            // Instantiate a message
            Message msg = new MimeMessage(session);
 
            //Set message attributes
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject("Hello");
            msg.setSentDate(new Date());
            // Set message content
            msg.setText("Yo.");
            
            //Send the message
            //msg.saveChanges();
            //Transport.send(msg);
            System.out.println(msg.getHeader("Message-ID")[0]);
            Transport.send(msg);
            System.out.println(msg.getHeader("Message-ID")[0]);
            
        }
        
        catch (MessagingException mex) {
            // Prints all nested (chained) exceptions as well
            mex.printStackTrace();
        }
    }
}//End of class