package billboards.controlPanel;

import billboards.shared.BillboardDrawer;
import billboards.shared.GUITools;
import billboards.shared.TCPMessage;
import billboards.shared.XmlCreator;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class BillboardEditor extends JFrame {
    public static int WIDTH = 1600;
    public static int HEIGHT = 400;
    private static JFrame frame;
    public static boolean EDITWINDOWOPEN = false;

    //Values for the dropdown boxes
    private static String[] colours = {"WHITE", "LIGHT_GRAY", "GRAY", "DARK_GRAY", "BLACK", "RED", "PINK", "ORANGE", "YELLOW", "GREEN", "MAGENTA", "CYAN", "BLUE"};
    private static String[] elementOptions = {"None", "Text", "Image"};
    private static String[] ImageTypeOptions = {"URL", "DATA"};

    //Make the element panels Global variables
    private static JPanel Element1;
    private static JPanel Element2;
    private static JPanel Element3;

    /**
     * Initialise the Component values so that these values can be entered if it is an edit and set to 0 if it is a new billboard
     */
    public static String BillboardName = "";
    public static String BackgroundColour = "WHITE";
    public static int ColourIndex = 0;
    public static String[] Element1Values = new String[8];
    public static int Element1Index = 0;
    public static String[] Element2Values = new String[8];
    public static int Element2Index = 0;
    public static String[] Element3Values = new String[8];
    public static int Element3Index = 0;

    private static int Image1Index = 0;
    private static int Image2Index = 0;
    private static int Image3Index = 0;

    public static int TextcolourIndex1 = 4;
    public static int TextcolourIndex2 = 4;
    public static int TextcolourIndex3 = 4;

    public static boolean ISEDIT = false;
    public static boolean ERRORINVALUES = false;

    /**
     * Need to make all of the component values global variables, this is so that they can be grabed from other methods in the class.
     * Unfortunately this has to be done for all of the components even components that are not going to hold variable.
     * This is so that the billboardeditormain method is not over loaded and more difficult to understand
     */
    private static JTextField billboardnameText; private static JComboBox<String> backgroundColourBox; private static JComboBox<String> Element1OptionBox; private static JComboBox<String> Element2OptionBox; private static JComboBox<String> Element3OptionBox;

    private static JLabel Textcontentslabel1; private static JLabel WidthvalueLabel1; private static JLabel HeightvalueLabel1; private static JLabel ColourLabel1; private static JLabel Fontsizelabel1; private static JLabel YPositionLabel1; private static JLabel XPositionLabel1;

    private static JLabel Textcontentslabel2; private static JLabel WidthvalueLabel2; private static JLabel HeightvalueLabel2; private static JLabel ColourLabel2; private static JLabel Fontsizelabel2; private static JLabel YPositionLabel2; private static JLabel XPositionLabel2;

    private static JLabel Textcontentslabel3; private static JLabel WidthvalueLabel3; private static JLabel HeightvalueLabel3; private static JLabel ColourLabel3; private static JLabel Fontsizelabel3; private static JLabel YPositionLabel3; private static JLabel XPositionLabel3;

    private static JTextField Textcontent1; private static JTextField Widthvalue1; private static JTextField Heightvalue1; private static JComboBox<String> TextColour1; private static JTextField Fontsize1; private static JTextField YpositionValue1; private static JTextField XpositionValue1;


    private static JTextField Textcontent2; private static JTextField Widthvalue2; private static JTextField Heightvalue2; private static JComboBox<String> TextColour2; private static JTextField Fontsize2; private static JTextField YpositionValue2; private static JTextField XpositionValue2;

    private static JTextField Textcontent3; private static JTextField Widthvalue3; private static JTextField Heightvalue3; private static JComboBox<String> TextColour3; private static JTextField Fontsize3; private static JTextField YpositionValue3; private static JTextField XpositionValue3;

    private static JLabel ImageTypeLabel1; private static JComboBox<String> ImageTypeSelection1;

    private static JLabel DataLabel1; private static JButton Directoryopen1; private static JTextField Base64Image1;

    private static JLabel URLcontentslabel1; private static JTextField URLcontent1;

    private static JLabel ImageTypeLabel2; private static JComboBox<String> ImageTypeSelection2;

    private static JLabel DataLabel2; private static JButton Directoryopen2; private static JTextField Base64Image2;

    private static JLabel URLcontentslabel2; private static JTextField URLcontent2;

    private static JLabel ImageTypeLabel3; private static JComboBox<String> ImageTypeSelection3;

    private static JLabel DataLabel3; private static JButton Directoryopen3; private static JTextField Base64Image3;

    private static JLabel URLcontentslabel3; private static JTextField URLcontent3;

    /**
     * Method for if an existing billboard is being edited. it sets all the element values to their existing values.
     * It also sets up the fact that the frame will automatically display the values on the editor screen.
     * @param BillboardTitle A string value for the title of the billboard being edited
     * @param BackgroundColour A string value for the current Background colour of the billboard being edited
     * @param Element1 A string array of the values of Element 1 for the billboard being edited
     * @param Element2 A string array of the values of Element 2 for the billboard being edited
     * @param Element3 A string array of the values of Element 3 for the billboard being edited
     */
    public static void EditBillboard(String BillboardTitle, String BackgroundColour, String[] Element1, String[] Element2, String[] Element3,String billboardId, String creatorId){
        //Set the title to the existing billboard title
        BillboardName = BillboardTitle;
        //Find the correct starting index for the background colour
        ColourIndex = GUITools.FindIndexNumber(colours, BackgroundColour);
        Element1Index = GUITools.FindIndexNumber(elementOptions, Element1[0]);
        Element2Index = GUITools.FindIndexNumber(elementOptions, Element2[0]);
        Element3Index = GUITools.FindIndexNumber(elementOptions, Element3[0]);

        if(Element1Index == 2){
            Image1Index = GUITools.FindIndexNumber(ImageTypeOptions, Element1[1]);
        }
        if(Element2Index == 2){
            Image2Index = GUITools.FindIndexNumber(ImageTypeOptions, Element2[1]);
        }
        if(Element3Index == 2){
            Image3Index = GUITools.FindIndexNumber(ImageTypeOptions, Element3[1]);
        }

        //This checks whether there is a value for text colour. given that if there is no value in this position unless
        //The element is a Text type, this means that this component must be the Text colour.
        //Get the index number of the selection so that the initial value can be set.
        if(!Element1[6].isEmpty()) {
            TextcolourIndex1 = GUITools.FindIndexNumber(colours, Element1[6]);
        }
        if(!Element2[6].isEmpty()) {
            TextcolourIndex2 = GUITools.FindIndexNumber(colours, Element2[6]);
        }
        if(!Element3[6].isEmpty()) {
            TextcolourIndex3 = GUITools.FindIndexNumber(colours, Element3[6]);
        }

        //place the existing Element values into the global String arrays
        Element1Values = Element1;
        Element2Values = Element2;
        Element3Values = Element3;
        //set the is edit variable to true so that the main edit method will act appropriately
        ISEDIT = true;
        //Pass to the main edit method
        BillboardEditormain(billboardId,creatorId);
    }



    /**
     * The main method for the Billboard editor. Gives the ability to select the name and background colour of the billboard.
     * It also gives the option of adding up to 3 elements to the billboard, these elements are either text or an image.
     */
    public static void BillboardEditormain(String billboardId,String creatorId){
        //If the editor is not in edit mode reset all of the variable to their default values.
        //This is to ensure that all of the values are reset if an edit window was closed and a new billboard window is open.
        if (!ISEDIT){
            //Reset the billboard name
            BillboardName = "";
            //reset all of the indexes
            ColourIndex = 0;
            Element1Index = 0;
            Element2Index = 0;
            Element3Index = 0;
            Image1Index = 0;
            Image2Index = 0;
            Image3Index = 0;
            TextcolourIndex1 = 4;
            TextcolourIndex2 = 4;
            TextcolourIndex3 = 4;
        }
        //Initialise the frame
        frame = new JFrame("Billboard Editor");
        EDITWINDOWOPEN = true;
        frame.setSize(WIDTH, HEIGHT);

        //Window close warning. used to prevent accidental closing and loss of work from https://tips4java.wordpress.com/2009/05/01/closing-an-application/
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();

                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit the application? All unsaved work will be lost",
                        "Exit Application",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION)
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    ISEDIT = false;
                    EDITWINDOWOPEN = false;
            }
        });

        //Create the input panel for the element selection menu
        JPanel inputPanel = GUITools.CreatePanel(Color.WHITE);
        //Create a Border to increase aesthetic of the window
        JPanel BorderPanel1 = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel BorderPanel2 = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel BorderPanel3 = GUITools.CreatePanel(Color.LIGHT_GRAY);
        JPanel BorderPanel4 = GUITools.CreatePanel(Color.LIGHT_GRAY);

        //Add the panels to the frame
        frame.getContentPane().add(inputPanel, BorderLayout.CENTER);
        frame.getContentPane().add(BorderPanel1, BorderLayout.SOUTH);
        frame.getContentPane().add(BorderPanel2, BorderLayout.WEST);
        frame.getContentPane().add(BorderPanel3, BorderLayout.EAST);
        frame.getContentPane().add(BorderPanel4, BorderLayout.NORTH);

        //Input setup.
        //Input panel layout. Grid layout so that the Elements are rowed
        inputPanel.setLayout(new GridLayout(6, 1));
        //Billboard name field
        JPanel BillboardNamePanel = GUITools.CreatePanel(Color.WHITE);
        //Background colour field
        JPanel backcolour = GUITools.CreatePanel(Color.WHITE);
        //Element Fields
        Element1 = GUITools.CreatePanel(Color.WHITE);
        Element2 = GUITools.CreatePanel(Color.WHITE);
        Element3 = GUITools.CreatePanel(Color.WHITE);
        //apply and preview button field
        JPanel ButtonPanel = GUITools.CreatePanel(Color.WHITE);

        //Initialise the element component for each option
        InitialiseTextcomponents(4);
        InitialiseImagecomponents(4);
        //Billboard name components initialisation
        JLabel billboardnameLabel = GUITools.CreateLabel("Billboard Name: ");
        billboardnameText = new JTextField(BillboardName, 30);
        BillboardNamePanel.add(billboardnameLabel);
        BillboardNamePanel.add(billboardnameText);
        //Background colour components initialisation
        JLabel BackcolouroptionLabel = GUITools.CreateLabel("Background Colour: ");
        backgroundColourBox = GUITools.CreateCombobox(colours, ColourIndex);
        backcolour.add(BackcolouroptionLabel);
        backcolour.add(backgroundColourBox);
        //Element 1 components initialisation
        JLabel Element1optionLabel = GUITools.CreateLabel("Element 1: ");
        Element1OptionBox = GUITools.CreateCombobox(elementOptions, Element1Index);
        Element1.add(Element1optionLabel);
        Element1.add(Element1OptionBox);
        //Element 2 components initialisation
        JLabel Element2optionLabel = GUITools.CreateLabel("Element 2: ");
        Element2OptionBox = GUITools.CreateCombobox(elementOptions, Element2Index);
        Element2.add(Element2optionLabel);
        Element2.add(Element2OptionBox);
        //Element 3 components initialisation
        JLabel Element3optionlabel = GUITools.CreateLabel("Element 3: ");
        Element3OptionBox = GUITools.CreateCombobox(elementOptions, Element3Index);
        Element3.add(Element3optionlabel);
        Element3.add(Element3OptionBox);
        //Buttons components initialisation
        JButton Preview = new JButton("Preview");
        JButton Apply = new JButton("Apply");
        JButton Cancel = new JButton("Cancel");
        ButtonPanel.add(Preview);
        ButtonPanel.add(Apply);
        ButtonPanel.add(Cancel);

        //Action Listeners for Element 1 Drop down box
        ActionListener Element1OptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String)Element1OptionBox.getSelectedItem();
                Element1Index = 0;
                Image1Index = 0;
                Arrays.fill(Element1Values, null);

                Element1refactor(option);

                ActionListener ChangeImageType = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String option = (String)ImageTypeSelection1.getSelectedItem();
                        ResetImage1();
                        Element1Index = 0;
                        Image1Index = 0;
                        Directoryopen1 = new JButton("Find Image:");
                        switch (option){
                            case ("URL"):

                                Element1.add(URLcontentslabel1);
                                Element1.add(URLcontent1);

                                Element1.add(WidthvalueLabel1);
                                Element1.add(Widthvalue1);

                                Element1.add(HeightvalueLabel1);
                                Element1.add(Heightvalue1);

                                Element1.add(XPositionLabel1);
                                Element1.add(XpositionValue1);

                                Element1.add(YPositionLabel1);
                                Element1.add(YpositionValue1);
                                break;
                            case("DATA"):
                                Element1.add(DataLabel1);
                                Element1.add(Directoryopen1);
                                Element1.add(Base64Image1);

                                Element1.add(WidthvalueLabel1);
                                Element1.add(Widthvalue1);

                                Element1.add(HeightvalueLabel1);
                                Element1.add(Heightvalue1);

                                Element1.add(XPositionLabel1);
                                Element1.add(XpositionValue1);

                                Element1.add(YPositionLabel1);
                                Element1.add(YpositionValue1);

                                break;

                        }

                        //If the Directory button is pressed open a directory window and transform the file into Base64
                        ActionListener OpenDirectory = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String ImageString = filechooser();
                                Base64Image1.setText(ImageString);
                                frame.setVisible(false);
                                frame.setVisible(true);
                            }
                        };
                        Directoryopen1.addActionListener(OpenDirectory);
                        frame.setVisible(false);
                        frame.setVisible(true);


                    }
                };
                ImageTypeSelection1.addActionListener(ChangeImageType);

            }
        };
        //Action Listeners for Element 2 Drop down box
        ActionListener Element2OptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String)Element2OptionBox.getSelectedItem();
                Element2Index = 0;
                Image2Index = 0;
                Arrays.fill(Element2Values, null);
                Element2refactor(option);

                ActionListener ChangeImageType = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String option = (String)ImageTypeSelection2.getSelectedItem();
                        ResetImage2();
                        Element2Index = 0;
                        Image2Index = 0;
                        Directoryopen2 = new JButton("Find Image:");
                        switch (option){
                            case ("URL"):

                                Element2.add(URLcontentslabel2);
                                Element2.add(URLcontent2);

                                Element2.add(WidthvalueLabel2);
                                Element2.add(Widthvalue2);

                                Element2.add(HeightvalueLabel2);
                                Element2.add(Heightvalue2);

                                Element2.add(XPositionLabel2);
                                Element2.add(XpositionValue2);

                                Element2.add(YPositionLabel2);
                                Element2.add(YpositionValue2);
                                break;
                            case("DATA"):
                                Element2.add(DataLabel2);
                                Element2.add(Directoryopen2);
                                Element2.add(Base64Image2);

                                Element2.add(WidthvalueLabel2);
                                Element2.add(Widthvalue2);

                                Element2.add(HeightvalueLabel2);
                                Element2.add(Heightvalue2);

                                Element2.add(XPositionLabel2);
                                Element2.add(XpositionValue2);

                                Element2.add(YPositionLabel2);
                                Element2.add(YpositionValue2);

                                break;
                        }

                        //If the Directory button is pressed open a directory window and transform the file into Base64
                        ActionListener OpenDirectory = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String ImageString = filechooser();
                                Base64Image2.setText(ImageString);
                                frame.setVisible(false);
                                frame.setVisible(true);
                            }
                        };
                        Directoryopen2.addActionListener(OpenDirectory);
                        frame.setVisible(false);
                        frame.setVisible(true);


                    }
                };
                ImageTypeSelection2.addActionListener(ChangeImageType);
            }
        };
        //Action Listeners for Element 3 Drop down box
        ActionListener Element3OptionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String option = (String)Element3OptionBox.getSelectedItem();
                Element3Index = 0;
                Image3Index = 0;
                Arrays.fill(Element3Values, null);
                Element3refactor(option);

                ActionListener ChangeImageType = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String option = (String)ImageTypeSelection3.getSelectedItem();
                        Element3Index = 0;
                        Image3Index = 0;
                        ResetImage3();
                        Directoryopen3 = new JButton("Find Image:");
                        switch (option){
                            case ("URL"):
                                Element3.add(URLcontentslabel3);
                                Element3.add(URLcontent3);

                                Element3.add(WidthvalueLabel3);
                                Element3.add(Widthvalue3);

                                Element3.add(HeightvalueLabel3);
                                Element3.add(Heightvalue3);

                                Element3.add(XPositionLabel3);
                                Element3.add(XpositionValue3);

                                Element3.add(YPositionLabel3);
                                Element3.add(YpositionValue3);
                                break;
                            case("DATA"):
                                Element3.add(DataLabel3);
                                Element3.add(Directoryopen3);
                                Element3.add(Base64Image3);

                                Element3.add(WidthvalueLabel3);
                                Element3.add(Widthvalue3);

                                Element3.add(HeightvalueLabel3);
                                Element3.add(Heightvalue3);

                                Element3.add(XPositionLabel3);
                                Element3.add(XpositionValue3);

                                Element3.add(YPositionLabel3);
                                Element3.add(YpositionValue3);
                                break;
                        }


                        //If the Directory button is pressed open a directory window and transform the file into Base64
                        ActionListener OpenDirectory3 = new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String ImageString = filechooser();
                                Base64Image3.setText(ImageString);
                                frame.setVisible(false);
                                frame.setVisible(true);
                            }
                        };

                        Directoryopen3.addActionListener(OpenDirectory3);

                        frame.setVisible(false);
                        frame.setVisible(true);
                    }
                };
                ImageTypeSelection3.addActionListener(ChangeImageType);
            }
        };
        //Action Listener for the Preview button
        ActionListener PreviewListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FindComponentValues();
                if(!ERRORINVALUES) {
                    if(!BillboardDrawer.ISDRAWEROPEN) {
                        BillboardDrawer.Preview = true;
                        BillboardDrawer.BillboardDrawerMain(BackgroundColour, Element1Values, Element2Values, Element3Values);
                    }
                    else{
                        JOptionPane.showMessageDialog(frame, "Preview Window Already Open.");
                    }
                }
            }
        };
        //Action Listener for the Apply button
        ActionListener ApplyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FindComponentValues();

                //TODO needs to be transformed int XML file and sent to Server
                System.out.println(BillboardName);
                System.out.println(BackgroundColour);
                System.out.println();
                for(int i = 0; i < 8; i++){
                    System.out.println(Element1Values[i]);
                }
                System.out.println();
                for(int i = 0; i < 8; i++){
                    System.out.println(Element2Values[i]);
                }
                System.out.println();
                for(int i = 0; i < 8; i++){
                    System.out.println(Element3Values[i]);
                }

                //Make all of the billboard values into one long string for passing to an XML file
                String FullPass = (BillboardName +  "," +  BackgroundColour + "," + Element1Values[0] + "," + Element1Values[1] + "," + Element1Values[2] + "," + Element1Values[3] + "," + Element1Values[4] + "," + Element1Values[5] + "," + Element1Values[6] + "," + Element3Values[7] + ","
                        + Element2Values[0] + "," + Element2Values[1] + "," + Element2Values[2] + "," + Element2Values[3] + "," + Element2Values[4] + "," + Element2Values[5] + "," + Element2Values[6] + "," +  Element2Values[7] + ","
                        + Element3Values[0] + "," + Element3Values[1] + "," + Element3Values[2] + "," + Element3Values[3] + "," + Element3Values[4] + "," + Element3Values[5] + "," + Element3Values[6]+ "," + Element3Values[7] );
                System.out.println(FullPass);


                try {
                    XmlCreator creator = new XmlCreator();

                    String result = creator.createXmlString(new Element[] {
                            BillboardDataToXmlElement(Element1Values, creator),
                            BillboardDataToXmlElement(Element2Values, creator),
                            BillboardDataToXmlElement(Element3Values, creator)
                    });

                    String message = TCPMessage.build(TCPMessage.type.REQUEST_CREATE_OR_EDIT_BILLBOARD,billboardId,BillboardName,result,creatorId);
                    ServerCommunication.getResponse(message);
                    StartControlPanel.bcp.UpdateTable(0);


                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                }



            }
        };

        //Action listener for the Cancel Button, Will create a conformation box and close the Editor
        ActionListener CancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit the application? All unsaved User data will be lost",
                        "Exit Application",
                        JOptionPane.YES_NO_OPTION);
                if(result == JOptionPane.YES_OPTION){
                    CloseWindow();
                }
            }
        };



        //Listeners for the buttons and the element selection drop down boxes
        Element1OptionBox.addActionListener(Element1OptionListener);
        Element2OptionBox.addActionListener(Element2OptionListener);
        Element3OptionBox.addActionListener(Element3OptionListener);
        Preview.addActionListener(PreviewListener);
        Apply.addActionListener(ApplyListener);
        Cancel.addActionListener(CancelListener);

        //If the window is in edit mode, refactor the elements so that the correct components are added to the window
        if(ISEDIT){
            Element1refactor(Element1Values[0]);
            Element2refactor(Element2Values[0]);
            Element3refactor(Element3Values[0]);
        }

        //Add all the option panels to the input panel
        inputPanel.add(BillboardNamePanel);
        inputPanel.add(backcolour);
        inputPanel.add(Element1);
        inputPanel.add(Element2);
        inputPanel.add(Element3);
        inputPanel.add(ButtonPanel);

        frame.setVisible(true);

    }

    /**
     * Converts the data from the GUI to Xml Elements for the document creation
     * @param ElementValues the GUI data
     * @param creator the Xml File creator
     * @return Xml Element
     */
    public static Element BillboardDataToXmlElement(String[] ElementValues, XmlCreator creator) {
        Element element = null;
        if(ElementValues[0].equals("Image")) { // TODO: make this work for image data too
            if(ElementValues[1].equals("URL")) {
                element = creator.createPictureElement(ElementValues[2], XmlCreator.pictureType.url);
            }
            else{
                element = creator.createPictureElement(ElementValues[2], XmlCreator.pictureType.data);
            }
        }
        else if(ElementValues[0].equals("Text")) { // TODO: replace size with information/message?
            if(Integer.parseInt(ElementValues[2]) + Integer.parseInt(ElementValues[3]) <= 100) {
                element = creator.createInformationElement(ElementValues[1], ElementValues[6]); // TODO: make colour optional
            }
            else {
                element = creator.createMessageElement(ElementValues[1], ElementValues[6]);
            }
        }
        return element;
    }

    /**
     * Method for closing the Edit Window
     */
    public static void CloseWindow(){
        frame.dispose();
        ISEDIT = false;
        EDITWINDOWOPEN = false;
    }

    /**
     * Checks the current type of element for element 1 and adds all the components for that element type
     * @param option A string value of the Element type
     */
    private static void Element1refactor(String option){
        switch (option){
            case ("None"):
                //If none is selected  remove all elements but the element selection box and label
                resetelement1();
                break;
            case ("Text"):
                //if text is selected remove all elements but the element selection box and label, reinitialise the text components and add the text components
                resetelement1();
                InitialiseTextcomponents(1);
                Element1.add(Textcontentslabel1); Element1.add(Textcontent1);

                Element1.add(WidthvalueLabel1); Element1.add(Widthvalue1);

                Element1.add(HeightvalueLabel1); Element1.add(Heightvalue1);

                Element1.add(YPositionLabel1); Element1.add(YpositionValue1);

                Element1.add(XPositionLabel1); Element1.add(XpositionValue1);

                Element1.add(ColourLabel1); Element1.add(TextColour1);

                Element1.add(Fontsizelabel1); Element1.add(Fontsize1);
                break;
            case ("Image"):
                //if Image is selected remove all elements but the element selection box and label, reinitialise the image components and add the image components
                resetelement1();
                InitialiseImagecomponents(1);
                Element1.add(ImageTypeLabel1); Element1.add(ImageTypeSelection1);

                if(Element1Index == 2){
                    if(Image1Index == 0){
                        Element1.add(URLcontentslabel1); Element1.add(URLcontent1);
                    }
                    else {
                        Element1.add(DataLabel1); Element1.add(Directoryopen1); Element1.add(Base64Image1);
                    }
                }
                else{
                    Element1.add(URLcontentslabel1); Element1.add(URLcontent1);
                }

                Element1.add(WidthvalueLabel1); Element1.add(Widthvalue1);

                Element1.add(HeightvalueLabel1); Element1.add(Heightvalue1);

                Element1.add(XPositionLabel1); Element1.add(XpositionValue1);

                Element1.add(YPositionLabel1); Element1.add(YpositionValue1);
                break;
        }
        //Cycle the Frame to add the new elements
        frame.setVisible(false);
        frame.setVisible(true);
    }

    /**
     * Checks the current type of element for element 1 and adds all the components for that element type
     * @param option A string value of the Element type
     */
    private static void Element2refactor(String option){
        switch (option){
            case ("None"):
                //If none is selected  remove all elements but the element selection box and label
                resetelement2();
                break;
            case ("Text"):
                //if text is selected remove all elements but the element selection box and label, reinitialise the text components and add the text components
                resetelement2();
                InitialiseTextcomponents(2);
                Element2.add(Textcontentslabel2); Element2.add(Textcontent2);

                Element2.add(WidthvalueLabel2); Element2.add(Widthvalue2);

                Element2.add(HeightvalueLabel2); Element2.add(Heightvalue2);

                Element2.add(YPositionLabel2); Element2.add(YpositionValue2);

                Element2.add(XPositionLabel2); Element2.add(XpositionValue2);

                Element2.add(ColourLabel2); Element2.add(TextColour2);

                Element2.add(Fontsizelabel2); Element2.add(Fontsize2);
                break;
            case ("Image"):
                //if Image is selected remove all elements but the element selection box, reinitialise the image components and label and add the image components
                resetelement2();
                InitialiseImagecomponents(2);
                Element2.add(ImageTypeLabel2); Element2.add(ImageTypeSelection2);
                if(Element2Index == 2){
                    if(Image2Index == 0){
                        Element2.add(URLcontentslabel2); Element2.add(URLcontent2);
                    }
                    else {
                        Element2.add(DataLabel2); Element2.add(Directoryopen2); Element2.add(Base64Image2);
                    }
                }
                else{
                    Element2.add(URLcontentslabel2); Element2.add(URLcontent2);
                }

                Element2.add(WidthvalueLabel2); Element2.add(Widthvalue2);

                Element2.add(HeightvalueLabel2); Element2.add(Heightvalue2);

                Element2.add(XPositionLabel2); Element2.add(XpositionValue2);

                Element2.add(YPositionLabel2); Element2.add(YpositionValue2);
                break;
        }
        //Cycle the Frame to add the new elements
        frame.setVisible(false);
        frame.setVisible(true);
    }

    /**
     * Checks the current type of element for element 1 and adds all the components for that element type
     * @param option A string value of the Element type
     */
    private static void Element3refactor(String option){
        switch (option){
            case ("None"):
                //If none is selected  remove all elements but the element selection box and label
                resetelement3();
                break;
            case ("Text"):
                //if text is selected remove all elements but the element selection box and label, reinitialise the text components and add the text components
                resetelement3();
                InitialiseTextcomponents(3);
                Element3.add(Textcontentslabel3); Element3.add(Textcontent3);

                Element3.add(WidthvalueLabel3); Element3.add(Widthvalue3);

                Element3.add(HeightvalueLabel3); Element3.add(Heightvalue3);

                Element3.add(YPositionLabel3); Element3.add(YpositionValue3);

                Element3.add(XPositionLabel3); Element3.add(XpositionValue3);

                Element3.add(ColourLabel3); Element3.add(TextColour3);

                Element3.add(Fontsizelabel3); Element3.add(Fontsize3);
                break;
            case ("Image"):
                //if Image is selected remove all elements but the element selection box and label, reinitialise the image components and add the image components
                resetelement3();
                InitialiseImagecomponents(3);
                Element3.add(ImageTypeLabel3); Element3.add(ImageTypeSelection3);
                if(Element3Index == 2){
                    if(Image3Index == 0){
                        Element3.add(URLcontentslabel3); Element3.add(URLcontent3);
                    }
                    else {
                        Element3.add(DataLabel3); Element3.add(Directoryopen3); Element3.add(Base64Image3);
                    }
                }
                else{
                    Element3.add(URLcontentslabel3); Element3.add(URLcontent3);
                }

                Element3.add(WidthvalueLabel3); Element3.add(Widthvalue3);

                Element3.add(HeightvalueLabel3); Element3.add(Heightvalue3);

                Element3.add(XPositionLabel3); Element3.add(XpositionValue3);

                Element3.add(YPositionLabel3); Element3.add(YpositionValue3);
                break;
        }
        //Cycle the Frame to add the new elements
        frame.setVisible(false);
        frame.setVisible(true);
    }

    /**
     * This resets all of the components of Element 1, except for the Element type selection JcomboBox and JLabel
     */
    private static void resetelement1(){
        Element1.remove(Textcontentslabel1); Element1.remove(ColourLabel1);Element1.remove(Fontsizelabel1); Element1.remove(Textcontent1); Element1.remove(TextColour1); Element1.remove(Fontsize1);
        Element1.remove(ImageTypeLabel1); Element1.remove(ImageTypeSelection1);
        ResetImage1();
    }

    /**
     * This resets all of the components of Element 2, except for the Element type selection JcomboBox and JLabel
     */
    private static void resetelement2(){
        Element2.remove(Textcontentslabel2); Element2.remove(ColourLabel2); Element2.remove(Fontsizelabel2); Element2.remove(Textcontent2); Element2.remove(TextColour2); Element2.remove(Fontsize2);
        Element2.remove(ImageTypeLabel2); Element2.remove(ImageTypeSelection2);
        ResetImage2();

    }

    /**
     * This resets all of the components of Element 3, except for the Element type selection JcomboBox and JLabel
     */
    private static void resetelement3(){
        Element3.remove(Textcontentslabel3); Element3.remove(ColourLabel3); Element3.remove(Fontsizelabel3); Element3.remove(Textcontent3); Element3.remove(TextColour3); Element3.remove(Fontsize3);
        Element3.remove(ImageTypeLabel3); Element3.remove(ImageTypeSelection3);
        ResetImage3();
    }

    /**
     * Resets all of the image values in element 1 except for the image type label and image selection Jcombobox
     */
    private static void ResetImage1(){
        Element1.remove(URLcontentslabel1); Element1.remove(URLcontent1); Element1.remove(WidthvalueLabel1); Element1.remove(Widthvalue1); Element1.remove(HeightvalueLabel1); Element1.remove(Heightvalue1); Element1.remove(XPositionLabel1);
        Element1.remove(YPositionLabel1); Element1.remove(XpositionValue1); Element1.remove(YpositionValue1); Element1.remove(DataLabel1); Element1.remove(Directoryopen1); Element1.remove(Base64Image1);
    }

    /**
     * Resets all of the image values in element 2 except for the image type label and image selection Jcombobox
     */
    private static void ResetImage2(){
        Element2.remove(URLcontentslabel2); Element2.remove(URLcontent2); Element2.remove(WidthvalueLabel2); Element2.remove(Widthvalue2); Element2.remove(HeightvalueLabel2); Element2.remove(Heightvalue2); Element2.remove(XPositionLabel2);
        Element2.remove(YPositionLabel2); Element2.remove(XpositionValue2); Element2.remove(YpositionValue2); Element2.remove(DataLabel2); Element2.remove(Directoryopen2); Element2.remove(Base64Image2);
    }

    /**
     * Resets all of the image values in element 3 except for the image type label and image selection Jcombobox
     */
    private static void ResetImage3(){
        Element3.remove(URLcontentslabel3); Element3.remove(URLcontent3); Element3.remove(WidthvalueLabel3); Element3.remove(Widthvalue3); Element3.remove(HeightvalueLabel3); Element3.remove(Heightvalue3); Element3.remove(XPositionLabel3);
        Element3.remove(YPositionLabel3); Element3.remove(XpositionValue3); Element3.remove(YpositionValue3); Element3.remove(DataLabel3); Element3.remove(Directoryopen3); Element3.remove(Base64Image3);
    }

    /**
     * Initialises all of the components used when an element is selected as a Text block
     * @param i The condition when the method is called.
     *          4 = when the method is first initialised
     *          1 = Element 1 is being reinitialised, reinitialise the element 1 editable components
     *          2 = Element 2 is being reinitialised, reinitialise the element 2 editable components
     *          3 = Element 3 is being reinitialised,  reinitialise the element 3 editable components
     */
    private static void InitialiseTextcomponents(int i){
        //Initialise the JLabels This Only needs to be done once on the first initialisation
        if(i == 4) {
            Textcontentslabel1 = GUITools.CreateLabel("Contents:");
            WidthvalueLabel1 = GUITools.CreateLabel("Width (1 < X < 100):");
            HeightvalueLabel1 = GUITools.CreateLabel("Height (1 < Y < 100):");
            ColourLabel1 = GUITools.CreateLabel("Colour:");
            Fontsizelabel1 = GUITools.CreateLabel("Font Size:");
            YPositionLabel1 =GUITools.CreateLabel("Y Position (1 < Y < 100):");
            XPositionLabel1 = GUITools.CreateLabel("X Position (B1 < X < 100):");

            Textcontentslabel2 = GUITools.CreateLabel("Contents:");
            WidthvalueLabel2 = GUITools.CreateLabel("Width (1 < X < 100):");
            HeightvalueLabel2 = GUITools.CreateLabel("Height (1 < Y < 100):");
            ColourLabel2 = GUITools.CreateLabel("Colour:");
            Fontsizelabel2 = GUITools.CreateLabel("Font Size:");
            YPositionLabel2 = GUITools.CreateLabel("Y Position (1 < Y < 100):");
            XPositionLabel2 = GUITools.CreateLabel("X Position (1 < X < 100):");


            Textcontentslabel3 = GUITools.CreateLabel("Contents:");
            WidthvalueLabel3 = GUITools.CreateLabel("Width (1 < X < 100):");
            HeightvalueLabel3 = GUITools.CreateLabel("Height (1 < Y < 100):");
            ColourLabel3 = GUITools.CreateLabel("Colour:");
            Fontsizelabel3 = GUITools.CreateLabel("Font Size:");
            YPositionLabel3 = GUITools.CreateLabel("Y Position (1 < Y < 100):");
            XPositionLabel3 = GUITools.CreateLabel("X Position (1 < X < 100):");

        }

        //Initialise the Element 1 Components
        if(i == 4 || i == 1) {
            //If the element index is 1 this means it is in edit mode. Therefore give the Components the values of the edited billboard.
            if (Element1Index == 1) {
                Textcontent1 = new JTextField(Element1Values[1], 20);
                Widthvalue1 = new JTextField(Element1Values[2], 5);
                Heightvalue1 = new JTextField(Element1Values[3], 5);
                TextColour1 = GUITools.CreateCombobox(colours, TextcolourIndex1);
                Fontsize1 = new JTextField(Element1Values[7], 5);
                YpositionValue1 = new JTextField(Element1Values[4], 5);
                XpositionValue1 = new JTextField(Element1Values[5], 5);
            }
            //If not than initialise the components with their starting values.
            else {
                Textcontent1 = new JTextField("", 20);
                Widthvalue1 = new JTextField("", 5);
                Heightvalue1 = new JTextField("", 5);
                TextColour1 = GUITools.CreateCombobox(colours, 4);
                Fontsize1 = new JTextField("", 5);
                YpositionValue1 = new JTextField("", 5);
                XpositionValue1 = new JTextField("", 5);
            }
        }

        //Initialise the Element 2 Components
        if(i == 4 || i == 2) {
            //If the element index is 1 this means it is in edit mode. Therefore give the Components the values of the edited billboard.
            if (Element2Index == 1) {
                Textcontent2 = new JTextField(Element2Values[1], 20);
                Widthvalue2 = new JTextField(Element2Values[2], 5);
                Heightvalue2 = new JTextField(Element2Values[3], 5);
                TextColour2 = GUITools.CreateCombobox(colours, TextcolourIndex2);
                Fontsize2 = new JTextField(Element2Values[7], 5);
                YpositionValue2 = new JTextField(Element2Values[4], 5);
                XpositionValue2 = new JTextField(Element2Values[5], 5);
            }
            //If not than initialise the components with their starting values.
            else {
                Textcontent2 = new JTextField("", 20);
                Widthvalue2 = new JTextField("", 5);
                Heightvalue2 = new JTextField("", 5);
                TextColour2 = GUITools.CreateCombobox(colours, 4);
                Fontsize2 = new JTextField("", 5);
                YpositionValue2 = new JTextField("", 5);
                XpositionValue2 = new JTextField("", 5);
            }
        }

        //Initialise the Element 3 Components
        if(i == 4 || i == 3) {
            //If the element index is 1 this means it is in edit mode. Therefore give the Components the values of the edited billboard.
            if (Element2Index == 1) {
                Textcontent3 = new JTextField(Element3Values[1], 20);
                Widthvalue3 = new JTextField(Element3Values[2], 5);
                Heightvalue3 = new JTextField(Element3Values[3], 5);
                TextColour3 = GUITools.CreateCombobox(colours, TextcolourIndex3);
                Fontsize3 = new JTextField(Element3Values[7], 5);
                YpositionValue3 = new JTextField(Element3Values[4], 5);
                XpositionValue3 = new JTextField(Element3Values[5], 5);
            }
            //If not than initialise the components with their starting values.
            else {
                Textcontent3 = new JTextField("", 20);
                Widthvalue3 = new JTextField("", 5);
                Heightvalue3 = new JTextField("", 5);
                TextColour3 = GUITools.CreateCombobox(colours, 4);
                Fontsize3 = new JTextField("", 5);
                YpositionValue3 = new JTextField("", 5);
                XpositionValue3 = new JTextField("", 5);
            }
        }
    }

    /**
     * Initialises all of the components used when an element is selected as an image
     * @param i The condition when the method is called.
     *          4 = when the method is first initialised
     *          1 = Element 1 is being reinitialised, reinitialise the element 1 editable components
     *          2 = Element 2 is being reinitialised, reinitialise the element 2 editable components
     *          3 = Element 3 is being reinitialised,  reinitialise the element 3 editable components
     */
    private static void InitialiseImagecomponents(int i) {
        //Initialise the JLabels This Only needs to be done once on the first initialisation
        if(i == 4) {
            URLcontentslabel1 = GUITools.CreateLabel("URL:");
            WidthvalueLabel1 = GUITools.CreateLabel("Width (1 < X < 100):");
            HeightvalueLabel1 = GUITools.CreateLabel("Height (1 < Y < 100):");
            XPositionLabel1 = GUITools.CreateLabel("X Position (1 < Y < 100):");
            YPositionLabel1 = GUITools.CreateLabel("Y Position (1 < Y < 100):");
            ImageTypeLabel1 = GUITools.CreateLabel("Image Type:");
            DataLabel1 = GUITools.CreateLabel("Select Image:");

            URLcontentslabel2 = GUITools.CreateLabel("URL:");
            WidthvalueLabel2 = GUITools.CreateLabel("Width (1 < X < 100):");
            HeightvalueLabel2 = GUITools.CreateLabel("Height (1 < Y < 100):");
            XPositionLabel2 = GUITools.CreateLabel("X Position (1 < Y < 100):");
            YPositionLabel2 = GUITools.CreateLabel("Y Position (1 < Y < 100):");
            ImageTypeLabel2 = GUITools.CreateLabel("Image Type:");
            DataLabel2 = GUITools.CreateLabel("Select Image:");

            URLcontentslabel3 = GUITools.CreateLabel("URL:");
            WidthvalueLabel3 = GUITools.CreateLabel("Width (1 < X < 100):");
            HeightvalueLabel3 = GUITools.CreateLabel("Height (1 < Y < 100):");
            XPositionLabel3 = GUITools.CreateLabel("X Position (1 < Y < 100):");
            YPositionLabel3 = GUITools.CreateLabel("Y Position (1 < Y < 100):");
            ImageTypeLabel3 = GUITools.CreateLabel("Image Type:");
            DataLabel3 = GUITools.CreateLabel("Select Image:");
        }

        //Initialise the Element 1 Components
        if(i == 4 || i == 1) {
                //If the element index is 2 this means it is in edit mode. Therefore give the Components the values of the edited billboard.
                if (Element1Index == 2) {
                        URLcontent1 = new JTextField(Element1Values[2], 40);
                        Widthvalue1 = new JTextField(Element1Values[3], 5);
                        Heightvalue1 = new JTextField(Element1Values[4], 5);
                        XpositionValue1 = new JTextField(Element1Values[5], 5);
                        YpositionValue1 = new JTextField(Element1Values[6], 5);
                        ImageTypeSelection1 = GUITools.CreateCombobox(ImageTypeOptions, Image1Index);
                        Base64Image1 = new JTextField(Element1Values[2], 30);
                    Directoryopen1 = new JButton("Find Image:");
                }
                //If not than initialise the components with their starting values.
                else {
                    URLcontent1 = new JTextField("", 40);
                    Widthvalue1 = new JTextField("", 5);
                    Heightvalue1 = new JTextField("", 5);
                    XpositionValue1 = new JTextField("", 5);
                    YpositionValue1 = new JTextField("", 5);
                    ImageTypeSelection1 = GUITools.CreateCombobox(ImageTypeOptions, 0);
                    Base64Image1 = new JTextField("", 30);
                    Directoryopen1 = new JButton("Find Image:");
                }
        }

        //Initialise the Element 2 Components
        if(i == 4 || i == 2) {
            //If the element index is 2 this means it is in edit mode. Therefore give the Components the values of the edited billboard.
            if (Element2Index == 2) {
                    URLcontent2 = new JTextField(Element2Values[2], 40);
                    Widthvalue2 = new JTextField(Element2Values[3], 5);
                    Heightvalue2 = new JTextField(Element2Values[4], 5);
                    XpositionValue2 = new JTextField(Element2Values[5], 5);
                    YpositionValue2 = new JTextField(Element2Values[6], 5);
                    ImageTypeSelection2 = GUITools.CreateCombobox(ImageTypeOptions, Image2Index);
                    Base64Image2 = new JTextField(Element2Values[2], 30);
                Directoryopen2 = new JButton("Find Image:");
            }
            //If not than initialise the components with their starting values.
            else {
                URLcontent2 = new JTextField("", 40);
                Widthvalue2 = new JTextField("", 5);
                Heightvalue2 = new JTextField("", 5);
                XpositionValue2 = new JTextField("", 5);
                YpositionValue2 = new JTextField("", 5);
                ImageTypeSelection2 = GUITools.CreateCombobox(ImageTypeOptions, 0);
                Base64Image2 = new JTextField("", 30);
                Directoryopen2 = new JButton("Find Image:");
            }
        }

        //Initialise the Element 3 Components
        if(i == 4 || i ==3) {
            //If the element index is 2 this means it is in edit mode. Therefore give the Components the values of the edited billboard.
            if (Element3Index == 2) {
                    URLcontent3 = new JTextField(Element3Values[2], 40);
                    Widthvalue3 = new JTextField(Element3Values[3], 5);
                    Heightvalue3 = new JTextField(Element3Values[4], 5);
                    XpositionValue3 = new JTextField(Element3Values[5], 5);
                    YpositionValue3 = new JTextField(Element3Values[6], 5);
                    ImageTypeSelection3 = GUITools.CreateCombobox(ImageTypeOptions, Image3Index);
                    Base64Image3 = new JTextField(Element3Values[2], 30);
                Directoryopen3 = new JButton("Find Image:");

            }
            //If not than initialise the components with their starting values.
            else {
                URLcontent3 = new JTextField("", 40);
                Widthvalue3 = new JTextField("", 5);
                Heightvalue3 = new JTextField("", 5);
                XpositionValue3 = new JTextField("", 5);
                YpositionValue3 = new JTextField("", 5);
                ImageTypeSelection3 = GUITools.CreateCombobox(ImageTypeOptions, 0);
                Base64Image3 = new JTextField("", 30);
                Directoryopen3 = new JButton("Find Image:");

            }
        }
        //Make the base64 image uneditable so that the file cannot be changed
        Base64Image1.setEditable(false);
        Base64Image2.setEditable(false);
        Base64Image3.setEditable(false);
    }

    /**
     * Finds the Values of all of the Components on the screen, whist also checking the values are correct.
     * It places these values into Strings And String Arrays.
     */
    private static void FindComponentValues(){
        //Find the current values of Name and background colour
        ERRORINVALUES = false;

        BillboardName = billboardnameText.getText();
        ERRORINVALUES = BillboardName.isEmpty();
        if(ERRORINVALUES){
            JOptionPane.showMessageDialog(frame, "Please enter a Billboard Title.");
        }
        else {
            BackgroundColour = (String) backgroundColourBox.getSelectedItem();

            //Empty the arrays of any previous values
            Arrays.fill(Element1Values, null);
            Arrays.fill(Element2Values, null);
            Arrays.fill(Element3Values, null);

            //Add the Element type to the first part of the Array
            Element1Values[0] = (String) Element1OptionBox.getSelectedItem();
            Element2Values[0] = (String) Element2OptionBox.getSelectedItem();
            Element3Values[0] = (String) Element3OptionBox.getSelectedItem();

            //Find each elements values, and check each element does not have an error.
            //If it does have an error, stop the the apply and show an error message displaying what element has the error
            Element1FindValues(Element1Values[0]);
            if (ERRORINVALUES) {
                JOptionPane.showMessageDialog(frame, "Error in Element1");
            } else {
                Element2FindValues(Element2Values[0]);
                if (ERRORINVALUES) {
                    JOptionPane.showMessageDialog(frame, "Error in Element2");
                } else {
                    Element3FindValues(Element3Values[0]);
                    if(ERRORINVALUES){
                        JOptionPane.showMessageDialog(frame, "Error in Element3");
                    }
                }
            }
        }

    }

    /**
     * Finds the values that are in the components of Element 1
     * @param ElementType The first value in the string array. is the type of element it is
     */
    private static void Element1FindValues(String ElementType){
        //If the element type is none nothing needs to be done
        if(!ElementType.equals("None")) {
            switch (ElementType) {
                case ("Text"):
                    //If the Element type is Text find the values
                    Element1Values[1] = Textcontent1.getText();
                    Element1Values[2] = Widthvalue1.getText();
                    Element1Values[3] = Heightvalue1.getText();
                    Element1Values[4] = YpositionValue1.getText();
                    Element1Values[5] = XpositionValue1.getText();
                    Element1Values[6] = (String)TextColour1.getSelectedItem();
                    Element1Values[7] = Fontsize1.getText();
                    ERRORINVALUES = CheckTextErrors(Element1Values);
                    break;
                case ("Image"):
                    //If the Element type is Image find the values
                    Element1Values[1] = (String)ImageTypeSelection1.getSelectedItem();
                    if(Element1Values[1].equals("URL")) {
                        Element1Values[2] = URLcontent1.getText();
                    }
                    else{
                        Element1Values[2] = Base64Image1.getText();
                    }
                    Element1Values[3] = Widthvalue1.getText();
                    Element1Values[4] = Heightvalue1.getText();
                    Element1Values[5] = XpositionValue1.getText();
                    Element1Values[6] = YpositionValue1.getText();
                    ERRORINVALUES = CheckImageErrors(Element1Values);
                    break;
            }
        }
    }

    /**
     * Finds the values that are in the components of Element 2
     * @param ElementType ElementType The first value in the string array. is the type of element it is
     */
    private static void Element2FindValues(String ElementType){
        //If the element type is none nothing needs to be done
        if(!ElementType.equals("None")) {
            switch (ElementType) {
                case ("Text"):
                    //If the Element type is Text find the values
                    Element2Values[1] = Textcontent2.getText();
                    Element2Values[2] = Widthvalue2.getText();
                    Element2Values[3] = Heightvalue2.getText();
                    Element2Values[4] = YpositionValue2.getText();
                    Element2Values[5] = XpositionValue2.getText();
                    Element2Values[6] = (String)TextColour2.getSelectedItem();
                    Element2Values[7] = Fontsize2.getText();
                    ERRORINVALUES = CheckTextErrors(Element2Values);
                    break;
                case ("Image"):
                    //If the Element type is Image find the values
                    Element2Values[1] = (String)ImageTypeSelection2.getSelectedItem();
                    if(Element2Values[1].equals("URL")) {
                        Element2Values[2] = URLcontent2.getText();
                    }
                    else{
                        Element2Values[2] = Base64Image2.getText();
                    }
                    Element2Values[3] = Widthvalue2.getText();
                    Element2Values[4] = Heightvalue2.getText();
                    Element2Values[5] = XpositionValue2.getText();
                    Element2Values[6] = YpositionValue2.getText();
                    ERRORINVALUES = CheckImageErrors(Element2Values);
                    break;
            }
        }

    }

    /**
     * Finds the values that are in the components of Element 3
     * @param ElementType ElementType The first value in the string array. is the type of element it is
     */
    private static void Element3FindValues(String ElementType){
        //If the element type is none nothing needs to be done
        if(!ElementType.equals("None")) {
            switch (ElementType) {
                case ("Text"):
                    //If the Element type is Text find the values
                    Element3Values[1] = Textcontent3.getText();
                    Element3Values[2] = Widthvalue3.getText();
                    Element3Values[3] = Heightvalue3.getText();
                    Element3Values[4] = YpositionValue3.getText();
                    Element3Values[5] = XpositionValue3.getText();
                    Element3Values[6] = (String)TextColour3.getSelectedItem();
                    Element3Values[7] = Fontsize3.getText();
                    ERRORINVALUES = CheckTextErrors(Element3Values);
                    break;
                case ("Image"):
                    //If the Element type is Image find the values
                    Element3Values[1] = (String)ImageTypeSelection3.getSelectedItem();
                    if(Element3Values[1].equals("URL")) {
                        Element3Values[2] = URLcontent3.getText();
                    }
                    else{
                        Element3Values[2] = Base64Image3.getText();
                    }
                    Element3Values[3] = Widthvalue3.getText();
                    Element3Values[4] = Heightvalue3.getText();
                    Element3Values[5] = XpositionValue3.getText();
                    Element3Values[6] = YpositionValue3.getText();
                    ERRORINVALUES = CheckImageErrors(Element3Values);
                    break;
            }
        }

    }

    /**
     * Checks that all of the Text number values are integers and are within bounds
     * @param values The Element values
     * @return Boolean value
     */
    private static boolean CheckTextErrors(String[] values){
        //Check that all of the number values are integers.
        // Also check that all of the number values that are between 1 and 100 are within these bounds
        if(!isInteger(values[2]) ||  !Inbounds(values[2])){
            return true;
        }
        else if(!isInteger(values[3]) ||  !Inbounds(values[3])){
            return true;
        }
        else if(!isInteger(values[4]) ||  !Inbounds(values[4])){
            return true;
        }
        else if(!isInteger(values[5]) ||  !Inbounds(values[5])){
            return true;
        }
        else if(!isInteger(values[7])){
            return true;
        }
        //Check that all of the components have a value
        else if(GUITools.EmptyValue(values)){
            return true;
        }
        return false;
    }

    /**
     * Checks that all of the Image number values are integers and are within bounds
     * @param values The Element values
     * @return Boolean value
     */
    private static boolean CheckImageErrors(String[] values){
        //Check that all of the number values are integers.
        // Also check that all of the number values that are between 1 and 100 are within these bounds
        if(!isInteger(values[3]) ||  !Inbounds(values[3])){
            return true;
        }
        else if(!isInteger(values[4]) ||  !Inbounds(values[4])){
            return true;
        }
        else if(!isInteger(values[5]) ||  !Inbounds(values[5])){
            return true;
        }
        else if(!isInteger(values[6]) ||  !Inbounds(values[6])){
            return true;
        }
        //Check that all of the components have a value
        else if(GUITools.EmptyValue(values)){
            return true;
        }
        return false;
    }

    /**
     * Method to check whether the value entered is with its bounds
     * @param Number Value
     * @return Boolean value
     */
    private static boolean Inbounds(String Number){
        int i = Integer.parseInt(Number);
        if(i > 100 || i < 1){
            return false;
        }
        return true;
    }

    /**
     * A method to check whether a string is an integer. From https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
     * @param s the string that is being checked
     * @return boolean value
     */
    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * Creates a file directory window and gets a file that is there.
     * It then encodes the file into BASE64 for transfer.
     * Fram //https://stackoverflow.com/questions/10621687/how-to-get-full-path-directory-from-file-chooser/10621739
     * @return A String that contains the image file in Base64 format
     */
    public static String filechooser(){
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif"));
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("Image Select");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setAcceptAllFileFilterUsed(false);

        String encodedFile = new String();

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
//            System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
            File file = chooser.getSelectedFile();

            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int)file.length()];
                fileInputStreamReader.read(bytes);
                encodedFile = Base64.getEncoder().encodeToString(bytes);
                System.out.println(encodedFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return encodedFile;
    }
}