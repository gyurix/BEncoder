package gyurix.bencoder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The main BEncoder class used for encoding and decoding BEncoded strings
 * Created by GyuriX on 2017. 01. 12
 */
public class BEncoder {
    /**
     * Input String, from which the data is read
     */
    private String in;
    /**
     * Output StringBuilder, stores the current output
     */
    private StringBuilder sb = new StringBuilder();
    /**
     * The index of the next readable character
     */
    private int id;

    /**
     * Initializes a new BEncoder, without any input
     */
    public BEncoder() {

    }

    /**
     * Initializes the new BEncoder with the given input
     *
     * @param in - The input of the BEncoder
     */
    public BEncoder(String in) {
        this.in = in;
    }

    /**
     * Sets the input of the BEncoder
     *
     * @param in - The new input you would like to use
     */
    public void setInput(String in) {
        this.in = in;
        id = 0;
    }

    /**
     * Resets the output of the BEncoder
     */
    public void resetOutput() {
        sb.setLength(0);
    }

    /**
     * Writes the given Object to the BEncoder output
     *
     * @param o - Writeable Object
     */
    public void write(Object o) {
        if (o instanceof Map) {
            sb.append('d');
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                write(e.getKey());
                write(e.getValue());
            }
            sb.append('e');
        } else if (o instanceof Iterable) {
            sb.append('l');
            writeAll((Iterable) o);
            sb.append('e');
        } else if (o.getClass().isArray()) {
            sb.append('l');
            if (o instanceof Object[]) {
                writeAll((Object[]) o);
            } else {
                int len = Array.getLength(o);
                for (int i = 0; i < len; ++i)
                    write(Array.get(o, i));
            }
            sb.append('e');
        } else if (o instanceof String) {
            String e = (String) o;
            sb.append(e.length()).append(':').append(e);
        } else if (o instanceof Long || o instanceof Integer || o instanceof Byte) {
            sb.append('i').append(o).append('e');
        } else if (o instanceof Boolean) {
            sb.append('i').append((Boolean) o ? '1' : '0').append('e');
        }
    }

    /**
     * Writes all the given objects to the BEncoder output
     *
     * @param os - Writeable objects
     */
    public void writeAll(Object... os) {
        for (Object o : os)
            write(o);
    }

    /**
     * Writes all the objects found in the given Iterable to the BEncoder output
     *
     * @param os - The Iterable containing all the writeable objects
     */
    public void writeAll(Iterable<Object> os) {
        for (Object o : os)
            write(o);
    }

    /**
     * Reads all the readable objects from the input
     *
     * @return The objects stored in the input BEncoded data
     */
    public ArrayList<Object> readAll() {
        ArrayList<Object> out = new ArrayList<>();
        while (true) {
            Object o = read();
            if (o == null)
                return out;
            out.add(o);
            ++id;
        }
    }

    /**
     * Reads the next object stored in the input BEncoded data
     *
     * @return The next object stored in the input BEncoded data
     */
    public Object read() {
        if (id >= in.length())
            return null;
        char type = in.charAt(id);
        ++id;
        if (type == 'i') {
            long out = 0;
            int start = id;
            int limit = id + 22;
            boolean neg = false;
            for (; id <= limit; ++id) {
                char c = in.charAt(id);
                if (id == start && c == '-') {
                    neg = true;
                    continue;
                }
                if (c == 'e')
                    return neg ? -out : out;
                out = out * 10 + (c - 48);
            }
        } else if (type == 'l') {
            ArrayList<Object> out = new ArrayList<>();
            while (true) {
                if (in.charAt(id) == 'e')
                    return out;
                out.add(read());
                ++id;
            }
        } else if (type == 'd') {
            LinkedHashMap<Object, Object> out = new LinkedHashMap<>();
            while (true) {
                if (in.charAt(id) == 'e')
                    return out;
                Object key = read();
                ++id;
                Object value = read();
                out.put(key, value);
                ++id;
            }
        } else if (type >= '0' && type <= '9') {
            int len = type - 48;
            int limit = id + 11;
            for (; id <= limit; ++id) {
                char c = in.charAt(id);
                if (c == ':') {
                    String out = in.substring(id + 1, id + len + 1);
                    id += len;
                    return out;
                }
                len = len * 10 + (c - 48);
            }
        }
        return null;
    }

    /**
     * Returns the current output of this BEncoder
     *
     * @return The current output of this BEncoder
     */
    @Override
    public String toString() {
        return sb.toString();
    }
}
