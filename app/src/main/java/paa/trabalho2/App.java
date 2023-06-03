package paa.trabalho2;

import javax.swing.*;

import org.jdesktop.swingx.JXGraph;
import paa.trabalho2.Implementation.ForcaBruta;
import paa.trabalho2.Shared.Caminhao;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

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
        JButton button = new JButton("OK");

        // Cria um CountDownLatch para sincronizar a execução
        CountDownLatch latch = new CountDownLatch(1);

        // Adiciona um ActionListener ao botão
        button.addActionListener(e -> {
            // Obtém o valor do campo de texto
            String input = textField.getText();
            caminhao.setCargaPossivel(Integer.parseInt(input));

            // Libera a execução
            latch.countDown();
        });

        panel.add(label);
        panel.add(textField);
        panel.add(button);

        // Adiciona o painel à janela JFrame
        frame.getContentPane().add(panel);

        // Configura o tamanho da janela
        frame.setSize(1000, 1000);

        // Define a ação padrão ao fechar a janela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Exibe a janela
        frame.setVisible(true);

        try {
            // Espera até que o usuário insira um valor e pressione o botão "OK"
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        ForcaBruta forcaBruta = new ForcaBruta(caminhao);
        forcaBruta.executeAlgorithm();
    }
}
