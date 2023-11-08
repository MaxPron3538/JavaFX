package com.example.syntaxanalysis;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

import javafx.scene.text.Text;

public class HelloApplication extends Application {

    Map<String,Integer> mapOfMatches = new HashMap<>();
    double[] arrSymbolFrequency;

    public void Initialize() {
        mapOfMatches.put(" ",0);

        for(int i = 0;i < 1200;i++) {
            if((i >= 32 & i <= 59)|(i >= 1040 & i <= 1111)){

                mapOfMatches.put(String.valueOf((char)i), 0);
            }
        }
    }

    public String parsingOfFile(String fieldPath){

        int sizeFile = 0;

        String basePath = "C://Users//maxpr//IdeaProjects//SyntaxAnalysis//src//main//java//com//example//syntaxanalysis//";

        try(InputStreamReader in = new InputStreamReader(new FileInputStream(basePath+fieldPath))) {

            arrSymbolFrequency = new double[mapOfMatches.size()];
            int count = 0,symbol = -1;

            while((symbol = in.read()) != -1) {
                if(mapOfMatches.containsKey(String.valueOf((char)symbol))){

                    int frequency = mapOfMatches.get(String.valueOf((char)symbol));
                    mapOfMatches.replace(String.valueOf((char)symbol),frequency+=1);
                }
                sizeFile++;
            }

            for(Map.Entry<String,Integer> entry : mapOfMatches.entrySet()){

                arrSymbolFrequency[count] = (double)entry.getValue()/(double)sizeFile;
                System.out.println(entry.getKey() + " " + entry.getValue() + " " + arrSymbolFrequency[count]);
                count++;
            }
        }
        catch(IOException ex) {

            System.out.println(ex.getMessage());
            return "ERROR,Wrong name of file";
        }
        return "Complete";
    }

    public double getMaxFrequency()
    {
        double maxFrequency = arrSymbolFrequency[0];

        for (int i = 1;i < arrSymbolFrequency.length;i++)
        {
            if(arrSymbolFrequency[i] > maxFrequency)
            {
                maxFrequency = arrSymbolFrequency[i];
            }
        }
        return maxFrequency;
    }

    public double getMinFrequency()
    {
        double minFrequency = arrSymbolFrequency[0];

        for(int i = 1; i < arrSymbolFrequency.length;i++)
        {
            if(arrSymbolFrequency[i] < minFrequency)
            {
                minFrequency = arrSymbolFrequency[i];
            }
        }
        return minFrequency;
    }

    public LineChart<String,Number> drawSceneOfDiagram()
    {
        List<String> arrSymbols = mapOfMatches.keySet().stream().toList();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Symbols");
        yAxis.setLabel("Frequency");

        xAxis.setCategories(FXCollections.<String>observableArrayList(arrSymbols));

        LineChart<String,Number> numberLineChart = new LineChart<String,Number>(xAxis,yAxis);
        numberLineChart.setTitle("Diagram of Syntax Analysis");

        XYChart.Series<String,Number> series = new XYChart.Series<>();

        for(Map.Entry<String,Integer> entry : mapOfMatches.entrySet())
        {
            series.getData().add(new XYChart.Data<>(entry.getKey(),entry.getValue()));
        }
        numberLineChart.getData().add(series);
        return numberLineChart;
    }

    public FlowPane drawSceneOfTable()
    {
        FlowPane root = new FlowPane();
        int count = 0;

        ObservableList<TableFrequency> listRow = FXCollections.observableArrayList(
                new TableFrequency("Frequency"),
                new TableFrequency("P(n)"));

        TableView<TableFrequency> tableRow = new TableView<>(listRow);
        TableColumn<TableFrequency,String> symbolRow = new TableColumn<>("Frequency");
        symbolRow.setCellValueFactory(new PropertyValueFactory<TableFrequency,String>("field"));
        tableRow.getColumns().add(symbolRow);
        tableRow.setPrefWidth(120);
        tableRow.setPrefHeight(180);
        root.getChildren().add(tableRow);

        for (Map.Entry<String,Integer> entry : mapOfMatches.entrySet())
        {
            ObservableList<TableFrequency> list = FXCollections.observableArrayList(new TableFrequency(entry.getValue()),new TableFrequency(arrSymbolFrequency[count]));
            TableView<TableFrequency> table = new TableView<>(list);
            TableColumn<TableFrequency,BigDecimal> symbolFColumn = new TableColumn<>(entry.getKey());
            symbolFColumn.setCellValueFactory(new PropertyValueFactory<TableFrequency,BigDecimal>("frequency"));
            table.getColumns().add(symbolFColumn);
            table.setPrefWidth(30);
            table.setPrefHeight(160);
            root.getChildren().addAll(table);
            count++;
        }
        return root;
    }

        @Override
    public void start(Stage PrimaryStage) throws IOException {

        btnSw1 = new Button("Diagram Of Frequency");
        btnSw1.setOnAction(
                e -> SwitchScenes2());

        btnSw2 = new Button("Table Of Frequency");
        btnSw2.setOnAction(
                e -> SwitchScenes3());

        btnSwBack1 = new Button("Back");
        btnSwBack1.setOnAction(
                e -> SwitchScenes1());

        btnSwBack2 = new Button("Back");
        btnSwBack2.setOnAction(
                e -> SwitchScenes1());

        btn1Close = new Button();
        btn1Close.setText("Close!");
        btn1Close.setOnAction(e -> CloseWindowClick());

        btn2Close = new Button();
        btn2Close.setText("Close!");
        btn2Close.setOnAction(e -> CloseWindowClick());

        btnSw1.setScaleY(2);
        btnSw2.setScaleY(2);

        HBox.setHgrow(btnSw1,Priority.ALWAYS);
        HBox.setHgrow(btnSw2,Priority.ALWAYS);
        HBox.setHgrow(btnSwBack1,Priority.ALWAYS);
        HBox.setHgrow(btnSwBack2,Priority.ALWAYS);
        btnSw1.setMaxWidth(Double.MAX_VALUE);
        btnSw2.setMaxWidth(Double.MAX_VALUE);
        btnSwBack1.setMaxWidth(Double.MAX_VALUE);
        btnSwBack2.setMaxWidth(Double.MAX_VALUE);

        Initialize();

        vbox1 = new VBox(10);
        vBox2 = new VBox(10);

        Label lblMax =  new Label();
        Label lblMin = new Label();

        Text text1 = new Text("Input file name:");
        text1.setFill(Color.AQUA);
        Label lbl = new Label();
        TextField textField = new TextField();
        textField.setPrefColumnCount(11);
        btn = new Button("Click");

        btn.setOnAction(new EventHandler<ActionEvent>() {
                       @Override
                       public void handle(ActionEvent actionEvent) {
                          lbl.setText(parsingOfFile(textField.getText()));
                          vbox1.getChildren().addAll(drawSceneOfDiagram(), btnSwBack1, btn1Close);
                          vBox2.getChildren().addAll(drawSceneOfTable(), btnSwBack2, btn2Close);
                          lblMax.setText("P(n)max = " + String.valueOf(getMaxFrequency()));
                          lblMin.setText("P(n)min = " + String.valueOf(getMinFrequency()));
                       }
        });

        pane = new FlowPane(Orientation.VERTICAL, 10, 30, btnSw1, btnSw2,text1, textField, lbl, btn,lblMax,lblMin);

        Image image  = new Image("C:\\Users\\maxpr\\IdeaProjects\\SyntaxAnalysis\\src\\main\\java\\com\\example\\syntaxanalysis\\datavisualizationtips_hdr.jpg");

        BackgroundImage bImg = new BackgroundImage(image,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundPosition.DEFAULT,
                 BackgroundSize.DEFAULT);
        Background bGround = new Background(bImg);

        pane.setBackground(bGround);

        vBox2.setStyle("-fx-background-color: burlywood");

        scene1 = new Scene(pane,600,360);
        scene2 = new Scene(vbox1,1280,500);
        scene3 = new Scene(vBox2,1590,450);

        stage = PrimaryStage;
        stage.setScene(scene1);
        stage.setTitle("SyntaxAnalysis");
        stage.show();
    }

    public void SwitchScenes1(){
        stage.setScene(scene1);
    }
    public void SwitchScenes2(){
        stage.setScene(scene2);
    }
    public void SwitchScenes3(){
        stage.setScene(scene3);
    }
    public void CloseWindowClick(){
        stage.close();
    }

    FlowPane pane;
    Button btn;
    Button btnSw1;
    Button btnSw2;
    Button btnSwBack1;
    Button btnSwBack2;
    Button btn1Close;
    Button btn2Close;
    VBox vbox1;
    VBox vBox2;
    Scene scene1;
    Scene scene2;
    Scene scene3;
    Stage stage;

    public static void main(String[] args) {
        launch();
    }
}