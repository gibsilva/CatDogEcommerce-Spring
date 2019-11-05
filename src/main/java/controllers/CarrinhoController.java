/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.ItensPedido;
import entidades.Pedido;
import entidades.Produto;
import entidades.ProdutoSelecionado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import repositorios.IItensPedidoRepositorio;
import repositorios.IPedidoRepositorio;
import repositorios.IProdutoRepositorio;

/**
 *
 * @author Gi
 */
@Controller
@Scope("session")
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final IProdutoRepositorio produtoRepositorio;
    private final IItensPedidoRepositorio itensPedidoRepositorio;
    private final IPedidoRepositorio pedidoRepositorio;
    
    private List<ProdutoSelecionado> produtosSelecionados = new ArrayList<>();
	private int valorFrete = 0;
	private int valorDesconto = 0;
	private String cepEntrega = "";

    @Autowired
    public CarrinhoController(IProdutoRepositorio produtoRepositorio, IItensPedidoRepositorio itensPedidoRepositorio,
    		IPedidoRepositorio pedidoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
        this.itensPedidoRepositorio = itensPedidoRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }

    @GetMapping
    public ModelAndView mostrar() {
        return new ModelAndView("carrinho");
    }

    @GetMapping("/detalhes/{id}")
    public ModelAndView detalhesProduto(@PathVariable("id") Integer id) {
        ModelAndView view = new ModelAndView("detalhes-produto");
        Produto produto = (produtoRepositorio.obterPorId(id));
        view.addObject("produto", produto);
        return view;
    }
    
	@PostMapping
	public ModelAndView adicionar(@RequestParam("itemId") int itemId, RedirectAttributes redirAttr) {
		Produto p = produtoRepositorio.obterPorId(itemId);
		produtosSelecionados.add(new ProdutoSelecionado(p, 1));
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("{listIndex}/alterar")
	public ModelAndView alterarQtd(@PathVariable("listIndex") int listIndex, @RequestParam("qtd") int quantidade, RedirectAttributes redirAttr) {
		ProdutoSelecionado sel = produtosSelecionados.get(listIndex);
		if (quantidade > 0) {
			sel.setQuantidade(quantidade);
			redirAttr.addFlashAttribute("msg", "Quantidade do item '" + sel.getItem().getNome() + "' alterada");
		} else {
			produtosSelecionados.remove(listIndex);
			redirAttr.addFlashAttribute("msg", "Item '" + sel.getItem().getNome() + "' removido");
		}
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("{listIndex}/incrementar")
	public ModelAndView incrementarQtd(@PathVariable("listIndex") int listIndex, RedirectAttributes redirAttr) {
		ProdutoSelecionado sel = produtosSelecionados.get(listIndex);
		int quantidadeAtual = sel.getQuantidade();
		quantidadeAtual++;
		sel.setQuantidade(quantidadeAtual);
		redirAttr.addFlashAttribute("msg", "Quantidade do produto '" + sel.getItem().getNome() + "' incrementada");
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("{listIndex}/decrementar")
	public ModelAndView decrementarQtd(@PathVariable("listIndex") int listIndex, RedirectAttributes redirAttr) {
		ProdutoSelecionado sel = produtosSelecionados.get(listIndex);
		int quantidadeAtual = sel.getQuantidade();
		quantidadeAtual--;
		if (quantidadeAtual > 0) {
			sel.setQuantidade(quantidadeAtual);
			redirAttr.addFlashAttribute("msg", "Quantidade do item '" + sel.getItem().getNome() + "' decrementada");
		} else {
			produtosSelecionados.remove(listIndex);
			redirAttr.addFlashAttribute("msg", "Item '" + sel.getItem().getNome() + "' removido");
		}
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("{listIndex}/remover")
	public ModelAndView remover(@PathVariable("listIndex") int listIndex, RedirectAttributes redirAttr) {
		ProdutoSelecionado sel = produtosSelecionados.remove(listIndex);
		redirAttr.addFlashAttribute("msg", "Produto '" + sel.getItem().getNome() + "' removido");
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("/frete")
	public ModelAndView calcularFrete(@RequestParam("cep") String cep) {
		if ("00000-000".equals(cep)) {
			valorFrete = 30;
		} else if ("11111-111".equals(cep)) {
			valorFrete = 20;
		} else if ("22222-222".equals(cep)) {
			valorFrete = 50;
		} else if ("04696-000".equals(cep)) {
			valorFrete = 0;
		} else {
			valorFrete = 25;
		}
		
		setCepEntrega(cep);
		
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("/cupom")
	public ModelAndView adicionarCupom(@RequestParam("cupom") String cupom) {
		if ("CATDOG20OFF".equals(cupom)) {
			valorDesconto = 20;
		}
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("/finalizar")
	public ModelAndView finalizarPedido(@RequestParam("txtFormaPagto") int formaPagamento, RedirectAttributes redirAttr) {
		Pedido pedido = new Pedido(0, 1, LocalDateTime.now(), formaPagamento, 1, valorDesconto, cepEntrega.replace("-", ""));
		List<ItensPedido> itens = new ArrayList<ItensPedido>();
		try {
			pedidoRepositorio.save(pedido);
			int idPedido = pedidoRepositorio.findByLastId();
			
			for(ProdutoSelecionado sel : produtosSelecionados) {
				ItensPedido item = new ItensPedido(0, sel.getItem().getId(), sel.getItem().getPrecoVenda(), sel.getQuantidade(), idPedido);
				itens.add(item);
			}
			
			for(ItensPedido i : itens) {
				itensPedidoRepositorio.save(i);
			}
			
			return new ModelAndView("redirect:/pedido?&idPedido=" + idPedido);
		} catch(Exception e) {
			redirAttr.addFlashAttribute("msg", "Ocorreu um erro ao salvar o pedido");
			return new ModelAndView("redirect:/carrinho");
		}	
	}
	
	public List<ProdutoSelecionado> getItensSelecionados() {
		return produtosSelecionados;
	}

	public void setItensSelecionados(List<ProdutoSelecionado> produtosSelecionados) {
		this.produtosSelecionados = produtosSelecionados;
	}

	public int getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(int valorFrete) {
		this.valorFrete = valorFrete;
	}
	
	public int getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(int valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
	
	public int getQtdeItens() {
		return produtosSelecionados.size();
	}
	
	public String getCepEntrega() {
		return this.cepEntrega;
	}
	
	public void setCepEntrega(String cepEntrega) {
		this.cepEntrega = cepEntrega;
	}
	
	public double getValorTotal() {
		double valor = 0;
		for (ProdutoSelecionado itemSel : produtosSelecionados) {
			valor += itemSel.getSubtotal();
		}
		return valor;
	}
	
	public double getValorFinal() {
		double valor = getValorTotal();
		valor += valorFrete;
		valor -= valorDesconto;
		return valor;
	}
}
