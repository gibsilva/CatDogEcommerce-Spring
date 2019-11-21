package entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdutoSelecionado {
	private Produto item;

	private int quantidade;

	private BigDecimal precoFinal;

	private LocalDateTime dataHoraInclusao;
	
	public ProdutoSelecionado() {

	}

	public ProdutoSelecionado(Produto produto, int quantidade) {
		this.item = produto;
		this.quantidade = quantidade;
		this.dataHoraInclusao = LocalDateTime.now();
	}

	public Produto getItem() {
		return item;
	}

	public void setItem(Produto item) {
		this.item = item;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getPrecoFinal() {
		return precoFinal;
	}

	public void setPrecoFinal(BigDecimal precoFinal) {
		this.precoFinal = precoFinal;
	}

	public LocalDateTime getDataHoraInclusao() {
		return dataHoraInclusao;
	}

	public void setDataHoraInclusao(LocalDateTime dataHoraInclusao) {
		this.dataHoraInclusao = dataHoraInclusao;
	}

	public double getSubtotal() {
		return item.getPrecoVenda() * quantidade;
	}
}
