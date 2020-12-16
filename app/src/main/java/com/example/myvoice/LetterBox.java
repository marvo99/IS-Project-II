package com.example.myvoice;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;



public class LetterBox extends AppCompatActivity {
//    private TextView textViewFrom;
//    private TextView textViewSubject;
//    private TextView textViewBody;

    private static final int WAITTIME = 10000; //time in milliseconds for waiting i.e. 10s; 1000ms = 1s

    private ArrayList<TheEmail> theEmailArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_letterbox);


//        textViewFrom = (TextView) findViewById(R.id.emailfrom);
//        textViewSubject = (TextView) findViewById(R.id.emailsubject);
//        textViewBody = (TextView) findViewById(R.id.emailmessage);

        Voice voice = new Voice(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        refreshData(voice);
    }

    private void refreshData(Voice voice) {
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final ScrollView scrollView = new ScrollView(this);

        //check that speaker is ready before checking emails
        if (voice.isSpeakerReady()) {
            //do not refresh screen until speech is finished
            if (voice.isSpeaking()) {
                repeatCheckEmail(WAITTIME, voice);
            } else {
                System.out.println("Checking for messages");
                ArrayList<TheEmail> allMsgs = getEmails();
                //if there are messages, show the messages
                if (!allMsgs.isEmpty()) {
                    showNonEmptyViews(linearLayout, scrollView, allMsgs, voice);
                } else {
                    showEmptyViews(linearLayout, voice);
                    Intent myIntent = new Intent(LetterBox.this,MainActivity.class);
                    startActivity(myIntent);
                    finish();
                    return;
                }
               // repeatCheckEmail(WAITTIME, voice);
                //Intent myIntent = new Intent(LetterBox.this,MainActivity.class);
                //startActivity(myIntent);
            }
        } else {
            repeatCheckEmail(5000,voice);
            return;

        }
    }


    //this method prints
    @SuppressLint("ResourceAsColor")
    private void showNonEmptyViews(LinearLayout l, ScrollView s, ArrayList<TheEmail> msgs, Voice voice) {
        //l.setBackgroundResource(R.color.background);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        System.out.println("Number of unread emails: " + msgs.size());

        if (msgs.size() > 0) {
            for (int i = 0; i < msgs.size(); i++) {

                String msgBody, msgSubject;

                int num = i + 1;

                voice.speak("Reading message " + num + " of " + msgs.size());
                TheEmail msg = msgs.get(i);

                TextView textViewFrom = new TextView(getApplicationContext());
                textViewFrom.setText("This email is from: " + msg.getMsgFrom());
                textViewFrom.setTextColor(R.color.background);
                textViewFrom.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 25);
                l.addView(textViewFrom);
                voice.speak("This email is from " + msg.getMsgFrom());
                msgSubject = "\nThe subject of the email is: " + msg.getMsgSubject();
                //check the email subject
                if (msg.getMsgSubject().isEmpty())
                    msgSubject = "This email has no subject";

                TextView textViewSubject = new TextView(getApplicationContext());
                textViewSubject.setText(msgSubject);
                textViewSubject.setTextColor(R.color.background);
                textViewSubject.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 25);
                l.addView(textViewSubject);

                voice.speak(msgSubject);

                msgBody = "\n Email body: " + msg.getMsgBodyContent();
                //check if body is empty
                if (msg.getMsgBodyContent().isEmpty())
                    msgBody = "This email has no body text";

                TextView textViewBody = new TextView(getApplicationContext());
                textViewBody.setTextColor(R.color.background);
                textViewBody.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 25);
                textViewBody.setText(msgBody);
                l.addView(textViewBody);

                voice.speak(msgBody);
            }

            s.addView(l);
            setContentView(s);


        }

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent myIntent = new Intent(LetterBox.this,MainActivity.class);
                startActivity(myIntent);
                finish();
                return;
            }

        }, 20000L);



    }

    //repeats to check the emails by looping every few seconds
    private void repeatCheckEmail(int timeInMilliseconds, final Voice voice) {
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refreshData(voice);
            }
        };

        handler.postDelayed(runnable, timeInMilliseconds);
    }



    private void showEmptyViews(LinearLayout l, Voice voice) {
        voice.speak("There are no emails to read out. Bye, for now!");

        l.setBackgroundResource(R.color.background);

        TextView textViewFrom = new TextView(getApplicationContext());
        //from.setText(msg.getMsgFrom());
        textViewFrom.setHint("From");
        l.addView(textViewFrom);

        TextView textViewSubject = new TextView(getApplicationContext());
        textViewSubject.setHint("Subject");
        l.addView(textViewSubject);

        TextView textViewBody = new TextView(getApplicationContext());
        textViewBody.setHint("Message");
        l.addView(textViewBody);

        setContentView(l);
    }

    //method to get all the unread emails and return them as an array list
    public ArrayList<TheEmail> getEmails() {
        theEmailArrayList.clear(); //clear arraylist to prevent repeating old messages

        String pop3host = "pop.gmail.com";
        String username = "voiceproject38@gmail.com";
        String password = "Gismima120";

        try {
            //get the session object
            Properties properties = new Properties();
            properties.put("mail.pop3.host", pop3host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
            Store emailStore = emailSession.getStore("pop3s");
            emailStore.connect(pop3host, username, password);

            //create the folder object and open it
            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);


            //4) retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            if (messages.length > 0) {
                //loop and get all the unread messages
                for (int xx = 0; xx < messages.length; xx++) {
                    Message message = messages[xx];

                    //convert the body of the email to text
                    String bodyContent = "";
                    Object content = message.getContent();
                    if (content instanceof String) {
                        bodyContent = (String) content;
                    } else if (content instanceof Multipart) {
                        MimeMultipart mimeMultipart = (MimeMultipart) content;
                        bodyContent = getTextFromMimeMultipart(mimeMultipart);
                    }

                    //get the email from the Address object
                    Address[] froms = message.getFrom();
                    String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
                    //create all the parts that make up the email
                    String msgFrom = email;
                    String msgSubject = message.getSubject();
                    String msgBody = bodyContent;

                    //create new email object
                    TheEmail msg = new TheEmail(msgFrom, msgSubject, msgBody);

                    //add email to the arraylist
                    theEmailArrayList.add(msg);
                }
            }

            //close the store and folder objects
            emailFolder.close(false);
            emailStore.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return the array of emails
        return theEmailArrayList;
    }

    //method to convert multipart email to text to enable reading out
    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
