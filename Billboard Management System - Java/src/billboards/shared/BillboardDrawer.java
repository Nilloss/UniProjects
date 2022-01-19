package billboards.shared;

//import javafx.scene.paint.Stop;

import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

public class BillboardDrawer {
    public static JFrame billboard;
    private static int Width;
    private static int Height;
    private static Color BackgroundColour;

    public static boolean ISDRAWEROPEN = false;
    public static boolean Preview = false;


//String BillboardTitle, String BackgroundColour, String[] Element1, String[] Element2, String[] Element3
    public static void BillboardDrawerMain(String BackgroundColourin, String[] Element1, String[] Element2, String[] Element3){
        ISDRAWEROPEN = true;
        billboard = new JFrame();
        billboard.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //Add a mouse action listener to close the window
        billboard.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                CloseWindow();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        //Add text field that is used to detect the escape button being pressed
        JTextField textField = new JTextField();
        textField.addKeyListener(new ESCKeyListener());
        billboard.add(textField);
        //billboard.setSize(Width,Height);
        billboard.setLayout(null);

        //adds fullscreen
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(billboard);

        Dimension d = billboard.getSize();
        Height = d.height;
        Width = d.width;
        System.out.println(Height + " " + Width + " " + d);

        BackgroundColour = FindColour(BackgroundColourin);

        //Set the Billboards background colour
        JPanel Background = GUITools.CreatePanel(BackgroundColour);
        Background.setBounds(0,0,Width,Height);
        Background.setLayout(null);
        billboard.add(Background);

        //Check each element for what type it is then pass it to the appropriate method.
        switch (Element1[0]){
            //if None do nothing
            case("Text"):
                JTextArea textArea = CreateText(Element1);
                Background.add(textArea);
                break;
            case ("Image"):
                JLabel label = new JLabel();
                    label = CreateImage(Element1);

                Background.add(label);
                break;
        }
        switch (Element2[0]){
            //if None do nothing
            case("Text"):
                JTextArea textArea = CreateText(Element2);
                Background.add(textArea);
                break;
            case ("Image"):
                JLabel label = new JLabel();
                    label = CreateImage(Element2);

                Background.add(label);
                break;
        }
        switch (Element3[0]){
            //if None do nothing
            case("Text"):
                JTextArea textArea = CreateText(Element3);
                Background.add(textArea);
                break;
            case ("Image"):
                JLabel label = new JLabel();

                    label = CreateImage(Element3);
                Background.add(label);
                break;
        }

        billboard.setVisible(true);

    }

    public static void displayXML(String data) {

        XmlInterpreter interpreter = null;
        try {
            interpreter = new XmlInterpreter(data, false);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        String[][] result = interpreter != null ? interpreter.xmlToStringMatrix() : new String[0][];
        String[][] tempElement = new String[3][8];

        String[] CurrentBillboardElement1;
        String[] CurrentBillboardElement2;
        String[] CurrentBillboardElement3;

        for (String[] res : result) {
            switch (res[0]) {
                case "information":
                    tempElement[0][0] = "Text";
                    tempElement[0][1] = res[1];

                    tempElement[0][2] = "50";
                    tempElement[0][3] = "40";
                    tempElement[0][4] = "85";
                    tempElement[0][5] = "34";

                    tempElement[0][6] = "BLACK";

                    tempElement[0][7] = "40";

                    break;
                case "message":
                    tempElement[1][0] = "Text";
                    tempElement[1][1] = res[1];

                    tempElement[1][2] = "50";
                    tempElement[1][3] = "20";
                    tempElement[1][4] = "25";
                    tempElement[1][5] = "32";

                    tempElement[1][6] = "BLACK";

                    tempElement[1][7] = "100";
                    break;
                case "picture":
                    tempElement[2][0] = "Image";
                    tempElement[2][1] = res[2].toUpperCase();
                    tempElement[2][2] = res[3];

                    tempElement[2][3] = "30";
                    tempElement[2][4] = "45";
                    tempElement[2][5] = "50";
                    tempElement[2][6] = "60";

                    tempElement[2][7] = null;
                    break;
            }
        }

        if (tempElement[0][0] != null) CurrentBillboardElement1 = tempElement[0];
        else CurrentBillboardElement1 = new String[]{"Text", " ", "50", "20", "25", "32", "BLUE", "100"};
        if (tempElement[1][0] != null) CurrentBillboardElement2 = tempElement[1];
        else CurrentBillboardElement2 = new String[]{"Text", " ", "50", "20", "25", "32", "BLUE", "100"};
        if (tempElement[2][0] != null) CurrentBillboardElement3 = tempElement[2];
        else CurrentBillboardElement3 = new String[]{"Text", " ", "50", "20", "25", "32", "BLUE", "100"};

        BillboardDrawer.BillboardDrawerMain("GREY", CurrentBillboardElement1, CurrentBillboardElement2, CurrentBillboardElement3);
    }

    /**
     * Finds the colour variable that was input
     * @param Input A string of the color that is trying to be found
     * @return  A colour variable that can be implemented.
     */
    private static Color FindColour(String Input){
        Color output = Color.WHITE;
        //Check each possible input and output the corresponding colour
        switch (Input){
            case ("LIGHT_GRAY"):
                output = Color.LIGHT_GRAY;
                break;
            case ("GRAY"):
                output = Color.GRAY;
                break;
            case ("DARK_GRAY"):
                output = Color.DARK_GRAY;
                break;
            case ("BLACK"):
                output = Color.BLACK;
                break;
            case ("RED"):
                output = Color.RED;
                break;
            case ("PINK"):
                output = Color.PINK;
                break;
            case ("ORANGE"):
                output = Color.ORANGE;
                break;
            case ("YELLOW"):
                output = Color.YELLOW;
                break;
            case ("GREEN"):
                output = Color.GREEN;
                break;
            case ("MAGENTA"):
                output = Color.MAGENTA;
                break;
            case ("CYAN"):
                output = Color.CYAN;
                break;
            case ("BLUE"):
                output = Color.BLUE;
                break;
        }
        return output;
    }

    /**
     * Creates the text field that contains the requested text
     * @param Element A String array with the element values
     * @return A Jtext area with the text on it
     */
    private static JTextArea CreateText(String[] Element){
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setFont(new Font("Arial", Font.BOLD, Integer.parseInt(Element[7])));
        textArea.setText(Element[1]);
        textArea.setBounds(ResizeToScreen(Element[5], Width), ResizeToScreen(Element[4], Height), ResizeToScreen(Element[2], Width), ResizeToScreen(Element[3], Height));
        textArea.setText(Element[1]);
        textArea.setForeground(FindColour(Element[6]));
        textArea.setOpaque(false);
        return textArea;
    }

    /**
     * Builds a JLabel that contains an image from a URL that can then be resized and moved to the desired location
     * From: https://stackoverflow.com/questions/18550284/java-resize-image-from-an-url, And https://stackoverflow.com/questions/42752611/display-url-image-into-a-jpanel, AND //https://stackoverflow.com/questions/10621687/how-to-get-full-path-directory-from-file-chooser/10621739
     * @param Element A String Array with the Desired image properties
     * @return A JLabel with the image in it
     */
    public static JLabel CreateImage(String[] Element) {
        URL url = null;
        int WID  = ResizeToScreen(Element[3], Width);
        int HIG = ResizeToScreen(Element[4], Height);
        //Make it so that the entered X and Y position are at the Centre of the Image
        int PosX = ResizeToScreen(Element[5], Width) - WID/2;
        int PosY = ResizeToScreen(Element[6], Height) - HIG/2;
        BufferedImage inputImage = null;
        BufferedImage outputImage = null;
        if(Element[1].equals("URL")) {
            try {
                url = new URL(Element[2]);
            } catch (MalformedURLException e) {
                JLabel Imagenotfound = new JLabel("Image Not Found");
                Imagenotfound.setBounds(Width/2 - 300/2, Height/2, 300, 100);
                Imagenotfound.setFont(new Font("Arial", Font.PLAIN, 20));
                System.out.println("Image Not Found");
                return Imagenotfound;
            }
            try {
                inputImage = ImageIO.read(url);
            } catch (IOException e) {
                JLabel Imagenotfound = new JLabel("Image Not Found");
                Imagenotfound.setBounds(Width/2 - 300/2, Height/2, 300, 100);
                Imagenotfound.setFont(new Font("Arial", Font.PLAIN, 20));
                System.out.println("Image Not Found");
                return Imagenotfound;
            }
        }
        else if(Element[1].equals("DATA")){
            byte[] bytes = Base64.getDecoder().decode(Element[2]);
            ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
            try {
                inputImage = ImageIO.read(bin);
            } catch (IOException e) {
                JLabel Imagenotfound = new JLabel("Image Not Found");
                Imagenotfound.setBounds(Width/2 - 300/2, Height/2, 300, 100);
                Imagenotfound.setFont(new Font("Arial", Font.PLAIN, 20));
                System.out.println("Image Not Found");
                return Imagenotfound;
            }
        }

        try {
            outputImage = new BufferedImage(WID, HIG, inputImage.getType());
        }
        catch (NullPointerException e){
            JLabel Imagenotfound = new JLabel("Image Not Found");
            Imagenotfound.setBounds(Width/2 - 300/2, Height/2, 300, 100);
            Imagenotfound.setFont(new Font("Arial", Font.PLAIN, 20));
            System.out.println("Image Not Found");
            return Imagenotfound;
        }
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, WID, HIG, null);
        g2d.dispose();

        JLabel label = new JLabel(new ImageIcon(outputImage));
        label.setBounds(PosX, PosY, WID, HIG);
        return label;

    }

    /**
     * Closes the billboard drawers window.
     */
    public static void CloseWindow(){
        billboard.dispose();
        Preview = false;
        ISDRAWEROPEN = false;
    }



    /**
     * Finds the position on the screen using the 1 to 100 number of the element as a percentage across the screen
     * @param Value The string with the percentage value
     * @param dimension what plane we are looking for
     * @return A int value of the screen position
     */
    private static int ResizeToScreen(String Value, int dimension){
        double WT = Double.parseDouble(Value);
        double WID = dimension;
        double Pos = WID * (WT/100);
        return  (int)Pos;
    }

    /**
     * An escape key listener that detects when the escape key is pressed it than either closes the display or exits the program depending
     * on whether the drawer was called as a preview from the control panel or as a viewer from the viewer
     */
    private static class ESCKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            char ch = event.getKeyChar();
            if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                //If escape is pressed the Window closes
                if(Preview) {
                    billboard.dispose();
                }
                else{
                    System.exit(0);
                }
                Preview = false;
                ISDRAWEROPEN = false;
            }
        }
    }

    /**
     * A mouse click listener that detects when the mouse is clicked anywhere on the viewer and it either closes the display or exits the program depending
     * on whether the drawer was called as a preview from the control panel or as a viewer from the viewer
     */
    private static class MouseClickListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            //If the mouse is clicked the Window closes
            if(Preview) {
                billboard.dispose();
            }
            else{
                System.exit(0);
            }
            Preview = false;
            ISDRAWEROPEN = false;
        }
    }

}
