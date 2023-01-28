package pwr.bsadowski.clientApp;


import interfaces.IShop;
import model.*;
import pwr.bsadowski.policy.MyPolicy;

import java.awt.*;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientApp implements Serializable {

    private static final long serialVersionUID = 1L;
    private static IShop shop;
    private static int id;
    private static List<ItemType> clientLookupList = new ArrayList<>();
    private static final List<OrderLine> clientShoppingCartList = new ArrayList<>();
    private final List<PlaceOrder> clientSOList = new ArrayList<>();
    private final StatusListenerImp statusListener = new StatusListenerImp(this);
    private static int clientPortToConnect;


    private static ClientGUI frame;

    public ClientApp() throws RemoteException {
    }


    public void registerClient() throws RemoteException{
        Policy.setPolicy(new MyPolicy());

        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
            System.out.println("SM was set");
        }

        try{
            System.out.println("Registering...");
            Registry registry = LocateRegistry.getRegistry("localhost", clientPortToConnect);
            shop = (IShop) registry.lookup("shop");
            System.out.println("Registered!");

            Client client = new Client();
            client.setName(ClientGUI.name);
            id = shop.register(client);

        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void weTomListe() throws RemoteException {
        clientLookupList = shop.getItemList();
    }

    public void updateStatus(int id, Status status){
        Optional<PlaceOrder> updateOrderStatus = clientSOList.stream().filter(o -> o.getId() == id).findFirst();

        updateOrderStatus.ifPresent(submittedOrder -> submittedOrder.setStatus(status));

        frame.updateOrdersTable();
    }


    public StatusListenerImp getStatusListener() {
        return statusListener;
    }

    public IShop getShop() {
        return shop;
    }

    public int getId() {
        return id;
    }

    public static List<ItemType> getClientLookupList() {
        return clientLookupList;
    }

    public List<OrderLine> getClientShoppingCartList() {
        return clientShoppingCartList;
    }

    public List<PlaceOrder> getClientSOList() {
        return clientSOList;
    }

    public ClientGUI getFrame() {
        return frame;
    }


    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            try {
                frame = new ClientGUI();
                frame.setTitle(ClientGUI.name);
                frame.updateShopTable();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        clientPortToConnect = Integer.parseInt(args[0]);

    }
}