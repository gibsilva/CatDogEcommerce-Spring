package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
	
	@GetMapping
	public ModelAndView criado(@RequestParam("idPedido") Integer idPedido) {
		ModelAndView view = new ModelAndView("pedido/pedido-finalizado");
		view.addObject("idPedido", idPedido);
		return view;
	}
}
