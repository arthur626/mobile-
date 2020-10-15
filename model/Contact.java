package br.com.aaemobile.aaemobile.model;

public class Contact {


    private String id;
    private String codigolocal;
    private String centrocusto_id;
    private String descricaolocal;
    private String bairro;
    private String cidade;
    private String latitude;
    private String longitude;
    private String sigla;
    private String estado;
    private String tempogasto;
    private String regiaoID;
    private String regiaoDescricao;
    private String poloatendimentosID;
    private String poloatenidmentosDescricao;
    private String contatoID;
    private String contatoCLID;
    private String contatoNome;
    private String contatoEndereco;
    private String contatoLatitude;
    private String contatoLongitude;
    private String areaconstruida;
    private String areacapina;
    private String enderecolocal;
    private String frequencia;
    private String raio;
    private String situacao;

    public Contact(){

    }

    public Contact(String id, String codigolocal, String centrocusto_id, String descricaolocal, String bairro, String cidade, String latitude, String longitude, String sigla, String estado, String tempogasto, String regiaoID, String regiaoDescricao, String poloatendimentosID, String poloatenidmentosDescricao, String contatoID, String contatoCLID, String contatoNome, String contatoEndereco, String contatoLatitude, String contatoLongitude, String areaconstruida, String areacapina, String enderecolocal, String frequencia, String raio, String situacao) {
        this.id = id;
        this.codigolocal = codigolocal;
        this.centrocusto_id = centrocusto_id;
        this.descricaolocal = descricaolocal;
        this.bairro = bairro;
        this.cidade = cidade;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sigla = sigla;
        this.estado = estado;
        this.tempogasto = tempogasto;
        this.regiaoID = regiaoID;
        this.regiaoDescricao = regiaoDescricao;
        this.poloatendimentosID = poloatendimentosID;
        this.poloatenidmentosDescricao = poloatenidmentosDescricao;
        this.contatoID = contatoID;
        this.contatoCLID = contatoCLID;
        this.contatoNome = contatoNome;
        this.contatoEndereco = contatoEndereco;
        this.contatoLatitude = contatoLatitude;
        this.contatoLongitude = contatoLongitude;
        this.areaconstruida = areaconstruida;
        this.areacapina = areacapina;
        this.enderecolocal = enderecolocal;
        this.frequencia = frequencia;
        this.raio = raio;
        this.situacao = situacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigolocal() {
        return codigolocal;
    }

    public void setCodigolocal(String codigolocal) {
        this.codigolocal = codigolocal;
    }

    public String getCentrocusto_id() {
        return centrocusto_id;
    }

    public void setCentrocusto_id(String centrocusto_id) {
        this.centrocusto_id = centrocusto_id;
    }

    public String getDescricaolocal() {
        return descricaolocal;
    }

    public void setDescricaolocal(String descricaolocal) {
        this.descricaolocal = descricaolocal;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTempogasto() {
        return tempogasto;
    }

    public void setTempogasto(String tempogasto) {
        this.tempogasto = tempogasto;
    }

    public String getRegiaoID() {
        return regiaoID;
    }

    public void setRegiaoID(String regiaoID) {
        this.regiaoID = regiaoID;
    }

    public String getRegiaoDescricao() {
        return regiaoDescricao;
    }

    public void setRegiaoDescricao(String regiaoDescricao) {
        this.regiaoDescricao = regiaoDescricao;
    }

    public String getPoloatendimentosID() {
        return poloatendimentosID;
    }

    public void setPoloatendimentosID(String poloatendimentosID) {
        this.poloatendimentosID = poloatendimentosID;
    }

    public String getPoloatenidmentosDescricao() {
        return poloatenidmentosDescricao;
    }

    public void setPoloatenidmentosDescricao(String poloatenidmentosDescricao) {
        this.poloatenidmentosDescricao = poloatenidmentosDescricao;
    }

    public String getContatoID() {
        return contatoID;
    }

    public void setContatoID(String contatoID) {
        this.contatoID = contatoID;
    }

    public String getContatoCLID() {
        return contatoCLID;
    }

    public void setContatoCLID(String contatoCLID) {
        this.contatoCLID = contatoCLID;
    }

    public String getContatoNome() {
        return contatoNome;
    }

    public void setContatoNome(String contatoNome) {
        this.contatoNome = contatoNome;
    }

    public String getContatoEndereco() {
        return contatoEndereco;
    }

    public void setContatoEndereco(String contatoEndereco) {
        this.contatoEndereco = contatoEndereco;
    }

    public String getContatoLatitude() {
        return contatoLatitude;
    }

    public void setContatoLatitude(String contatoLatitude) {
        this.contatoLatitude = contatoLatitude;
    }

    public String getContatoLongitude() {
        return contatoLongitude;
    }

    public void setContatoLongitude(String contatoLongitude) {
        this.contatoLongitude = contatoLongitude;
    }

    public String getAreaconstruida() {
        return areaconstruida;
    }

    public void setAreaconstruida(String areaconstruida) {
        this.areaconstruida = areaconstruida;
    }

    public String getAreacapina() {
        return areacapina;
    }

    public void setAreacapina(String areacapina) {
        this.areacapina = areacapina;
    }

    public String getEnderecolocal() {
        return enderecolocal;
    }

    public void setEnderecolocal(String enderecolocal) {
        this.enderecolocal = enderecolocal;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public String getRaio() {
        return raio;
    }

    public void setRaio(String raio) {
        this.raio = raio;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
