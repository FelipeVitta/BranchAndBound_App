package paa.trabalho2.Implementation;


import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jdesktop.swingx.JXGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import paa.trabalho2.Shared.BestWay;
import paa.trabalho2.Shared.Caminhao;

import javax.swing.*;

public class ForcaBruta {

    private Caminhao truck;
    private JPanel jPanel;
    private JFreeChart graph;
    private JLabel currentPayloadLabel;
    private JLabel currentGasConsumeLabel;
    private JLabel currentPayloadValue;
    private JLabel currentGasConsumeValue;
    private ChartPanel chartPanel;

    public ForcaBruta(Caminhao truck) {
        this.truck = truck;
        jPanel = createGUI();
        graph = createGraph();
        currentPayloadLabel = new JLabel("Carga atual: ");
        currentPayloadValue = new JLabel();

        currentGasConsumeLabel = new JLabel("Consumo de gasolina atual: ");
        currentGasConsumeValue = new JLabel();

        chartPanel = new ChartPanel(graph);
        chartPanel.setSize(900,900);
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(chartPanel);
        jPanel.add(currentPayloadLabel);
        jPanel.add(currentPayloadValue);
        jPanel.add(currentGasConsumeLabel);
        jPanel.add(currentGasConsumeValue);

    }

    public Caminhao getCaminhao() {
        return truck;
    }

    public void setCaminhao(Caminhao truck) {
        this.truck = truck;
    }

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
    public boolean canTheTruckCarryMoreItems(List<List<Integer>> matriz, Integer loja, Caminhao truck) {
        List<Integer> novasCargas = getDestinos(matriz.get(loja));
        int tam = truck.getCargaAtual().size();
        if (truck.getCargaAtual().contains(loja)) {
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
    public boolean canIGoToThisLoja(List<List<Integer>> matriz, Integer loja, Caminhao truck) {
        return !getLojasWithoutPermissionToGo(matriz).contains(loja)
                && canTheTruckCarryMoreItems(matriz, loja, truck);
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
    public List<Integer> mandatoryStorers(List<List<Integer>> matrizCompleta) {
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
        for (Integer loja : combinacao) {
            if (canIGoToThisLoja(copiaMatrizCompleta, loja, truck)) {
                copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, loja, truck);
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
        int i = 0;
        float[] currentCoordinates = { copiaMatrizCompleta.get(0).get(1), copiaMatrizCompleta.get(0).get(2) };
        float[] coordinatesToGo = { 0, 0 };
        for (Integer loja : combinacao) {

            coordinatesToGo[0] = copiaMatrizCompleta.get(loja).get(1);
            coordinatesToGo[1] = copiaMatrizCompleta.get(loja).get(2);
            calcularDistancias(currentCoordinates[0], currentCoordinates[1], coordinatesToGo[0], coordinatesToGo[1],
                    truck);
            copiaMatrizCompleta = changeMatriz(copiaMatrizCompleta, loja, truck);
            currentCoordinates[0] = coordinatesToGo[0];
            currentCoordinates[1] = coordinatesToGo[1];

        }
        // Calculando a volta para a origem
        calcularDistancias(currentCoordinates[0], currentCoordinates[1], copiaMatrizCompleta.get(0).get(1),
                copiaMatrizCompleta.get(i).get(2), truck);

        if (truck.getCombustivelGastoAtual() < way.getCombustivelGasto()) {
            way.setCombustivelGasto(truck.getCombustivelGastoAtual());
            way.setCaminho(combinacao);
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

    private JPanel createGUI(){
        JFrame frame = new JFrame("Exemplo de Interface Gráfica");

        // Cria um painel JPanel
        JPanel panel = new JPanel();

        // Adiciona o painel à janela JFrame
        frame.getContentPane().add(panel);

        // Configura o tamanho da janela
        frame.setSize(1200, 1000);

        // Define a ação padrão ao fechar a janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Exibe a janela
        frame.setVisible(true);

        return panel;
    }

    private JFreeChart createGraph(){
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Posicao das Lojas",
                "Eixo X",
                "Eixo Y",
                null,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        return chart;
    }

    private XYDataset createDataset(List<List<Integer>> storesInfo) {
        // Cria uma série de pontos
        XYSeries series = new XYSeries("Lojas");

        for (List<Integer> storeInfo : storesInfo){
            String label = "Loja " + storeInfo.get(0);
            series.add(storeInfo.get(1), storeInfo.get(2));

            XYTextAnnotation textAnnotation = new XYTextAnnotation(label, storeInfo.get(1),storeInfo.get(2));
            this.graph.getXYPlot().addAnnotation(textAnnotation);
        }

        // Cria um conjunto de dados XY e adiciona a série
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);



        return dataset;
    }

    private void drawBestWay(BestWay bestWay, List<List<Integer>> mainMatrix){

        List<Integer> bestWayIndex = bestWay.getCaminho();
        List<XYLineAnnotation> lineAnnotations = new ArrayList<>();

        for(int currentIndex = 0; currentIndex < bestWayIndex.size() - 1; currentIndex++){

            int startX = mainMatrix.get(bestWayIndex.get(currentIndex)).get(1);
            int startY = mainMatrix.get(bestWayIndex.get(currentIndex)).get(2);

            int endX = mainMatrix.get(bestWayIndex.get(currentIndex + 1)).get(1);
            int endY = mainMatrix.get(bestWayIndex.get(currentIndex + 1)).get(2);

            XYLineAnnotation lineAnnotation = new XYLineAnnotation(startX, startY, endX, endY,
                    new BasicStroke(1.5f), Color.BLACK);

            lineAnnotations.add(lineAnnotation);

            // Nessa parte aqui eu preciso pegar o valor atual do combustivel e o que o caminhão está levando
            this.currentPayloadValue.setText(Integer.toString(currentIndex));
            this.currentGasConsumeValue.setText("Troquei para o id" + currentIndex);
            this.graph.getXYPlot().addAnnotation(lineAnnotation);
            this.graph.fireChartChanged();
          try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void executeAlgorithm(){
        List<List<Integer>> mainMatrix = this.readFile("E:\\GitHub Projects\\paa-trabalho2\\app\\src\\main\\java\\paa\\trabalho2\\Implementation\\lojas.txt");
        List<Integer> mandatoryStores = this.mandatoryStorers(mainMatrix);
        BestWay bestWay = new BestWay();

        long startTime = System.currentTimeMillis();

        for (Integer store : mandatoryStores) {
            truck.setCargaAtual(new ArrayList<>());
            this.generateCombinations(mandatoryStores, store, mainMatrix, truck, bestWay);
        }

        XYDataset dataset = createDataset(mainMatrix);
        this.graph.getXYPlot().setDataset(0, dataset);

        bestWay.getCaminho().add(0, 0);
        bestWay.getCaminho().add(0);
        System.out.println(bestWay);

        drawBestWay(bestWay, mainMatrix);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("\nTempo total de execucao: " + totalTime + " ms");
    }
}

