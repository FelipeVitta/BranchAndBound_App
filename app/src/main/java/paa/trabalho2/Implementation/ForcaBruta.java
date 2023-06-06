package paa.trabalho2.Implementation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.data.xy.XYDataset;
import paa.trabalho2.Shared.BestWay;
import paa.trabalho2.Shared.Caminhao;

public class ForcaBruta extends AlgorithmsBase {

    public ForcaBruta(Caminhao truck) {
        super(truck, "Brute Force Execution");
    }

    // METODO PARA LER O "stores.txt"
    public List<List<Integer>> readFile(String path) {
        try {

            BufferedReader buff = new BufferedReader(new FileReader(path));
            String linha = "";
            int i = 0;
            List<List<Integer>> matriz = new ArrayList<>();
            String[] lineString;
            while (true) {
                linha = buff.readLine();
                if (linha != null) {
                    matriz.add(i, new ArrayList<>());
                    lineString = linha.split(" ");
                    for (int y = 0; y < lineString.length; y++) {
                        matriz.get(i).add(y, Integer.parseInt(lineString[y]));
                    }
                } else {
                    break;
                }
                i++;
            }

            buff.close();
            return matriz;

        } catch (FileNotFoundException e) {
            e.getCause();
        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }

    // METODO PARA RETORNAR OS DESTINOS DE UMA LOJA
    public List<Integer> getDestinos(List<Integer> storeList) {
        // Se o tamanho da lista da loja for igual a 3, ela não tem destinos
        if (storeList.size() == 3) {
            return null;
        }
        // pegando os destinos daquela loja
        int i = 3;
        List<Integer> destinos = new ArrayList<>();
        while (true) {
            if (storeList.size() == i) {
                break;
            } else {
                destinos.add(storeList.get(i));
                i++;
            }
        }
        return destinos;
    }

    // METODO PARA RETORNAR AS LOJAS QUE O CAMINHÃO NÃO PODE PASSAR NO MOMENTO PARA
    // NÃO TER QUE PASSAR NOVAMENTE
    public List<Integer> getLojasWithoutPermissionToGo(List<List<Integer>> stores) {
        List<Integer> semPermissao = new ArrayList<>();
        for (List<Integer> store : stores) {
            List<Integer> destinos = getDestinos(store);
            if (destinos != null) {
                for (Integer lol : destinos) {
                    semPermissao.add(lol);
                }
            }
        }
        return semPermissao;
    }

    // METODO PARA DETERMINAR SE O CAMINHÃO PODE CARREGAR OS ITENS DA LOJA
    // ESPECIFICADA
    public boolean canTheTruckCarryMoreItems(List<List<Integer>> matriz, Integer store, Caminhao truck) {
        List<Integer> novasCargas = getDestinos(matriz.get(store));
        int tam = truck.getCargaAtual().size();
        if (truck.getCargaAtual().contains(store)) {
            tam = tam - 1;
        }
        if (novasCargas != null) {
            tam = tam + novasCargas.size();
        }
        if (truck.getCargaPossivel() - tam >= 0) {
            return true;
        } else {
            return false;
        }
    }

    // METODO PARA RETORNAR SE O CAMINHAO PODE IR PARA A LOJA ESPECIFICADA
    public boolean canIGoToThisLoja(List<List<Integer>> matriz, Integer store, Caminhao truck) {
        return !getLojasWithoutPermissionToGo(matriz).contains(store)
                && canTheTruckCarryMoreItems(matriz, store, truck);
    }

    // METODO PARA MUDAR A MATRIZ PARA SABER QUAIS LOJAS AGORA PODEM SER ACESSADAS E
    // PARA ENCHER A CARGA DO CAMINHÃO COM OS NOVOS PRODUTOS
    public List<List<Integer>> changeMatriz(List<List<Integer>> matriz, Integer passei, Caminhao truck) {
        int tamMatriz = matriz.get(passei).size();
        if (truck.getCargaAtual().contains(passei)) {
            truck.getCargaAtual().remove(passei);
        }
        if (tamMatriz > 3) {
            while (matriz.get(passei).size() > 3) {
                truck.getCargaAtual().add(matriz.get(passei).get(3));
                matriz.get(passei).remove(3);
            }
            return matriz;
        } else {
            return matriz;
        }
    }

    // METODO PARA RETORNAR AS LOJAS QUE O CAMINHAO PRECISA PASSAR
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

    // METODO PARA GERAR AS COMBINAÇÕES POSSÍVEIS COMEÇANDO A PARTIR DE UMA LOJA
    // (PRINCIPAL)
    public void generateCombinations(List<Integer> valores, int numeroInicial, List<List<Integer>> matrizCompleta,
            Caminhao truck, BestWay bestWay) {
        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i) == numeroInicial) {
                List<Integer> caminho = new ArrayList<>();
                caminho.add(valores.get(i));
                gerarCaminhos(valores, numeroInicial, i, caminho, matrizCompleta, truck, bestWay);
            }
        }
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

    // METODO AUXILIAR DO generateCombinations PARA GERAR CAMINHOS
    public void gerarCaminhos(List<Integer> valores, int numeroInicial, int indice, List<Integer> caminho,
            List<List<Integer>> matrizCompleta, Caminhao truck, BestWay bestWay) {
        if (caminho.size() == valores.size()) {
            // fazendo copia da matriz Original
            List<List<Integer>> copiaMatrizCompleta = fazerCopiaMatriz(matrizCompleta);
            tryCombinations(copiaMatrizCompleta, new ArrayList<>(caminho), truck, bestWay);
            // combinacoes.add(new ArrayList<>(caminho)); -> COLOCAR CODIGO PRA TESTAR TODOS
            // OS CAMINHOS
            return;
        }

        for (int i = 0; i < valores.size(); i++) {
            if (i != indice && !caminho.contains(valores.get(i))) {
                caminho.add(valores.get(i));
                gerarCaminhos(valores, numeroInicial, i, caminho, matrizCompleta, truck, bestWay);
                caminho.remove(caminho.size() - 1);
            }
        }
    }

    // METODO DE FORÇA BRUTA PARA TESTAR TODAS AS COMBINAÇÕES
    public void tryCombinations(List<List<Integer>> copiaMatrizCompleta, List<Integer> combinacao,
            Caminhao truck, BestWay bestWay) {
        List<List<Integer>> copia = fazerCopiaMatriz(copiaMatrizCompleta);
        for (Integer store : combinacao) {
            if (canIGoToThisLoja(copiaMatrizCompleta, store, truck)) {
                copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, store, truck);
            } else {
                truck.setCombustivelGastoAtual(0.0f);
                truck.setCargaAtual(new ArrayList<>());
                return;
            }
        }
        truck.setCargaAtual(new ArrayList<>());
        truck.setCombustivelGastoAtual(0.0f);
        // System.out.println("\n" + combinacao + "\n");
        calculaGasosa(combinacao, copia, truck, bestWay);
    }

    // METODO PARA CALCULAR A QUANTIDADE DE GASOLINA GASTA EM CADA TRAJETO VALIDO
    public void calculaGasosa(List<Integer> combinacao, List<List<Integer>> copiaMatrizCompleta, Caminhao truck,
            BestWay way) {
        List<Float> combustivel = new ArrayList<>(Arrays.asList(0.0f));
        List<List<Integer>> cargas = new ArrayList<>();
        cargas.add(new ArrayList<>());
        float[] currentCoordinates = { copiaMatrizCompleta.get(0).get(1), copiaMatrizCompleta.get(0).get(2) };
        float[] coordinatesToGo = { 0, 0 };
        for (Integer store : combinacao) {

            coordinatesToGo[0] = copiaMatrizCompleta.get(store).get(1);
            coordinatesToGo[1] = copiaMatrizCompleta.get(store).get(2);
            calcularDistancias(currentCoordinates[0], currentCoordinates[1], coordinatesToGo[0], coordinatesToGo[1],
                    truck);
            copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, store, truck);
            combustivel.add(this.truck.getCombustivelGastoAtual());
            cargas.add(new ArrayList<>(this.truck.getCargaAtual()));
            currentCoordinates[0] = coordinatesToGo[0];
            currentCoordinates[1] = coordinatesToGo[1];

        }
        // Calculando a volta para a origem
        calcularDistancias(currentCoordinates[0], currentCoordinates[1], copiaMatrizCompleta.get(0).get(1),
                copiaMatrizCompleta.get(0).get(2), truck);

        combustivel.add(this.truck.getCombustivelGastoAtual());
        cargas.add(new ArrayList<>(this.truck.getCargaAtual()));

        if (truck.getCombustivelGastoAtual() < way.getCombustivelGasto()) {
            way.setCombustivelGasto(truck.getCombustivelGastoAtual());
            way.setCaminho(new ArrayList<>(combinacao));
            way.setCargas(new ArrayList<>(cargas));
            way.setCombustiveis(new ArrayList<>(combustivel));
        }
        // System.out.println("Combustivel Gasto = " +
        // truck.getCombustivelGastoAtual());
        truck.setCombustivelGastoAtual(0.0f);
    }

    // METODO PARA CALCULAR A DISTANCIA EUCLIDIANA
    public void calcularDistancias(float x1, float y1, float x2, float y2, Caminhao truck) {
        float difX = x2 - x1;
        float difY = y2 - y1;
        difX = Math.abs(difX);
        difY = Math.abs(difY);
        float litros = 0.0f;

        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        // System.out.println(x1 + " " + y1 + " para " + x2 + " " + y2);
        // System.out.println("Carga Atual do caminhão = " +
        // truck.getCargaAtual().size());
        litros = (float) (distancia / (10 - 0.5 * truck.getCargaAtual().size()));
        truck.setCombustivelGastoAtual(truck.getCombustivelGastoAtual() + litros);
    }

    public void executeAlgorithm() {
        List<List<Integer>> mainMatrix = this.readFile(
                "F:\\VSCODE\\dantas-certo\\app\\src\\main\\java\\paa\\trabalho2\\Implementation\\lojas.txt");
        List<Integer> mandatoryStores = this.mandatoryStores(mainMatrix);
        BestWay bestWay = new BestWay();

        long startTime = System.currentTimeMillis();

        for (Integer store : mandatoryStores) {
            truck.setCargaAtual(new ArrayList<>());
            this.generateCombinations(mandatoryStores, store, mainMatrix, truck, bestWay);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        this.setExecutionTimeValue(totalTime);

        XYDataset dataset = createDataset(mainMatrix);
        this.graph.getXYPlot().setDataset(0, dataset);

        bestWay.getCaminho().add(0, 0);
        bestWay.getCaminho().add(0);
        System.out.println(bestWay);
        System.out.println("Cargas = " + bestWay.getCargas());
        System.out.println("Combustiveis = " + bestWay.getCombustiveis());

        drawBestWay(bestWay, mainMatrix);

        System.out.println("\nTempo total de execucao: " + totalTime + " ms");
    }
}
