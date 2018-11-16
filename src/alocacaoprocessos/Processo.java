
package alocacaoprocessos;

import javafx.scene.layout.StackPane;

public class Processo {
    int id, tamanho, tempoCriacao, tempoAloc, tempoConc, tempoDuracao, posInicio=0, posFim=0, tamFisico;
    float porc=0;
    String status;
    Frame frame;
    StackPane desenho;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public int getTempoCriacao() {
        return tempoCriacao;
    }

    public void setTempoCriacao(int tempoCriacao) {
        this.tempoCriacao = tempoCriacao;
    }

    public int getTempoAloc() {
        return tempoAloc;
    }

    public void setTempoAloc(int tempoAloc) {
        this.tempoAloc = tempoAloc;
    }

    public int getTempoConc() {
        return tempoConc;
    }

    public void setTempoConc(int tempoConc) {
        this.tempoConc = tempoConc;
    }

    public int getTempoDuracao() {
        return tempoDuracao;
    }

    public void setTempoDuracao(int tempoDuracao) {
        this.tempoDuracao = tempoDuracao;
    }

    public float getPorc() {
        return porc;
    }

    public void setPorc(float porc) {
        this.porc = porc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    Processo(int id, int tamanho, int tempoCriacao, int tempoDuracao, String status, float porc, int tamFisico){
        this.id = id;
        this.tamanho = tamanho;
        this.tempoCriacao = tempoCriacao;
        this.tempoDuracao = tempoDuracao;
        this.status = status;
        this.porc = porc;
        this.tamFisico = tamFisico;
    }
    
    public void setPosFim(int x){
        this.posFim = x;
    }
    
    public void setPosInicio(int x){
        this.posInicio = x;
    }
}
