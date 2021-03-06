package pr0.ves.simplechat.Server;


import pr0.ves.simplechat.common.ClientData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ServerThread extends Thread {
    private Socket socket = null;
    private ClientData clientData;
    private final List<MessageListener> listeners = new ArrayList<>();
    private boolean initialized = false;
    private boolean connectionCheck = true;

    ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while (!initialized) {
                inputLine = in.readLine();
                if (inputLine.startsWith("CHECKNAME=")) {
                    this.clientData = new ClientData(inputLine.split("=",2)[1].trim(), socket.getInetAddress().getHostAddress(), in, out);
                }
                readMessage(inputLine);
            }
            while ((inputLine = in.readLine()) != null) {
                readMessage(inputLine);
            }

        } catch (IOException e) {
            readMessage("EXIT");
            e.printStackTrace();
        }
    }

    void addListener(MessageListener toAdd) {
        listeners.add(toAdd);
    }

    void setInitialized(boolean check) {
        this.initialized = check;
    }

    ClientData getClientData() {
        return clientData;
    }

    boolean isInitialized() {
        return initialized;
    }

    Socket getSocket() {
        return socket;
    }

    private void readMessage(String message) {
        listeners.forEach(listener -> listener.processMessage(this,message));
    }

    public boolean isConnectionCheck() {
        return connectionCheck;
    }

    public void setConnectionCheck(boolean connectionCheck) {
        this.connectionCheck = connectionCheck;
    }
}
