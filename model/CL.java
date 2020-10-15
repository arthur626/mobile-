package br.com.aaemobile.aaemobile.model;

public class CL {
    private String idCL;
    private String centrocusto;
    private String descricao;

    public CL(String idCL, String centrocusto, String descricao) {
        this.idCL = idCL;
        this.centrocusto = centrocusto;
        this.descricao = descricao;
    }
    public CL(){

    }

    public String getIdCL() {
        return idCL;
    }

    public void setIdCL(String idCL) {
        this.idCL = idCL;
    }

    public String getCentrocusto() {
        return centrocusto;
    }

    public void setCentrocusto(String centrocusto) {
        this.centrocusto = centrocusto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
