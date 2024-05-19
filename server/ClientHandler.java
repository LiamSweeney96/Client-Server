import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/*
    This is a class to deal with the client input. It validates arguments,
    and updates the auction where necessary.
*/

public class ClientHandler extends Thread {
    private final Socket socket;
    private static final ArrayList<Item> itemArray = new ArrayList<>();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );
            FileWriter writer = new FileWriter("log.txt", true);
            InetAddress inet = socket.getInetAddress();
            String[] arguments;
            String args, address = inet.toString().replaceAll("/", "");
            DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
            LocalDateTime time = LocalDateTime.now();

            // Read from client until end of input
            while ((args = in.readLine()) != null) {

                args = args.replaceAll("[\\[\\]]", "");
                arguments = args.split(", ");
                System.out.println(Arrays.toString(arguments));

                // Validate arguments
                if (arguments.length > 0 && arguments.length < 4) {

                    // Validate 'show' argument, print relevant output if successful
                    if (arguments.length == 1) {
                        if (Objects.equals(arguments[0], "show")) {
                            if (itemArray.size() == 0){
                                out.println("There are currently no items in this auction.");
                            } else {
                                writer.write(date.format(time) + " | " + address + " | show " + "\n");
                                for (Item item : itemArray){
                                    out.println(show(item));
                                }
                            }
                        }
                        break;
                    }

                    // Validate 'item' arguments, create new item with given name if successful
                    if (arguments.length == 2) {
                        if (Objects.equals(arguments[0], "item")) {
                            if (checkString(arguments[1])){
                                out.println("Invalid item name.");
                                break;
                            }
                            // If item array is empty, create item
                            if (itemArray.size() == 0) {
                                Item item = new Item(arguments[1], "<no bids>");
                                itemArray.add(item);
                                writer.write(date.format(time) + " | " + "<no bids>" + " | item " + item.getItemName() + "\n");
                                out.println("Success.");
                            } else {
                                // Check for duplicate items
                                if (checkItem(arguments[1])){
                                    Item item = new Item(arguments[1], address);
                                    itemArray.add(item);
                                    writer.write(date.format(time) + " | " + "<no bids>" + " | item " + item.getItemName() + "\n");
                                    out.println("Success.");
                                } else {
                                    out.println("Failure.");
                                }
                            }
                            break;
                        }
                    }

                    // Validate 'bid' arguments, update item details if so
                    if (arguments.length == 3) {
                        if (Objects.equals(arguments[0], "bid")) {
                            if (checkString(arguments[1])){
                                out.println("Invalid item name.");
                                break;
                            }
                            // Check if argument can be parsed to double
                            if (!validateDouble(arguments[2])){
                                out.println("Invalid argument, cannot parse to double.");
                                break;
                            }
                            Item item = new Item(arguments[1], address);
                            item = getItem(item);

                            // Check for duplicate items
                            if (checkItem(item.getItemName())){
                                out.println("Failure.");
                                break;
                            }
                            // Update the bid and IP address
                            if (itemArray.size() != 0) {
                                double newBid = Double.parseDouble(arguments[2]);
                                if (item.getHighestBid() < newBid){
                                    item.setBid(newBid);
                                    item.setIPAddress(address);
                                    writer.write(date.format(time) + " | " + address + " | bid " + item.getItemName() + " " + newBid + "\n");
                                    out.println("Accepted.");
                                    break;
                                }
                                out.println("Rejected.");
                                break;
                            }
                        }
                        out.println("Failure.");
                        break;
                    }

                } else {
                    out.println("Please input valid arguments.");
                    break;
                }
            }

            // Close all open readers, writers, and the socket
            writer.close();
            out.close();
            in.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Formats an output string if items have been stored
    public String show(Item item) {
        return item.buildOutput(item.getIPAddress());
    }

    // Checks if item already exists in the arraylist
    public boolean checkItem(String itemName){
        for (Item item : itemArray){
            System.out.println(item.getItemName());
            if (Objects.equals(itemName, item.getItemName())){
                return false;
            }
        }
        return true;
    }

    // Validates that an item name is alphabetic
    public boolean checkString(String arg){
        return !arg.matches("[a-zA-Z]+");
    }

    // Validates that arguments can be converted to double where necessary
    public boolean validateDouble(String arg){
        try {
            Double.parseDouble(arg);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public Item getItem(Item item){
        for (Item i : itemArray) {
            if (Objects.equals(item.getItemName(), i.getItemName())){
                item = i;
                break;
            }
        }
        return item;
    }
}