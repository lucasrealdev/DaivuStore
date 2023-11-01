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
        Runnable emailTask = () -> sendEmail(to);

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
        props.put("mail.smtp.port", "587");
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
            Cliente cliente = clienteDAO.get(id);
            cliente.setCode(String.valueOf(code));
            cliente.setId(id);

            clienteDAO.update(cliente, null);

            Message message = new MimeMessage(session);

            // campo de cabeçalho, do destino
            message.setFrom(new InternetAddress(from));

            // Seta quem vai receber
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Cabecalho do texto
            message.setSubject("Codigo de Verificação - DAIVU");

            // Mensagem pra ser eviada
            message.setContent("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                    "<html dir=\"ltr\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\">\n" +
                    " <head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                    "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                    "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                    "  <title>Empty template</title>\n" +
                    "  <style type=\"text/css\">\n" +
                    "#outlook a {\n" +
                    "\tpadding:0;\n" +
                    "}\n" +
                    ".es-button {\n" +
                    "\tmso-style-priority:100!important;\n" +
                    "\ttext-decoration:none!important;\n" +
                    "}\n" +
                    "a[x-apple-data-detectors] {\n" +
                    "\tcolor:inherit!important;\n" +
                    "\ttext-decoration:none!important;\n" +
                    "\tfont-size:inherit!important;\n" +
                    "\tfont-family:inherit!important;\n" +
                    "\tfont-weight:inherit!important;\n" +
                    "\tline-height:inherit!important;\n" +
                    "}\n" +
                    ".es-desk-hidden {\n" +
                    "\tdisplay:none;\n" +
                    "\tfloat:left;\n" +
                    "\toverflow:hidden;\n" +
                    "\twidth:0;\n" +
                    "\tmax-height:0;\n" +
                    "\tline-height:0;\n" +
                    "\tmso-hide:all;\n" +
                    "}\n" +
                    "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:30px!important; text-align:left } h2 { font-size:24px!important; text-align:left } h3 { font-size:20px!important; text-align:left } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:30px!important; text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:24px!important; text-align:left } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important; text-align:left } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } a.es-button, button.es-button { font-size:18px!important; display:inline-block!important } .es-adaptive table, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; max-height:inherit!important } .h-auto { height:auto!important } }\n" +
                    "</style>\n" +
                    " </head>\n" +
                    " <body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\n" +
                    "  <div dir=\"ltr\" class=\"es-wrapper-color\" lang=\"en\" style=\"background-color:#FFFFFF\">\n" +
                    "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FFFFFF\">\n" +
                    "     <tr>\n" +
                    "      <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                    "       <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
                    "         <tr>\n" +
                    "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                    "           <table bgcolor=\"#ffffff\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:320px\">\n" +
                    "             <tr>\n" +
                    "              <td align=\"left\" style=\"Margin:0;padding-top:15px;padding-bottom:15px;padding-left:20px;padding-right:20px;border-radius:15px;background-color:#FFFFFF;box-shadow: 0px 13px 30px 5px rgba(0,0,0,0.2);\" bgcolor=\"#FFFFFF\">\n" +
                    "               <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"none\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                 <tr>\n" +
                    "                  <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:280px\">\n" +
                    "                   <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:20px;Margin:0;font-size:0\">\n" +
                    "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=\"padding:0;Margin:0;border-bottom:0px solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:16px;color:#2b3865;font-size:16px\"><strong>Verification Code</strong></p></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:5px;Margin:0;font-size:0\">\n" +
                    "                       <table border=\"0\" width=\"30%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=\"padding:0;Margin:0;border-bottom:0px solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:23px;color:#767e9e;font-size:15px\">Enter this verification code on app to change password</p></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:20px;Margin:0;font-size:0\">\n" +
                    "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=\"padding:0;Margin:0;border-bottom:0px solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:57px;color:#2b3865;font-size:38px\"><strong>"+code+"</strong></p></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:15px;Margin:0;font-size:0\">\n" +
                    "                       <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                    "                         <tr>\n" +
                    "                          <td style=\"padding:0;Margin:0;border-bottom:0px solid #cccccc;background:unset;height:1px;width:100%;margin:0px\"></td>\n" +
                    "                         </tr>\n" +
                    "                       </table></td>\n" +
                    "                     </tr>\n" +
                    "                     <tr>\n" +
                    "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#869aff;font-size:14px\">DAIVU</p></td>\n" +
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