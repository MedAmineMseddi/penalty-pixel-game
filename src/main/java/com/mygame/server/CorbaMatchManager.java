package com.mygame.server;

/**
 * CORBA stub / placeholder for match manager.
 * Real CORBA implementation would require an ORB (JacORB or similar) and IDL definitions.
 *
 * This class provides methods the client code can call locally while you integrate a CORBA backend.
 */
public class CorbaMatchManager {

    public String createMatch(String hostPlayer) {
        // stub: return a generated match id
        String id = "MATCH-" + System.currentTimeMillis();
        System.out.println("createMatch host=" + hostPlayer + " -> " + id);
        return id;
    }

    public boolean joinMatch(String matchId, String playerId) {
        System.out.println(playerId + " joined " + matchId);
        return true;
    }

    public void startMatch(String matchId) {
        System.out.println("startMatch " + matchId);
    }

    public void endMatch(String matchId) {
        System.out.println("endMatch " + matchId);
    }

    public String getMatchStatus(String matchId) {
        return "ONGOING";
    }
}
