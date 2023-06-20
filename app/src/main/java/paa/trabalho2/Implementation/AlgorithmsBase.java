//Feito por:
//Felipe Leal, Guilherme Dantas e Laura Iara
package paa.trabalho2.Implementation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import paa.trabalho2.Shared.BestWay;
import paa.trabalho2.Shared.Caminhao;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmsBase {
    protected Caminhao truck;
    protected Float lowerBound;
    protected JPanel jPanel;
    protected JFreeChart graph;
    protected JLabel currentPayloadLabel;
    protected JLabel currentGasConsumeLabel;
    protected JLabel executionTimeLabel;
    protected JLabel currentPayloadValue;
    protected JLabel currentGasConsumeValue;
    protected JLabel executionTimeValue;
    protected ChartPanel chartPanel;
    protected long executionTime;

    public AlgorithmsBase(Caminhao truck, String jFrameTitle){
        this.truck = truck;
        jPanel = createGUI(jFrameTitle);
        graph = createGraph();
        currentPayloadLabel = new JLabel("Carga atual: ");
        currentPayloadValue = new JLabel();

        currentGasConsumeLabel = new JLabel("Consumo de gasolina atual: ");
        currentGasConsumeValue = new JLabel();

        executionTimeLabel = new JLabel("Execution time: ");
        executionTimeValue = new JLabel();

        chartPanel = new ChartPanel(graph);
        chartPanel.setSize(900,900);
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(chartPanel);
        jPanel.add(currentPayloadLabel);
        jPanel.add(currentPayloadValue);
        jPanel.add(currentGasConsumeLabel);
        jPanel.add(currentGasConsumeValue);
        jPanel.add(executionTimeLabel);
        jPanel.add(executionTimeValue);
    }

    protected void setExecutionTimeValue(long executionTime){
        this.executionTimeValue.setText((Long.toString(executionTime)));
    }

    public Caminhao getTruck() {
        return truck;
    }

    public void setTruck(Caminhao truck) {
        this.truck = truck;
    }

    protected JPanel createGUI(String jFrameTitle){
        JFrame frame = new JFrame(jFrameTitle);

        // Cria um painel JPanel
        JPanel panel = new JPanel();

        // Adiciona o painel à janela JFrame
        frame.getContentPane().add(panel);

        // Configura o tamanho da janela
        frame.setSize(1200, 1000);

        // Define a ação padrão ao fechar a janela
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Exibe a janela
        frame.setVisible(true);

        return panel;
    }

    protected JFreeChart createGraph(){
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

    protected XYDataset createDataset(List<List<Integer>> storesInfo) {
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

    protected void drawBestWay(BestWay bestWay, List<List<Integer>> mainMatrix){

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
            this.currentPayloadValue.setText(bestWay.getCargasAsStringInIndex(currentIndex + 1));
            this.currentGasConsumeValue.setText(Float.toString(bestWay.getCombustiveis().get(currentIndex + 1)));
            this.graph.getXYPlot().addAnnotation(lineAnnotation);
            this.graph.fireChartChanged();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected List<List<Integer>> readFile(String path) {
        try {

            BufferedReader buff = new BufferedReader(new FileReader(path));
            String linha;
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

        } catch (IOException e) {
            e.getCause();
        }
        return null;
    }
}
