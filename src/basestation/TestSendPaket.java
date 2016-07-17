package basestation;

//TODO
//was ist hier noch unbekannt/unter arbeit
// kann man wirklich die stati in 5 bit unterbringen, aber doch nur messagesize in byte -> lieber 8bit machen?????

public class TestSendPaket extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 4;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 134;//TODO which AM_TYPE ???

    /** Create a new TestSensorMsg of size 2. */
    public TestSendPaket() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new TestSensorMsg of the given data_length. */
    public TestSendPaket(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new TestSensorMsg with the given data_length
     * and base offset.
     */
    public TestSendPaket(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new TestSensorMsg using the given byte array
     * as backing store.
     */
    public TestSendPaket(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new TestSensorMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public TestSendPaket(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new TestSensorMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public TestSendPaket(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new TestSensorMsg embedded in the given message
     * at the given base offset.
     */
    public TestSendPaket(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new TestSensorMsg embedded in the given message
     * at the given base offset and length.
     */
    public TestSendPaket(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <TestSensorMsg> \n";
      try {
        s += "  [counter=0x"+Long.toHexString(get_state())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: counter
    //   Field type: int, unsigned
    //   Offset (bits): 0
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field is signed (false).
     */
    public static boolean isSigned_node_id() {
        return false;
    }
    
    public static boolean isSigned_state() {
        return false;
    }
    
    public static boolean isSigned_samplerate() {
        return false;
    }

    /**
     * Return whether the field is an array (false).
     */
    public static boolean isArray_node_id() {
        return false;
    }

    public static boolean isArray_state() {
        return false;
    }

    public static boolean isArray_samplerate() {
        return false;
    }
    
    /**
     * Return the offset (in bytes) of the field
     */
    public static int offset_node_id() {
        return 0;
    }

    public static int offset_state() {
        return 1;
    }

    public static int offset_samplerate() {
        return 2;
    }
    
    /**
     * Return the offset (in bits) of the field
     */
    public static int offsetBits_node_id() {
        return 0;
    }

    public static int offsetBits_state() {
        return 8;
    }

    public static int offsetBits_samplerate() {
        return 16;
    }
    
    /**
     * Return the value (as a int) of the field
     */
    public int get_node_id() {
        return (int)getUIntBEElement(offsetBits_node_id(), 8);
    }

    public int get_state() {
        return (int)getUIntBEElement(offsetBits_state(), 8);
    }

    public int get_samplerate() {
        return (int)getUIntBEElement(offsetBits_samplerate(), 16);
    }
    
    /**
     * Set the value of the field
     */
    public void set_node_id(int value) {
        setUIntBEElement(offsetBits_node_id(), 8, value);
    }

    public void set_state(int value) {
        setUIntBEElement(offsetBits_state(), 8, value);
    }

    public void set_samplerate(int value) {
        setUIntBEElement(offsetBits_samplerate(), 16, value);
    }
    
    /**
     * Return the size, in bytes
     */
    public static int size_node_id() {
        return 1;
    }

    public static int size_state() {
        return 1;
    }

    public static int size_samplerate() {
        return 2;
    }
    
    /**
     * Return the size, in bits
     */
    public static int sizeBits_node_id() {
        return 8;
    }

    public static int sizeBits_state() {
        return 8;
    }    

    public static int sizeBits_samplerate() {
        return 16;
    }
}
