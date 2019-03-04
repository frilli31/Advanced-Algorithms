import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DPA extends Graph {
    public DPA(final int n, final int m) {
        super();

        // rendi il grafo completo
        IntStream.rangeClosed(1, m).forEach(this::addNode);
        for (int source : l.keySet())
            for (int destination : l.keySet())
                addArc(source, destination);

        // DPATrial: aggiunge m copie dei primi nodi a nodeNumbers
        List<Integer> nodeNumbers = new ArrayList<>();
        l.keySet().forEach(k -> nodeNumbers.addAll(Collections.nCopies(m, k)));

        IntStream.rangeClosed(m + 1, n).forEach(u -> {
            // estrae lista di m nodi scelti trai i presenti
            List<Integer> v1 = new Random().ints(m, 0, nodeNumbers.size())
                    .map(nodeNumbers::get)
                    .boxed()
                    .collect(Collectors.toList());
            // aggiungo una copia dei biliettini nell'urna
            nodeNumbers.addAll(v1);
            nodeNumbers.add(u);
            addNode(u);
            v1.forEach(dest -> addArc(u, dest));
        });
    }
}
