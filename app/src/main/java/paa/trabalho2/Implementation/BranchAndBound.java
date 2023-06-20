//Feito por:
//Felipe Leal, Guilherme Dantas e Laura Iara
package paa.trabalho2.Implementation;

import org.jfree.data.xy.XYDataset;
import paa.trabalho2.Shared.BestWay;
import paa.trabalho2.Shared.Caminhao;
import java.util.ArrayList;
import java.util.List;

public class BranchAndBound extends AlgorithmsBase {

    public BranchAndBound(Caminhao truck) {
        super(truck, "Branch and Bound Execution");
        this.lowerBound = 10000000f; // setando o lower bound
    }

    // METODO PARA RETORNAR OS DESTINOS DE UMA LOJA
    public List<Integer> getDestinations(List<Integer> loja) {
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
    public List<Integer> getStoresWithoutPermissionToGo(List<List<Integer>> lojas) {
        List<Integer> semPermissao = new ArrayList<>();
        for (List<Integer> loja : lojas) {
            List<Integer> destinos = getDestinations(loja);
            if (destinos != null) {
                semPermissao.addAll(destinos);
            }
        }
        return semPermissao;
    }

    // METODO PARA DETERMINAR SE O CAMINHÃO PODE CARREGAR OS ITENS DA LOJA
    // ESPECIFICADA
    public boolean canTheTruckCarryMoreItems(List<List<Integer>> matriz, Integer loja) {
        List<Integer> novasCargas = getDestinations(matriz.get(loja));
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
    public boolean canIGoToThisStore(List<List<Integer>> matriz, Integer loja) {
        return !getStoresWithoutPermissionToGo(matriz).contains(loja)
                && canTheTruckCarryMoreItems(matriz, loja);
    }

    // METODO PARA MUDAR O CONTEXTO DA MATRIZ, ENCHER A CARGA DO CAMINHÃO COM OS
    // NOVOS PRODUTOS E ATUALIZAR O ARRAY DE
    // CARGAS DO CAMINHAO
    public List<List<Integer>> changeMatrix(List<List<Integer>> matriz, Integer passei) {
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
        List<Integer> destinos = getStoresWithoutPermissionToGo(matrizCompleta);
        for (int i = 1; i < matrizCompleta.size(); i++) {
            if (matrizCompleta.get(i).size() > 3 || destinos.contains(i)) {
                lojasParaPassar.add(i);
            }
        }
        return lojasParaPassar;
    }

    // METODO PARA FAZER UMA COPIA DA MATRIZ ORIGINAL
    public List<List<Integer>> doCopyMatrix(List<List<Integer>> matrizOriginal) {
        List<List<Integer>> matrizCopia = new ArrayList<>();

        for (List<Integer> lista : matrizOriginal) {
            List<Integer> novaLinha = new ArrayList<>(lista);
            matrizCopia.add(novaLinha);
        }
        return matrizCopia;
    }

    // METODO PARA PEGAR A MATRIZ COMPLETA
    public List<List<Integer>> getMatrizCompleta() {
        return readFile(
                "F:\\VSCODE\\dantas-certo\\app\\src\\main\\java\\paa\\trabalho2\\Implementation\\lojas.txt");
    }

    // METODO PARA GERAR AS COMBINAÇÕES POSSIVEIIS COMEÇANDO A PARTIR DE UMA LOJA
    public void generateCombinations(List<Integer> valores, int numeroInicial, BestWay bestWay) {
        List<List<Integer>> matrizCompleta = getMatrizCompleta();
        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i) == numeroInicial) {
                List<Integer> caminho = new ArrayList<>();
                //Colocando as informações iniciais do caminhão
                this.truck.getCargas().add(new ArrayList<>());
                this.truck.getCombustiveis().add(0.0f);
                // atualizando o combustivel gasto atual do caminhão
                calcularDistancias(matrizCompleta.get(0).get(1), matrizCompleta.get(0).get(2),
                        matrizCompleta.get(numeroInicial).get(1),
                        matrizCompleta.get(numeroInicial).get(2));
                caminho.add(valores.get(i));
                // atualizando as cargas do caminhão e mudando o contexto da matriz
                changeMatrix(matrizCompleta, valores.get(i));
                // atualizando a lista de cargas do caminhão
                this.truck.getCargas().add(new ArrayList<>(this.truck.getCargaAtual()));
                branchAndBound(valores, i, caminho, matrizCompleta, bestWay);
            }
        }

    }

    // METODO DO CALCULO DO LOWER BOUND
    public float calculateBtwTwoStores(float x1, float y1, float x2, float y2, List<Integer> caminho,
            List<Integer> valores) {
        float difX = x2 - x1;
        float difY = y2 - y1;
        difX = Math.abs(difX);
        difY = Math.abs(difY);
        float litros;

        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        litros = (float) (distancia / (10 - 0.5 * this.truck.getCargaAtual().size()));        
            return litros;       
    }

    // METODO PRINCIPAL DE BRANCH AND BOUND RESPONSAVEL PELAS PODAS
    public void branchAndBound(List<Integer> valores, int indice, List<Integer> caminho,
            List<List<Integer>> matrizCompleta, BestWay bestWay) {
        int storeToGo;
        int actualStore;
        // Condição de parada --> se todas as lojas já foram visitadas
        if (caminho.size() == valores.size()) {
            actualStore = caminho.get(caminho.size() - 1);
            storeToGo = 0;
            // Calculando o combustivel gasto ao voltar para a origem
            calcularDistancias(matrizCompleta.get(actualStore).get(1), matrizCompleta.get(actualStore).get(2),
                    matrizCompleta.get(storeToGo).get(1), matrizCompleta.get(storeToGo).get(2));
            // Atualizando as cargas do caminhão ao voltar para a origem
            changeMatrix(matrizCompleta, 0);
            this.truck.getCargas().add(new ArrayList<>(this.truck.getCargaAtual()));
            // Se o resultado desse caminho for melhor, salvar o resultado no BestWay
            if (this.truck.getCombustivelGastoAtual() < bestWay.getCombustivelGasto()) {
                bestWay.setCaminho(new ArrayList<>(caminho));
                bestWay.setCargas(this.truck.getCargas());
                bestWay.setCombustiveis(this.truck.getCombustiveis());
                bestWay.setCombustivelGasto(this.truck.getCombustivelGastoAtual());
            }
            return;
        }

        for (int i = 0; i < valores.size(); i++) {
            if (i != indice && !caminho.contains(valores.get(i))) {
                // CONDIÇÃO DE PODA (VIOLAÇÃO DE RESTRIÇÃO)
                if (!canIGoToThisStore(matrizCompleta, valores.get(i))) {
                    continue;
                }
                storeToGo = valores.get(i);
                actualStore = caminho.get(caminho.size() - 1);
                // Calculo do Lower Bound
                lowerBound = this.truck.getCombustivelGastoAtual()
                        + calculateBtwTwoStores(matrizCompleta.get(actualStore).get(1),
                                matrizCompleta.get(actualStore).get(2), matrizCompleta.get(storeToGo).get(1),
                                matrizCompleta.get(storeToGo).get(2), caminho, valores);
                // CONDIÇÃO DE PODA POR MAIS PROMISSOR
                if (!(lowerBound < bestWay.getCombustivelGasto())) {
                    continue;
                }
                // Fazendo copias antes da modificação da matriz
                List<List<Integer>> copia = doCopyMatrix(matrizCompleta);
                List<Integer> copyTruck = new ArrayList<>(this.truck.getCargaAtual());
                float copiaCombustivel = this.truck.getCombustivelGastoAtual();
                List<List<Integer>> copiaCargas = new ArrayList<>(this.truck.getCargas());
                List<Float> copiaCombustiveis = new ArrayList<>(this.truck.getCombustiveis());
                // Atualizando o combustivel gasto atual do caminhão e o array de combustiveis
                calcularDistancias(matrizCompleta.get(actualStore).get(1),
                        matrizCompleta.get(actualStore).get(2),
                        matrizCompleta.get(storeToGo).get(1), matrizCompleta.get(storeToGo).get(2));
                // Mudando o estado da matriz, atualizando a carga do caminhão e o array de
                // cargas
                matrizCompleta = changeMatrix(matrizCompleta, valores.get(i));
                this.truck.getCargas().add(new ArrayList<>(this.truck.getCargaAtual()));
                // Adicionando a loja ao caminho
                caminho.add(valores.get(i));
                // Chamando recursivamente a função
                branchAndBound(valores, i, caminho, matrizCompleta, bestWay);
                // Removendo a loja do caminho
                caminho.remove(caminho.size() - 1);
                // Removendo todos os efeitos da adição daquela loja no caminho
                matrizCompleta = copia;
                this.truck.setCargaAtual(new ArrayList<>(copyTruck));
                this.truck.setCombustivelGastoAtual(copiaCombustivel);
                this.truck.setCargas(copiaCargas);
                this.truck.setCombustiveis(copiaCombustiveis);
            }
        }
    }

    // METODO PARA CALCULAR A DISTANCIA EUCLIDIANA E ATUALIZAR O COMBUSTIVEL GASTO
    // DO CAMINHÃO
    public void calcularDistancias(float x1, float y1, float x2, float y2) {
        float difX = x2 - x1;
        float difY = y2 - y1;
        difX = Math.abs(difX);
        difY = Math.abs(difY);
        float litros;

        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        litros = (float) (distancia / (10 - 0.5 * this.truck.getCargaAtual().size()));
        this.truck.setCombustivelGastoAtual(this.truck.getCombustivelGastoAtual() + litros);
        this.truck.getCombustiveis().add(this.truck.getCombustivelGastoAtual());
    }

    // METODO INICIAL DO BRANCH AND BOUND
    public BestWay beginFunction(List<Integer> lojasToPass, List<List<Integer>> matrizPrincipal, BestWay bestWay) {
        // Podando os nós de todas as lojas que inicialmente não podem ser visitadas
        List<Integer> lojasCantGo = getStoresWithoutPermissionToGo(matrizPrincipal);
        List<Integer> beginLojas = new ArrayList<>();
        for (Integer loja : lojasToPass) {
            if (!lojasCantGo.contains(loja)) {
                if (canIGoToThisStore(matrizPrincipal, loja))
                    beginLojas.add(loja);
            }
        }

        for (Integer loja : beginLojas) {
            this.truck.setCargaAtual(new ArrayList<>());
            this.truck.setCombustivelGastoAtual(0.0f);
            this.truck.setCargas(new ArrayList<>());
            this.truck.setCombustiveis(new ArrayList<>());
            generateCombinations(lojasToPass, loja, bestWay);
        }

        bestWay.getCaminho().add(0, 0);
        bestWay.getCaminho().add(0);

        System.out.println("Melhor caminho = " + bestWay.getCaminho());
        System.out.println("Combustivel gasto = " + bestWay.getCombustivelGasto());
        System.out.println("Cargas = " + bestWay.getCargas());
        System.out.println("Combustiveis = " + bestWay.getCombustiveis());

        return bestWay;

    }

    public void executeAlgorithm() {
        List<List<Integer>> mainMatrix = this.readFile(
                "F:\\VSCODE\\dantas-certo\\app\\src\\main\\java\\paa\\trabalho2\\Implementation\\lojas.txt");
        List<Integer> mandatoryStores = this.mandatoryStores(mainMatrix);
        BestWay best = new BestWay();

        long startTime = System.currentTimeMillis();

        BestWay bestWay = this.beginFunction(mandatoryStores, mainMatrix, best);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        this.setExecutionTimeValue(totalTime);

        XYDataset dataset = createDataset(mainMatrix);
        this.graph.getXYPlot().setDataset(0, dataset);

        drawBestWay(bestWay, mainMatrix);

        System.out.println("\nTempo total de execucao: " + totalTime + " ms");

    }

}
