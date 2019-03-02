import java.util.*;
import java.util.stream.IntStream;

public class ER extends Graph {
    public ER(int n, double p) {
        super();
        IntStream.iterate(1, x->x+1)
                .limit(n)
                .forEach(this::addNode);
        Random rn = new Random();
        for(int source: l.keySet())
            for(int destination: l.keySet())
                if(rn.nextFloat()<p)
                    addArc(source, destination);
    }
}
