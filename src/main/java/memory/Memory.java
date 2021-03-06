package memory;

import shell.Shell;
import utils.Utils;
import virtualmemory.virtualmemory;
import java.util.Vector;

public class Memory {
    private static byte[] memory = new byte[256];

    // zapisz całą stronę w wybranej ramce
    public static boolean write(Vector<Byte> data, int frame) {
        if (frame < 0 || frame > 15) {
            return false;
        }

        for (int i = 0; i < data.size(); i++) {
            memory[frame * 16 + i] = data.get(i);
        }

        Utils.log(String.format("loaded page to frame %d", frame));
        return true;
    }

    // odczytaj zawartość całej ramki
    public static Vector<Byte> readFrame(int frame) {
        Utils.log(String.format("reading page from frame %d", frame));

        // czytam z pamięci
        Vector<Byte> tmp = new Vector<Byte>();
        try {
            for (int i = 0; i < 16; i++) {
                tmp.add(memory[frame * 16 + i]);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return tmp;
    }

    // wyświetl surową zawartość pamięci
    public static void print() {
        for (int i = 0; i < 16; i++) {
            Shell.print(String.format("%n%2d:", i));
            for (int j = 0; j < 16; j++) {
                Shell.print(String.format(" %3d", memory[i * 16 + j]));
            }
        }
        Shell.println("");

        Utils.log("showing raw contents of RAM");
    }

    // zapisz pojedynczy bajt zgodnie z tablicą stronic danego procesu
    public void write(byte data, int processID, byte address) {
        // translacja adresu
        int page = (address & 0xf0) >>> 4;
        int offset = address & 0x0f;

        // sprawdzam wartość w PageTable danego procesu
        int frame = virtualmemory.getFrame(processID, page);

        // wpisuję do pamięci
        memory[frame * 16 + offset] = data;

        Utils.log(String.format("loaded page %d (PID = %d) to frame %d", page, processID, frame));
    }

    // zapisz pojedynczy bajt
    void write(byte data, int i) {
        try {
            memory[i] = data;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Utils.log(String.format("loaded byte to address %d", i));
    }

    // odczytaj bajt spod adresu zgodnego z tablicą stronic danego procesu
    public byte read(int processID, byte address) {
        // translacja adresu
        int page = (address & 0xf0) >>> 4;
        int offset = address & 0x0f;

        // sprawdzam wartość w PageTable danego procesu
        int frame;
        frame = virtualmemory.getFrame(processID, page);

        // czytam z pamięci
        Utils.log(String.format("reading byte from address %d", frame * 16 + offset));
        return memory[frame * 16 + offset];
    }

    // odczytaj bajt z podanej komórki pamięci
    public byte read(int i) {
        Utils.log(String.format("reading byte from address %d", i));

        try {
            return memory[i];
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }

    }
}