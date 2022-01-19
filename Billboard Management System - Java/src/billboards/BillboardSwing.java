package billboards;
import javax.swing.*;
public abstract class BillboardSwing {
    // the applications main frame
    protected JFrame frame;

    /**
     * A constructor that initialises the JFrame object with a title.
     * @param title (String) The title of the application/window
     */
    public BillboardSwing(String title) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * A method that initialises the size of the window.
     * @param width (int) the width of the window
     * @param height (int) the height of the window
     */
    public void initialise(int width, int height) {
        frame.setSize(width,height);
    }

    /**
     * A method that initialises the size of the window and the visibility
     * of the window.
     * @param width (int) the width of the window
     * @param height (int) the height of the window
     * @param visibility (boolean) the visibility of the window (true or false)
     */
    public void initialise(int width, int height, boolean visibility) {

        frame.setSize(width,height);
        frame.setResizable(false);
        frame.setVisible(visibility);
    }

    /**
     * I'm working on a more elegant solution but this
     * works for now.
     */
    public void update() {
        frame.setVisible(false);
        frame.setVisible(true);
    }
}
