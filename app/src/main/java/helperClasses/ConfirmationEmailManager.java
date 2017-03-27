package helperClasses;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by savanpatel on 2017-02-14.
 */

public class ConfirmationEmailManager {

    private static ConfirmationEmailManager confirmationEmailManagerInstance = null;
    private final String USERNAME = "support@scaniq.net";
    private final String PASSWORD = "support999";
    Properties props = null;

    public ConfirmationEmailManager() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtpout.secureserver.net");
        props.put("mail.smtp.port", "80");
    }

    public static ConfirmationEmailManager getInstance()
    {
        if(confirmationEmailManagerInstance == null)
        {
            confirmationEmailManagerInstance = new ConfirmationEmailManager();
        }
        return confirmationEmailManagerInstance;
    }


    public void sendMail(String newUser, String MYSQLRRuid)
    {
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME , PASSWORD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("support@scaniq.net"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(newUser));
            //Delete next line before publishing...
//            message.setRecipients(Message.RecipientType.BCC,
//                    InternetAddress.parse("spatel@scaniq.net"));

            message.setSubject("ScanIQ Registration - Your New Scan ID is " + MYSQLRRuid);

            String md5 = "";
            String content =
                    "<html><body>" +
                            "Thank you for installing ScanIQ! " +
                            "<br><br>ScanIQ allows you to connect a scanner or printer to your mobile device and email or fax documents in seconds." +
                            "<br><br><br>Supported scanners:" +
                            "<br>Canon ImageFORMULA P-Series" +
                            "<br>Fujitsu ScanSnap ixSeries" +
                            "<br><br><br>If you have any questions or inquiries please reply to this email or call us at 1-877-421-7226" +
                            "<br><br>Please " +
                            "<a href='http://scaniq.secureserverdot.com/RRdb/confirm.php?md5=md5tag' > confirm your subscription here. </a><br><br>" +
                            "</body></html>";

            String str = content.replace("md5tag", MYSQLRRuid);
            message.setContent(str, "text/html");
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}