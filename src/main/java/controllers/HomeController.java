/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Girlaine
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/detalhes") //chamada na url
    public String detalhes() {
        return "detalhesProduto"; //aqui o nome da tela
    }

    @GetMapping("/carrinho") //chamada na url
    public String carrinho() {
        return "carrinho"; //aqui o nome da tela
    }
    
    @GetMapping("/cliente/meuPerfil") //chamada na url
    public String meuPerfil() {
        return "/clientes/meuPerfil"; //aqui o nome da tela
    }
}
