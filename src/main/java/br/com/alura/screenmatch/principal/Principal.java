package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String APIKEY = "&apikey=68145b8b";
    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.trim().replace(" ", "+") + APIKEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.trim().replace(" ", "+") + "&season=" + i + APIKEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);

        for (int i = 0; i < dados.totalTemporadas(); i++) {
            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            System.out.println("\nTemporada " + (i+1) + "\n");
            for (int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println("\t" + episodiosTemporada.get(j).titulo());
            }
        }

//        temporadas.forEach(t-> t.episodios().forEach(e-> System.out.println(e.titulo())));

//        List<String> nomes = Arrays.asList("Jacque", "Iasmin", "Paulo", "Rodrigo", "Nico");
//
//        nomes.stream()
//                .sorted()
//                .limit(3)
//                .filter(n->n.startsWith("N"))
//                .map(n-> n.toUpperCase())
//                .forEach(System.out::println);

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()).collect(Collectors.toList());
//      .toList() // n permite alteracao

//        System.out.println("Top 10 episódios:");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
////                .peek(e -> System.out.println("Primeiro filtro(N/A)" + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
////                .peek(e -> System.out.println("Segundo filtro(ordenacao)" + e))
//                .limit(10)
////                .peek(e -> System.out.println("Terceiro filtro(limitie)" + e))
//                .map(e -> e.titulo().toUpperCase())
////                .peek(e -> System.out.println("Quarto filtro(map)" + e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                            .map(d -> new Episodio(t.temporada(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.println("Digite um trecho do titulo do episodio");
//        String trechoTitulo = leitura.nextLine();
//
//         Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//
//         if(episodioBuscado.isPresent()){
//             System.out.println("Episódio encontrado!");
//             System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//         } else {
//             System.out.println("Episódio não encontrado");
//         }

//
//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate databusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(databusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episodio: " + e.getTitulo() +
//                                " Data lançamento: " + e.getDataLancamento().format(formatador)
//                ));

         Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                 .filter(e -> e.getAvaliacao()>0.0)
                 .collect(Collectors.groupingBy(Episodio::getTemporada,
                         Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao()>0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println(est);
        System.out.println("Média: " + est.getAverage());
        System.out.println("Menor avaliação: " + est.getMin());
        System.out.println("Maior avaliação: " + est.getMax());
        System.out.println("Quantidade: " + est.getCount());
    }
}
