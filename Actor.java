import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Actor{
    public String name;
    public List<String> network;

    public Actor(String name,List<String>network){
        this.name = name;
        this.network = network;
    }
    public void updateNetwork(ArrayList<String> new_network){
        Set<String> set = new HashSet<>(network);
        set.addAll(new_network);
        network = new ArrayList<>(set);
    }
}