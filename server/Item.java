/*
    This class creates an Item object which stores its
    highest bid, name, and IP Address of the highest bidder.
*/

public class Item {
    private double highestBid;
    private final String itemName;
    private String ipAddress;

    public Item(String iName, String ip){
        this.itemName = iName;
        this.ipAddress = ip;
    }

    // Return the highest big for the current item
    public double getHighestBid(){
        return highestBid;
    }

    // Return the name of the item
    public String getItemName(){
        return itemName;
    }

    // Return the IPAddress associated with the item
    public String getIPAddress(){
        return ipAddress;
    }

    // Set the new bid to be the highest bid
    public void setIPAddress(String address){
        ipAddress = address;
    }

    // Set the new bid to be the highest bid
    public void setBid(double newBid){
        highestBid = newBid;
    }

    // Build the output string based on the items data
    public String buildOutput(String ipAddress){
        return getItemName() + " : " + getHighestBid() + " : " + ipAddress;
    }

}
