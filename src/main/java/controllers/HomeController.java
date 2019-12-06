/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entidades.Imagem;
import entidades.Produto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@Autowired
	ServletContext servletContext; 
	
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
        List<Produto> produtos = produtoRepositorio.obterPorLimite(15);
        for(Produto p : produtos) {
        	p.setImagens(imagemRepositorio.findByIdProduto(p.getId()));
        }
        view.addObject("produtos", produtos);
        return view;
    }
    
    @PostMapping("/pesquisa")
    public ModelAndView consultaProdutos(@RequestParam("filtro") String filtro) {
        ModelAndView view = new ModelAndView("produto-com-filtro");
        List<Produto> produtos = produtoRepositorio.obterPorNome(filtro);
        for(Produto p : produtos) {
        	p.setImagens(imagemRepositorio.findByIdProduto(p.getId()));
        }
        view.addObject("produtos", produtos);
        return view;
    }
    
    @GetMapping("/detalhes/{id}")
    public ModelAndView detalhesProduto(@PathVariable("id") Integer id) {
        ModelAndView view = new ModelAndView("detalhes-produto");
        Produto produto = (produtoRepositorio.obterPorId(id));
        produto.setImagens(imagemRepositorio.findByIdProduto(produto.getId()));
        view.addObject("produto", produto);
        return view;
    }
    
    @GetMapping("/produtos/imagem/{id}")
    public void getImageAsByteArray(HttpServletResponse response, @PathVariable("id") int id) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        BufferedOutputStream output = null;
        try {
        	Optional<Imagem> optImg = imagemRepositorio.findById(id);
        	Imagem img = optImg.get();
            fis = new FileInputStream(new File(img.getCaminho()));
            bis = new BufferedInputStream(fis);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            output = new BufferedOutputStream(response.getOutputStream());
            for (int data; (data = bis.read()) > -1;) {
                output.write(data);
            }
        } catch (IOException e) {

        } finally {
            fis.close();
            bis.close();
            output.close();
        }
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
    
     @GetMapping("/Alimentos/Produtos") //chamada na url
    public String ProdutosLista() {
        return "/produto-com-filtro"; //aqui o nome da tela
    }
}
