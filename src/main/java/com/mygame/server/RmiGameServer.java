package com.mygame.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Very small RMI server + interface for later networked play.
 *
 * Usage:
 *  - Start this main on a host to export the GameRemote.
 */
public class RmiGameServer {

    public interface GameRemote extends Remote {
        void registerPlayer(String playerId) throws RemoteException;
        void sendAction(String playerId, String actionJson) throws RemoteException;
        String getState(String matchId) throws RemoteException;
    }

    public static class GameRemoteImpl extends UnicastRemoteObject implements GameRemote {
        protected GameRemoteImpl() throws RemoteException {
            super();
        }

        @Override
        public void registerPlayer(String playerId) throws RemoteException {
            System.out.println("Register player: " + playerId);
        }

        @Override
        public void sendAction(String playerId, String actionJson) throws RemoteException {
            System.out.println("Action from " + playerId + " -> " + actionJson);
        }

        @Override
        public String getState(String matchId) throws RemoteException {
            return "{\"state\":\"stub\",\"matchId\":\""+matchId+"\"}";
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            GameRemoteImpl impl = new GameRemoteImpl();
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("PenaltyGameRemote", impl);
            System.out.println("RMI GameRemote bound. Ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
