package paa.trabalho2.Shared;

import java.util.ArrayList;
import java.util.List;

public class BestWay {

    private List<Integer> caminho;
    private float combustivelGasto;
    private int i = 0;

    public BestWay(List<Integer> caminho, float combustivelGasto) {
        this.caminho = caminho;
        this.combustivelGasto = combustivelGasto;
    }

    public BestWay() {
        this.combustivelGasto = 100000.0f;
        this.caminho = new ArrayList<>(0);
    }

    public int getI() {
        return i;
    }

    public List<Integer> getCaminho() {
        return caminho;
    }

    public void setCaminho(List<Integer> caminho) {
        i++;
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

}
