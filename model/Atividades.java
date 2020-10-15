package br.com.aaemobile.aaemobile.model;

public class Atividades {

    private String itenID;
    private  String itenchecklist;
    private String itenDescricao;


    public Atividades() {


    }

    public Atividades(String itenID, String itenchecklist, String itenDescricao) {
        this.itenID = itenID;
        this.itenchecklist = itenchecklist;
        this.itenDescricao = itenDescricao;
    }

    public String getItenID() {
        return itenID;
    }

    public void setItenID(String itenID) {
        this.itenID = itenID;
    }

    public String getItenchecklist() {
        return itenchecklist;
    }

    public void setItenchecklist(String itenchecklist) {
        this.itenchecklist = itenchecklist;
    }

    public String getItenDescricao() {
        return itenDescricao;
    }

    public void setItenDescricao(String itenDescricao) {
        this.itenDescricao = itenDescricao;
    }
}
