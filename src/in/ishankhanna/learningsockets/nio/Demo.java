/**
 * Copyright 2015 ishan
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.ishankhanna.learningsockets.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

public class Demo {

    static ByteBuffer buffer = ByteBuffer.allocate(512);

    public static void main(String[] args) throws IOException {

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        server.socket().bind(new InetSocketAddress(1337));
        Selector selector = Selector.open();
        SelectionKey serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Set readyKeys = selector.selectedKeys();
            Iterator iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                if (key == serverKey) {
                    if (key.isAcceptable()) {
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
                        clientKey.attach(new Integer(0));
                    } else if (key.isConnectable()) {

                    } else if (key.isReadable()) {

                    } else if (key.isWritable()) {

                    }
                } else {
                    SocketChannel client = (SocketChannel) key.channel();

                    if (!key.isReadable())
                        continue;
                    int bytesread = client.read(buffer);
                    if (bytesread == -1) {
                        key.cancel();
                        client.close();
                        continue;
                    }
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    System.out.println(new String(bytes));
                    buffer.clear();
                }
            }
        }
    }
}
