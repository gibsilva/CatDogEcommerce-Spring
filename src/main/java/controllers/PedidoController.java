package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import entidades.ItensPedido;
import repositorios.IItensPedidoRepositorio;
import repositorios.IPedidoRepositorio;
import repositorios.IProdutoRepositorio;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
	
	private final IPedidoRepositorio pedidoRepositorio;
	private final IItensPedidoRepositorio itensPedidoRepositorio;
	private final IProdutoRepositorio produtoRepositorio;
	
	@Autowired
	public PedidoController( IPedidoRepositorio pedidoRepositorio, IItensPedidoRepositorio itensPedidoRepositorio,
			IProdutoRepositorio produtoRepositorio) {
		this.pedidoRepositorio = pedidoRepositorio;
		this.itensPedidoRepositorio = itensPedidoRepositorio;
		this.produtoRepositorio = produtoRepositorio;
	}
	
	@GetMapping
	public ModelAndView criado(@RequestParam("idPedido") Integer idPedido) {
		ModelAndView view = new ModelAndView("pedido/pedido-finalizado");
		view.addObject("idPedido", idPedido);
		return view;
	}
	
	@GetMapping("/lista")
	public ModelAndView lista() {
		ModelAndView view = new ModelAndView("pedido/lista-pedidos");
		view.addObject("pedidos", pedidoRepositorio.findAll());
		return view;
	}
	
	@GetMapping("/resumo/{id}")
	public ModelAndView resumo(@PathVariable("id") int id) {
		ModelAndView view = new ModelAndView("pedido/resumo");
		view.addObject("pedido", pedidoRepositorio.obterPorId(id));
		
		List<ItensPedido> itens = itensPedidoRepositorio.obterPorIdPedido(id);
		for(ItensPedido i : itens) {
			if(i.getProduto() == null)
				i.setProduto(produtoRepositorio.obterPorId(i.getIdProduto()));
			if(i.getPedido() == null)
				i.setPedido(pedidoRepositorio.obterPorId(i.getIdPedido()));
		}
		
		view.addObject("itensPedido", itens);
		return view;
	}
}
