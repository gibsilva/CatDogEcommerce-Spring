/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.Produto;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import repositorios.IImagemRepositorio;
import repositorios.IProdutoRepositorio;

/**
 *
 * @author Girlaine
 */
@Controller
@RequestMapping("/")
public class HomeController {

    private final IProdutoRepositorio produtoRepositorio;
    private final IImagemRepositorio imagemRepositorio;
    
    @Autowired
    public HomeController(IProdutoRepositorio produtoRepositorio, IImagemRepositorio imagemRepositorio){
        this.produtoRepositorio = produtoRepositorio;
        this.imagemRepositorio = imagemRepositorio;
    }
    
    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView view = new ModelAndView("home");
        List<Produto> produtos = produtoRepositorio.findAll();
        view.addObject("produtos", produtos);
        return view;
    }
    
    @GetMapping("/venda/formaPagamento")
    public String formaPagamento() {
        return "/venda/forma-pagamento";
    }
        
    @GetMapping("/venda/select-endereco")
    public String checkout() {
        return "/venda/select-endereco";
    }

    @GetMapping("/venda/resumo-venda") //chamada na url
    public String resumoVenda() {
        return "/venda/resumo-venda"; //aqui o nome da tela
    }

    @GetMapping("/cliente/meuPerfil") //chamada na url
    public String meuPerfil() {
        return "/clientes/meuPerfil"; //aqui o nome da tela
    }
    
    @GetMapping("/cliente/pedido-selecionado") //chamada na url
    public String pedidoSelecionado() {
        return "/clientes/pedido-selecionado"; //aqui o nome da tela
    }
}
