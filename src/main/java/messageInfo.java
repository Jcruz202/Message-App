import java.io.Serializable;
import java.util.ArrayList;

public class messageInfo implements Serializable {
    String data;
    Boolean sendALl;
    ArrayList<Integer> numClients = new ArrayList<>();
    ArrayList<Integer> onlineClients = new ArrayList<>();

}
