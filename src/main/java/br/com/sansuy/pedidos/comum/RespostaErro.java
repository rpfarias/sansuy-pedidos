package br.com.sansuy.pedidos.comum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Corpo JSON padronizado de erro devolvido pela API.
 * Formato: { timestamp, status, erro, mensagem, caminho, campos? }.
 */
public class RespostaErro {

    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String caminho;
    /** Preenchido apenas em erros de validacao de campos (400). */
    private List<CampoInvalido> campos;

    public RespostaErro() {
    }

    public RespostaErro(int status, String erro, String mensagem, String caminho) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
    }

    /** Par campo→motivo, usado em erros de Bean Validation. */
    public static class CampoInvalido {
        private String campo;
        private String motivo;

        public CampoInvalido() {
        }

        public CampoInvalido(String campo, String motivo) {
            this.campo = campo;
            this.motivo = motivo;
        }

        public String getCampo() {
            return campo;
        }

        public void setCampo(String campo) {
            this.campo = campo;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<CampoInvalido> getCampos() {
        return campos;
    }

    public void setCampos(List<CampoInvalido> campos) {
        this.campos = campos;
    }
}
