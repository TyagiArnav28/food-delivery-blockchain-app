package core;

import ui.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.security.Security; // Import Security
// Import all our blockchain classes
import core.Block;
import core.Wallet;
import core.OrderStatusEvent;
import core.StringUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CardLayoutPanel extends JPanel {
    private CardLayout layout;
    private Navigator navigator;
    private MenuScreen menuScreen;
    private CartScreen cartScreen;
    private RestaurantScreen restaurantScreen;
    private DriverScreen driverScreen;
    private HomeScreen homeScreen;
    private TrackOrderScreen trackOrderScreen;
    private List<CartItem> cartItems;

    // --- OUR NEW BLOCKCHAIN LOGIC ---
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static int difficulty = 2; // Keep it low for fast testing
    public static Wallet customerWallet;
    public static Wallet restaurantWallet;
    public static Wallet driverWallet;
    public static String currentUserRole;
    // ---------------------------------

    public CardLayoutPanel(Navigator navigator) {
        this.navigator = navigator;
        this.layout = new CardLayout();
        this.cartItems = new ArrayList<>();

     // --- INITIALIZE THE BLOCKCHAIN ---
     // Setup Bouncy castle as a Security Provider
     Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

     // Create the wallets for our participants
     customerWallet = new Wallet();
     restaurantWallet = new Wallet();
     driverWallet = new Wallet();

     // Create Genesis Block
     Block genesis = new Block("0");
     blockchain.add(genesis);
     System.out.println("Genesis Block created. Blockchain is ready.");
     // ---------------------------------
        setLayout(layout);

        LoginScreen login = new LoginScreen(navigator, this);
        homeScreen = new HomeScreen(navigator);
        menuScreen = new MenuScreen(navigator, cartItems);
        
        // **IMPORTANT:** We now pass the blockchain logic to the CartScreen
        // We must update this line to pass 'this'
        cartScreen = new CartScreen(navigator, cartItems, this);
        restaurantScreen = new RestaurantScreen(navigator, this);
        driverScreen = new DriverScreen(navigator, this);
        trackOrderScreen = new TrackOrderScreen(navigator, this);

        add(login, "login");
        add(homeScreen, "home");
        add(menuScreen, "menu");
        add(cartScreen, "cart");
        add(restaurantScreen, "restaurant");
        add(driverScreen, "driver");
        add(trackOrderScreen, "track");
    }

    public void show(String name) {
        if (name.equals("cart")) {
            cartScreen.refresh(cartItems);
        }
        // --- ADD THIS IF BLOCK ---
        if (name.equals("home")) {
            homeScreen.refresh(); // Tell the home screen to rebuild itself
        }
        layout.show(this, name);
    }

    // --- BLOCKCHAIN HELPER METHODS ---
    // (We moved these from our old NoobChain.java)

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = StringUtil.getDificultyString(difficulty); // Use our StringUtil method

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
            
            // Loop through the events in the block and verify signatures
            for(OrderStatusEvent event : currentBlock.transactions) { // Use our new class name
                 if(!event.verifySignature()) {
                     System.out.println("Signature on Event is Invalid");
                     return false;
                }
            }
        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}