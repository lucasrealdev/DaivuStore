package com.example.sprintjava;

import com.example.components.Notifier;
import com.example.components.VisiblePasswordFieldSkin;
import com.example.dao.ClienteDAO;
import com.example.dao.EmailSender;
import com.example.dao.GoogleAuth;
import com.example.entity.Cliente;
import com.example.entity.IdUser;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class HelloController implements Serializable{
    @FXML
    private Rectangle rec1;
    @FXML
    private Rectangle rec2;
    @FXML
    private Rectangle rec3;
    @FXML
    private Button buttonGoogle;
    @FXML
    private Button signUpButton;
    @FXML
    private ImageView imageViewer;
    @FXML
    private Button switchLayoutButton;
    @FXML
    private TextField tfEmailLogin;
    @FXML
    private PasswordField tfPasswordLogin;
    @FXML
    private TextField tfEmailRegister;
    @FXML
    private PasswordField tfPasswordRegister;
    @FXML
    private TextField tfUsernameRegister;
    @FXML
    private Button eyeRegister;
    @FXML
    private Button eyeLogin;
    @FXML
    private CheckBox rememberMe;
    @FXML
    private Label code_email_label;
    @FXML
    private TextField code_tf;
    @FXML
    private TextField password;
    @FXML
    private Pane popup_code;
    @FXML
    private Pane popup_password;
    @FXML
    private ImageView loading_code;

    private final EmailSender emailSender = new EmailSender();
    private final String[] imagePaths = {"/imgs/perfume1.png", "/imgs/perfume2.png", "/imgs/perfume3.png"};
    private int currentImageIndex = 0;
    private FadeTransition fadeTransition;
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private double xOffset = 0;
    private double yOffset = 0;

    public HelloController() {
    }

    @FXML
    private void initialize() {
        configureFadeTransition();
        configureTimeline();
        configureEyeIcons();
        runLaterTasks();
    }

    private void configureFadeTransition() {
        fadeTransition = new FadeTransition(Duration.seconds(2), imageViewer);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
    }

    private void configureTimeline() {
        Duration duration = Duration.seconds(4);
        KeyFrame keyFrame = new KeyFrame(duration, e -> changeImage());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void configureEyeIcons() {
        if (eyeLogin != null) {
            setEyeGraphic(eyeLogin, tfPasswordLogin);
        } else if (eyeRegister != null) {
            setEyeGraphic(eyeRegister, tfPasswordRegister);
        }
    }

    private void setEyeGraphic(Button eyeButton, PasswordField passwordField) {
        eyeButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/imgs/closeEye.png")).toExternalForm())));
        VisiblePasswordFieldSkin skin = new VisiblePasswordFieldSkin(passwordField, eyeButton);
        passwordField.setSkin(skin);
    }

    private void runLaterTasks() {
        Platform.runLater(() -> {
            carregarLog();
            applyCodeTextFieldFormatter();
        });
    }

    private void applyCodeTextFieldFormatter() {
        if (code_tf != null) {
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                if (Pattern.matches("[0-9]*", newText) && newText.length() <= 4) {
                    return change;
                }
                return null;
            };
            TextFormatter<Integer> textFormatter = new TextFormatter<>(new IntegerStringConverter(), null, filter);
            code_tf.setTextFormatter(textFormatter);
        }
    }


    private final FXMLLoader fxmlLoaderL = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
    private final FXMLLoader fxmlLoaderR = new FXMLLoader(HelloApplication.class.getResource("registerScreen.fxml"));
    private final FXMLLoader fxmlLoaderMainAdm = new FXMLLoader(HelloApplication.class.getResource("mainAdm.fxml"));
    private final FXMLLoader fxmlLoaderMainClient = new FXMLLoader(HelloApplication.class.getResource("mainClient.fxml"));
    private final FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loading.fxml"));
    @FXML
    void closePopup() {
        popup_code.setVisible(false);
    }
    @FXML
    void closePopupPassword() {
        popup_password.setVisible(false);
    }

    @FXML
    void googleRegister() {
        try {
            Stage stage2 = (Stage) switchLayoutButton.getScene().getWindow();
            String[] credentials = GoogleAuth.getCredentials();
            closeAuthenticationTab(stage2, tfEmailRegister);
            register(credentials[0], credentials[1], credentials[2], true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void googleLogin() {
        try {
            Stage stage2 = (Stage) switchLayoutButton.getScene().getWindow();
            String[] credentials = GoogleAuth.getCredentials();
            closeAuthenticationTab(stage2, tfEmailLogin);
            login(credentials[1], credentials[2], true).thenAccept(loginResult -> {
                if (!loginResult) {
                    showNotification("Você ainda não cadastrou essa conta!", false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void LoginClicked() {
        String email = tfEmailLogin.getText();
        String senha = tfPasswordLogin.getText();
        login(email, senha, false);
    }

    @FXML
    void RegisterClicked() {
        String nomeDeUsuario = tfUsernameRegister.getText();
        String email = tfEmailRegister.getText();
        String senha = tfPasswordRegister.getText();

        if (nomeDeUsuario.isBlank() || email.isBlank() || senha.isBlank()) {
            showNotification("Nome de usuário, email e senha são obrigatórios.", false);
            return;
        }

        if (!isValidEmail(email)) {
            showNotification("Email inválido. Digite um email válido.", false);
            return;
        }

        if (isStrongPassword(senha)) {
            showNotification("Senha fraca. A senha deve incluir 8 caracteres, 1 letra maiuscula e um caracter especial.", false);
            return;
        }

        if (nomeDeUsuario.length() <= 7){
            showNotification("Seu nome de usuario deve ter pelomenos 8 caracteres.", false);
            return;
        }

        register(nomeDeUsuario, email, senha, false);
    }

    private CompletableFuture<Boolean> login(String email, String senha, boolean isGoogle) {
        CompletableFuture<Boolean> loginResult = new CompletableFuture<>();

        Stage stage = (Stage) switchLayoutButton.getScene().getWindow();
        AtomicBoolean navigationDone = new AtomicBoolean(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            load(stage, fxmlLoader);
            Integer loginSuccessId = clienteDAO.verificarLogin(email, senha);
            Platform.runLater(() -> handleLoginResult(loginSuccessId, isGoogle, stage, navigationDone, loginResult));
        });

        executor.shutdown();
        return loginResult;
    }

    private void handleLoginResult(Integer loginSuccessId, boolean isGoogle, Stage stage, AtomicBoolean navigationDone, CompletableFuture<Boolean> loginResult) {
        if (!navigationDone.get()) {
            if (loginSuccessId != null) {
                boolean isAdm = clienteDAO.verificarAdm(loginSuccessId);
                String notificationMessage = isAdm ? "Login como Administrador bem-sucedido." : "Login bem-sucedido.";

                if (isAdm) {
                    load(stage, fxmlLoaderMainAdm);
                } else {
                    load(stage, fxmlLoaderMainClient);
                }
                IdUser.setIduser(loginSuccessId);

                if (rememberMe != null && rememberMe.isSelected()) {
                    salvarLog(clienteDAO.get(Long.valueOf(loginSuccessId)));
                }
                loginResult.complete(true);
                showNotification(notificationMessage, true);
            } else {
                if (!isGoogle) {
                    showNotification("Login falhou.", false);
                }
                load(stage, fxmlLoaderL);
                loginResult.complete(false);
            }
            navigationDone.set(true);
        }
    }


    private void register(String nomeDeUsuario, String email, String senha, boolean isGoogle) {
        CompletableFuture<Boolean> registrationResult = new CompletableFuture<>();

        Stage stage = (Stage) switchLayoutButton.getScene().getWindow();
        AtomicBoolean navigationDone = new AtomicBoolean(false);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            load(stage, fxmlLoader);
            boolean emailRepetido = clienteDAO.verificarEmailExistente(email);
            Platform.runLater(() -> handleRegistration(emailRepetido, nomeDeUsuario, email, senha, isGoogle, stage, navigationDone, registrationResult));
        });

        executor.shutdown();
    }

    private void handleRegistration(boolean emailRepetido, String nomeDeUsuario, String email, String senha, boolean isGoogle, Stage stage, AtomicBoolean navigationDone, CompletableFuture<Boolean> registrationResult) {
        if (!navigationDone.get()) {
            if (emailRepetido) {
                String notificationMessage = isGoogle ? "Conta Já Cadastrada" : "Email já está em uso. Escolha outro email.";
                showNotification(notificationMessage, false);
                navigationDone.set(true);
                load(stage, fxmlLoaderL);
                registrationResult.complete(false);
            } else {
                int insercaoSucesso = clienteDAO.save(new Cliente(nomeDeUsuario, email, senha));
                String notificationMessage = (insercaoSucesso == 1) ? "Cadastro bem-sucedido" : "Erro ao cadastrar o cliente";
                showNotification(notificationMessage, insercaoSucesso == 1);
                load(stage, fxmlLoaderL);
                registrationResult.complete(insercaoSucesso == 1);
                navigationDone.set(true);
            }
        }
    }

    @FXML
    void forgotPassowordAction() {
        loading_code.setVisible(true);
        sendCode();
    }

    @FXML
    void resend_code(){
        sendCode();
        loading_code.setVisible(true);
    }

    @FXML
    void confirm_password(){
        String senha = password.getText();
        if (senha.isBlank()){
            showNotification("A senha não pode estar vazia.", false);
            return;
        }
        if (isStrongPassword(senha)) {
            showNotification("Senha fraca. A senha deve incluir 8 caracteres, 1 letra maiuscula e um caracter especial.", false);
            return;
        }

        String sql = "SELECT id FROM clientes WHERE email = ?";
        Long id = Long.valueOf(clienteDAO.destinoCodigo(tfEmailLogin.getText(),sql,"id"));
        Cliente cliente = clienteDAO.get(id);
        cliente.setSenha(senha);
        clienteDAO.update(cliente, null);
        popup_password.setVisible(false);
        showNotification("A sua senha foi alterada com sucesso!", true);
    }

    @FXML
    void confirm_code(){
        String sql = "SELECT id FROM clientes WHERE email = ?";
        Long id = Long.valueOf(clienteDAO.destinoCodigo(tfEmailLogin.getText(),sql,"id"));
        Cliente cliente = clienteDAO.get(id);

        if (code_tf.getText().equals(cliente.getCode())){
            showNotification("Codigo Confirmado com Sucesso!",true);
            popup_code.setVisible(false);
            popup_password.setVisible(true);
        }
        else{
            showNotification("Codigo Invalido!",false);
        }
    }

    private void sendCode(){
        String to = tfEmailLogin.getText(); //Destino do Email
        if (clienteDAO.verificarEmailExistente(to)){
            emailSender.sendEmailAsync(to, loading_code);
            code_tf.setText("");
            code_email_label.setText("Please enter the code we sent to the email " + to);
            popup_code.setVisible(true);
        }else if(tfEmailLogin.getText().isEmpty()){
            showNotification("Digite o Email Cadastrado No Login",false);
        } else{
            showNotification("Email inexistente, Verifique o Email", false);
        }
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@([\\w-]+\\.)+[A-Za-z]{2,4}$");
    }

    private boolean isStrongPassword(String senha) {
        if (senha.length() < 8) {
            return false; // Senha muito curta
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : senha.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                // Verifica se o caractere é um caracter especial
                hasSpecialChar = !Character.isLetterOrDigit(c);
            }
        }

        // Verifica se todos os critérios foram atendidos
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    @FXML
    void mouseEntered(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        sourceNode.setStyle("-fx-text-fill: #027fe9;");
    }

    @FXML
    void mouseExited(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        sourceNode.setStyle("-fx-text-fill:" + sourceNode.getId() + ";");
    }

    @FXML
    void ButtonGoogle() {
        buttonGoogle.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1.03;
            -fx-scale-Y: 1.03;
        """);
    }

    @FXML
    void changeToRegister() {
        Stage stage = (Stage) switchLayoutButton.getScene().getWindow();
        load(stage, fxmlLoaderR);
    }

    @FXML
    void changeToLogin() {
        Stage stage = (Stage) switchLayoutButton.getScene().getWindow();
        load(stage, fxmlLoaderL);
    }

    @FXML
    void ButtonGoogleExited() {
        buttonGoogle.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1;
            -fx-scale-Y: 1;
        """);
    }

    @FXML
    void signUpButton() {
        signUpButton.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1.03;
            -fx-scale-Y: 1.03;
        """);
    }

    @FXML
    void signUpButtonExited() {
        signUpButton.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1;
            -fx-scale-Y: 1;
        """);
    }

    @FXML
    void closeClicked() {
        Platform.exit();
        System.exit(0);
    }

    private void changeImage() {
        if (imageViewer != null) {
            currentImageIndex = (currentImageIndex + 1) % imagePaths.length;
            Image newImage = new Image(Objects.requireNonNull(getClass().getResource(imagePaths[currentImageIndex])).toExternalForm());

            imageViewer.setImage(newImage);
            imageViewer.setOpacity(0.0);

            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);

            fadeTransition.playFromStart();

            switch (currentImageIndex) {
                case 0 -> {
                    rec1.setFill(Color.WHITE);
                    rec2.setFill(Color.rgb(194, 194, 199));
                    rec3.setFill(Color.rgb(194, 194, 199));

                    rec1.setWidth(36);
                    rec2.setWidth(22);
                    rec3.setWidth(22);

                    rec1.setLayoutX(215);
                    rec2.setLayoutX(255);
                    rec3.setLayoutX(281);

                    imageViewer.setFitHeight(437);
                    imageViewer.setFitWidth(437);
                    imageViewer.setY(0);
                }
                case 1 -> {
                    rec1.setFill(Color.rgb(194, 194, 199));
                    rec2.setFill(Color.WHITE);
                    rec3.setFill(Color.rgb(194, 194, 199));

                    rec1.setWidth(22);
                    rec2.setWidth(36);
                    rec3.setWidth(22);

                    rec1.setLayoutX(215);
                    rec2.setLayoutX(241);
                    rec3.setLayoutX(281);

                    imageViewer.setFitHeight(437);
                    imageViewer.setFitWidth(437);
                    imageViewer.setY(0);
                }
                case 2 -> {
                    rec1.setFill(Color.rgb(194, 194, 199));
                    rec2.setFill(Color.rgb(194, 194, 199));
                    rec3.setFill(Color.WHITE);

                    rec1.setWidth(22);
                    rec2.setWidth(22);
                    rec3.setWidth(36);

                    rec1.setLayoutX(215);
                    rec2.setLayoutX(241);
                    rec3.setLayoutX(267);

                    imageViewer.setFitHeight(585);
                    imageViewer.setFitWidth(437);
                    imageViewer.setY(-70);
                }
            }
        }
    }

    private void load(Stage stage2, FXMLLoader fxmlLoader){
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {
                if (event.getSceneY() <= 100) {
                    stage2.setX(event.getScreenX() - xOffset);
                    stage2.setY(event.getScreenY() - yOffset);
                }
            });
            if (stage2 != null) {
                stage2.setScene(scene);
                stage2.setResizable(false);
                scene.setFill(Color.TRANSPARENT);
                stage2.initStyle(StageStyle.TRANSPARENT);
                stage2.show();
            }
        } catch (IllegalStateException ignore){}
        catch (Exception e) {e.printStackTrace();}
    }
    @FXML
    private void productClicked(){
        abrirLinkNoNavegador("https://daivu.netlify.app/#produtos");
    }
    @FXML
    private void storeClicked(){
        abrirLinkNoNavegador("https://daivu.netlify.app");
    }
    @FXML
    private void aboutClicked(){
        abrirLinkNoNavegador("https://daivu.netlify.app/#sobre");
    }


    private void abrirLinkNoNavegador(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            // Lidar com erros, como a falta de suporte ao Desktop
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao abrir o link");
            alert.setHeaderText(null);
            alert.setContentText("Ocorreu um erro ao tentar abrir o link.");

            alert.showAndWait();
        }
    }

    private void showNotification(String message, boolean isSuccess) {
        Notifier notifier = new Notifier(message, isSuccess);
        notifier.show();
    }
    private void closeAuthenticationTab(Stage stage2, TextField textField) {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_W);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_W);
            stage2.setAlwaysOnTop(true);
            stage2.setAlwaysOnTop(false);
            textField.requestFocus();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    private void salvarLog(Cliente cliente) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("logAccount.ser"))) {
            out.writeObject(cliente);
            System.out.println("Lista de produtos salva com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void carregarLog() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("logAccount.ser"))) {
            Cliente cliente = (Cliente) in.readObject();
            if (!cliente.getEmail().isBlank()){
                boolean isAdm = clienteDAO.verificarAdm(Math.toIntExact(cliente.getId()));
                Stage stage2 = (Stage) switchLayoutButton.getScene().getWindow();
                if (isAdm) {
                    showNotification("Login como Administrador bem Sucedido.", true);
                    load(stage2, fxmlLoaderMainAdm);
                    IdUser.setIduser(Math.toIntExact(cliente.getId()));
                } else {
                    load(stage2, fxmlLoaderMainClient);
                    showNotification("Login bem Sucedido.", true);
                    IdUser.setIduser(Math.toIntExact(cliente.getId()));
                }
            }
        } catch (FileNotFoundException ignore){}
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}