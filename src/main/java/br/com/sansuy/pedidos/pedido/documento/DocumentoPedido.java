package br.com.sansuy.pedidos.pedido.documento;

/**
 * Documento gerado do pedido: o nome do arquivo, o content-type e os bytes.
 * É o "produto" da Factory, independente do formato concreto.
 */
public class DocumentoPedido {

    private final String nomeArquivo;
    private final String contentType;
    private final byte[] conteudo;

    public DocumentoPedido(String nomeArquivo, String contentType, byte[] conteudo) {
        this.nomeArquivo = nomeArquivo;
        this.contentType = contentType;
        this.conteudo = conteudo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getConteudo() {
        return conteudo;
    }
}
