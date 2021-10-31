package com.cargo.controller;

import com.cargo.algorithm.FlatEarthDist;
import com.cargo.algorithm.Node;
import com.cargo.model.Cargo;
import com.cargo.model.LatLon;
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
import com.dlsc.gmapsfx.shapes.Polyline;
import com.dlsc.gmapsfx.shapes.PolylineOptions;
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


public class CargoController implements Initializable, MapComponentInitializedListener,
		ElevationServiceCallback, GeocodingServiceCallback, DirectionsServiceCallback {

	protected GoogleMapView mapComponent;
	protected GoogleMap map;
	protected DirectionsPane directions;

	private Button btnZoomIn;
	private Button btnZoomOut;
	private Button back;
	private Label lblZoom;
	private Label lblCenter;
	private Label lblClick;
	private ComboBox<MapTypeIdEnum> mapTypeCombo;
	DirectionsRenderer renderer;

	private MarkerOptions markerOptions2;
	private Marker pinMarker = null;
	private Marker myMarker2;
	private Button btnHideMarker;
	private Button btnDeleteMarker;

    @FXML
    private TextField idField;

	@FXML
    private TextField customerNameField;

    @FXML
    private TextField customerAddressField;

	@FXML
	private TextField productField;

    @FXML
    private TextField usersIdField;

	@FXML
	private TextField latitudeField;

	@FXML
	private TextField longitudeField;

	@FXML
	private CheckBox cargoStatusField;

    @FXML
    private TableView<Cargo> TableView;

    @FXML
    private TableColumn<Cargo, Integer> idColumn;

    @FXML
    private TableColumn<Cargo, String> customerNameColumn;

    @FXML
    private TableColumn<Cargo, String> customerAddressColumn;

	@FXML
	private TableColumn<Cargo, String> productColumn;

    @FXML
    private TableColumn<Cargo, String> usersIdColumn;

	@FXML
	private TableColumn<Cargo, Boolean> cargoStatusColumn;

	@FXML
	private TableColumn<Cargo, String> latitudeColumn;

	@FXML
	private TableColumn<Cargo, String> longitudeColumn;

	@FXML
	private Button loginBtn;

	@FXML
	private Button showMapButton;

    @FXML
    private void insertButton() {
    	String query = "insert into cargo values('"+idField.getText()+"','"+customerNameField.getText()+"','"+customerAddressField.getText()+"','"+usersIdField.getText()+"','"+latitudeField.getText()+"','"+longitudeField.getText()+"','"+cargoStatusField.isSelected()+"','"+productField.getText()+"')";
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
    private void updateButton() {
    String query = "UPDATE cargo SET customer_name='"+customerNameField.getText()+"',customer_address='"+customerAddressField.getText()+"',product='"+productField.getText()+"',users_id='"+usersIdField.getText()+"',latitude='"+latitudeField.getText()+"',longitude='"+longitudeField.getText()+"',cargo_status='"+cargoStatusField.isSelected()+"' WHERE ID='"+idField.getText()+"'";
    executeQuery(query);
	showUsers();
    }
    
    @FXML
    private void deleteButton() {
    	String query = "DELETE FROM cargo WHERE ID="+idField.getText()+"";
    	executeQuery(query);
    	showUsers();
    }

	@FXML
	private void showButton() throws IOException {
		//Get Stage
		Stage stageMain = (Stage) showMapButton.getScene().getWindow();
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

			stageMain.show();
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

		tb.getItems().addAll(btnZoomIn, btnZoomOut,back, mapTypeCombo, lblClick);

		bp.setTop(tb);

		bp.setCenter(mapComponent);

		Stage stage2 = new Stage();
		Scene scene = new Scene(bp,1500,700);
		stage2.setScene(scene);
		stage2.setTitle("Harita Sayfası");
		stage2.show();
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

	private void showMarker(Cargo item, String iconPath) {
		MarkerOptions options = new MarkerOptions();
		options.position(new LatLong(item.getLatitude(), item.getLongitude()));
		Marker marker = new Marker(options);
		map.addMarker(marker);

		map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
			LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            System.out.println("Test marker clicked: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());

			InfoWindowOptions infoOptions1 = new InfoWindowOptions();
			MarkerOptions markerOptions1 = new MarkerOptions();
			LatLong lll = new LatLong(item.getLatitude()+3,item.getLongitude());
			markerOptions1.position(lll)
					.visible(true);
			Marker mm = new Marker(markerOptions1);
			infoOptions1.content("<p>ürün: <b>"+item.getProduct()+"</b><br><br><i>"+item.getCustomerAddress()+"</i></p><b>"+item.getCustomerName()+"</b>").position(lll);
			InfoWindow window12 = new InfoWindow(infoOptions1);
			window12.open(map, mm);
		});
	}

	private void pinMap(Cargo item, String iconPath) {
    	if(pinMarker!=null){
    		map.removeMarker(pinMarker);
		}
		MarkerOptions options = new MarkerOptions();
		options.icon("/Users/mehmet/Downloads/GMapsFX-master-11/GMapsFX/src/main/resources/images/fleg.png")
				.position(new LatLong(item.getLatitude(), item.getLongitude()));
		pinMarker = new Marker(options);
		map.addMarker(pinMarker);

	}

	private void kuryeToMap(ObservableList<Users> item, String iconPath) {

		MarkerOptions options = new MarkerOptions();
		options.icon("/Users/mehmet/Downloads/GMapsFX-master-11/GMapsFX/src/main/resources/images/man.png")
				.position(new LatLong(item.get(0).getLatitude(), item.get(0).getLongitude()));
		Marker pinMarker = new Marker(options);
		map.addMarker(pinMarker);

		map.addUIEventHandler(pinMarker, UIEventType.click, (JSObject obj) -> {
			InfoWindowOptions infoOptions1 = new InfoWindowOptions();
			MarkerOptions markerOptions1 = new MarkerOptions();
			LatLong lll = new LatLong(item.get(0).getLatitude()+7.5,item.get(0).getLongitude());
			markerOptions1.position(lll)
					.visible(true);
			Marker mm = new Marker(markerOptions1);
			infoOptions1.content("<p></b>"+item.get(0).getName()+" "+item.get(0).getSurname()+"<b></p>").position(lll);
			InfoWindow window1 = new InfoWindow(infoOptions1);
			window1.open(map, mm);
		});

	}

	private void checkCenter(LatLong center) {
//        System.out.println("Testing fromLatLngToPoint using: " + center);
//        Point2D p = map.fromLatLngToPoint(center);
//        System.out.println("Testing fromLatLngToPoint result: " + p);
//        System.out.println("Testing fromLatLngToPoint expected: " + mapComponent.getWidth()/2 + ", " + mapComponent.getHeight()/2);
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
					Cargo usr = TableView.getSelectionModel().getSelectedItem();
					idField.setText(usr.getId()+"");
					customerNameField.setText(usr.getCustomerName());
					customerAddressField.setText(usr.getCustomerAddress());
					productField.setText(usr.getProduct());
					latitudeField.setText(usr.getLatitude().toString());
					longitudeField.setText(usr.getLongitude().toString());
				}
			}
		});
    	usersIdField.setText(String.valueOf(SessionMapUser.getSessionMap("userData").getId()));
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
    
    public ObservableList<Cargo> getCargoList(){
    	ObservableList<Cargo> cargoList = FXCollections.observableArrayList();
    	Connection connection = getConnection();
    	String query = "SELECT * FROM cargo";
    	Statement st;
    	ResultSet rs;
    	
    	try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			List<Cargo> cargos = new ArrayList<>();
			Cargo cargo;
			while(rs.next()) {
				cargo = new Cargo(rs.getInt("Id"),rs.getString("customer_name"),rs.getString("customer_address"),rs.getString("users_id"),rs.getDouble("Latitude"),rs.getDouble("Longitude"),rs.getBoolean("cargo_status"),rs.getString("product"));
					if (cargo.getUsersId().equals(String.valueOf(SessionMapUser.getSessionMap("userData").getId()))) {
						cargos.add(cargo);
					}
				}
			Collections.sort(cargos,(o1,o2)->o1.getId().compareTo(o2.getId()));
			cargoList.addAll(cargos);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return cargoList;
    }


	public ObservableList<Cargo> getCargoMapList(){
		ObservableList<Cargo> cargoList = FXCollections.observableArrayList();
		Connection connection = getConnection();
		String query = "SELECT * FROM cargo";
		Statement st;
		ResultSet rs;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(query);
			List<Cargo> cargos = new ArrayList<>();
			Cargo cargo;
			while(rs.next()) {
				cargo = new Cargo(rs.getInt("Id"),rs.getString("customer_name"),rs.getString("customer_address"),rs.getString("users_id"),rs.getDouble("Latitude"),rs.getDouble("Longitude"),rs.getBoolean("cargo_status"),rs.getString("product"));
				if (cargo.getUsersId().equals(String.valueOf(SessionMapUser.getSessionMap("userData").getId())) && !cargo.getCargoStatus()) {
					cargos.add(cargo);
				}
			}
			Collections.sort(cargos,(o1,o2)->o1.getId().compareTo(o2.getId()));
			cargoList.addAll(cargos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cargoList;
	}

	public ObservableList<Users> getUser(){
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
				if (rs.getInt("id")==SessionMapUser.getSessionMap("userData").getId()) {
					users = new Users(rs.getInt("Id"), rs.getString("Name"), rs.getString("Surname"), rs.getString("Username"), rs.getString("Password"), rs.getDouble("Latitude"), rs.getDouble("Longitude"));
					usersList.add(users);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usersList;
	}

    // I had to change ArrayList to ObservableList I didn't find another option to do this but this works :)
    public void showUsers() {
    	ObservableList<Cargo> list = getCargoList();

    	idColumn.setCellValueFactory(new PropertyValueFactory<Cargo,Integer>("id"));
    	customerNameColumn.setCellValueFactory(new PropertyValueFactory<Cargo,String>("customerName"));
    	customerAddressColumn.setCellValueFactory(new PropertyValueFactory<Cargo,String>("customerAddress"));
    	usersIdColumn.setCellValueFactory(new PropertyValueFactory<Cargo,String>("usersId"));
		latitudeColumn.setCellValueFactory(new PropertyValueFactory<Cargo,String>("latitude"));
		longitudeColumn.setCellValueFactory(new PropertyValueFactory<Cargo,String>("longitude"));
		cargoStatusColumn.setCellValueFactory(new PropertyValueFactory<Cargo,Boolean>("cargoStatus"));
		productColumn.setCellValueFactory(new PropertyValueFactory<Cargo,String>("product"));

		TableView.setItems(list);
    }

	@Override
	public void mapInitialized() {
		ObservableList<Users> user = getUser();
		//System.out.println("MainApp.mapInitialised....");

		//Once the map has been loaded by the Webview, initialize the map details.
		LatLong center = new LatLong(user.get(0).getLatitude(), user.get(0).getLongitude());
		mapComponent.addMapReadyListener(() -> {
			// This call will fail unless the map is completely ready.
			checkCenter(center);
		});

		MapOptions options = new MapOptions();
		options.center(center)
				.mapMarker(true)
				.zoom(2)
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




		map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {

			/*if(clickMyMarker!=null){
				map.removeMarker(clickMyMarker);
			}*/

			LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
			Cargo tmp = new Cargo();
			tmp.setLatitude(ll.getLatitude());
			tmp.setLongitude(ll.getLongitude());
			lblClick.setText(ll.toString());

			Double lat = ll.getLatitude();
			Double lon = ll.getLongitude();
			latitudeField.setText(lat.toString());
			longitudeField.setText(lon.toString());
			System.out.println("Latitude: "+ll.getLatitude()+"\nlongitude: "+ll.getLongitude());
			pinMap(tmp,"https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Disc_Plain_blue.svg/32px-Disc_Plain_blue.svg.png");
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
		ObservableList<Cargo> list = getCargoMapList();

		//System.out.println(list.get(0).getLatitude());

		//showMarker(list.get(0).getLatitude(),list.get(0).getLongitude(),"https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Disc_Plain_blue.svg/32px-Disc_Plain_blue.svg.png");



		List<LatLong> latLongList = new ArrayList<>();
		List<Integer> idList = new ArrayList<>();
		latLongList.add(new LatLong(user.get(0).getLatitude(),user.get(0).getLongitude()));
		idList.add(user.get(0).getId());
		map.setCenter(new LatLong(user.get(0).getLatitude(),user.get(0).getLongitude()));
		list.stream().filter(i->i.getLatitude()!=null && i.getLatitude()!=null).forEach(item-> {
			showMarker(item, "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Disc_Plain_blue.svg/32px-Disc_Plain_blue.svg.png");

			LatLong latLong = new LatLong(item.getLatitude(),item.getLongitude());

			latLongList.add(latLong);
			idList.add(item.getId());
		});

		map.zoomProperty().set(map.getZoom() + 1);


		Cargo tmpUser = new Cargo();
		tmpUser.setLatitude(user.get(0).getLatitude());
		tmpUser.setLongitude(user.get(0).getLongitude());
		kuryeToMap(user, "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/Disc_Plain_blue.svg/32px-Disc_Plain_blue.svg.png");

		List<Node> nodeList = new ArrayList<>();
		Node nodeKurye = new Node("kurye",new LatLon(user.get(0).getLatitude(),user.get(0).getLongitude()));
//nodeList.add(nodeKurye);
		for (int i=0;i<list.size();i++){
			Cargo cargo = list.get(i);
			Node node = new Node(list.get(i).getCustomerName(),new LatLon(cargo.getLatitude(),cargo.getLongitude()));
			nodeList.add(node);
			node.setSelected(false);
		}

/*Node nodeA = new Node("A");
Node nodeB = new Node("B");
Node nodeC = new Node("C");
Node nodeD = new Node("D");
Node nodeE = new Node("E");
Node nodeF = new Node("F");*/
		double actualDistance = FlatEarthDist.distance(nodeKurye.getLatLng().getLatitude(), nodeKurye.getLatLng().getLongitude(),
				latLongList.get(0).getLatitude(), latLongList.get(0).getLongitude());
			for (int j=0;j<nodeList.size();j++) {
				System.out.println("Test kurye bilgileri : latitude :  "+nodeKurye.getLatLng().getLatitude()+" longtitude : "+nodeKurye.getLatLng().getLongitude());
				System.out.println("Test Node bilgileri  : latitude :  "+nodeList.get(j).getLatLng().getLatitude()+" longtitude : "+nodeList.get(j).getLatLng().getLongitude());

				actualDistance = FlatEarthDist.distance(nodeKurye.getLatLng().getLatitude(), nodeKurye.getLatLng().getLongitude(),
						nodeList.get(j).getLatLng().getLatitude(), nodeList.get(j).getLatLng().getLongitude());

				System.out.println(" actualDistance : "+actualDistance);
			}

		Random random = new Random(System.currentTimeMillis());
		System.out.println("nodeList.size :"+nodeList.size());
		for (int i=0;i<nodeList.size();i++){
			double distance = Double.MAX_VALUE;
			int destinationIndex = -1;
			for (int j=0;j<nodeList.size();j++){
				if(nodeList.get(j).isSelected()){
					continue;
				}

				actualDistance = FlatEarthDist.distance(nodeKurye.getLatLng().getLatitude(), nodeKurye.getLatLng().getLongitude(),
						nodeList.get(j).getLatLng().getLatitude(), nodeList.get(j).getLatLng().getLongitude());
				if(distance>actualDistance){
					distance = actualDistance;
					destinationIndex = j;
				}
				//nodeList.get(i).addDestination(nodeList.get(j),(int)actualDistance);
			}
			if(destinationIndex!=-1){
				int r1 = random.nextInt(9);
				int r2 = random.nextInt(9);
				int g1 = random.nextInt(9);
				int g2 = random.nextInt(9);
				int b1 = random.nextInt(9);
				int b2 = random.nextInt(9);
				String colorCode = "#"+r1+""+r2+""+g1+""+g2+""+b1+""+b2;
				LatLong[] ary0 = new LatLong[2];
				ary0[0] = new LatLong(nodeKurye.getLatLng().getLatitude(),nodeKurye.getLatLng().getLongitude());
				ary0[1] = new LatLong(nodeList.get(destinationIndex).getLatLng().getLatitude(),nodeList.get(destinationIndex).getLatLng().getLongitude());
				MVCArray mvc0 = new MVCArray(ary0);
				PolylineOptions polyOpts0 = new PolylineOptions()
						.path(mvc0)
						.strokeColor(colorCode)
						.strokeWeight(i+5)
						.editable(false)
						.zIndex(1)
						.visible(true);

				Polyline poly0 = new Polyline(polyOpts0);
				poly0.setEditable(false);
				map.addMapShape(poly0);
				nodeKurye.setLatLng(new LatLon(ary0[1].getLatitude(),ary0[1].getLongitude()));
				nodeList.get(destinationIndex).setSelected(true);
			}
		}


		System.out.println("END MAP INITIALIZED");
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

			try{
				System.out.println("Distancia total = " + e.getRoutes().get(0).getLegs().get(0).getDistance().getText());
			} catch(Exception ex){
				System.out.println("ERRO: " + ex.getMessage());
			}
			System.out.println("LEG(0)");
			System.out.println(e.getRoutes().get(0).getLegs().get(0).getSteps().size());

			System.out.println(renderer.toString());
		}
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

}
