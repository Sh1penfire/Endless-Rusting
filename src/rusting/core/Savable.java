package rusting.core;

import arc.Core;
import arc.util.io.Reads;
import arc.util.io.Writes;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Savable {

    public void startWrite(){
        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        Writes write = Writes.get(new DataOutputStream(arr));

        write(write);

        byte[] bytes = arr.toByteArray();
        String save = new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);

        Core.settings.put("your.save.key.entry", save);
    }

    public void startRead(){
        ByteArrayInputStream arr = new ByteArrayInputStream(Core.settings.getString("your.save.key.entry").getBytes(StandardCharsets.UTF_8));
        Reads read = Reads.get(new DataInputStream(arr));
        read(read, read.b());
    }

    public void read(Reads read, Byte version){

    }

    public void write(Writes write){


    }
}
