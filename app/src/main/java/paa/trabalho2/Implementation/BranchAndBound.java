package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BranchAndBound {

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
                for (Integer lol : destinos) {
                    semPermissao.add(lol);
                }
            }
        }
        return semPermissao;
    }

    // METODO PARA DETERMINAR SE O CAMINHÃO PODE CARREGAR OS ITENS DA LOJA
    // ESPECIFICADA
    public boolean canTheTruckCarryMoreItems(List<List<Integer>> matriz, Integer loja, Caminhao caminhao) {
        List<Integer> novasCargas = getDestinos(matriz.get(loja));
        int tam = caminhao.getCargaAtual().size();
        if (caminhao.getCargaAtual().contains(loja)) {
            tam = tam - 1;
        }
        if (novasCargas != null) {
            tam = tam + novasCargas.size();
        }
        if (caminhao.getCargaPossivel() - tam >= 0) {
            return true;
        } else {
            return false;
        }
    }

    // METODO PARA RETORNAR SE O CAMINHAO PODE IR PARA A LOJA ESPECIFICADA
    public boolean canIGoToThisLoja(List<List<Integer>> matriz, Integer loja, Caminhao caminhao) {
        return !getLojasWithoutPermissionToGo(matriz).contains(loja)
                && canTheTruckCarryMoreItems(matriz, loja, caminhao);
    }

    // METODO PARA MUDAR A MATRIZ PARA SABER QUAIS LOJAS AGORA PODEM SER ACESSADAS E
    // PARA ENCHER A CARGA DO CAMINHÃO COM OS NOVOS PRODUTOS
    public List<List<Integer>> changeMatriz(List<List<Integer>> matriz, Integer passei, Caminhao caminhao) {
        int tamMatriz = matriz.get(passei).size();
        if (caminhao.getCargaAtual().contains(passei)) {
            caminhao.getCargaAtual().remove(passei);
        }
        if (tamMatriz > 3) {
            while (matriz.get(passei).size() > 3) {
                caminhao.getCargaAtual().add(matriz.get(passei).get(3));
                matriz.get(passei).remove(3);
            }
            return matriz;
        } else {
            return matriz;
        }
    }

    // METODO PARA RETORNAR AS LOJAS QUE O CAMINHAO PRECISA PASSAR
    public List<Integer> getLojasNeedToPass(List<List<Integer>> matrizCompleta) {
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

    // METODO PARA GERAR AS COMBINAÇÕES POSSÍVEIS COMEÇANDO A PARTIR DE UMA LOJA
    // (PRINCIPAL)
    public void generateCombinations(List<Integer> valores, int numeroInicial, List<List<Integer>> matrizCompleta,
            Caminhao caminhao, BestWay bestWay) {
        matrizCompleta = getMatrizCompleta();
        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i) == numeroInicial) {
                List<Integer> caminho = new ArrayList<>();
                caminho.add(valores.get(i));
                changeMatriz(matrizCompleta, valores.get(i), caminhao);
                gerarCaminhos(valores, i, caminho, matrizCompleta, caminhao, bestWay);
            }
        }

    }

    // METODO PRINCIPAL DE BRANCH AND BOUND RESPONSAVEL PELAS PODAS
    public void gerarCaminhos(List<Integer> valores, int indice, List<Integer> caminho,
            List<List<Integer>> matrizCompleta, Caminhao caminhao, BestWay bestWay) {

        if (caminho.size() == valores.size()) {
            List<List<Integer>> copiaMatrizCompleta = getMatrizCompleta();
            // Chama o método para calcular a quantidade gasta de cada combinação
            // conveniente e ver se é melhor que a melhor atual
            calculaGasosa(caminho, copiaMatrizCompleta, caminhao, bestWay);
            return;
        }

        for (int i = 0; i < valores.size(); i++) {
            if (i != indice && !caminho.contains(valores.get(i))) {
                // CONDIÇÃO DE PODA (VIOLAÇÃO DE RESTRIÇÃO)
                if (!canIGoToThisLoja(matrizCompleta, valores.get(i), caminhao)) {
                    continue;
                }
                // fazendo copia da matriz antes da modificação
                List<List<Integer>> copia = fazerCopiaMatriz(matrizCompleta);
                // fazendo copia da carga atual do caminhão antes da modificação
                List<Integer> copiacam = new ArrayList<>(caminhao.getCargaAtual());
                // mudando o estado da matriz e atualizando os valores novos do caminhão
                matrizCompleta = changeMatriz(matrizCompleta, valores.get(i), caminhao);
                caminho.add(valores.get(i));
                // chamando recursivamente a função
                gerarCaminhos(valores, i, caminho, matrizCompleta, caminhao, bestWay);
                // removendo os efeitos da adição da ultima loja no caminho
                caminho.remove(caminho.size() - 1);
                matrizCompleta = copia;
                caminhao.setCargaAtual(new ArrayList<>(copiacam));
            }
        }
    }

    // METODO PARA CALCULAR A QUANTIDADE DE GASOLINA GASTA EM CADA TRAJETO VALIDO
    public void calculaGasosa(List<Integer> combinacao, List<List<Integer>> copiaMatrizCompleta, Caminhao caminhao,
            BestWay way) {
        List<Float> combustivel = new ArrayList<>(Arrays.asList(0.0f));
        List<List<Integer>> cargas = new ArrayList<>();
        cargas.add(new ArrayList<>());
        int i = 0;
        float[] currentCoordinates = { copiaMatrizCompleta.get(0).get(1), copiaMatrizCompleta.get(0).get(2) };
        float[] coordinatesToGo = { 0, 0 };
        for (Integer loja : combinacao) {
            coordinatesToGo[0] = copiaMatrizCompleta.get(loja).get(1);
            coordinatesToGo[1] = copiaMatrizCompleta.get(loja).get(2);
            calcularDistancias(currentCoordinates[0], currentCoordinates[1], coordinatesToGo[0], coordinatesToGo[1],
                    caminhao);
            copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, loja, caminhao); // atualizando a carga do caminhao
            combustivel.add(caminhao.getCombustivelGastoAtual());
            cargas.add(new ArrayList<>(caminhao.getCargaAtual()));
            currentCoordinates[0] = coordinatesToGo[0];
            currentCoordinates[1] = coordinatesToGo[1];
        }
        // Calculando a volta para a origem
        calcularDistancias(currentCoordinates[0], currentCoordinates[1], copiaMatrizCompleta.get(0).get(1),
                copiaMatrizCompleta.get(i).get(2), caminhao);

        combustivel.add(caminhao.getCombustivelGastoAtual());
        cargas.add(new ArrayList<>(caminhao.getCargaAtual()));

        if (caminhao.getCombustivelGastoAtual() < way.getCombustivelGasto()) {
            way.setCombustivelGasto(caminhao.getCombustivelGastoAtual());
            way.setCaminho(new ArrayList<>(combinacao));
            way.setCargas(cargas);
            way.setCombustiveis(combustivel);
        }
        caminhao.setCargaAtual(new ArrayList<>());
        caminhao.setCombustivelGastoAtual(0.0f);
    }

    // METODO PARA CALCULAR A DISTANCIA EUCLIDIANA
    public void calcularDistancias(float x1, float y1, float x2, float y2, Caminhao caminhao) {
        float difX = x2 - x1;
        float difY = y2 - y1;
        difX = Math.abs(difX);
        difY = Math.abs(difY);
        float litros = 0.0f;

        float distancia = (float) Math.sqrt(difX * difX + difY * difY);
        litros = (float) (distancia / (10 - 0.5 * caminhao.getCargaAtual().size()));
        caminhao.setCombustivelGastoAtual(caminhao.getCombustivelGastoAtual() + litros);
    }

    // METODO INICIAL DO BRANCH AND BOUND
    public void branchAndBound(List<Integer> lojasToPass, List<List<Integer>> matrizPrincipal, Caminhao caminhao,
            BestWay bestWay) {
        // podando os nós de todas as lojas que inicialmente não podem ser visitadas
        List<Integer> lojasCantGo = getLojasWithoutPermissionToGo(matrizPrincipal);
        List<Integer> beginLojas = new ArrayList<>();
        for (Integer loja : lojasToPass) {
            if (!lojasCantGo.contains(loja)) {
                if (canIGoToThisLoja(matrizPrincipal, loja, caminhao))
                    beginLojas.add(loja);
            }
        }
        for (Integer loja : beginLojas) {
            matrizPrincipal = getMatrizCompleta();
            caminhao.setCargaAtual(new ArrayList<>());
            generateCombinations(lojasToPass, loja, matrizPrincipal, caminhao, bestWay);
        }

        bestWay.getCaminho().add(0, 0);
        bestWay.getCaminho().add(0);
        System.out.println("Melhor caminho = " + bestWay.getCaminho());
        System.out.println("Combustivel gasto = " + bestWay.getCombustivelGasto());
        System.out.println("Cargas = " + bestWay.getCargas());
        System.out.println("Combustiveis = " + bestWay.getCombustiveis());

    }

    public static void main(String[] args) {
        BranchAndBound paa = new BranchAndBound();
        Caminhao caminhao = new Caminhao(Integer.parseInt(args[1]));
        BestWay best = new BestWay();
        // Lendo a matriz principal
        List<List<Integer>> matrizPrincipal = paa.readFile(args[0]);
        // Identificando as lojas que precisam ser acessadas
        List<Integer> lojasNeedToPass = paa.getLojasNeedToPass(matrizPrincipal);

        long inicio = System.currentTimeMillis();

        paa.branchAndBound(lojasNeedToPass, matrizPrincipal, caminhao, best);

        long fim = System.currentTimeMillis();
        long tempoTotal = fim - inicio;

        System.out.println("\nTempo total de execucao: " + tempoTotal + " ms");

    }

}

class Caminhao {

    private int cargaPossivel;
    private List<Integer> cargaAtual = new ArrayList<>();
    private float combustivelGastoAtual;

    public Caminhao(int cargaPossivel) {
        this.cargaPossivel = cargaPossivel;
        this.combustivelGastoAtual = 0.0f;
    }

    public Caminhao(List<Integer> cargaAtual, float combustivelGastoAtual) {
        this.cargaAtual = cargaAtual;
        this.combustivelGastoAtual = combustivelGastoAtual;
    }

    public List<Integer> getCargaAtual() {
        return cargaAtual;
    }

    public void setCargaAtual(List<Integer> cargaAtual) {
        this.cargaAtual = cargaAtual;
    }

    public float getCombustivelGastoAtual() {
        return combustivelGastoAtual;
    }

    public void setCombustivelGastoAtual(float combustivelGastoAtual) {
        this.combustivelGastoAtual = combustivelGastoAtual;
    }

    public int getCargaPossivel() {
        return cargaPossivel;
    }

    public void setCargaPossivel(int cargaPossivel) {
        this.cargaPossivel = cargaPossivel;
    }

    @Override
    public String toString() {
        return "Caminhao [cargaAtual=" + cargaAtual + "]";
    }

}

class BestWay {

    private List<Integer> caminho;
    private float combustivelGasto;
    private List<List<Integer>> cargas;
    private List<Float> combustiveis;

    public BestWay(List<Integer> caminho, float combustivelGasto) {
        this.caminho = caminho;
        this.combustivelGasto = combustivelGasto;
    }

    public BestWay() {
        this.combustivelGasto = 100000.0f;
        this.caminho = new ArrayList<>(0);
        this.cargas = new ArrayList<>();
        this.combustiveis = new ArrayList<>();
    }

    public List<Float> getCombustiveis() {
        return combustiveis;
    }

    public void setCombustiveis(List<Float> combustiveis) {
        this.combustiveis = combustiveis;
    }

    public List<Integer> getCaminho() {
        return caminho;
    }

    public void setCaminho(List<Integer> caminho) {
        this.caminho = caminho;
    }

    public float getCombustivelGasto() {
        return combustivelGasto;
    }

    public void setCombustivelGasto(float combustivelGasto) {
        this.combustivelGasto = combustivelGasto;
    }

    @Override
    public String toString() {
        return "Melhor caminho = " + caminho + "\nCombustivel Gasto = " + combustivelGasto;
    }

    public List<List<Integer>> getCargas() {
        return cargas;
    }

    public void setCargas(List<List<Integer>> cargas) {
        this.cargas = cargas;
    }

}
