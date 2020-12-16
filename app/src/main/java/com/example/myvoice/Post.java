package com.example.myvoice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Post extends AsyncTask<Void,Void,Void> {
    private ProgressDialog progressDialog;
    private String email;
    private String subject;
    private String message;
    private Session sesh;
    private Context cont;

    public Post(Context context, String email, String subject, String message) {
        this.cont = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring the properties for gmail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //a new session is made
        sesh = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Organization.EMAIL, Organization.PASSWORD);
                    }
                });

        try {
            //define MimeMessage object
            MimeMessage mm = new MimeMessage(sesh);

            // sender address
            mm.setFrom(new InternetAddress(Organization.EMAIL));
            //receiver email
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            // subject
            mm.setSubject(subject);
            // message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(cont,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(cont,"Message Sent", Toast.LENGTH_LONG).show();
    }
}
