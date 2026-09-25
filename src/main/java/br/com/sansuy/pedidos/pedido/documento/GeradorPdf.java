package br.com.sansuy.pedidos.pedido.documento;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.sansuy.pedidos.comum.RegraNegocioException;
import br.com.sansuy.pedidos.pedido.ItemPedido;
import br.com.sansuy.pedidos.pedido.PedidoVenda;

/**
 * Gerador de documento em PDF (OpenPDF). Um resumo simples do pedido com uma
 * tabela de itens.
 */
@Component
public class GeradorPdf implements GeradorDocumento {

    @Override
    public FormatoDocumento formato() {
        return FormatoDocumento.PDF;
    }

    @Override
    public DocumentoPedido gerar(PedidoVenda pedido) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, saida);
            doc.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            doc.add(new Paragraph("Pedido de Venda nº " + pedido.getId(), titulo));
            doc.add(new Paragraph("Cliente: " + pedido.getCliente().getRazaoSocial()));
            doc.add(new Paragraph("Status: " + pedido.getStatus()));
            doc.add(new Paragraph("Data: " + pedido.getDataCriacao()));
            doc.add(new Paragraph("Valor total: R$ " + pedido.getValorTotal()));
            doc.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(5);
            tabela.setWidthPercentage(100);
            adicionarCabecalho(tabela, "Produto", "Descricao", "Metragem", "Preco Unit.", "Subtotal");
            for (ItemPedido item : pedido.getItens()) {
                tabela.addCell(item.getProduto().getCodigo());
                tabela.addCell(item.getProduto().getDescricao());
                tabela.addCell(String.valueOf(item.getMetragem()));
                tabela.addCell(String.valueOf(item.getPrecoUnitario()));
                tabela.addCell(String.valueOf(item.getSubtotal()));
            }
            doc.add(tabela);

            doc.close();
        } catch (DocumentException e) {
            throw new RegraNegocioException("Falha ao gerar PDF do pedido " + pedido.getId());
        }

        return new DocumentoPedido(
                "pedido-" + pedido.getId() + ".pdf", "application/pdf", saida.toByteArray());
    }

    private void adicionarCabecalho(PdfPTable tabela, String... titulos) {
        Font negrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        for (String t : titulos) {
            com.lowagie.text.pdf.PdfPCell celula =
                    new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(t, negrito));
            celula.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabela.addCell(celula);
        }
    }
}
