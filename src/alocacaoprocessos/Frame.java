package alocacaoprocessos;

public class Frame {
    int posFim, posInicio, tamFisico, tamanho;
    boolean ocupado;
    Frame(int posInicio, int posFim, boolean ocupado, int tamanho){
        this.posInicio = posInicio;
        this.posFim = posFim;
        this.tamFisico = posFim - posInicio;
        this.tamanho=tamanho;
        this.ocupado=ocupado;
    }
}
