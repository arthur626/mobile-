package br.com.aaemobile.aaemobile.model;

public class Equipamento {

    private  String id;
    private String codigoequipamento;
    private String descricaoequipamento;
    private String centrocusto_id;
    private String local_id;
    private String modelo;
    private String tag;
    private String numeroserie;
    private String btu;
    private String fabricante;
    private String tipoequipamento;
    private String fornecedor;


    public Equipamento(){

    }

    public Equipamento(String id, String codigoequipamento, String descricaoequipamento, String centrocusto_id, String local_id, String modelo, String tag, String numeroserie, String btu, String fabricante, String tipoequipamento, String fornecedor) {
        this.id = id;
        this.codigoequipamento = codigoequipamento;
        this.descricaoequipamento = descricaoequipamento;
        this.centrocusto_id = centrocusto_id;
        this.local_id = local_id;
        this.modelo = modelo;
        this.tag = tag;
        this.numeroserie = numeroserie;
        this.btu = btu;
        this.fabricante = fabricante;
        this.tipoequipamento = tipoequipamento;
        this.fornecedor = fornecedor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoequipamento() {
        return codigoequipamento;
    }

    public void setCodigoequipamento(String codigoequipamento) {
        this.codigoequipamento = codigoequipamento;
    }

    public String getDescricaoequipamento() {
        return descricaoequipamento;
    }

    public void setDescricaoequipamento(String descricaoequipamento) {
        this.descricaoequipamento = descricaoequipamento;
    }

    public String getCentrocusto_id() {
        return centrocusto_id;
    }

    public void setCentrocusto_id(String centrocusto_id) {
        this.centrocusto_id = centrocusto_id;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNumeroserie() {
        return numeroserie;
    }

    public void setNumeroserie(String numeroserie) {
        this.numeroserie = numeroserie;
    }

    public String getBtu() {
        return btu;
    }

    public void setBtu(String btu) {
        this.btu = btu;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getTipoequipamento() {
        return tipoequipamento;
    }

    public void setTipoequipamento(String tipoequipamento) {
        this.tipoequipamento = tipoequipamento;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }
}
