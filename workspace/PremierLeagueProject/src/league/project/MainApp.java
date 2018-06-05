package league.project;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import league.project.controller.MainLayoutController;
import league.project.util.AlertDialog;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	/**
	 * Constructor
	 */
	public MainApp() {
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setResizable(false);
		this.primaryStage.setTitle("League Project");

		initMainLayout();

	}

	/**
	 * Initializes the root layout.
	 */
	public void initMainLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

			MainLayoutController mainLayoutController = loader.getController();
			mainLayoutController.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		
		}
	}

	/**
	 * Returns the main stage.
	 *
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public BorderPane getRootLayout() {
		return rootLayout;
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			AlertDialog.showError("System Error",
					"There was an error (" + e + "). Contact the system administrator if this problem persists!");
		}
	}
}
