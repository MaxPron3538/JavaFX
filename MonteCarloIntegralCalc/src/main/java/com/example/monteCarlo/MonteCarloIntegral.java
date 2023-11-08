package com.example.monteCarlo;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.function.Function;

import javafx.scene.text.Text;

public class MonteCarloIntegral extends Application {

    public void initialStepOfFunc(int base,int step){
        baseOfFunc = base;
        stepOfFunc = step;
    }

    public double calculateSumOfPowerX(int power,int step){
        double sumXPower = 0;
        for(int cordX = baseOfFunc;cordX < step;cordX++){
            sumXPower += Math.pow(cordX,power);
        }
       return sumXPower;
    }

    public double calculateSumOfPowerXY(int power,int step){
        double sumXYPower = 0;
        for(int cordX = baseOfFunc;cordX < step;cordX++){
            sumXYPower += arrY[cordX]*Math.pow(cordX,power);
        }
        return sumXYPower;
    }

    public double[][] buildMatrixX(int matrixSize,int step){
        double[][] matrixSumPowerX = new double[matrixSize][matrixSize];
        int power = 0,count = 0;

        for(int i = matrixSize-1;i>=0;i--){
            for(int j = matrixSize-1;j>=0;j--){
                matrixSumPowerX[i][j] = calculateSumOfPowerX(power,step);
                power++;
            }
            count++;
            power = count;
        }
        return  matrixSumPowerX;
    }

    public double[] buildVectorY(int vectorSize,int step){
        double[] vectorSumPowerY = new double[vectorSize];
        int power = 0;

        for(int i = vectorSize-1;i>=0;i--){
            vectorSumPowerY[i] = calculateSumOfPowerXY(power,step);
            power++;
        }
        return vectorSumPowerY;
    }

    public double[][] invert(double a[][]){
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i=0; i<n; ++i) {
            b[i][i] = 1;
        }
        gaussian(a, index);

        for (int i=0; i<n-1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];
                }
            }
        }

        for (int i=0; i<n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    public void gaussian(double a[][], int index[]){
        int n = index.length;
        double c[] = new double[n];

        for (int i=0; i<n; ++i) {
            index[i] = i;
        }
        for (int i=0; i<n; ++i){
            double c1 = 0;
            for (int j=0; j<n; ++j){
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        int k = 0;
        for (int j=0; j<n-1; ++j){
            double pi1 = 0;
            for (int i=j; i<n; ++i){
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1){
                    pi1 = pi0;
                    k = i;
                }
            }

            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i=j+1; i<n; ++i){
                double pj = a[index[i]][j]/a[index[j]][j];
                a[index[i]][j] = pj;
                for (int l=j+1; l<n; ++l) {
                    a[index[i]][l] -= pj * a[index[j]][l];
                }
            }
        }
    }

    public double[] calculateSquareCoef(double[][] matrixX,double[] vectorY){
        double[] vectorSquareMV = new double[vectorY.length];

        for (int i = 0; i < matrixX.length;i++) {
            for (int j = 0; j < matrixX.length;j++) {
                vectorSquareMV[i] += matrixX[i][j] * vectorY[j];
            }
        }
        return vectorSquareMV;
    }

    public double[] buildOfPolynomial(int sizeOfPolynomial,int step){
        double[] arrApproximateY = new double[stepOfFunc-baseOfFunc];
        double[] vectorSquareCoef = calculateSquareCoef(invert(buildMatrixX(sizeOfPolynomial,step)),buildVectorY(sizeOfPolynomial,step));
        int count = 0;

        for(int x = baseOfFunc;x < stepOfFunc;x++) {
            int power = sizeOfPolynomial-1;
            for (int c = 0; c < vectorSquareCoef.length; c++) {
                arrApproximateY[count] += Math.pow(x, power) * vectorSquareCoef[c];
                power--;
            }
            count++;
        }
        return arrApproximateY;
    }

    public int selectCorrectPowerOfPoly(int sizeOfFunc,double[] arrInputY,int step){
        int countOfPoly = 2;
        double preDiff = 1000000;

        double midX = Arrays.stream(arrInputY).sum()/arrInputY.length;

        while (true) {
           double postDiff = 0;
           double diffInputY = 0;
           initialStepOfFunc(0, sizeOfFunc);
           double[] arrApproximateY = buildOfPolynomial(countOfPoly, sizeOfFunc);

           for (int i = 0; i < arrInputY.length-step; i++) {
               diffInputY += Math.pow(arrInputY[i]-midX,2);
               postDiff += Math.abs(arrApproximateY[i] - arrInputY[i]);
           }

           if (postDiff < preDiff) {
               if(postDiff < Math.sqrt(diffInputY)){
                   return countOfPoly;
               }
               preDiff = postDiff;
               countOfPoly++;
           }
        }
    }

    public double calculateIntegral(double a,double b){
        double inc = 0.000001;
        double square = 0;

        for(double x = a+inc;x<b;x+=inc){
            double y = 1-Math.sin(x);
            square+=y*inc;
        }
        return square;
    }

    public double squareOfRectangle(double a,double b,double fMax){
        return (b-a)*fMax;
    }

    public double calcIntegral_MonteCarlo(double a,double b,int N){

        double Spar = squareOfRectangle(a,b,fMax);
        int k = 0,count = 0;

        while(count < N){
            Random random = new Random();
            double cordX = random.nextDouble()*(b-a);
            double cordY = random.nextDouble()*fMax;
            double expectFunc = 1-Math.sin(cordX);
            if(mapCord.size() <= 50) {
                mapCord.put(cordX, cordY);
            }
            if(cordY <= expectFunc){
                k++;
            }
            count++;
        }
        return Spar * k/N;
    }

    double calcExpectedMath(int N){
        double sumS = 0;

        for(int i = 0;i < 3;i++) {
            listS.add(calcIntegral_MonteCarlo(a,b,N));
            sumS += listS.get(i);
        }
        return sumS/3;
    }

    double calcDispersion(double expectedMath){
        double sumD = 0;

        for(int i = 0;i < 3;i++){
            sumD += Math.pow(listS.get(i)-expectedMath,2);
        }
        return sumD/3;
    }

    public void drawCom(LineChart lineChart){
        for(Map.Entry cord :  mapCord.entrySet()){
            XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data(cord.getKey(),cord.getValue()));
            lineChart.getData().add(series);
        }
    }

    public LineChart drawSceneOfDiagram(){

        double inc = 0.1;
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Координата X");
        yAxis.setLabel("Координата Y");

        LineChart numberLineChart = new LineChart(xAxis,yAxis);
        numberLineChart.setTitle("Графік функції f(x) = 1-sin(x)");

        XYChart.Series series = new XYChart.Series();
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();

        /*
        for(double cordX = a;cordX <= b+inc;cordX+=inc) {
            double cordY = 1-Math.sin(cordX);
            series.getData().add(new XYChart.Data(cordX,cordY));
            if(cordY > fMax){
               fMax = cordY;
            }
        }
        */
        int sizeOfFunc = 100;
        int step = 2;
        int approxSize = sizeOfFunc+step;
        arrY = new double [approxSize];

        for (double cordX = 0; cordX < sizeOfFunc; cordX++) {
            double cordY = Math.sqrt(cordX/7)+Math.sin(cordX/13); //Math.cos(cordX /14);
            double ys = cordY + 0.3 * Math.random();
            arrY[((int) cordX)] = ys;
            series.getData().add(new XYChart.Data(cordX,ys));
        }

        int power = selectCorrectPowerOfPoly(sizeOfFunc,arrY,step);
        initialStepOfFunc(0, sizeOfFunc+step);
        double[] approximateY = buildOfPolynomial(power, sizeOfFunc);

        for (double cordX = baseOfFunc; cordX < stepOfFunc; cordX++){
            series1.getData().add(new XYChart.Data(cordX, approximateY[(int) cordX]));
        }

        numberLineChart.setCreateSymbols(false);

        series.setName("Коректний Інтеграл - " + calculateIntegral(a,b));
        series1.setName("Інтеграл методом Монте Карло для 50 точок - " + calcIntegral_MonteCarlo(a,b,50));
        series2.setName("Максимум функції - " + fMax);
        numberLineChart.getData().add(series);
        numberLineChart.getData().add(series1);
        numberLineChart.getData().add(series2);

        return numberLineChart;
    }

    public LineChart drawSceneOfMonteCarlo(){
        LineChart lineChartMonteCarlo = drawSceneOfDiagram();
        lineChartMonteCarlo.setTitle("Метод Монте Карло для 50 точок");
        XYChart.Series series = new XYChart.Series();
        drawCom(lineChartMonteCarlo);
        return lineChartMonteCarlo;
    }

    public FlowPane drawSceneOfTable()
    {
        FlowPane root = new FlowPane();
        String[] arrRows = new String[]{"Точне значення Інтегралу","Математичне очікування","Середньо квадратичне відхилення","Абсолютна похибка"};

        ObservableList<TableValue> listRow = FXCollections.observableArrayList(new TableValue("100"),new TableValue("1000"));

        TableView<TableValue> tableRow = new TableView<>(listRow);
        TableColumn<TableValue,String> symbolRow = new TableColumn<>("Кількість точок n");
        symbolRow.setCellValueFactory(new PropertyValueFactory<TableValue,String>("field"));
        tableRow.getColumns().add(symbolRow);
        tableRow.setPrefWidth(120);
        tableRow.setPrefHeight(180);
        root.getChildren().add(tableRow);

        double mCarloHund = calcIntegral_MonteCarlo(a,b,100);
        double mCarloThous = calcIntegral_MonteCarlo(a,b,1000);

        addToTable(mCarloHund,mCarloThous,root,"Точне значення Інтегралу");

        double expectedMathHund = calcExpectedMath(100);
        listS = new ArrayList<>();
        double expectedMathThous = calcExpectedMath(1000);

        addToTable(expectedMathHund,expectedMathThous,root,"Математичне очікування");

        addToTable(calcDispersion(expectedMathHund),calcDispersion(expectedMathThous),root,"Середньо квадратичне відхилення");

        addToTable(expectedMathHund-mCarloHund,expectedMathThous-mCarloThous,root,"Абсолютна похибка");

        return root;
    }

    public void addToTable(double arg1,double arg2,FlowPane root,String column){

        ObservableList<TableValue> list = FXCollections.observableArrayList(
                new TableValue(arg1), new TableValue(arg2));

        TableView<TableValue> table = new TableView<>(list);
        TableColumn<TableValue,String> symbolFColumn = new TableColumn<>(column);
        symbolFColumn.setCellValueFactory(new PropertyValueFactory<TableValue,String>("result"));
        table.getColumns().add(symbolFColumn);
        table.setPrefWidth(230);
        table.setPrefHeight(200);
        root.getChildren().addAll(table);
    }

        @Override
    public void start(Stage PrimaryStage) throws IOException {

            btnSw1 = new Button("Графік Функції");
            btnSw1.setOnAction(
                    e -> SwitchScenes2());

            btnSw2 = new Button("Візуалізація методу Монте Карло");
            btnSw2.setOnAction(
                    e -> SwitchScenes3());

            btnSw3 = new Button("Результати Дослідження");
            btnSw3.setOnAction(
                    e -> SwitchScenes4());

            btnSwBack1 = new Button("Back");
            btnSwBack1.setOnAction(
                    e -> SwitchScenes1());

            btnSwBack2 = new Button("Back");
            btnSwBack2.setOnAction(
                    e -> SwitchScenes1());

            btnSwBack3 = new Button("Back");
            btnSwBack3.setOnAction(
                    e -> SwitchScenes1());

            btnClose = new Button();
            btnClose.setText("Close");
            btnClose.setOnAction(e -> CloseWindowClick());

            btnSw1.setScaleY(2.5);
            btnSw2.setScaleY(2.5);
            btnSw3.setScaleY(2.5);

            btnClose.setScaleY(2.5);

            btnSw1.setLayoutX(20);
            btnSw1.setLayoutY(100);

            btnSw1.setMaxWidth(Double.MAX_VALUE);
            btnSw2.setMaxWidth(Double.MAX_VALUE);
            btnSw3.setMaxWidth(Double.MAX_VALUE);
            btnSwBack1.setMaxWidth(Double.MAX_VALUE);
            btnSwBack2.setMaxWidth(Double.MAX_VALUE);
            btnSwBack3.setMaxWidth(Double.MAX_VALUE);
            btnClose.setMaxWidth(Double.MAX_VALUE);


            vbox1 = new VBox(drawSceneOfDiagram(), btnSwBack1);
            //vBox2 = new VBox(drawSceneOfMonteCarlo(), btnSwBack2);
            vBox3 = new VBox(drawSceneOfTable(), btnSwBack3);


        Label lblTab1 =  new Label();
        lblTab1.setText("                    Курсова робота Проня Максима");
        lblTab1.setScaleX(2);
        lblTab1.setScaleY(2);
        Label lblTab2 = new Label();

        pane = new FlowPane(Orientation.VERTICAL, 30, 100,lblTab1 ,btnSw1, btnSw2,btnSw3,btnClose,lblTab2);

        Image image  = new Image("C:\\Users\\maxpr\\IdeaProjects\\MonteCarlo\\src\\main\\java\\com\\example\\monteCarlo\\OG-BLOG_data.jpg");

        BackgroundImage bImg = new BackgroundImage(image,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundRepeat.NO_REPEAT,
                 BackgroundPosition.DEFAULT,
                 BackgroundSize.DEFAULT);
        Background bGround = new Background(bImg);

        pane.setBackground(bGround);

        vBox3.setStyle("-fx-background-color: burlywood");

        scene1 = new Scene(pane,1200,630);
        scene2 = new Scene(vbox1,1280,500);
       // scene3 = new Scene(vBox2,1280,500);
        scene4 = new Scene(vBox3,1040,300);


        stage = PrimaryStage;
        stage.setScene(scene1);
        stage.setTitle("Integral Monte Carlo");
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
    public void SwitchScenes4(){
        stage.setScene(scene4);
    }
    public void CloseWindowClick(){
        stage.close();
    }

    Map<Double,Double> mapCord = new HashMap<>();
    List<Double> listS = new ArrayList<>();
    double a = 0,b = 3;
    double fMax;
    FlowPane pane;
    Button btnSw1;
    Button btnSw2;
    Button btnSw3;
    Button btnSwBack1;
    Button btnSwBack2;
    Button btnSwBack3;
    Button btnClose;
    VBox vbox1;
    VBox vBox2;
    VBox vBox3;
    Scene scene1;
    Scene scene2;
    Scene scene3;
    Scene scene4;
    Stage stage;
    int stepOfFunc = 0;
    int baseOfFunc = 0;
    double arrY[];
    double inc = 0;


    public static void main(String[] args) {
        Application.launch();
    }


}