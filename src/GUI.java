import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;

public class GUI extends JFrame {

    private JSONObject weatherData;

    // define GUI features
    public GUI() {
        super("weather app");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 650);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        
        // search box attributes
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15,15,351,45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        // weather images
        JLabel weatherConditionImage = new JLabel(loadImage("images/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);
        
        // temperature
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
        
        // weather conditions
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);
        
        // humidity attributes
        JLabel humidityImage = new JLabel(loadImage("images/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);
        JLabel humidityText = new JLabel("<html><b>Humidity:</b> 100% </html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);
        
        // windspeed attributes
        JLabel windspeedImage = new JLabel(loadImage("images/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);
        
        // search button functionality
        JButton searchButton = new JButton(loadImage("images/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // get users location
                String input = searchTextField.getText();

                // validation
                if(input.replaceAll("\\s","").length() <= 0) {
                    return;
                }

                // get weather data
                weatherData = WeatherApp.getWeatherData(input);

                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");
                
                switch(weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("images/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("images/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("images/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("images/snow.png"));
                        break;
                }

                // updating attributes
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
                
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });

        add(searchButton);
    }

    // create images
    private ImageIcon loadImage(String resourcePath) {

        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(resourcePath));

            return new ImageIcon(image);
        } 
        catch(IOException exception) {
            exception.printStackTrace();
        }

        System.out.println("Could not find image");

        return null;
    }
}
