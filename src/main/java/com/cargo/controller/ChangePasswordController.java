package com.cargo.controller;

import com.cargo.model.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class ChangePasswordController implements Initializable {

    @FXML
    private TextField idField;

	@FXML
    private TextField currentPasswordField;

    @FXML
    private TextField newPasswordField;

	@FXML
	private TextField confirmNewPasswordField;

	@FXML
	private Label errorLabel;

	@FXML
	private TableView<Users> TableView;

	@FXML
	private TableColumn<Users, Integer> idColumn;

	@FXML
	private TableColumn<Users, String> nameColumn;

	@FXML
	private TableColumn<Users, String> surnameColumn;

	@FXML
	private TableColumn<Users, String> usernameColumn;

	@FXML
	private TableColumn<Users, String> passwordColumn;

	@FXML
	private Button loginBtn;

	@FXML
	private void loginButton() throws IOException {
    	//Get Stage
		Stage stageMain = (Stage) loginBtn.getScene().getWindow();
		//Close Stage
		stageMain.close();

		Stage stage2 = new Stage();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
		Scene scene = new Scene(parent, 435, 150);
		stage2.setScene(scene);
		stage2.setTitle("User Login Page");
		stage2.show();
	}
    
    
    @FXML 
    private void updateButton() {
		ObservableList<Users> usr = getUser(Integer.valueOf(idField.getText()));

		if (currentPasswordField.getText().equals(usr.get(0).getPassword()) && !usr.get(0).getPassword().equals(-1)) {
			if (newPasswordField.getText().equals(confirmNewPasswordField.getText())){
				String query = "UPDATE users SET password='" + newPasswordField.getText() + "' WHERE ID='" + idField.getText() + "'";
				executeQuery(query);
				errorLabel.setText("Şifreniz Değiştirildi");
				errorLabel.setStyle("-fx-background-color: #4dfa03");
				showUsers();
			}else{
				errorLabel.setText("Şifreler Eşleşmiyor");
				errorLabel.setStyle("-fx-background-color: #fad803");
			}
		}else{
			errorLabel.setText("Yanlış Şifre");
			errorLabel.setStyle("-fx-background-color: #e34242");
		}
    }
    
    public void executeQuery(String query) {
    	Connection conn = getConnection();
    	Statement st;
    	try {
			st = conn.createStatement();
			st.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
		showUsers();
    }
    
    public Connection getConnection() {
    	Connection conn;
    	try {
			conn = DriverManager.getConnection("jdbc:postgresql://34.125.191.31:9753/cargo_db","postgres","postgres");
    		return conn;
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public ObservableList<Users> getUsersList(){
    	ObservableList<Users> booksList = FXCollections.observableArrayList();
    	Connection connection = getConnection();
    	String query = "SELECT * FROM users";
    	Statement st;
    	ResultSet rs;
    	
    	try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			Users users;
			while(rs.next()) {
				users = new Users(rs.getInt("Id"),rs.getString("Name"),rs.getString("Surname"),rs.getString("Username"),rs.getString("Password"),rs.getDouble("Latitude"),rs.getDouble("Longitude"));
				booksList.add(users);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return booksList;
    }



	public ObservableList<Users> getUser(int id){
		ObservableList<Users> booksList = FXCollections.observableArrayList();
		Connection connection = getConnection();
		String query = "SELECT * FROM users ";
		Statement st;
		ResultSet rs;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			Users users;
			while(rs.next()) {
				users = new Users(rs.getInt("Id"),rs.getString("Name"),rs.getString("Surname"),rs.getString("Username"),rs.getString("Password"),rs.getDouble("Latitude"),rs.getDouble("Longitude"));
				if (users.getId()==id)
					booksList.add(users);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return booksList;
	}

	// I had to change ArrayList to ObservableList I didn't find another option to do this but this works :)
	public void showUsers() {
		ObservableList<Users> list = getUsersList();

		nameColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("name"));
		surnameColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("surname"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("username"));
		passwordColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("password"));
		idColumn.setCellValueFactory(new PropertyValueFactory<Users,Integer>("id"));;

		TableView.setItems(list);
	}

}
