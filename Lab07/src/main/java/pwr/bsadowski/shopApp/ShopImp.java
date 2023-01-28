package pwr.bsadowski.shopApp;

import interfaces.IShop;
import interfaces.IStatusListener;
import pwr.bsadowski.exceptions.OrderNotFoundException;
import model.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ShopImp extends UnicastRemoteObject implements IShop {

    private HashMap<Integer, String> clientsId = new HashMap<>();
    private Integer uniqueId = 0;
    private List<ItemType> itemList = new ArrayList<>();
    private HashMap<Integer, IStatusListener> subscribersClients = new HashMap<>();
    static List<SubmittedOrder> so = new ArrayList<>();

    protected ShopImp() throws RemoteException {
        super();
    }

    @Override
    public int register(Client c) throws RemoteException {
        uniqueId++;
        clientsId.put(uniqueId, c.getName());
        return uniqueId;
    }

    @Override
    public List<ItemType> getItemList() throws RemoteException {
        if(itemList.isEmpty()){
            ItemType item1 = new ItemType();
            item1.setName("Jeans");
            item1.setPrice(20);
            item1.setCategory(1);
            itemList.add(item1);

            ItemType item2 = new ItemType();
            item2.setName("T-Shirt");
            item2.setPrice(25);
            item2.setCategory(1);
            itemList.add(item2);

            ItemType item3 = new ItemType();
            item3.setName("Hat");
            item3.setPrice(5);
            item3.setCategory(1);
            itemList.add(item3);

            ItemType item4 = new ItemType();
            item4.setName("Pen");
            item4.setPrice(2);
            item4.setCategory(2);
            itemList.add(item4);
        }

        return itemList;
    }

    @Override
    public int placeOrder(Order o) throws RemoteException {
        SubmittedOrder tempSO = new SubmittedOrder(o);
        so.add(tempSO);
        ShopApp.frame.updateShopTable();
        return tempSO.getId();
    }

    @Override
    public List<SubmittedOrder> getSubmittedOrders() throws RemoteException {
        return so;
    }

    @Override
    public boolean setStatus(int id, Status s) throws RemoteException {
        Optional<SubmittedOrder> soOptional = so.stream().filter(o -> o.getId() == id).findFirst();

        if(soOptional.isEmpty()){
            return false;
        }

        soOptional.get().setStatus(s);

        if (subscribersClients.containsKey(soOptional.get().getOrder().getClientID())){
            subscribersClients.get(soOptional.get().getOrder().getClientID()).statusChanged(id, soOptional.get().getStatus());
        }

        ShopApp.frame.updateShopTable();
        return true;
    }

    @Override
    public Status getStatus(int id) throws RemoteException {
        Optional<SubmittedOrder> soOptional = so.stream().filter(o -> o.getId() == id).findFirst();

        if(soOptional.isPresent()){
            return soOptional.get().getStatus();
        }
        else{
            throw new OrderNotFoundException("Error 404. Order with id " + id + " not found." );
        }
    }

    @Override
    public boolean subscribe(IStatusListener ic, int clientId) throws RemoteException {
        if(subscribersClients.containsKey(clientId)){
            return false;
        }
        subscribersClients.put(clientId, ic);
        return true;
    }

    @Override
    public boolean unsubscribe(int clientId) throws RemoteException {
        if(!subscribersClients.containsKey(clientId)){
            return false;
        }
        subscribersClients.remove(clientId);
        return true;
    }


}
