package ru.geekbrains.lesson1networkstorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    private void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress("localhost", 9000));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started");

        while (true) {
            selector.select();
            System.out.println("New selector event");
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    System.out.println("New selector acceptable event");
                    register(selector, serverSocket);
                }

                if (selectionKey.isReadable()) {
                    System.out.println("New selector readable event");
//                    System.out.println("New message: " + readMessage(selectionKey) + "Thread name: " + Thread.currentThread().getName());
                    readMessage(selectionKey);
                    writeMessage(selectionKey, readMessage(selectionKey));
                }
                iterator.remove();
            }
        }
    }

    public void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        System.out.println("New client is connected");
    }

    public ByteBuffer readMessage(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        client.read(byteBuffer);
        String message = new String(byteBuffer.array());
        System.out.println("New message: " + message + "Thread name: " + Thread.currentThread().getName());
        return byteBuffer;
    }

    private void writeMessage(SelectionKey key, ByteBuffer byteBuffer) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        client.write(byteBuffer);
    }

}
