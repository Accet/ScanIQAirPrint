package helperClasses;

import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by savanpatel on 2017-03-07.
 */

public class EmailSender extends javax.mail.Authenticator {
    private String user;
    private String password;

    private String[] to;
    private String from;

    private String cc;
    private String bcc;

    private String port;
    private String sport;

    private String host;

    private String subject;
    private String body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart multipart;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getTo() {
        return to;
    }

    public void setCC(String cc) {
        this.cc = cc;
    }

    public String getCC() {
        return cc;
    }

    public void setTo(String[] to) {
        this.to = to;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Multipart getMultipart() {
        return multipart;
    }

    public void setMultipart(Multipart multipart) {
        this.multipart = multipart;
    }

    public EmailSender() {
        host = "smtpout.secureserver.net"; // default smtp server
        port = "3535"; // default smtp port
        sport = "3535"; // default socketfactory port

        user = ""; // username
        password = ""; // password
        from = ""; // email sent from
        subject = ""; // email subject
        body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        multipart = new MimeMultipart("alternative");

        // There is something wrong with MailCap, javamail can not find a
        // handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap
                .getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public EmailSender(String user, String pass) {
        this();
        this.user = user;
        password = pass;
    }

    public boolean send(boolean isFax) throws Exception {
        Properties props = _setProperties();

        if (!user.equals("") && !password.equals("") && to.length > 0
                && !from.equals("") && !subject.equals("") ) {
//            && !body.equals("")
            Session session = Session.getInstance(props, this);

            MimeMessage msg = new MimeMessage(session);

            if (isFax) {
                msg.setFrom(new InternetAddress(from));
            } else {
                msg.setFrom(new InternetAddress("ScanIQ@scaniq.com"));
            }

            if (  cc != null && !cc.equals("")) {
                InternetAddress ccAddress = new InternetAddress(getCC());
                msg.addRecipient(MimeMessage.RecipientType.CC, ccAddress);
            }
            if ( bcc != null && !bcc.equals("")) {
                InternetAddress bccAddress = new InternetAddress(getBCC());
                msg.addRecipient(Message.RecipientType.BCC, bccAddress);
            }

            InternetAddress[] addressTo = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                Log.i("Email", "to[i] ->" + to[i]);

                addressTo[i] = new InternetAddress(to[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // setup message body
            MimeBodyPart messageBodyPart = new MimeBodyPart();

//            messageBodyPart.setText(body);
            messageBodyPart.setText(body, "utf-8", "html");
            multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            msg.setContent(multipart);


            Log.i("Port","-> "+port);

            // send email
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(new File(filename).getName());

        multipart.addBodyPart(messageBodyPart);
    }


    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    private Properties _setProperties() {

        Properties props = new Properties();
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.setProperty("mail.transport.protocol", "smtp");
        return props;
    }

    // the getters and setters
    public String getBody() {
        return body;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public void setBody(String _body) {
        this.body = _body;
    }

    public String getBCC() {
        return bcc;
    }

    public void setBCC(String bcc) {
        this.bcc = bcc;
    }
}
