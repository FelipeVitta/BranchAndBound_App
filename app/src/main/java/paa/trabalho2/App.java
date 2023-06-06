package paa.trabalho2;

import paa.trabalho2.Implementation.BranchAndBound;
import paa.trabalho2.Implementation.ForcaBruta;
import paa.trabalho2.Shared.Caminhao;

import javax.swing.*;
import java.util.concurrent.Semaphore;

public class App {
    public static void main(String[] args) {
        Caminhao caminhao = new Caminhao();
        JFrame frame = new JFrame("Exemplo de Interface Gráfica");

        // Cria um painel JPanel
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Insira o valor da capacidade máxima de carga do caminhão:");

        // Cria um campo de texto JTextField
        JTextField textField = new JTextField(10);

        // Cria um botão JButton
        JButton buttonBruteForce = new JButton("Brute Force");
        JButton buttonBranchBound = new JButton("Branch and Bound");

        // Cria um CountDownLatch para sincronizar a execução
        Semaphore semaphore = new Semaphore(1);

        // Array para armazenar o botão clicado
        final String[] clickedButton = {""};

        panel.add(label);
        panel.add(textField);
        panel.add(buttonBruteForce);
        panel.add(buttonBranchBound);

        // Adiciona o painel à janela JFrame
        frame.getContentPane().add(panel);

        // Configura o tamanho da janela
        frame.setSize(1000, 1000);

        // Define a ação padrão ao fechar a janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Exibe a janela
        frame.setVisible(true);

        // ActionListener para o botão "Brute Force"
        buttonBruteForce.addActionListener(e -> {
            // Obtém o valor do campo de texto
            String input = textField.getText();
            caminhao.setCargaPossivel(Integer.parseInt(input));

            // Armazena o botão clicado
            clickedButton[0] = "Brute Force";

            // Libera a execução
            semaphore.release();
        });

        // ActionListener para o botão "Branch and Bound"
        buttonBranchBound.addActionListener(e -> {
            // Obtém o valor do campo de texto
            String input = textField.getText();
            caminhao.setCargaPossivel(Integer.parseInt(input));

            // Armazena o botão clicado
            clickedButton[0] = "Branch and Bound";

            // Libera a execução
            semaphore.release();
        });

        try {
            while (true){
                // Espera até que o usuário insira um valor e pressione o botão
                semaphore.acquire();
                // Executa o algoritmo com base no botão clicado
                if (clickedButton[0].equals("Brute Force")) {
                    ForcaBruta forcaBruta = new ForcaBruta(caminhao);
                    forcaBruta.executeAlgorithm();
                } else if (clickedButton[0].equals("Branch and Bound")) {
                    BranchAndBound branchAndBound = new BranchAndBound(caminhao);
                    branchAndBound.executeAlgorithm();
                }

                frame.invalidate();
                frame.repaint();
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
