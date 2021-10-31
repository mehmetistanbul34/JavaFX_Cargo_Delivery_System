package com.cargo.controller;

import com.cargo.model.Users;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

	@FXML
	private TextField usernameField;

	@FXML
	private TextField passwordField;

	@FXML
	private Button loginBtn;

	@FXML
	private Button mainBtn;

	@FXML
	private Button changePasswordBtn;

	@FXML
	private void loginButton() throws IOException {
		Users list = getUsersList();

		if (list!=null) {
			SessionMapUser.addSession("userData",list);
			//Get Stage
			Stage stageMain = (Stage) loginBtn.getScene().getWindow();
			//Close Stage
			stageMain.close();

			Stage stage = new Stage();
			Parent parent = FXMLLoader.load(getClass().getResource("/view/Cargo.fxml"));
			Scene scene = new Scene(parent, 1250, 420);
			stage.setScene(scene);
			stage.setTitle("Teslimat Durum Ekranı");
			stage.show();
		}
	}

	@FXML
	private void changePasswordButton() throws IOException {
			//Get Stage
			Stage stageMain = (Stage) changePasswordBtn.getScene().getWindow();
			//Close Stage
			stageMain.close();

			Stage stage = new Stage();
			Parent parent = FXMLLoader.load(getClass().getResource("/view/ChangePassword.fxml"));
			Scene scene = new Scene(parent, 900, 300);
			stage.setScene(scene);
			stage.setTitle("Change Password User Page");
			stage.show();
	}

	@FXML
	private void mainButton() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) mainBtn.getScene().getWindow();
		//Close Stage
		stageMain.close();

		Stage stage = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
		Scene scene = new Scene(parent, 400, 100);
		stage.setScene(scene);
		stage.setTitle("Select Page");
		stage.show();
	}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    public Connection getConnection() {
    	Connection conn;
    	try {
    		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cargo_db","postgres","1111");
    		return conn;
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }

	public Users getUsersList(){
		Connection connection = getConnection();
		String query = "SELECT * FROM users WHERE username='"+usernameField.getText()+"' AND password='"+passwordField.getText()+"'";
		Statement st;
		ResultSet rs;
		Users users=null;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			if(rs.next()) {
				users = new Users(rs.getInt("Id"),rs.getString("Name"),rs.getString("Surname"),rs.getString("Username"),rs.getString("Password"),rs.getDouble("Latitude"),rs.getDouble("Longitude"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}


}
