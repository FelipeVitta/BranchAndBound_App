package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForcaBruta {

    // METODO PARA LER O "lojas.txt"
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

    // METODO PARA GERAR AS COMBINAÇÕES POSSÍVEIS COMEÇANDO A PARTIR DE UMA LOJA
    // (PRINCIPAL)
    public void generateCombinations(List<Integer> valores, int numeroInicial, List<List<Integer>> matrizCompleta,
            Caminhao caminhao, BestWay bestWay) {
        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i) == numeroInicial) {
                List<Integer> caminho = new ArrayList<>();
                caminho.add(valores.get(i));
                gerarCaminhos(valores, numeroInicial, i, caminho, matrizCompleta, caminhao, bestWay);
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
            List<List<Integer>> matrizCompleta, Caminhao caminhao, BestWay bestWay) {
        if (caminho.size() == valores.size()) {
            // fazendo copia da matriz Original
            List<List<Integer>> copiaMatrizCompleta = fazerCopiaMatriz(matrizCompleta);
            tryCombinations(copiaMatrizCompleta, new ArrayList<>(caminho), caminhao, bestWay);
            // combinacoes.add(new ArrayList<>(caminho)); -> COLOCAR CODIGO PRA TESTAR TODOS
            // OS CAMINHOS
            return;
        }

        for (int i = 0; i < valores.size(); i++) {
            if (i != indice && !caminho.contains(valores.get(i))) {
                caminho.add(valores.get(i));
                gerarCaminhos(valores, numeroInicial, i, caminho, matrizCompleta, caminhao, bestWay);
                caminho.remove(caminho.size() - 1);
            }
        }
    }

    // METODO DE FORÇA BRUTA PARA TESTAR TODAS AS COMBINAÇÕES
    public void tryCombinations(List<List<Integer>> copiaMatrizCompleta, List<Integer> combinacao,
            Caminhao caminhao, BestWay bestWay) {
        List<List<Integer>> copia = fazerCopiaMatriz(copiaMatrizCompleta);
        for (Integer loja : combinacao) {
            if (canIGoToThisLoja(copiaMatrizCompleta, loja, caminhao)) {
                copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, loja, caminhao);
            } else {
                caminhao.setCombustivelGastoAtual(0.0f);
                caminhao.setCargaAtual(new ArrayList<>());
                return;
            }
        }
        caminhao.setCargaAtual(new ArrayList<>());
        caminhao.setCombustivelGastoAtual(0.0f);
        // System.out.println("\n" + combinacao + "\n");
        calculaGasosa(combinacao, copia, caminhao, bestWay);
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
            copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, loja, caminhao);
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
        // System.out.println("Combustivel Gasto = " +
        // caminhao.getCombustivelGastoAtual());
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
        // System.out.println(x1 + " " + y1 + " para " + x2 + " " + y2);
        // System.out.println("Carga Atual do caminhão = " +
        // caminhao.getCargaAtual().size());
        litros = (float) (distancia / (10 - 0.5 * caminhao.getCargaAtual().size()));
        caminhao.setCombustivelGastoAtual(caminhao.getCombustivelGastoAtual() + litros);
    }

    public static void main(String[] args) {
        ForcaBruta paa = new ForcaBruta();
        Caminhao caminhao = new Caminhao(Integer.parseInt(args[1]));
        BestWay best = new BestWay();
        // Lendo a matriz principal
        List<List<Integer>> matrizPrincipal = paa.readFile(args[0]);
        // Identificando as lojas que precisam ser acessadas
        List<Integer> lojasNeedToPass = paa.getLojasNeedToPass(matrizPrincipal);

        long inicio = System.currentTimeMillis();

        for (Integer loja : lojasNeedToPass) {
            caminhao.setCargaAtual(new ArrayList<>());
            paa.generateCombinations(lojasNeedToPass, loja, matrizPrincipal, caminhao, best);
        }

        best.getCaminho().add(0, 0);
        best.getCaminho().add(0);
        System.out.println(best);
        System.out.println("Cargas = " + best.getCargas());
        System.out.println("Combustiveis = " + best.getCombustiveis());

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
