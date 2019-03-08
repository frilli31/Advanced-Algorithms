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

        // DPATrial: aggiunge m copie dei primi m nodi a nodeNumbers
        List<Integer> nodeNumbers = new ArrayList<>();
        l.keySet().forEach(k -> nodeNumbers.addAll(Collections.nCopies(m, k)));

        Random rn = new Random();
        IntStream.rangeClosed(m + 1, n).forEach(u -> {
            // estrae lista di m nodi scelti trai i presenti
            Long added_arcs = rn.ints(m, 0, nodeNumbers.size())
                    .map(nodeNumbers::get)
                    .distinct()
                    .peek(nodeNumbers::add)
                    .peek(dest->this.addArc(u,dest))
                    .count();
            nodeNumbers.addAll(Collections.nCopies(added_arcs.intValue()+1, u));
        });
    }
}
