/**
 * @author Bartosz Sadowski
 * javac -d bin (sciezki)
 * jar -cfv Lab07_pop.jar -C bin .
 * java -p Lab07_pop.jar -m Lab07/pwr.bsadowski.shopApp.ShopApp
 */

package pwr.bsadowski.shopApp;

import pwr.bsadowski.policy.MyPolicy;

import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Policy;

public class ShopApp implements Serializable {

    private static final long serialVersionUID = 1L;
    private ShopImp shop;
    static ShopGUI frame;
    private static int portToHost;

    void ini() throws RemoteException{
        Policy.setPolicy(new MyPolicy());

        if (System.getSecurityManager() == null){
            System.setSecurityManager(new SecurityManager());
            System.out.println("SM was set");
        }
        shop = new ShopImp();
        try{
            Registry registry = LocateRegistry.createRegistry(portToHost);
            registry.bind("shop", shop);
            System.out.println("Serwer jest uruchomiony!");
        }catch (Exception e){
            System.out.println("Remote error - " + e);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new ShopGUI();
                frame.updateShopTable();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        portToHost = Integer.parseInt(args[0]);
    }
}
