/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mtbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Il bot riceve sia /ciao che ciao
 *
 * @author skela
 */
public class bot extends TelegramLongPollingBot {
private final String mirko_id = "XXXXXXX";
private final String aleks_id = "XXXXXXX";
private boolean isNextNotifyMessage = false;

private final String log_userequest = "../../res/data/log/userequest.log";
private final String help = "../../res/data/help";
private final String oggidir = "../../res/data/oggi/";
private final String fullpng = "../../res/data/full/orario.png";
private final String rec = "../../res/data/rec/rec";
private final String userlist = "../../res/data/start-stop/user_registered";

private SendMessage message;

private List<String> users_id;

public bot() {
        super();
        users_id = new LinkedList();

        FileReader fr;

        try {
                fr = new FileReader(userlist);
                BufferedReader br = new BufferedReader(fr);
                String buf = "";

                while((buf = br.readLine()) != null) {
                        users_id.add(buf);
                }

                br.close();

        } catch (FileNotFoundException ex) {
                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
        }

}

private void log(String path, CharSequence data) {
        FileWriter fw;
        try {
                fw = new FileWriter(path, true);
                fw.write(data + "\n");
                fw.close();
        } catch (IOException ex) {
                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
        }
}

private void sendImageUploadingAFile(String filePath, String chatId) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId(chatId);
        // Set the photo file as a new photo (You can also use InputStream with a method overload)
        sendPhotoRequest.setNewPhoto(new File(filePath));
        try {
                // Execute the method
                sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
                e.printStackTrace();
        }
}

@Override
public String getBotToken() {
        return "YOUR_API_TOKEN_HERE";
}

@Override
public void onUpdateReceived(Update update){
        // We check if the update has a message and the message has text
        if ((update.hasMessage() && update.getMessage().hasText())) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
                this.log(log_userequest, "[" + sdf.format(new Date()) + "] " + update.getMessage().getChat().toString() + " " + update.getMessage().getCaption() + " " + update.getMessage().getText());

                if(!isNextNotifyMessage || ((!(update.getMessage().getChatId().toString()).equals(mirko_id)) && (!(update.getMessage().getChatId().toString()).equals(aleks_id)))) {
                        if(("/help".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/help" + "@" + this.getBotUsername()))) {
                                //read from file
                                FileReader helpfile;
                                try {
                                        helpfile = new FileReader(help);
                                        BufferedReader br = new BufferedReader(helpfile);
                                        String stringmessage = "", buf;


                                        while((buf = br.readLine()) != null) {
                                                stringmessage = stringmessage + buf + "\n";
                                        }

                                        br.close();

                                        message = new SendMessage()
                                                  .setChatId((update.getMessage().getChatId()))
                                                  .setText(stringmessage);
                                } catch (IOException ex) {

                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                try {
                                        sendMessage(message); // Call method to send the message
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        } else if(("/oggi".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/oggi" + "@" + this.getBotUsername()))) {
                                Calendar c = Calendar.getInstance();
                                switch(c.get(Calendar.DAY_OF_WEEK)) {
                                case 1: //domenica
                                        message = new SendMessage()
                                                  .setChatId(((update.getMessage().getChatId())))
                                                  .setText("Oggi è Domenica, dovresti essere a Messa a fare il bravo bambino :'(");
                                        break;

                                case 7: //sabato
                                        message = new SendMessage()
                                                  .setChatId(((update.getMessage().getChatId())))
                                                  .setText("Oggi è Sabato!");

                                        break;

                                default:
                                        FileReader file;
                                        try {
                                                file = new FileReader(oggidir + c.get(Calendar.DAY_OF_WEEK));
                                                BufferedReader br = new BufferedReader(file);
                                                String stringmessage = "", buf;

                                                while((buf = br.readLine()) != null) {
                                                        stringmessage = stringmessage + buf + "\n";
                                                }

                                                br.close();

                                                message = new SendMessage()
                                                          .setChatId((update.getMessage().getChatId()))
                                                          .setText(stringmessage);

                                        } catch (IOException ex) {
                                                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        break;
                                }

                                try {
                                        sendMessage(message);
                                        //log operation
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        } else if(("/dmn".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/dmn" + "@" + this.getBotUsername()))) {
                                Calendar c = Calendar.getInstance();
                                switch((c.get(Calendar.DAY_OF_WEEK)+1)) {
                                case 1: //domenica
                                case 8:
                                        message = new SendMessage()
                                                  .setChatId(((update.getMessage().getChatId())))
                                                  .setText("Oggi è Domenica, dovresti essere a Messa a fare il bravo bambino :'(");
                                        break;

                                case 7: //sabato
                                        message = new SendMessage()
                                                  .setChatId(((update.getMessage().getChatId())))
                                                  .setText("Oggi è Sabato!");

                                        break;

                                default:
                                        FileReader file;
                                        try {
                                                file = new FileReader(oggidir + (c.get(Calendar.DAY_OF_WEEK)+1));
                                                BufferedReader br = new BufferedReader(file);
                                                String stringmessage = "", buf;

                                                while((buf = br.readLine()) != null) {
                                                        stringmessage = stringmessage + buf + "\n";
                                                }

                                                br.close();

                                                message = new SendMessage()
                                                          .setChatId((update.getMessage().getChatId()))
                                                          .setText(stringmessage);

                                        } catch (IOException ex) {
                                                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        break;
                                }

                                try {
                                        sendMessage(message);
                                        //log operation
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }

                        } else if(("/full".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/full" + "@" + this.getBotUsername()))) {
                                this.sendImageUploadingAFile(fullpng, update.getMessage().getChatId().toString());

                        } else if(("/rec".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/rec" + "@" + this.getBotUsername()))) {
                                FileReader file;
                                try {
                                        file = new FileReader(rec);
                                        BufferedReader br = new BufferedReader(file);
                                        String stringmessage = "", buf;


                                        while((buf = br.readLine()) != null) {
                                                stringmessage = stringmessage + buf + "\n";
                                        }

                                        br.close();

                                        message = new SendMessage()
                                                  .setChatId((update.getMessage().getChatId()))
                                                  .setText(stringmessage);
                                } catch (IOException ex) {

                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                try {
                                        sendMessage(message); // Call method to send the message
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        } else if(("/start".contains(update.getMessage().getText())) || (update.getMessage().getText().equals("/start" + "@" + this.getBotUsername()))) {
                                String buf = "Sei già registrato al servizio di notifica.";

                                if(!users_id.contains(update.getMessage().getChatId().toString())) {
                                        buf = "Aggiunto al servizio di notifica info varie.\nAdesso puoi iniziare ad usare MTbot :D";
                                        users_id.add(update.getMessage().getChatId().toString());
                                        try {
                                                FileWriter fw = new FileWriter(userlist, true);
                                                fw.write(update.getMessage().getChatId().toString() + "\n");
                                                fw.close();

                                        } catch (IOException ex) {
                                                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                }
                                message = new SendMessage()
                                          .setChatId((update.getMessage().getChatId()))
                                          .setText(buf);
                                try {
                                        sendMessage(message); // Call method to send the message
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }

                        } else if(("/end".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/end" + "@" + this.getBotUsername()))) {
                                String buf = "Non hai attivato il servizio di notifica.\nErgo, prima di fermare il servizio, dovresti prima attivarlo ;)";

                                if(users_id.contains(update.getMessage().getChatId().toString())) {
                                        buf = "Hai disattivato il servizio di notifica.\nPer riattivarlo /start, altrimenti sei un brutto asociale.";
                                        users_id.remove(update.getMessage().getChatId().toString());
                                        try {
                                                FileWriter fw = new FileWriter(userlist, false);
                                                String a;
                                                for(Iterator<String> iter = users_id.listIterator(); iter.hasNext(); ) {
                                                        a = iter.next();
                                                        fw.write(a + "\n");
                                                }
                                                fw.close();
                                        } catch (IOException ex) {
                                                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                        }


                                }

                                message = new SendMessage()
                                          .setChatId((update.getMessage().getChatId()))
                                          .setText(buf);

                                try {
                                        sendMessage(message); // Call method to send the message
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }


                        } else if(("/notify".equals(update.getMessage().getText())) || (update.getMessage().getText().equals("/notify" + "@" + this.getBotUsername()))) {
                                if((update.getMessage().getChatId().toString().equals(mirko_id)) || (update.getMessage().getChatId().toString().equals(aleks_id))) {
                                        message = new SendMessage()
                                                  .setChatId((update.getMessage().getChatId()))
                                                  .setText("Invia adesso il messaggio da notificare al resto dell'umanità");

                                        try {
                                                sendMessage(message); // Call method to send the message
                                                isNextNotifyMessage = true;
                                        } catch (TelegramApiException ex) {
                                                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                } else {
                                        message = new SendMessage()
                                                  .setChatId((update.getMessage().getChatId()))
                                                  .setText("Non sei abilitato a questa funzione\nSto chiamando la Postale..");
                                        try {
                                                sendMessage(message); // Call method to send the message
                                        } catch (TelegramApiException ex) {
                                                Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                }
                        }
                } else {

                        message = new SendMessage();

                        for(Iterator<String> iter = users_id.listIterator(); iter.hasNext(); ) {
                                message.setChatId(iter.next());
                                message.setText((update.getMessage().getText()));
                                try {
                                        sendMessage(message); // Call method to send the message
                                } catch (TelegramApiException ex) {
                                        Logger.getLogger(bot.class.getName()).log(Level.SEVERE, null, ex);
                                }

                        }

                        isNextNotifyMessage = false;

                }

        }
}

@Override
public String getBotUsername() {
        return "MirkoToroBot";
}

}
