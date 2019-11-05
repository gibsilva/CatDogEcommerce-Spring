package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import repositorios.IPedidoRepositorio;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
	
	private final IPedidoRepositorio pedidoRepositorio;
	
	@Autowired
	public PedidoController( IPedidoRepositorio pedidoRepositorio) {
		this.pedidoRepositorio = pedidoRepositorio;
	}
	
	@GetMapping
	public ModelAndView criado(@RequestParam("idPedido") Integer idPedido) {
		ModelAndView view = new ModelAndView("pedido/pedido-finalizado");
		view.addObject("idPedido", idPedido);
		return view;
	}
	
	@GetMapping("/lista")
	public ModelAndView lista() {
		ModelAndView view = new ModelAndView("pedido/lista");
		view.addObject("pedidos", pedidoRepositorio.findAll());
		return view;
	}
}
