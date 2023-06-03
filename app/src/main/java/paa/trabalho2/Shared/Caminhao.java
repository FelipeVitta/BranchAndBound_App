package paa.trabalho2.Shared;

import java.util.ArrayList;
import java.util.List;

public class Caminhao {

    private int cargaPossivel;
    private List<Integer> cargaAtual = new ArrayList<>();
    private float combustivelGastoAtual;

    public Caminhao() {
    }

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
