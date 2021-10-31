package com.cargo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;


public class MainController {

	@FXML
	private Button LoginButton;
	//private Button adminButton;

	@FXML
	private Button RegisterButton;
	//private Button userButton;

	@FXML
	private void login() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) LoginButton.getScene().getWindow();
		//Close Stage
		stageMain.close();

		Stage stage2 = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
		Scene scene = new Scene(parent, 435, 150);
		stage2.setScene(scene);
		stage2.setTitle("Login Sayfası");
		stage2.show();
	}

	@FXML
	private void register() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) RegisterButton.getScene().getWindow();
		//Close Stage
		stageMain.close();

		Stage stage2 = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/RegisterUser.fxml"));
		Scene scene = new Scene(parent, 1050, 415);
		stage2.setScene(scene);
		stage2.setTitle("User Register Page");
		stage2.show();
	}

	/*
	@FXML
	private void loginAdmin() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) adminButton.getScene().getWindow();
		//Close Stage
		stageMain.close();

		Stage stage2 = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/LoginAdmin.fxml"));
		Scene scene = new Scene(parent, 300, 150);
		stage2.setScene(scene);
		stage2.setTitle("User Page");
		stage2.show();
	}

	@FXML
	private void loginUser() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) userButton.getScene().getWindow();
		//Close Stage
		stageMain.close();

		Stage stage2 = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/LoginUser.fxml"));
		Scene scene = new Scene(parent, 300, 150);
		stage2.setScene(scene);
		stage2.setTitle("Admin Page");
		stage2.show();
	}

	 */

}
