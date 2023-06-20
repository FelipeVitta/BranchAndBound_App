//Feito por:
//Felipe Leal, Guilherme Dantas e Laura Iara
package paa.trabalho2.Shared;

import org.jfree.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BestWay {

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

    public String getCargasAsStringInIndex(int index){
        return this.cargas.get(index).stream().map(Object::toString).collect(Collectors.joining(", "));
    }

}
