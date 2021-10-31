package com.cargo.controller;

import com.cargo.algorithm.FlatEarthDist;
import com.cargo.algorithm.Graph;
import com.cargo.algorithm.Node;
import com.cargo.model.Users;
import com.dlsc.gmapsfx.GoogleMapView;
import com.dlsc.gmapsfx.MapComponentInitializedListener;
import com.dlsc.gmapsfx.javascript.event.UIEventType;
import com.dlsc.gmapsfx.javascript.object.*;
import com.dlsc.gmapsfx.service.directions.*;
import com.dlsc.gmapsfx.service.elevation.*;
import com.dlsc.gmapsfx.service.geocoding.GeocoderStatus;
import com.dlsc.gmapsfx.service.geocoding.GeocodingResult;
import com.dlsc.gmapsfx.service.geocoding.GeocodingService;
import com.dlsc.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.dlsc.gmapsfx.shapes.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


public class RegisterUserController implements Initializable, MapComponentInitializedListener,
		ElevationServiceCallback, GeocodingServiceCallback, DirectionsServiceCallback {

	protected GoogleMapView mapComponent;
	protected GoogleMap map;
	protected DirectionsPane directions;

	private static Double staticLat = 0.01;
	private static Double staticLon = 0.02;

	private Button btnZoomIn;
	private Button btnZoomOut;
	private Button back;
	private Marker pinMarker = null;
	private Label lblZoom;
	private Label lblCenter;
	private Label lblClick;
	private ComboBox<MapTypeIdEnum> mapTypeCombo;

	private MarkerOptions markerOptions2;
	private Marker clickMyMarker;
	private Marker myMarker2;
	private Button btnHideMarker;
	private Button btnDeleteMarker;

    @FXML
    private TextField idField;

	@FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

	@FXML
	private TextField usernameField;

    @FXML
    private TextField passwordField;

	@FXML
	private TextField latitudeField;

	@FXML
	private TextField longitudeField;

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
	private TableColumn<Users, String> latitudeColumn;

	@FXML
	private TableColumn<Users, String> longitudeColumn;

	@FXML
	private Button loginBtn;

	@FXML
	private Button mapBtn;

	public ObservableList<Users> getUserList(){
		ObservableList<Users> usersList = FXCollections.observableArrayList();
		Connection connection = getConnection();
		String query = "SELECT * FROM users";
		Statement st;
		ResultSet rs;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			List<Users> users1 = new ArrayList<>();
			Users users;
			while(rs.next()) {
				users = new Users(rs.getInt("Id"),rs.getString("Name"),rs.getString("Surname"),rs.getString("Username"),rs.getString("Password"),rs.getDouble("latitude"),rs.getDouble("Longitude"));
				users1.add(users);
			}
			//Collections.sort(cargos,(o1, o2)->o1.getId().compareTo(o2.getId()));
			usersList.addAll(users1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usersList;
	}

    @FXML
    private void insertButton() {
    	String query = "insert into users values('"+idField.getText()+"','"+nameField.getText()+"','"+surnameField.getText()+"','"+usernameField.getText()+"','"+passwordField.getText()+"','"+latitudeField.getText()+"','"+longitudeField.getText()+"')";
    	executeQuery(query);
		showUsers();
    }

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
		stage2.setTitle("Login Sayfası");
		stage2.show();
	}

	@FXML
	private void setMapButton() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) mapBtn.getScene().getWindow();
		//Close Stage
		stageMain.close();

		System.out.println("Java version: " + System.getProperty("java.home"));
		mapComponent = new GoogleMapView(Locale.getDefault().getLanguage(), null);
		mapComponent.addMapInitializedListener(this);

		BorderPane bp = new BorderPane();
		 ToolBar tb = new ToolBar();

        btnZoomIn = new Button("Zoom In");
        btnZoomIn.setOnAction(e -> {
            map.zoomProperty().set(map.getZoom() + 1);
        });
        btnZoomIn.setDisable(true);

        btnZoomOut = new Button("Zoom Out");
        btnZoomOut.setOnAction(e -> {
            map.zoomProperty().set(map.getZoom() - 1);
        });
        btnZoomOut.setDisable(true);

		back = new Button("Back");
		back.setOnAction(e -> {
			//Get Stage
			Stage stageMain2 = (Stage) back.getScene().getWindow();
			//Close Stage
			stageMain2.close();

			Stage stage2 = new Stage();
			Parent parent = null;
			try {
				parent = FXMLLoader.load(getClass().getResource("/view/RegisterUser.fxml"));
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
			Scene scene = new Scene(parent, 1050, 415);
			stage2.setScene(scene);
			stage2.setTitle("User Register Page");
			stage2.show();
		});
		back.setDisable(false);

        lblZoom = new Label();
        lblCenter = new Label();
        lblClick = new Label();

        mapTypeCombo = new ComboBox<>();
        mapTypeCombo.setOnAction( e -> {
           map.setMapType(mapTypeCombo.getSelectionModel().getSelectedItem() );
        });
        mapTypeCombo.setDisable(true);

        Button btnType = new Button("Map type");
        btnType.setOnAction(e -> {
            map.setMapType(MapTypeIdEnum.HYBRID);
        });

		btnHideMarker = new Button("Hide Marker");
		btnHideMarker.setOnAction(e -> {hideMarker();});

		btnDeleteMarker = new Button("Delete Marker");
		btnDeleteMarker.setOnAction(e -> {deleteMarker();});

        tb.getItems().addAll(btnZoomIn, btnZoomOut,back, mapTypeCombo,
                new Label("Zoom: "), lblZoom,
                new Label("Center: "), lblCenter,
                new Label("Click: "), lblClick,
				btnHideMarker, btnDeleteMarker);

        bp.setTop(tb);

		bp.setCenter(mapComponent);

		Stage stage2 = new Stage();

		Scene scene = new Scene(bp);
		stage2.setScene(scene);
		stage2.setTitle("User Login Page");
		stage2.show();

		/*
		Node nodeA = new Node("A");
		Node nodeB = new Node("B");
		Node nodeC = new Node("C");
		Node nodeD = new Node("D");
		Node nodeE = new Node("E");
		Node nodeF = new Node("F");

		nodeA.addDestination(nodeB, FlatEarthDist.distance(user.lat,user.lng,target.lat,target.lng));
		nodeA.addDestination(nodeC, 15);

		nodeB.addDestination(nodeD, 12);
		nodeB.addDestination(nodeF, 15);

		nodeC.addDestination(nodeE, 10);

		nodeD.addDestination(nodeE, 2);
		nodeD.addDestination(nodeF, 1);

		nodeF.addDestination(nodeE, 5);

		Graph graph = new Graph();

		graph.addNode(nodeA);
		graph.addNode(nodeB);
		graph.addNode(nodeC);
		graph.addNode(nodeD);
		graph.addNode(nodeE);
		graph.addNode(nodeF);

		graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);

		 */
	}

    @FXML 
    private void updateButton() {
		String query = "UPDATE users SET name='"+nameField.getText()+"',surname='"+surnameField.getText()+"',username='"+usernameField.getText()+"',password='"+passwordField.getText()+"',latitude='"+latitudeField.getText()+"',longitude='"+longitudeField.getText()+"' WHERE ID='"+idField.getText()+"'";
		executeQuery(query);
		showUsers();
    }
    
    @FXML
    private void deleteButton() {
    	String query = "DELETE FROM users WHERE ID="+idField.getText()+"";
    	executeQuery(query);
		showUsers();
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

		TableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
				//Check whether item is selected and set value of selected item to Label
				if(TableView.getSelectionModel().getSelectedItem() != null)
				{
					Users usr = TableView.getSelectionModel().getSelectedItem();
					idField.setText(usr.getId()+"");
					nameField.setText(usr.getName());
					surnameField.setText(usr.getSurname());
					usernameField.setText(usr.getUsername());
					passwordField.setText(usr.getPassword());
				}
			}
		});



		longitudeField.setText(staticLon.toString());
		latitudeField.setText(staticLat.toString());
		System.out.println("staticLat: "+staticLat);
		System.out.println("staticLon: "+staticLon);
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
    	ObservableList<Users> usersList = FXCollections.observableArrayList();
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
				usersList.add(users);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return usersList;
    }

	// I had to change ArrayList to ObservableList I didn't find another option to do this but this works :)
	public void showUsers() {
		ObservableList<Users> list = getUsersList();


		idColumn.setCellValueFactory(new PropertyValueFactory<Users,Integer>("id"));;
		nameColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("name"));
		surnameColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("surname"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("username"));
		passwordColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("password"));
		latitudeColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("latitude"));
		longitudeColumn.setCellValueFactory(new PropertyValueFactory<Users,String>("longitude"));

		TableView.setItems(list);
	}

	DirectionsRenderer renderer;

	private void showMarker(double lat, double lng) {
		if(pinMarker!=null){
			map.removeMarker(pinMarker);
		}
		MarkerOptions options = new MarkerOptions();
		options.icon("/Users/mehmet/Downloads/GMapsFX-master-11/GMapsFX/src/main/resources/images/fleg.png")
				.position(new LatLong(lat, lng));
		pinMarker = new Marker(options);
		map.addMarker(pinMarker);
	}

	private void showUserMarker(Users users) {
		if(pinMarker!=null){
			map.removeMarker(pinMarker);
		}
		MarkerOptions options = new MarkerOptions();
		options.icon("/Users/mehmet/Downloads/GMapsFX-master-11/GMapsFX/src/main/resources/images/man.png")
				.position(new LatLong(users.getLatitude(), users.getLongitude()));
		Marker pinMarker = new Marker(options);
		map.addMarker(pinMarker);

		map.addUIEventHandler(pinMarker, UIEventType.click, (JSObject obj) -> {
			InfoWindowOptions infoOptions1 = new InfoWindowOptions();
			MarkerOptions markerOptions1 = new MarkerOptions();
			LatLong lll = new LatLong(users.getLatitude()+7.5,users.getLongitude());
			markerOptions1.position(lll)
					.visible(true);
			Marker mm = new Marker(markerOptions1);
			infoOptions1.content("<p></b>"+users.getName()+" "+users.getSurname()+"<b></p>").position(lll);
			InfoWindow window1 = new InfoWindow(infoOptions1);
			window1.open(map, mm);
		});
	}

	private void hideMarker() {
//		System.out.println("deleteMarker");

		boolean visible = myMarker2.getVisible();

		//System.out.println("Marker was visible? " + visible);

		myMarker2.setVisible(! visible);

//				markerOptions2.visible(Boolean.FALSE);
//				myMarker2.setOptions(markerOptions2);
//		System.out.println("deleteMarker - made invisible?");
	}

	private void deleteMarker() {
		//System.out.println("Marker was removed?");
		map.removeMarker(myMarker2);
	}

	private void checkCenter(LatLong center) {
//        System.out.println("Testing fromLatLngToPoint using: " + center);
//        Point2D p = map.fromLatLngToPoint(center);
//        System.out.println("Testing fromLatLngToPoint result: " + p);
//        System.out.println("Testing fromLatLngToPoint expected: " + mapComponent.getWidth()/2 + ", " + mapComponent.getHeight()/2);
	}

	@Override
	public void mapInitialized() {

		//System.out.println("MainApp.mapInitialised....");

		//Once the map has been loaded by the Webview, initialize the map details.
		LatLong center = new LatLong(47.606189, -122.335842);
		mapComponent.addMapReadyListener(() -> {
			// This call will fail unless the map is completely ready.
			checkCenter(center);
		});

		MapOptions options = new MapOptions();
		options.center(center)
				.mapMarker(true)
				.zoom(9)
				.overviewMapControl(false)
				.panControl(false)
				.rotateControl(false)
				.scaleControl(false)
				.streetViewControl(false)
				.zoomControl(false)
				.mapType(MapTypeIdEnum.TERRAIN)
				.clickableIcons(false)
				.disableDefaultUI(true)
				.disableDoubleClickZoom(true)
				.keyboardShortcuts(false)
				.styleString("[{'featureType':'landscape','stylers':[{'saturation':-100},{'lightness':65},{'visibility':'on'}]},{'featureType':'poi','stylers':[{'saturation':-100},{'lightness':51},{'visibility':'simplified'}]},{'featureType':'road.highway','stylers':[{'saturation':-100},{'visibility':'simplified'}]},{\"featureType\":\"road.arterial\",\"stylers\":[{\"saturation\":-100},{\"lightness\":30},{\"visibility\":\"on\"}]},{\"featureType\":\"road.local\",\"stylers\":[{\"saturation\":-100},{\"lightness\":40},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"stylers\":[{\"visibility\":\"off\"}]},{\"featureType\":\"water\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"on\"},{\"lightness\":-25},{\"saturation\":-100}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#ffff00\"},{\"lightness\":-25},{\"saturation\":-97}]}]");

		//[{\"featureType\":\"landscape\",\"stylers\":[{\"saturation\":-100},{\"lightness\":65},{\"visibility\":\"on\"}]},{\"featureType\":\"poi\",\"stylers\":[{\"saturation\":-100},{\"lightness\":51},{\"visibility\":\"simplified\"}]},{\"featureType\":\"road.highway\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"road.arterial\",\"stylers\":[{\"saturation\":-100},{\"lightness\":30},{\"visibility\":\"on\"}]},{\"featureType\":\"road.local\",\"stylers\":[{\"saturation\":-100},{\"lightness\":40},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"stylers\":[{\"visibility\":\"off\"}]},{\"featureType\":\"water\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"on\"},{\"lightness\":-25},{\"saturation\":-100}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#ffff00\"},{\"lightness\":-25},{\"saturation\":-97}]}]
		map = mapComponent.createMap(options,false);
		directions = mapComponent.getDirec();

		map.setHeading(123.2);
//        System.out.println("Heading is: " + map.getHeading() );

		map.fitBounds(new LatLongBounds(new LatLong(30, 120), center));
//        System.out.println("Bounds : " + map.getBounds());

		lblCenter.setText(map.getCenter().toString());
		map.centerProperty().addListener((ObservableValue<? extends LatLong> obs, LatLong o, LatLong n) -> {
			lblCenter.setText(n.toString());
		});

		lblZoom.setText(Integer.toString(map.getZoom()));
		map.zoomProperty().addListener((ObservableValue<? extends Number> obs, Number o, Number n) -> {
			lblZoom.setText(n.toString());
		});

//      map.addStateEventHandler(MapStateEventType.center_changed, () -> {
//			System.out.println("center_changed: " + map.getCenter());
//		});
//        map.addStateEventHandler(MapStateEventType.tilesloaded, () -> {
//			System.out.println("We got a tilesloaded event on the map");
//		});


		map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {

			/*if(clickMyMarker!=null){
				map.removeMarker(clickMyMarker);
			}*/

			LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
			lblClick.setText(ll.toString());
			staticLat = ll.getLatitude();
			staticLon = ll.getLongitude();
			System.out.println("Latitude: "+ll.getLatitude()+"\nlongitude: "+ll.getLongitude());
			showMarker(ll.getLatitude(),ll.getLongitude());

		});

		btnZoomIn.setDisable(false);
		btnZoomOut.setDisable(false);
		mapTypeCombo.setDisable(false);

		mapTypeCombo.getItems().addAll( MapTypeIdEnum.ALL );

		GeocodingService gs = new GeocodingService();

		DirectionsService ds = new DirectionsService();
		renderer = new DirectionsRenderer(true, map, directions);

		DirectionsWaypoint[] dw = new DirectionsWaypoint[2];
		dw[0] = new DirectionsWaypoint("São Paulo - SP");
		dw[1] = new DirectionsWaypoint("Juiz de Fora - MG");

		DirectionsRequest dr = new DirectionsRequest(
				"Belo Horizonte - MG",
				"Rio de Janeiro - RJ",
				TravelModes.DRIVING,
				dw, false);
		ds.getRoute(dr, this, renderer);

		LatLong[] location = new LatLong[1];
		location[0] = new LatLong(-19.744056, -43.958699);
		LocationElevationRequest loc = new LocationElevationRequest(location);
		ElevationService es = new ElevationService();
		es.getElevationForLocations(loc, this);

		//showMarker(49.4850385203162,160.23251737499996,"https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Disc_Plain_blue.svg/32px-Disc_Plain_blue.svg.png");
		ObservableList<Users> list = getUserList();

		//System.out.println(list.get(0).getLatitude());

		//showMarker(list.get(0).getLatitude(),list.get(0).getLongitude(),"https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Disc_Plain_blue.svg/32px-Disc_Plain_blue.svg.png");

		list.stream().filter(i->i.getLatitude()!=null && i.getLatitude()!=null).forEach(item-> {
			showUserMarker(item);
		});

		list.stream().filter(i->i.getLatitude()!=null && i.getLatitude()!=null).forEach(item-> System.out.println("lat: "+item.getLatitude()+", lot: "+item.getLongitude()));


	}

	@Override
	public void elevationsReceived(ElevationResult[] results, ElevationStatus status) {
		if(status.equals(ElevationStatus.OK)){
			for(ElevationResult e : results){
				System.out.println(" Elevation on "+ e.getLocation().toString() + " is " + e.getElevation());
			}
		}
	}

	@Override
	public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
		if(status.equals(GeocoderStatus.OK)){
			for(GeocodingResult e : results){
				System.out.println(e.getVariableName());
				System.out.println("GEOCODE: " + e.getFormattedAddress() + "\n" + e.toString());
			}

		}

	}

	@Override
	public void directionsReceived(DirectionsResult results, DirectionStatus status) {
		if(status.equals(DirectionStatus.OK)){
			mapComponent.getMap().showDirectionsPane();
			System.out.println("OK");

			DirectionsResult e = results;
			GeocodingService gs = new GeocodingService();

			System.out.println("SIZE ROUTES: " + e.getRoutes().size() + "\n" + "ORIGIN: " + e.getRoutes().get(0).getLegs().get(0).getStartLocation());
			//gs.reverseGeocode(e.getRoutes().get(0).getLegs().get(0).getStartLocation().getLatitude(), e.getRoutes().get(0).getLegs().get(0).getStartLocation().getLongitude(), this);
			System.out.println("LEGS SIZE: " + e.getRoutes().get(0).getLegs().size());
			System.out.println("WAYPOINTS " +e.getGeocodedWaypoints().size());
            /*double d = 0;
            for(DirectionsLeg g : e.getRoutes().get(0).getLegs()){
                d += g.getDistance().getValue();
                System.out.println("DISTANCE " + g.getDistance().getValue());
            }*/
			try{
				System.out.println("Distancia total = " + e.getRoutes().get(0).getLegs().get(0).getDistance().getText());
			} catch(Exception ex){
				System.out.println("ERRO: " + ex.getMessage());
			}
			System.out.println("LEG(0)");
			System.out.println(e.getRoutes().get(0).getLegs().get(0).getSteps().size());
            /*for(DirectionsSteps ds : e.getRoutes().get(0).getLegs().get(0).getSteps()){
                System.out.println(ds.getStartLocation().toString() + " x " + ds.getEndLocation().toString());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ds.getStartLocation())
                        .title(ds.getInstructions())
                        .animation(Animation.DROP)
                        .visible(true);
                Marker myMarker = new Marker(markerOptions);
                map.addMarker(myMarker);
            }
                    */
			System.out.println(renderer.toString());
		}
	}

}
