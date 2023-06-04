package paa.trabalho2.Implementation;

import org.jfree.data.xy.XYDataset;
import paa.trabalho2.Shared.BestWay;
import paa.trabalho2.Shared.Caminhao;
import java.util.ArrayList;
import java.util.List;

public class BranchAndBound extends AlgorithmsBase{

    public BranchAndBound(Caminhao truck) {
        super(truck);
    }

    // METODO PARA RETORNAR OS DESTINOS DE UMA LOJA
    public List<Integer> getDestinos(List<Integer> loja) {
        // Se o tamanho da lista da loja for igual a 3, ela não tem destinos
        if (loja.size() == 3) {
            return null;
        }
        // pegando os destinos daquela loja
        int i = 3;
        List<Integer> destinos = new ArrayList<>();
        while (true) {
            if (loja.size() == i) {
                break;
            } else {
                destinos.add(loja.get(i));
                i++;
            }
        }
        return destinos;
    }

    // METODO PARA RETORNAR AS LOJAS QUE O CAMINHÃO NÃO PODE PASSAR NO MOMENTO PARA
    // NÃO TER QUE PASSAR NOVAMENTE
    public List<Integer> getLojasWithoutPermissionToGo(List<List<Integer>> lojas) {
        List<Integer> semPermissao = new ArrayList<>();
        for (List<Integer> loja : lojas) {
            List<Integer> destinos = getDestinos(loja);
            if (destinos != null) {
                semPermissao.addAll(destinos);
            }
        }
        return semPermissao;
    }

    // METODO PARA DETERMINAR SE O CAMINHÃO PODE CARREGAR OS ITENS DA LOJA
    // ESPECIFICADA
    public boolean canTheTruckCarryMoreItems(List<List<Integer>> matriz, Integer loja) {
        List<Integer> novasCargas = getDestinos(matriz.get(loja));
        int tam = this.truck.getCargaAtual().size();
        if (this.truck.getCargaAtual().contains(loja)) {
            tam = tam - 1;
        }
        if (novasCargas != null) {
            tam = tam + novasCargas.size();
        }
        return this.truck.getCargaPossivel() - tam >= 0;
    }

    // METODO PARA RETORNAR SE O this.truck PODE IR PARA A LOJA ESPECIFICADA
    public boolean canIGoToThisLoja(List<List<Integer>> matriz, Integer loja) {
        return !getLojasWithoutPermissionToGo(matriz).contains(loja)
                && canTheTruckCarryMoreItems(matriz, loja);
    }

    // METODO PARA MUDAR A MATRIZ PARA SABER QUAIS LOJAS AGORA PODEM SER ACESSADAS E
    // PARA ENCHER A CARGA DO CAMINHÃO COM OS NOVOS PRODUTOS
    public List<List<Integer>> changeMatriz(List<List<Integer>> matriz, Integer passei) {
        int tamMatriz = matriz.get(passei).size();
        this.truck.getCargaAtual().remove(passei);
        if (tamMatriz > 3) {
            while (matriz.get(passei).size() > 3) {
                this.truck.getCargaAtual().add(matriz.get(passei).get(3));
                matriz.get(passei).remove(3);
            }
            return matriz;
        } else {
            return matriz;
        }
    }

    // METODO PARA RETORNAR AS LOJAS QUE O this.truck PRECISA PASSAR
    public List<Integer> mandatoryStores(List<List<Integer>> matrizCompleta) {
        List<Integer> lojasParaPassar = new ArrayList<>();
        List<Integer> destinos = getLojasWithoutPermissionToGo(matrizCompleta);
        for (int i = 1; i < matrizCompleta.size(); i++) {
            if (matrizCompleta.get(i).size() > 3 || destinos.contains(i)) {
                lojasParaPassar.add(i);
            }
        }
        return lojasParaPassar;
    }

    // METODO PARA FAZER UMA COPIA DA MATRIZ ORIGINAL
    public List<List<Integer>> fazerCopiaMatriz(List<List<Integer>> matrizOriginal) {
        List<List<Integer>> matrizCopia = new ArrayList<>();

        for (List<Integer> lista : matrizOriginal) {
            List<Integer> novaLinha = new ArrayList<>(lista);
            matrizCopia.add(novaLinha);
        }
        return matrizCopia;
    }

    // metodo para pegar a matriz completa
    public List<List<Integer>> getMatrizCompleta() {
        return readFile("lojas.txt");
    }

    // METODO PARA GERAR AS COMBINAÇÕES POSSIVEIIS COMEÇANDO A PARTIR DE UMA LOJA
    // (PRINCIPAL)
    public void generateCombinations(List<Integer> valores, int numeroInicial, BestWay bestWay) {
        List<List<Integer>> matrizCompleta = getMatrizCompleta();
        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i) == numeroInicial) {
                List<Integer> caminho = new ArrayList<>();
                caminho.add(valores.get(i));
                changeMatriz(matrizCompleta, valores.get(i));
                gerarCaminhos(valores, i, caminho, matrizCompleta, bestWay);
            }
        }

    }

    // METODO PRINCIPAL DE BRANCH AND BOUND RESPONSAVEL PELAS PODAS
    public void gerarCaminhos(List<Integer> valores, int indice, List<Integer> caminho,
                              List<List<Integer>> matrizCompleta, BestWay bestWay) {

        if (caminho.size() == valores.size()) {
            List<List<Integer>> copiaMatrizCompleta = getMatrizCompleta();
            // Chama o método para calcular a quantidade gasta de cada combinação
            // conveniente e ver se é melhor que a melhor atual
            calculaGasosa(caminho, copiaMatrizCompleta, bestWay);
            return;
        }

        for (int i = 0; i < valores.size(); i++) {
            if (i != indice && !caminho.contains(valores.get(i))) {
                // CONDIÇÃO DE PODA (VIOLAÇÃO DE RESTRIÇÃO)
                if (!canIGoToThisLoja(matrizCompleta, valores.get(i))) {
                    continue;
                }
                // fazendo copia da matriz antes da modificação
                List<List<Integer>> copia = fazerCopiaMatriz(matrizCompleta);
                // fazendo copia da carga atual do caminhão antes da modificação
                List<Integer> copiacam = new ArrayList<>(this.truck.getCargaAtual());
                // mudando o estado da matriz e atualizando os valores novos do caminhão
                matrizCompleta = changeMatriz(matrizCompleta, valores.get(i));
                caminho.add(valores.get(i));
                // chamando recursivamente a função
                gerarCaminhos(valores, i, caminho, matrizCompleta, bestWay);
                // removendo os efeitos da adição da ultima loja no caminho
                caminho.remove(caminho.size() - 1);
                matrizCompleta = copia;
                this.truck.setCargaAtual(new ArrayList<>(copiacam));
            }
        }
    }

    // METODO PARA CALCULAR A QUANTIDADE DE GASOLINA GASTA EM CADA TRAJETO VALIDO
    public void calculaGasosa(List<Integer> combinacao, List<List<Integer>> copiaMatrizCompleta, BestWay way) {
        int i = 0;
        float[] currentCoordinates = { copiaMatrizCompleta.get(0).get(1), copiaMatrizCompleta.get(0).get(2) };
        float[] coordinatesToGo = { 0, 0 };
        for (Integer loja : combinacao) {
            coordinatesToGo[0] = copiaMatrizCompleta.get(loja).get(1);
            coordinatesToGo[1] = copiaMatrizCompleta.get(loja).get(2);
            calcularDistancias(currentCoordinates[0], currentCoordinates[1], coordinatesToGo[0], coordinatesToGo[1]);
            copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, loja); // atualizando a carga do caminhao
            currentCoordinates[0] = coordinatesToGo[0];
            currentCoordinates[1] = coordinatesToGo[1];

        }
        // Calculando a volta para a origem
        calcularDistancias(currentCoordinates[0], currentCoordinates[1], copiaMatrizCompleta.get(0).get(1),
                copiaMatrizCompleta.get(i).get(2));
        if (this.truck.getCombustivelGastoAtual() < way.getCombustivelGasto()) {
            way.setCombustivelGasto(this.truck.getCombustivelGastoAtual());
            List<Integer> melhorCaminho = new ArrayList<>(combinacao);
            way.setCaminho(melhorCaminho);
        }
        this.truck.setCargaAtual(new ArrayList<>());
        this.truck.setCombustivelGastoAtual(0.0f);
    }

    // METODO PARA CALCULAR A DISTANCIA EUCLIDIANA
    public void calcularDistancias(float x1, float y1, float x2, float y2) {
        float difX = x2 - x1;
        float difY = y2 - y1;
        difX = Math.abs(difX);
        difY = Math.abs(difY);
        float litros;

        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        litros = (float) (distancia / (10 - 0.5 * this.truck.getCargaAtual().size()));
        this.truck.setCombustivelGastoAtual(this.truck.getCombustivelGastoAtual() + litros);
    }

    // METODO INICIAL DO BRANCH AND BOUND
    public BestWay branchAndBound(List<Integer> lojasToPass, List<List<Integer>> matrizPrincipal, BestWay bestWay) {
        // podando os nós de todas as lojas que inicialmente não podem ser visitadas
        List<Integer> lojasCantGo = getLojasWithoutPermissionToGo(matrizPrincipal);
        List<Integer> beginLojas = new ArrayList<>();
        for (Integer loja : lojasToPass) {
            if (!lojasCantGo.contains(loja)) {
                beginLojas.add(loja);
            }
        }
        for (Integer loja : beginLojas) {
            this.truck.setCargaAtual(new ArrayList<>());
            generateCombinations(lojasToPass, loja, bestWay);
        }

        bestWay.getCaminho().add(0, 0);
        bestWay.getCaminho().add(0);

        return bestWay;

    }

    public void executeAlgorithm() {
        List<List<Integer>> mainMatrix = this.readFile("E:\\GitHub Projects\\paa-trabalho2\\app\\src\\main\\java\\paa\\trabalho2\\Implementation\\lojas.txt");
        List<Integer> mandatoryStores = this.mandatoryStores(mainMatrix);
        BestWay best = new BestWay();

        long startTime = System.currentTimeMillis();

        BestWay bestWay = this.branchAndBound(mandatoryStores, mainMatrix, best);

        XYDataset dataset = createDataset(mainMatrix);
        this.graph.getXYPlot().setDataset(0, dataset);

        drawBestWay(bestWay, mainMatrix);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        super.executionTime = totalTime;

        System.out.println("\nTempo total de execucao: " + totalTime + " ms");

    }

}

