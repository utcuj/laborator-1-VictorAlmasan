package league.project.util;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import league.project.exception.ContractDAOException;
import league.project.model.ContractDAO;
import league.project.model.PlayerPosition;

public class AlertDialog {

	public static Optional<ButtonType> showConfirmationDialog(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		return alert.showAndWait();
	}

	public static void showError(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.ERROR);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("/css/error.css");
		dialogPane.getStyleClass().add("error");
		alert.setTitle("Error Dialog");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	public static void showSuccess(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("/css/success.css");
		dialogPane.getStyleClass().add("success");
		alert.setTitle("Confirmation Dialog");
		alert.setGraphic(new ImageView("/images/okAlert.png"));
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	public static void showInformation(String headerText, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add("/css/information.css");
		dialogPane.getStyleClass().add("information");
		alert.setTitle("Information Dialog");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	public static Optional<Pair<String, String>> loginDialog() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Login Dialog");
		dialog.setHeaderText("Welcome! Enter your credentials");

		// Set the icon (must be included in the project).
		dialog.setGraphic(new ImageView("/images/login.png"));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was
		// entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button
		// is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});

		return dialog.showAndWait();
	}

	public static PlayerPosition choiceDialog() throws ContractDAOException {
		ChoiceDialog<PlayerPosition> dialog = new ChoiceDialog<>(null, ContractDAO.getPlayerPositionsList());
		dialog.setTitle("Choice Dialog");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setContentText("Choose player position:");

		Optional<PlayerPosition> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}

	public static void showAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		DialogPane dialogPane = alert.getDialogPane();
		Text text1=new Text("Application designed and developed by \n");
		text1.setStyle("-fx-font-size: 16px; -fx-fill: black;");
		Text text3=new Text("VICTOR-PETRU ALMĂȘAN \n");
		text3.setStyle("-fx-font-size: 16px; -fx-fill: green;");
		Text text2=new Text("almasan.victor@yahoo.com");
		text2.setStyle("-fx-font-size: 16px; -fx-fill: blue; -fx-underline: true;");
		TextFlow flow = new TextFlow(text1,text3,text2);
		dialogPane.setContent(flow);
		alert.setTitle("League Application");
		alert.setHeaderText("About");
		alert.showAndWait();
	}

}
