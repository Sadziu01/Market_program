package pwr.bsadowski.clientApp;


import interfaces.IStatusListener;
import model.Status;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StatusListenerImp extends UnicastRemoteObject implements IStatusListener {

    ClientApp client;

    public StatusListenerImp(ClientApp client) throws RemoteException  {
        this.client = client;
    }

    @Override
    public void statusChanged(int id, Status status) throws RemoteException {
        client.updateStatus(id, status);
    }
}
