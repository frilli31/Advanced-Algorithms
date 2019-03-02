import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DPA extends Graph {
    public DPA(int n, int m) {
        super();
        if(m>n){
            m=n;
        }

        // rendi il grafo completo
        IntStream.rangeClosed(1, m).forEach(this::addNode);
        for(int source: l.keySet())
            for(int destination: l.keySet())
                addArc(source, destination);

        // DPATrial: aggiunge m copie dei primi nodi a nodeNumbers
        final int finalM = m;
        List<Integer> nodeNumbers = new ArrayList<>();
        l.keySet().forEach(k-> nodeNumbers.addAll(Collections.nCopies(finalM, k)));

        Random rn = new Random();
        for(int u=m+1; u<=n; u++) {
            // estrae lista di m nodi scelti trai i presenti
            List<Integer> v1 = IntStream.range(0, m)
                    .map(x -> rn.nextInt(nodeNumbers.size()))
                    .map(x -> nodeNumbers.get(x))
                    .boxed()
                    .collect(Collectors.toList());
            // aggiungo una copia dei biliettini nell'urna
            nodeNumbers.addAll(v1);
            nodeNumbers.add(u);
            final int me = u;
            addNode(me);
            v1.forEach(dest -> addArc(me, dest));
        }

    }
}
