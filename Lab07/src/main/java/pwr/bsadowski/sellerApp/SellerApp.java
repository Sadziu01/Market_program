package pwr.bsadowski.sellerApp;

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

import static java.lang.Thread.sleep;


public class SellerApp implements Serializable {

    private static final long serialVersionUID = 1L;
    private IShop shop;
    private List<SubmittedOrder> sellerSOList = new ArrayList<>();
    private static SellerGUI frame;
    private static int sellerPortToConnect;


    public void registerSeller() throws RemoteException{
        Policy.setPolicy(new MyPolicy());

        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
            System.out.println("SM was set");
        }

        try{
            System.out.println("Registering...");
            Registry registry = LocateRegistry.getRegistry("localhost", sellerPortToConnect);
            shop = (IShop) registry.lookup("shop");
            System.out.println("Registered!");

        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void refreshContent() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    sellerSOList = shop.getSubmittedOrders();
                    frame.updateSellerTable();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        t.start();
    }

    public IShop getShop() {
        return shop;
    }

    public void setShop(IShop shop) {
        this.shop = shop;
    }

    public List<SubmittedOrder> getSellerSOList() {
        return sellerSOList;
    }

    public void setSellerSOList(List<SubmittedOrder> sellerSOList) {
        this.sellerSOList = sellerSOList;
    }

    public SellerGUI getFrame() {
        return frame;
    }

    public void setFrame(SellerGUI frame) {
        this.frame = frame;
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            try {
                frame = new SellerGUI();
                frame.setVisible(true);
                frame.updateSellerTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sellerPortToConnect = Integer.parseInt(args[0]);
    }
}