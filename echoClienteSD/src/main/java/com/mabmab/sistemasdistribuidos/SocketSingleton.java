package com.mabmab.sistemasdistribuidos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketSingleton {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    private SocketSingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Socket getSocket() throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(ConnectionConfig.SERVER_IP, ConnectionConfig.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        return socket;
    }

    public static synchronized PrintWriter getOutputWriter() throws IOException {
        if (out == null || socket.isClosed()) {
            getSocket(); // Ensure socket is initialized
        }
        return out;
    }

    public static synchronized BufferedReader getBufferedReader() throws IOException {
        if (in == null || socket.isClosed()) {
            getSocket(); // Ensure socket is initialized
        }
        return in;
    }

    public static synchronized void closeSocket() throws IOException {
        if (socket != null && !socket.isClosed()) {
            in.close();
            out.close();
            socket.close();
        }
    }
}
