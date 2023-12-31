package com.example.sprintjava;

import com.example.components.Notifier;
import com.example.dao.ClienteDAO;
import com.example.dao.ProdutoDAO;
import com.example.entity.Cliente;
import com.example.entity.IdUser;
import com.example.entity.Produto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainAdmController {
    @FXML
    private FlowPane containerP;
    @FXML
    private FlowPane containerC;
    @FXML
    private TextField tfDescricaoP;
    @FXML
    private TextField tfLinkP;
    @FXML
    private TextField tfMarcaP;
    @FXML
    private TextField tfPrecoP;
    @FXML
    private TextField tfQuantidadeP;
    @FXML
    private Pane root;
    @FXML
    private Rectangle rectangle;
    @FXML
    private Pane accountPane;
    @FXML
    private Label email;
    @FXML
    private Label username;
    @FXML
    private Label labelProduct;
    @FXML
    private AnchorPane ap;

    private final ProdutoDAO productDAO = new ProdutoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isAlterActive = false;
    private long id;

    @FXML
    private void initialize() {
        if (root != null) {
            loadPage("mainAdmPanel", 160, 20);
        }
        Platform.runLater(() -> {
            loadData();
            if (ap != null) {
                Scene scene = ap.getScene();
                scene.setOnMouseClicked((MouseEvent event) -> {
                    Double xScene = event.getX();
                    Double yScene = event.getY();

                    if (xScene <= 81 || xScene >= 281 || yScene <= 500 || yScene >= 580 && accountPane.isVisible()){
                        accountPane.setVisible(false);
                    }
                });
            }
        });
    }

    @FXML
    void closeClicked() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void homeClickedAdm() {
        loadPage("mainClientPanel", 160, 20);
        setPosition(12, 31);
    }

    @FXML
    void perfumClickedAdm() {
        loadPage("manageProductsPanel", 10, 10);
        setPosition(12, 97);
    }

    @FXML
    void clientsClickedAdm() {
        loadPage("manageClientsPanel", 30, 25);
        setPosition(12, 159);
    }

    private void clearProductFields() {
        tfDescricaoP.clear();
        tfLinkP.clear();
        tfMarcaP.clear();
        tfPrecoP.clear();
        tfQuantidadeP.clear();
    }

    @FXML
    void createClicked() {
        String descricao = tfDescricaoP.getText();
        String link = tfLinkP.getText();
        String marca = tfMarcaP.getText();
        String precoStr = tfPrecoP.getText();
        String mlStr = tfQuantidadeP.getText();
        System.out.println(descricao);
        if (descricao.isEmpty()) {
            showNotification("A descrição não pode estar em branco.", false);
            return;
        }

        if (link.isEmpty()) {
            showNotification("O link não pode estar em branco.", false);
            return;
        }

        if (marca.isEmpty()) {
            showNotification("A marca não pode estar em branco.", false);
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            showNotification("O preço deve ser um número válido.", false);
            return;
        }

        try {
            mlStr += "ml";
        } catch (NumberFormatException e) {
            showNotification("A quantidade(ml) deve ser um número inteiro válido.", false);
            return;
        }

        try {
            new Image(link);
        } catch (Exception e){
            showNotification("Link da imagem invalido", false);
            return;
        }

        if (isAlterActive) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Deseja Alterar?");
            confirmationDialog.setHeaderText(null);
            confirmationDialog.setContentText("Você tem certeza que deseja alterar esse item do sistema?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                Produto novoProduto = new Produto(marca, descricao, mlStr, preco, link);
                novoProduto.setId(id);
                boolean resultado = productDAO.update(novoProduto, null);
                if (resultado){
                    loadProductsCard();
                    showNotification("Produto atualizado com sucesso!", true);
                }
                else{
                    showNotification("Produto nao foi atualizado, erro no banco de dados!", false);
                }
            }
            labelProduct.setText("Create Product");
            isAlterActive = false;
            clearProductFields();
            return;
        }

        Produto novoProduto = new Produto(marca, descricao, mlStr, preco, link);
        int resultado = productDAO.save(novoProduto);
        if (resultado == 1){
            loadProductsCard();
            showNotification("Produto cadastrado com sucesso!", true);
            clearProductFields();
        }
        else{
            showNotification("Produto nao foi cadastrado, erro no banco de dados!", false);
        }
    }

    @FXML
    void mouseEntered(MouseEvent event) {
        scaleNode(event.getSource(), 1.03);
    }

    @FXML
    void mouseExited(MouseEvent event) {
        scaleNode(event.getSource(), 1.0);
    }

    private void scaleNode(Object node, double scaleFactor) {
        Node sourceNode = (Node) node;
        sourceNode.setStyle(
                "-fx-transition: -fx-background-color 1s ease, -fx-scale-x 1s ease, -fx-scale-y 1s ease; " +
                        "-fx-scale-x: " + scaleFactor + "; -fx-scale-Y: " + scaleFactor + ";"
        );
    }

    private void loadData() {
        loadClientsCard();
        loadProductsCard();
    }

    private void loadClientsCard() {
        if (containerC != null) {
            List<Cliente> clientsList = clienteDAO.getAllClients();
            containerC.getChildren().clear();
            clientsList.forEach(this::createCardC);
        }
    }

    private void loadProductsCard() {
        if (containerP != null) {
            containerP.setAlignment(Pos.TOP_CENTER);
            List<Produto> productsList = productDAO.getAllProdutos();
            containerP.getChildren().clear();
            productsList.forEach(this::createCardP);
        }
    }

    private void createCardC(Cliente cliente) {
        if (containerC != null) {
            AnchorPane anchorPane = createAnchorPane(812, 84);

            TextField nomeCliente = createTextField(cliente.getNomeDeUsuario(), 90, 9, "bold 15.5px sans-serif");

            TextField emailCliente = createTextField(cliente.getEmail(), 92, 32, "normal 15.5px sans-serif");

            Label idCliente = createLabel("N° " + cliente.getId(), 102, 58, "normal 15.5px sans-serif");


            String string = (cliente.isAdm() == 0) ? "user" : "admin";
            Label eAdmin = createLabel(string, 357, 30, "bold 15.5px sans-serif");

            Button buttonAlter = createButton(650, 23, "Alterar", "buttonSign");

            Button buttonDelete = createButton(485, 24, "Deletar", "buttonSign");
            buttonDelete.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #AE2626 0%, rgba(241,24,24, 0.93) 100%); -fx-font: bold 16px sans-serif;");

            ImageView imgChave = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/chave.png"))));
            imgChave.setLayoutX(313);
            imgChave.setLayoutY(23);

            ImageView imgPerfil = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/imgs/people2.png"))));
            imgPerfil.setLayoutX(13);
            imgPerfil.setLayoutY(6);

            buttonAlter.setOnMouseClicked(e -> alterClient(emailCliente, nomeCliente, buttonAlter, buttonDelete, cliente.getId()));
            buttonDelete.setOnMouseClicked(event -> deleteCliente(cliente, emailCliente, nomeCliente, buttonDelete, buttonAlter));

            anchorPane.setOnMouseEntered(event -> {
                anchorPane.setStyle("-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #b9babd");
            });
            anchorPane.setOnMouseExited(event -> {
                anchorPane.setStyle("-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: transparent");
            });

            anchorPane.getChildren().addAll(nomeCliente, emailCliente, idCliente, eAdmin, buttonAlter, buttonDelete, imgChave, imgPerfil);
            containerC.getChildren().add(anchorPane);
        }
    }
    private String originEmail;
    private String originName;
    private void alterClient(TextField emailCliente, TextField nomeCliente, Button confirm, Button cancel, Long id){
        if (confirm.getText().equalsIgnoreCase("Confirmar")){
            Cliente cliente = clienteDAO.get(id);
            cliente.setId(id);
            confirm.setText("Alterar");
            cancel.setText("Deletar");
            nomeCliente.setEditable(false);
            emailCliente.setEditable(false);
            nomeCliente.setStyle(nomeCliente.getStyle().replace("blue", "black"));
            emailCliente.setStyle(emailCliente.getStyle().replace("blue", "black"));

            if (!isValidEmail(emailCliente.getText())) {
                showNotification("Email inválido. Digite um email válido.", false);
                emailCliente.setText(originEmail);
                nomeCliente.setText(originName);
                return;
            }

            if (nomeCliente.getText().length() <= 7){
                showNotification("Seu nome de usuário deve ter pelo menos 8 caracteres.", false);
                emailCliente.setText(originEmail);
                nomeCliente.setText(originName);
                return;
            }

            if (!nomeCliente.getText().equals(originName) || !emailCliente.getText().equals(originEmail)) {
                if (clienteDAO.verificarNomeDeUsuarioExistente(nomeCliente.getText()) && !nomeCliente.getText().equals(originName)) {
                    emailCliente.setText(originEmail);
                    nomeCliente.setText(originName);
                    showNotification("Nome já está em uso!", false);
                }

                if (clienteDAO.verificarEmailExistente(emailCliente.getText()) && !emailCliente.getText().equals(originEmail)) {
                    emailCliente.setText(originEmail);
                    nomeCliente.setText(originName);
                    showNotification("Email já está em uso!", false);
                }

                cliente.setNomeDeUsuario(nomeCliente.getText());
                cliente.setEmail(emailCliente.getText());

                if (clienteDAO.update(cliente, null)) {
                    showNotification("Cliente atualizado!", true);
                } else {
                    showNotification("Não foi possível atualizar!", false);
                    emailCliente.setText(originEmail);
                    nomeCliente.setText(originName);
                }
            }
        } else {
            originEmail = emailCliente.getText();
            originName = nomeCliente.getText();
            confirm.setText("Confirmar");
            cancel.setText("Cancelar");
            nomeCliente.setEditable(true);
            emailCliente.setEditable(true);
            nomeCliente.setStyle(nomeCliente.getStyle().replace("black", "blue"));
            emailCliente.setStyle(emailCliente.getStyle().replace("black", "blue"));
        }
    }

    @FXML
    void accountClicked() {
        Cliente clienteLogado = clienteDAO.get(Long.parseLong(IdUser.getIduser().toString()));
        username.setText("Usuário: " + clienteLogado.getNomeDeUsuario());
        email.setText("Email: " + clienteLogado.getEmail());
        accountPane.setVisible(!accountPane.isVisible());
    }

    @FXML
    void backClicked() {
        //APAGAR ARQUIVO
        try {
            File file = new File("logAccount.ser");
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Arquivo logAccount.ser apagado com sucesso.");
                } else {
                    System.err.println("Não foi possível apagar o arquivo.");
                }
            } else {
                System.err.println("O arquivo logAccount.ser não existe.");
            }
        } catch (Exception ignore) {}

        navigateTo("loginScreen.fxml");
    }

    private void createCardP(Produto produto) {
        if (containerP != null) {
            AnchorPane card = createAnchorPane(752, 90);

            Label label = createLabel("R$ " + produto.getPreco(), 248, 30, "bold 20px sans-serif");

            Label nameLabel = createLabel(produto.getMarca(), 73, 6, "bold 20px sans-serif");

            Label descriptionLabel = createLabel(produto.getDescricao(), 74, 36, "bold 15.5px sans-serif");
            descriptionLabel.setTextFill(Color.web("#7bac69"));

            Label mlLabel = createLabel(produto.getMl(), 73, 57, "bold 15.5px sans-serif");
            mlLabel.setTextFill(Color.web("#7bac69"));

            Button alterButton = createButton(591, 31, "Alterar", "buttonSign");
            alterButton.setOnMouseClicked(e -> alterClicked(produto));

            Button deleteButton = createButton(431, 30, "Deletar", "buttonSign");
            deleteButton.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #AE2626 0%, rgba(241,24,24, 0.93) 100%); -fx-font: bold 16px sans-serif;");
            deleteButton.setOnMouseClicked(event -> deleteProduct(produto));

            loadProductImageAsync(produto.getLinkImagem(), card);

            card.setOnMouseEntered(event -> {
                card.setStyle("-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: #b9babd");
            });
            card.setOnMouseExited(event -> {
                card.setStyle("-fx-border-radius: 8px; -fx-background-radius: 8px; -fx-background-color: transparent");
            });

            card.getChildren().addAll(label, nameLabel, descriptionLabel, mlLabel, alterButton, deleteButton);
            containerP.getChildren().add(card);
        }
    }

    private void alterClicked(Produto produto){
        if (produto != null) {
            labelProduct.setText(" Alterar Produto");
            id = produto.getId();
            isAlterActive = true;
            tfDescricaoP.setText(produto.getDescricao());
            tfLinkP.setText(produto.getLinkImagem());
            tfMarcaP.setText(produto.getMarca());
            tfPrecoP.setText(produto.getPreco().toString());
            tfQuantidadeP.setText(produto.getMl().replace("ml", ""));
        }
    }
    private AnchorPane createAnchorPane(double width, double height) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(width, height);
        return anchorPane;
    }

    private TextField createTextField(String text, double layoutX, double layoutY, String font) {
        TextField textField = new TextField(text);
        textField.setLayoutX(layoutX);
        textField.setLayoutY(layoutY);
        textField.setEditable(false);
        textField.setStyle("-fx-font: " + font + "; border: none; -fx-text-fill: black");
        textField.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        return textField;
    }

    private Label createLabel(String text, double layoutX, double layoutY, String font) {
        Label label = new Label(text);
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        label.setStyle("-fx-font: " + font + ";");
        return label;
    }

    private Button createButton(double layoutX, double layoutY, String text, String styleClass) {
        Button button = new Button(text);
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY);
        button.setPrefHeight(36);
        button.setPrefWidth(147);
        button.getStyleClass().add(styleClass);
        button.setStyle("-fx-font: bold 16px sans-serif;");
        button.setTextFill(Color.WHITE);
        return button;
    }

    private void deleteCliente(Cliente cliente, TextField emailCliente, TextField nomeCliente, Button cancel, Button confirm) {
        if (cancel.getText().equalsIgnoreCase("Deletar")){
            if (cliente.isAdm() == 1) {
                showNotification("Você não pode excluir um administrador do sistema!", false);
                return;
            }
            Alert confirmationDialog = createConfirmationDialog("Deseja Excluir?", "Você tem certeza que deseja excluir esse usuario?");
            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                clienteDAO.delete(cliente.getId());
                loadClientsCard();
                showNotification("Cliente removido do sistema!", true);
            }
        } else{
            confirm.setText("Alterar");
            cancel.setText("Deletar");
            nomeCliente.setEditable(false);
            emailCliente.setEditable(false);
            nomeCliente.setText(originName);
            emailCliente.setText(originEmail);
            nomeCliente.setStyle(nomeCliente.getStyle().replace("blue", "black"));
            emailCliente.setStyle(emailCliente.getStyle().replace("blue", "black"));
        }
    }

    private void deleteProduct(Produto produto) {
        Alert confirmationDialog = createConfirmationDialog("Deseja Excluir?", "Você tem certeza que deseja excluir esse produto?");
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            productDAO.delete(produto.getId());
            loadProductsCard();
            showNotification("Produto removido do sistema!", true);
        }
    }

    private Alert createConfirmationDialog(String title, String contentText) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle(title);
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText(contentText);
        return confirmationDialog;
    }

    private void navigateTo(String fxmlPath) {
        try {
            stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            scene.setOnMouseDragged(event -> {
                if (event.getSceneY() <= 100) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            if (stage != null) {
                stage.setScene(scene);
                stage.setResizable(false);
                scene.setFill(Color.TRANSPARENT);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            }
        } catch (IllegalStateException ignore) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProductImageAsync(String imageURL, AnchorPane card) {
        Image cachedImage = ImageCache.getImageByURL(imageURL);

        if (cachedImage != null) {
            displayImage(cachedImage, card);
        } else {
            ImageCache.cacheImage(imageURL);

            Thread imageLoadThread = new Thread(() -> {
                Image image = new Image(imageURL, true);
                image.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() >= 1.0) {
                        Platform.runLater(() -> displayImage(image, card));
                    }
                });
            });
            imageLoadThread.setDaemon(true);
            imageLoadThread.start();
        }
    }

    private void displayImage(Image image, AnchorPane card) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(69);
        imageView.setFitHeight(66);
        imageView.setLayoutX(4);
        imageView.setLayoutY(13);
        card.getChildren().add(imageView);
    }
    private void loadPage(String page, int x, int y){
        Parent newPane = null;

        try {
            newPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(page + ".fxml")));
        } catch (IOException ex){
            Logger.getLogger(MainClientController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (newPane != null) {
            root.getChildren().clear();
            newPane.setLayoutX(x);
            newPane.setLayoutY(y);
            root.getChildren().add(newPane);
        }
    }
    private void showNotification(String message, boolean isSuccess) {
        Notifier notifier = new Notifier(message, isSuccess);
        notifier.show();
    }

    private void setPosition(double x, double y) {
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@([\\w-]+\\.)+[A-Za-z]{2,4}$");
    }
}
