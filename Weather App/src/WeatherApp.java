import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class WeatherApp {

    private JSONObject weatherData;
    JFrame frame;
    JTextField textField;
    Font myFont = new Font("Exo 2", Font.BOLD,30);
    JButton searchButton;
    JLabel weatherConditionImage;
    JLabel weatherDesc;
    JLabel tempLevel;
    JLabel humidityImage;
    JLabel humidityLevel;
    JLabel windspeedImage;
    JLabel windspeedLevel;

    public WeatherApp(){
        frame = new JFrame("Weather App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(420, 550);
        frame.setLayout(null);

        textField = new JTextField();
        textField.setBounds(50,25, 250, 50);
        textField.setFont(myFont);
        textField.setEditable(true);

        searchButton = new JButton(loadImage("src/Assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(315, 25, 50, 50);

        weatherConditionImage = new JLabel(loadImage("src/Assets/cloudy2.png"));
        weatherConditionImage.setBounds(0, 100, 395, 217);

        tempLevel = new JLabel("10 C");
        tempLevel.setFont(new Font("Exo 2", Font.BOLD, 40));
        tempLevel.setBounds(170, 280, 450, 54);

        weatherDesc = new JLabel("Cloudy");
        weatherDesc.setFont(new Font("Exo 2", Font.BOLD, 30));
        weatherDesc.setBounds(160, 315, 450, 54);

        windspeedImage = new JLabel(loadImage("src/Assets/windspeed2.png"));
        windspeedImage.setBounds(25, 370, 100, 65);

        windspeedLevel = new JLabel("Windspeed: 10 km/h");
        windspeedLevel.setFont(new Font("Exo 2", Font.BOLD, 15));
        windspeedLevel.setBounds(25, 425, 450, 54);

        humidityImage = new JLabel(loadImage("src/Assets/humidity2.png"));
        humidityImage.setBounds(275, 370, 100, 65);

        humidityLevel = new JLabel("Humidity: 50%");
        humidityLevel.setFont(new Font("Exo 2", Font.BOLD, 15));
        humidityLevel.setBounds(275, 425, 450, 54);

        searchButton = new JButton(loadImage("src/Assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(315, 25, 50, 50);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = textField.getText();

                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                weatherData = AppFunctions.getWeatherData(userInput);

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/Assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/Assets/cloudy2.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/Assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/Assets/snow.png"));
                        break;
                }

                double temp = (double) weatherData.get("temp");
                tempLevel.setText(temp + " C");

                weatherDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityLevel.setText(humidity + " %");

                double windspeed = (double) weatherData.get("windspeed");
                windspeedLevel.setText(windspeed + " km/h");


            }
        });

        frame.add(humidityLevel);
        frame.add(windspeedLevel);
        frame.add(windspeedImage);
        frame.add(humidityImage);
        frame.add(weatherDesc);
        frame.add(tempLevel);
        frame.add(weatherConditionImage);
        frame.add(searchButton);
        frame.add(textField);
        frame.setVisible(true);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);

        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find file");
        return null;
    }


    public static void main(String[] args) {
        WeatherApp a = new WeatherApp();

//        System.out.print(AppFunctions.getLocationData("Toronto"));

//        System.out.print(AppFunctions.getCurrentTime());



    }
}