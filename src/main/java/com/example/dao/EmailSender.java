package com.example.dao;

import com.example.components.Notifier;
import com.example.entity.Cliente;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailSender {
    ClienteDAO clienteDAO = new ClienteDAO();
    javafx.scene.image.ImageView loading;
    public EmailSender(){}

    public void sendEmailAsync(String to, ImageView loading) {
        this.loading = loading;
        Runnable emailTask = () -> {
            sendEmail(to);
        };

        Thread thread = new Thread(emailTask);
        thread.start();
    }

    private void sendEmail(String to){
        String from = "kaiotestes08@gmail.com";

        final String username = "kaiotestes08@gmail.com";//email de testes
        final String password = "qpez hgbd noej trgt";//senha para aplicativos terceiros

        // GMail SMTP server
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "568");
        props.put("mail.smtp.ssl.trust", host);

        // Pega a session do objeto
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Random random = new Random();

            //Parte que cria o codigo aleatorio, com 4 digitos
            int code = random.nextInt(1000,9999);

            String sql = "SELECT id FROM clientes WHERE email = ?";
            Long id = Long.valueOf(clienteDAO.destinoCodigo(to,sql,"id"));
            Cliente cliente = new Cliente();
            cliente.setCode(String.valueOf(code));
            cliente.setId(id);

            clienteDAO.updateCodigo(cliente);

            Message message = new MimeMessage(session);

            // campo de cabeçalho, do destino
            message.setFrom(new InternetAddress(from));

            // Seta quem vai receber
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Cabecalho do texto
            message.setSubject("Codigo de Verificação - DAIVU");

            // Mensagem pra ser eviada
            message.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.=\n" +
                    "w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                    "<html dir=3D\"ltr\" xmlns=3D\"http://www.w3.org/1999/xhtml\" xmlns:o=3D\"urn:sch=\n" +
                    "emas-microsoft-com:office:office\" lang=3D\"en\">\n" +
                    " <head>\n" +
                    "  <meta charset=3D\"UTF-8\">\n" +
                    "  <meta content=3D\"width=3Ddevice-width, initial-scale=3D1\" name=3D\"viewpor=\n" +
                    "t\">\n" +
                    "  <meta name=3D\"x-apple-disable-message-reformatting\">\n" +
                    "  <meta http-equiv=3D\"X-UA-Compatible\" content=3D\"IE=3Dedge\">\n" +
                    "  <meta content=3D\"telephone=3Dno\" name=3D\"format-detection\">\n" +
                    "  <title>Empty template</title>\n" +
                    "  <style type=3D\"text/css\">\n" +
                    "#outlook a {\n" +
                    "=09padding:0;\n" +
                    "}\n" +
                    ".es-button {\n" +
                    "=09mso-style-priority:100!important;\n" +
                    "=09text-decoration:none!important;\n" +
                    "}\n" +
                    "a[x-apple-data-detectors] {\n" +
                    "=09color:inherit!important;\n" +
                    "=09text-decoration:none!important;\n" +
                    "=09font-size:inherit!important;\n" +
                    "=09font-family:inherit!important;\n" +
                    "=09font-weight:inherit!important;\n" +
                    "=09line-height:inherit!important;\n" +
                    "}\n" +
                    ".es-desk-hidden {\n" +
                    "=09display:none;\n" +
                    "=09float:left;\n" +
                    "=09overflow:hidden;\n" +
                    "=09width:0;\n" +
                    "=09max-height:0;\n" +
                    "=09line-height:0;\n" +
                    "=09mso-hide:all;\n" +
                    "}\n" +
                    "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:=\n" +
                    "150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important =\n" +
                    "} h1 { font-size:30px!important; text-align:left } h2 { font-size:24px!impo=\n" +
                    "rtant; text-align:left } h3 { font-size:20px!important; text-align:left } .=\n" +
                    "es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-siz=\n" +
                    "e:30px!important; text-align:left } .es-header-body h2 a, .es-content-body =\n" +
                    "h2 a, .es-footer-body h2 a { font-size:24px!important; text-align:left } .e=\n" +
                    "s-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size=\n" +
                    ":20px!important; text-align:left } .es-menu td a { font-size:14px!important=\n" +
                    " } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-hea=\n" +
                    "der-body a { font-size:14px!important } .es-content-body p, .es-content-bod=\n" +
                    "y ul li, .es-content-body ol li, .es-content-body a { font-size:14px!import=\n" +
                    "ant } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-=\n" +
                    "footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock u=\n" +
                    "l li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[c=\n" +
                    "lass=3D\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1,=\n" +
                    " .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r=\n" +
                    ", .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!importa=\n" +
                    "nt } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-ali=\n" +
                    "gn:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { dis=\n" +
                    "play:inline!important } .es-button-border { display:inline-block!important =\n" +
                    "} a.es-button, button.es-button { font-size:18px!important; display:inline-=\n" +
                    "block!important } .es-adaptive table, .es-left, .es-right { width:100%!impo=\n" +
                    "rtant } .es-content table, .es-header table, .es-footer table, .es-content,=\n" +
                    " .es-footer, .es-header { width:100%!important; max-width:600px!important }=\n" +
                    " .es-adapt-td { display:block!important; width:100%!important } .adapt-img =\n" +
                    "{ width:100%!important; height:auto!important } .es-m-p0 { padding:0px!impo=\n" +
                    "rtant } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:=\n" +
                    "0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding=\n" +
                    "-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobi=\n" +
                    "le-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-d=\n" +
                    "esk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!i=\n" +
                    "mportant; float:none!important; max-height:inherit!important; line-height:i=\n" +
                    "nherit!important } tr.es-desk-hidden { display:table-row!important } table.=\n" +
                    "es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display=\n" +
                    ":table-cell!important } .es-menu td { width:1%!important } table.es-table-n=\n" +
                    "ot-adapt, .esd-block-html table { width:auto!important } table.es-social { =\n" +
                    "display:inline-block!important } table.es-social td { display:inline-block!=\n" +
                    "important } .es-desk-hidden { display:table-row!important; width:auto!impor=\n" +
                    "tant; overflow:visible!important; max-height:inherit!important } .h-auto { =\n" +
                    "height:auto!important } }\n" +
                    "</style>\n" +
                    " </head>\n" +
                    " <body style=3D\"width:100%;font-family:arial, 'helvetica neue', helvetica, =\n" +
                    "sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:=\n" +
                    "0;Margin:0\">\n" +
                    "  <div dir=3D\"ltr\" class=3D\"es-wrapper-color\" lang=3D\"en\" style=3D\"backgrou=\n" +
                    "nd-color:#FFFFFF\">\n" +
                    "   <table class=3D\"es-wrapper\" width=3D\"100%\" cellspacing=3D\"0\" cellpadding=\n" +
                    "=3D\"0\" role=3D\"none\" style=3D\"mso-table-lspace:0pt;mso-table-rspace:0pt;bor=\n" +
                    "der-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;heig=\n" +
                    "ht:100%;background-repeat:repeat;background-position:center top;background-=\n" +
                    "color:#FFFFFF\">\n" +
                    "     <tr>\n" +
                    "      <td valign=3D\"top\" style=3D\"padding:0;Margin:0\">\n" +
                    "       <table cellpadding=3D\"0\" cellspacing=3D\"0\" class=3D\"es-content\" alig=\n" +
                    "n=3D\"center\" role=3D\"none\" style=3D\"mso-table-lspace:0pt;mso-table-rspace:0=\n" +
                    "pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !importan=\n" +
                    "t;width:100%\">\n" +
                    "         <tr>\n" +
                    "          <td align=3D\"center\" style=3D\"padding:0;Margin:0\">\n" +
                    "           <table bgcolor=3D\"#ffffff\" class=3D\"es-content-body\" align=3D\"ce=\n" +
                    "nter\" cellpadding=3D\"0\" cellspacing=3D\"0\" role=3D\"none\" style=3D\"mso-table-=\n" +
                    "lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px=\n" +
                    ";background-color:#FFFFFF;width:320px\">\n" +
                    "             <tr>\n" +
                    "              <td align=3D\"left\" style=3D\"Margin:0;padding-top:15px;padding=\n" +
                    "-bottom:15px;padding-left:20px;padding-right:20px;border-radius:15px;backgr=\n" +
                    "ound-color:#FFFFFF;box-shadow: 0px 13px 30px 5px rgba(0,0,0,0.2);\" bgcolor=\n" +
                    "=3D\"#FFFFFF\">\n" +
                    "               <table cellpadding=3D\"0\" cellspacing=3D\"0\" width=3D\"100%\" ro=\n" +
                    "le=3D\"none\" style=3D\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-colla=\n" +
                    "pse:collapse;border-spacing:0px\">\n" +
                    "                 <tr>\n" +
                    "                  <td align=3D\"center\" valign=3D\"top\" style=3D\"padding:0;Ma=\n" +
                    "rgin:0;width:280px\">\n" +
                    "                   <table cellpadding=3D\"0\" cellspacing=3D\"0\" width=3D\"100%=\n" +
                    "\" role=3D\"presentation\" style=3D\"mso-table-lspace:0pt;mso-table-rspace:0pt;=\n" +
                    "border-collapse:collapse;border-spacing:0px\">\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:20px;Margin:0;f=\n" +
                    "ont-size:0\">\n" +
                    "                       <table border=3D\"0\" width=3D\"100%\" height=3D\"100%\" c=\n" +
                    "ellpadding=3D\"0\" cellspacing=3D\"0\" role=3D\"presentation\" style=3D\"mso-table=\n" +
                    "-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0p=\n" +
                    "x\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=3D\"padding:0;Margin:0;border-bottom:0px=\n" +
                    " solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:0;Margin:0\"><p =\n" +
                    "style=3D\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;m=\n" +
                    "so-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica,=\n" +
                    " sans-serif;line-height:16px;color:#2b3865;font-size:16px\"><strong>Verifica=\n" +
                    "tion Code</strong></p></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:5px;Margin:0;fo=\n" +
                    "nt-size:0\">\n" +
                    "                       <table border=3D\"0\" width=3D\"30%\" height=3D\"100%\" ce=\n" +
                    "llpadding=3D\"0\" cellspacing=3D\"0\" role=3D\"presentation\" style=3D\"mso-table-=\n" +
                    "lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px=\n" +
                    "\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=3D\"padding:0;Margin:0;border-bottom:0px=\n" +
                    " solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:0;Margin:0\"><p =\n" +
                    "style=3D\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;m=\n" +
                    "so-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica,=\n" +
                    " sans-serif;line-height:23px;color:#767e9e;font-size:15px\">Enter this verif=\n" +
                    "ication code on app to change password</p></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:20px;Margin:0;f=\n" +
                    "ont-size:0\">\n" +
                    "                       <table border=3D\"0\" width=3D\"100%\" height=3D\"100%\" c=\n" +
                    "ellpadding=3D\"0\" cellspacing=3D\"0\" role=3D\"presentation\" style=3D\"mso-table=\n" +
                    "-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0p=\n" +
                    "x\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=3D\"padding:0;Margin:0;border-bottom:0px=\n" +
                    " solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:0;Margin:0\"><p =\n" +
                    "style=3D\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;m=\n" +
                    "so-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica,=\n" +
                    " sans-serif;line-height:57px;color:#2b3865;font-size:38px\"><strong>"+code+"</st=\n" +
                    "rong></p></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:15px;Margin:0;f=\n" +
                    "ont-size:0\">\n" +
                    "                       <table border=3D\"0\" width=3D\"100%\" height=3D\"100%\" c=\n" +
                    "ellpadding=3D\"0\" cellspacing=3D\"0\" role=3D\"presentation\" style=3D\"mso-table=\n" +
                    "-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0p=\n" +
                    "x\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=3D\"padding:0;Margin:0;border-bottom:0px=\n" +
                    " solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=3D\"center\" style=3D\"padding:0;Margin:0\"><p =\n" +
                    "style=3D\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;m=\n" +
                    "so-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica,=\n" +
                    " sans-serif;line-height:21px;color:#869aff;font-size:14px\">DAIVU</p></td>\n" +
                    "                     </tr>\n" +
                    "                   </table></td>\n" +
                    "                 </tr>\n" +
                    "               </table></td>\n" +
                    "             </tr>\n" +
                    "           </table></td>\n" +
                    "         </tr>\n" +
                    "       </table></td>\n" +
                    "     </tr>\n" +
                    "   </table>\n" +
                    "  </div>\n" +
                    " </body>\n" +
                    "</html>", "text/html");

            // Envia a mensagem
            Transport.send(message);

            showNotification("Email Enviado!",true);
            loading.setVisible(false);
        } catch (MessagingException e) {
            showNotification("Email Não Pode ser Enviado!",false);
            loading.setVisible(false);
            throw new RuntimeException(e);
        }
    }

    private void showNotification(String message, boolean isSuccess) {
        Platform.runLater(() -> {
            Notifier notifier = new Notifier(message, isSuccess);
            notifier.show();
        });
    }
}