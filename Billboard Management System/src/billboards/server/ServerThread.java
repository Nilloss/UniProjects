package billboards.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerThread extends Thread {
    public commands command;

    public enum commands {
        start,
        stop,
        help,
        exit
    }

    public ServerThread() {
        super();
        command = commands.stop;
    }

    @Override
    public void run() {
        while(command != commands.exit) {
            try {
                BufferedReader into = new BufferedReader(new InputStreamReader(System.in));
                String userInput = into.readLine();
                switch (userInput) {
                    case "start":
                        System.out.println("starting server");
                        command = commands.start;
                        break;
                    case "stop":
                        System.out.println("stopping server");
                        command = commands.stop;
                        break;
                    case "help":
                        System.out.print("There are four commands\n" +
                                "start --- starts the server, accepting requests and such\n" +
                                "stop  --- stops the server, from doing the above\n" +
                                "help  --- prints the help message\n" +
                                "exit  --- close the whole program\n" +
                                "for more information visit the server documentation");
                        command = commands.help;
                        break;
                    case "exit":
                        System.out.println("Exiting");
                        command = commands.exit;
                        break;
                    default:
                        System.out.println("Invalid command, if you need help, write 'help'.");
                        break;
                }
            }
            catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }
}
