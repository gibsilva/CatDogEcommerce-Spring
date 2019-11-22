/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.CartaoCredito;
import entidades.Cliente;
import entidades.Endereco;
import entidades.ItensPedido;
import entidades.Pedido;
import entidades.Produto;
import entidades.ProdutoSelecionado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import repositorios.ICartaoCreditoRepositorio;
import repositorios.IClienteRepositorio;
import repositorios.IEnderecoRepositorio;
import repositorios.IImagemRepositorio;
import repositorios.IItensPedidoRepositorio;
import repositorios.IPedidoRepositorio;
import repositorios.IProdutoRepositorio;
import utils.FormaPagamento;

@Controller
@Scope("session")
@RequestMapping("/carrinho")
public class CarrinhoController {

    private final IProdutoRepositorio produtoRepositorio;
    private final IItensPedidoRepositorio itensPedidoRepositorio;
    private final IPedidoRepositorio pedidoRepositorio;
    private final IEnderecoRepositorio enderecoRepositorio;
    private final IImagemRepositorio imagemRepositorio;
    private final ICartaoCreditoRepositorio cartaoRepositorio;
    
    private List<ProdutoSelecionado> produtosSelecionados = new ArrayList<>();
	private double valorFrete = 0;
	private int valorDesconto = 0;
	private Endereco endereco = null;
	private String cepEntrega = "0";
	private CartaoCredito cartao = new CartaoCredito();
	private int idUsuario = 0;
	private int formaPagamento = 0;
	private int parcela = 0;

    @Autowired
    public CarrinhoController(IProdutoRepositorio produtoRepositorio, IItensPedidoRepositorio itensPedidoRepositorio,
    		IPedidoRepositorio pedidoRepositorio, IEnderecoRepositorio enderecoRepositorio, IImagemRepositorio imagemRepositorio,
    		ICartaoCreditoRepositorio cartaoRepositorio) {
        this.produtoRepositorio = produtoRepositorio;
        this.itensPedidoRepositorio = itensPedidoRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.enderecoRepositorio = enderecoRepositorio;
        this.imagemRepositorio = imagemRepositorio;
        this.cartaoRepositorio = cartaoRepositorio;
    }

    @GetMapping
    public ModelAndView mostrar() {
        return new ModelAndView("carrinho");
    }
    
	@PostMapping
	public ModelAndView adicionar(@RequestParam("itemId") int itemId, RedirectAttributes redirAttr) {
		Produto p = produtoRepositorio.obterPorId(itemId);
		p.setImagens(imagemRepositorio.findByIdProduto(p.getId()));
		produtosSelecionados.add(new ProdutoSelecionado(p, 1));
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("{listIndex}/alterar")
	public ModelAndView alterarQtd(@PathVariable("listIndex") int listIndex, @RequestParam("qtd") int quantidade, RedirectAttributes redirAttr) {
		ProdutoSelecionado sel = produtosSelecionados.get(listIndex);
		if (quantidade > 0) {
			sel.setQuantidade(quantidade);
			//redirAttr.addFlashAttribute("msg", "Quantidade do item '" + sel.getItem().getNome() + "' alterada");
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
		//redirAttr.addFlashAttribute("msg", "Quantidade do produto '" + sel.getItem().getNome() + "' incrementada");
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
	public ModelAndView calcularFrete(@RequestParam("cep") String cep, HttpSession session, RedirectAttributes redirAttr) throws Exception {
		if ("04863450".equals(cep)) {
			valorFrete = 30;
		} else if ("04578910".equals(cep)) {
			valorFrete = 20;
		} else if ("22222222".equals(cep)) {
			valorFrete = 50;
		} else if ("04583110".equals(cep)) {
			valorFrete = 10;
		} else {
			valorFrete = 25;
		}
		
		//CorreiosPrecoPrazo result = new ConsultaCorreios().calcularPrecoPrazo("04794000", cep)[0];
		
		//valorFrete = result.getPrecoFrete();
		
		
		return new ModelAndView("redirect:/carrinho");
	}
	
	@PostMapping("/cupom")
	public ModelAndView adicionarCupom(@RequestParam("cupom") String cupom, RedirectAttributes redirAttr) {
		if ("CATDOG20OFF".equals(cupom.toUpperCase())) {
			valorDesconto = 20;
		} else if("CATDOG10OFF".equals(cupom.toUpperCase())) {
			valorDesconto = 10;
		}
		else {
			redirAttr.addFlashAttribute("msg", "Cupom inválido, verifique o código digitado");
		}
		return new ModelAndView("redirect:/carrinho/resumo-pedido");
	}
	
	@GetMapping("/resumo-pedido")
	public ModelAndView resumo(HttpSession session, RedirectAttributes redirAttr) {
		ModelAndView view = new ModelAndView("venda/resumo-venda");
		Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");
		if(cliente != null) {
			cliente.setEnderecos(enderecoRepositorio.findByIdCliente(cliente.getId()));
			cliente.setCartoes(cartaoRepositorio.findByIdCliente(cliente.getId()));
			if(this.endereco == null) {
				Optional<Endereco> optEnd = cliente.getEnderecos().stream().findFirst();
				if(optEnd.isPresent()){
					this.endereco = optEnd.get();
					this.cepEntrega = this.endereco.getCep();
					this.valorFrete = 15;
				}			
			}
			view.addObject("enderecos", cliente.getEnderecos());
			view.addObject("cartoes", cliente.getCartoes());
			cartao.setIdCliente(cliente.getId());
			view.addObject("cartao", this.cartao);
			return view;
		} else {
			return new ModelAndView("redirect:/login");
		}
	}
	
	@PostMapping("/seleciona-endereco")
	public ModelAndView selecionaEndereco(@RequestParam("cep") String cep, HttpSession session) {
		ModelAndView view = new ModelAndView("venda/resumo-venda");
		Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");
		if(cliente != null) {
			this.endereco = enderecoRepositorio.findByCepIdCliente(cliente.getId(), cep.replace("-", ""));
			cepEntrega = endereco.getCep();
			if ("04863450".equals(cep)) {
				valorFrete = 30;
			} else if ("04578910".equals(cep)) {
				valorFrete = 20;
			} else if ("22222222".equals(cep)) {
				valorFrete = 50;
			} else if ("04583110".equals(cep)) {
				valorFrete = 10;
			} else {
				valorFrete = 25;
			}
			return new ModelAndView("redirect:/carrinho/resumo-pedido");
		}
		return view;
	}
	
	@GetMapping("/forma-pagamento")
	public ModelAndView mostraFormaPagamento(CartaoCredito cartao) {
		return new ModelAndView("venda/forma-pagamento");
	}
	
	@PostMapping("/pagamento-boleto")
	public ModelAndView selecionaFormaPagamento(HttpSession session) {
		this.formaPagamento = 3;
		this.parcela = 0;
		limparDadosCartao();
		Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");
		this.cartao.setIdCliente(cliente.getId());
		return new ModelAndView("redirect:/carrinho/resumo-pedido");
	}
	
	@PostMapping("/pagamento-cartao-existente")
	public ModelAndView pagamentoCartaoExistente(@RequestParam("id") int id, @RequestParam("parcela") int parcela) {
		Optional<CartaoCredito> optCartao = cartaoRepositorio.findById(id);
		if(optCartao.isPresent()) this.cartao = optCartao.get();
		this.formaPagamento = 1;
		this.parcela = parcela;
		return new ModelAndView("redirect:/carrinho/resumo-pedido");
	}
	
	@PostMapping("/pagamento-cartao")
	public ModelAndView pagamentoCartao(@ModelAttribute("cartao") @Valid CartaoCredito cartao,
            BindingResult bindingResult, RedirectAttributes redirAttr, @RequestParam("parcela") int parcela, 
            @RequestParam("checkSalvar") String checkSalvar, HttpSession session) {
		Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
		cartao.setIdCliente(clienteLogado.getId());
		CartaoCredito cartCred = cartaoRepositorio.findByNumberAndIdCliente(clienteLogado.getId(), cartao.getNumero());
		if(cartCred != null) {
			redirAttr.addFlashAttribute("msg", "Cartão de crédito já cadastrado");
			this.cartao = cartCred;
			this.formaPagamento = 1;
			return new ModelAndView("redirect:/carrinho/resumo-pedido");
		}
		
		if(bindingResult.hasErrors()) {
			redirAttr.addFlashAttribute("msg", "Cartão inválido, verifique os dados digitados");
			return new ModelAndView("redirect:/carrinho/resumo-pedido").addObject("cartao", cartao);
		} else {
			if(checkSalvar != null && cartCred == null) cartaoRepositorio.save(cartao);
			this.cartao = cartao;
			this.formaPagamento = 1;
			this.setParcela(parcela);
		}
			
		return new ModelAndView("redirect:/carrinho/resumo-pedido");
	}
	
	@PostMapping("/finalizar")
	public ModelAndView finalizarPedido(RedirectAttributes redirAttr, HttpSession session) {
		Cliente clienteLogado = (Cliente) session.getAttribute("usuarioLogado");
		if(clienteLogado == null) {
			redirAttr.addFlashAttribute("msg", "Por favor faça o login antes de finalizar o pedido");
			return new ModelAndView("redirect:/login");
		}
		
		Pedido pedido = new Pedido(0, 1, LocalDateTime.now(), formaPagamento, clienteLogado.getId(), getValorDesconto(), endereco.getCep(), getValorFrete());
		pedido.setParcela(parcela);
		try {
			pedidoRepositorio.save(pedido);
			int idPedido = pedidoRepositorio.findByLastId();
			
			for(ProdutoSelecionado sel : produtosSelecionados) {
				ItensPedido item = new ItensPedido(0, sel.getItem().getId(), sel.getItem().getPrecoVenda(), sel.getQuantidade(), idPedido);
				itensPedidoRepositorio.save(item);
			}

			return new ModelAndView("redirect:/pedido?&idPedido=" + idPedido);
		} catch(Exception e) {
			redirAttr.addFlashAttribute("msg", "Ocorreu um erro ao salvar o pedido");
			return new ModelAndView("redirect:/carrinho");
		}
		finally {
			session.setAttribute("carrinhoController", null);
		}
	}
	
	public int getFormaPagamento() {
		return this.formaPagamento;
	}
	
	public void setFormaPagamento(int formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	
	public List<ProdutoSelecionado> getItensSelecionados() {
		return produtosSelecionados;
	}

	public void setItensSelecionados(List<ProdutoSelecionado> produtosSelecionados) {
		this.produtosSelecionados = produtosSelecionados;
	}

	public double getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(double valorFrete) {
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
	
	public Endereco getEndereco() {
		return this.endereco;
	}
	
	public void setCepEntrega(Endereco endereco) {
		this.endereco = endereco;
	}
	
	public int getIdUsuario() {
		return this.idUsuario;
	}
	
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
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
	
	public String getFormaPagamentoExtenso() {
		return FormaPagamento.formaPagamento(formaPagamento);
	}

	public int getParcela() {
		return parcela;
	}

	public void setParcela(int parcela) {
		this.parcela = parcela;
	}

	public String getCepEntrega() {
		return cepEntrega;
	}

	public void setCepEntrega(String cepEntrega) {
		this.cepEntrega = cepEntrega;
	}
	
	public void limparDadosCartao() {
		this.cartao.setCodigoSeguranca("");
		this.cartao.setNome("");
		this.cartao.setNumero("");
		this.cartao.setValidade("");
		this.cartao.setId(0);
	}
	
}
