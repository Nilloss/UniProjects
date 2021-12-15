package billboards.controlPanel;

import billboards.controlPanel.BillboardControlPanelSwing;

public class StartControlPanel {

    public static BillboardControlPanelSwing bcp;

    public static void main(String[] args){
        System.out.println("Starting Control Panel");
        // initialising the billboard object (for the control panel)
        bcp = new BillboardControlPanelSwing("Billboard Control Panel", 1080, 720);
        // the login screen method is used to initialise the login screen
        bcp.loginScreen();
    }
}
