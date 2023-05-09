package com.example.j5;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WindDataChart extends Application {


    @Override
    public void start(Stage stage) {
        // Read in the cumulative probability data from cumProbability.txt
        double[] cumProbabilities = new double[200];
        try (BufferedReader reader = new BufferedReader(new FileReader("cumProbability.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int index = Integer.parseInt(parts[0]);
                double probability = Double.parseDouble(parts[1]);
                cumProbabilities[index] = probability;
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
            return;
        }

        // Set up the axes for the chart
        NumberAxis xAxis = new NumberAxis("Wind Speed Squared", 0, 1300, 100);
        NumberAxis yAxis = new NumberAxis("Cumulative Probability", 0.0001, 1, 10);
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickCount(0);
        yAxis.setTickUnit(0.2);


        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Wind Data Analysis");

        //  discrete data values
        XYChart.Series<Number, Number> discreteSeries = new XYChart.Series<>();
        discreteSeries.setName("Discrete Data Values");
        for (int i = 0; i < cumProbabilities.length; i++) {
            double windSpeedSquared = i * i;
            discreteSeries.getData().add(new XYChart.Data<>(windSpeedSquared, cumProbabilities[i]));
        }

        //  probability that wind speed is less than each square root 2 interval
        XYChart.Series<Number, Number> probSeries = new XYChart.Series<>();
        probSeries.setName("Probability that wind speed is less than each square root 2 interval");
        double k = Math.sqrt(2) / 2;
        for (int i = 0; i < cumProbabilities.length; i++) {
            double windSpeedSquared = i * i;
            double probability = 1 - Math.exp(-k * windSpeedSquared);
            probSeries.getData().add(new XYChart.Data<>(windSpeedSquared, probability));
        }

        //OLS regression line for Maximum Entropy
        XYChart.Series<Number, Number> maxEntropySeries = new XYChart.Series<>();
        maxEntropySeries.setName("OLS Regression Line for Maximum Entropy");
        double m = -0.00022;
        double b = 0.424;
        maxEntropySeries.getData().add(new XYChart.Data<>(0, b));
        maxEntropySeries.getData().add(new XYChart.Data<>(1300, m * 1300 + b));
        lineChart.getData().addAll(discreteSeries, probSeries, maxEntropySeries);

        Scene scene = new Scene(lineChart, 800, 600);

        stage.setScene(scene);
        stage.show();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}