package com.example.j5;

import java.io.*;
import java.util.Scanner;

public class Wind {


    float[] windValues = new float[9000];


    static class Bin {
        float interval;
        int count;
        float cumprobability;

        Bin(float interval, int count, float cumprobability) {
            this.interval = interval;
            this.count = count;
            this.cumprobability = cumprobability;
        }
    }

    Bin[] histogram = new Bin[200];


    float userDefinedInterval;


    int numWindValues;

    public static void main(String[] args) {
        Wind wind = new Wind();


        Scanner input = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String fileName = input.nextLine();
        System.out.print("Enter interval width (50-100): ");
        float intervalWidth = input.nextFloat();


        while (intervalWidth < 50 || intervalWidth > 100) {
            System.out.print("Invalid interval width, enter a value between 50 and 100: ");
            intervalWidth = input.nextFloat();
        }
        wind.userDefinedInterval = intervalWidth;


        wind.histogram = new Bin[200];
        for (int i = 0; i < 200; i++) {
            wind.histogram[i] = new Bin(i * intervalWidth, 0, 0);
        }


        try {
            File file = new File(fileName);
            Scanner fileInput = new Scanner(file);


            for (int i = 0; i < 7; i++) {
                fileInput.nextLine();
            }


            while (fileInput.hasNextLine()) {
                String line = fileInput.nextLine();
                String[] values = line.split(",");


                try {
                    float windSpeed = Float.parseFloat(values[5]);
                    wind.windValues[wind.numWindValues] = windSpeed;
                    wind.numWindValues++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid wind speed value: " + values[5]);
                }
            }
            fileInput.close();


            for (int i = 0; i < wind.numWindValues; i++) {
                float squaredWindSpeed = wind.windValues[i] * wind.windValues[i];
                for (int j = 0; j < 200; j++) {
                    if (squaredWindSpeed >= wind.histogram[j].interval && squaredWindSpeed < wind.histogram[j+1].interval) {
                        wind.histogram[j].count += 1;
                        break;
                    }
                }
            }


            float cumulativeProbability = 1.0f;
            for (int j = 0; j < 200; j++) {
                cumulativeProbability -= (float)wind.histogram[j].count / (float)wind.numWindValues;
                if (cumulativeProbability < 0.0) {
                    break;
                }
                wind.histogram[j].cumprobability = cumulativeProbability;

                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter("cumProbability.txt", true));
                    String output = j + " " + wind.histogram[j].cumprobability + "\n";
                    writer.write(output);
                } catch (IOException e) {
                    System.out.println("Error writing to file: " + e.getMessage());
                } finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException e) {
                            System.out.println("Error closing writer: " + e.getMessage());
                        }
                    }
                }
            }

            float num = 0.0f;
            float den = 0.0f;
            for (int j = 0; j < 200; j++) {
                if (wind.histogram[j].cumprobability <= 0.01) {
                    break;
                }
                num -= Math.log(wind.histogram[j].cumprobability);
                den += wind.histogram[j+1].interval;
            }
            float k = num/den;

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter 'less', 'greaterEq', or 'q' to quit: ");
                String i = scanner.nextLine();

                if (i.equalsIgnoreCase("q")) {
                    System.out.println("Exiting program...");
                    break;
                }

                if (!i.equalsIgnoreCase("less") && !i.equalsIgnoreCase("greaterEq")) {
                    System.out.println("Invalid input, please enter 'less', 'greaterEq', or 'q' to quit.");
                    continue;
                }

                System.out.print("Enter wind speed: ");
                float windSpeed;

                try {
                    windSpeed = Float.parseFloat(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, please enter a valid float value.");
                    continue;
                }

                if (i.equalsIgnoreCase("less")) {
                    float prob = 1.0f - (float)Math.exp(-k * Math.pow(windSpeed, 2));
                    System.out.println("Probability wind speed < " + windSpeed + " is " + prob);
                } else if (i.equalsIgnoreCase("greaterEq")) {
                    float prob = (float)Math.exp(-k * Math.pow(windSpeed, 2));
                    System.out.println("Probability wind speed >= " + windSpeed + " is " + prob);
                }
            }
            System.out.println("Successfully read " + wind.numWindValues + " wind speed values from file.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }
    }

}