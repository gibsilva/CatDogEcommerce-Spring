package controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import entidades.Cliente;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import repositorios.IClienteRepositorio;
import repositorios.IEnderecoRepositorio;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final IClienteRepositorio repositorio;
    private final IEnderecoRepositorio enderecoRepositorio;

    @Autowired
    public ClienteController(IClienteRepositorio repositorio, IEnderecoRepositorio enderecoRepositorio) {
        this.repositorio = repositorio;
        this.enderecoRepositorio = enderecoRepositorio;
    }

    @GetMapping("/lista")
    public ModelAndView listaClientes() {
        List<Cliente> clientes = repositorio.findAll();
        ModelAndView view = new ModelAndView("cliente/lista-cliente");
        view.addObject("cliente", clientes);
        return view;
    }
    
    @GetMapping("/salvar")
    public ModelAndView salvar(Cliente cliente) {
        ModelAndView view = new ModelAndView("clientes/incluir-cliente");
        view.addObject("cliente", cliente);
        return view;
    }

    @PostMapping("/salvar")
    public ModelAndView salvar(@ModelAttribute("cliente") @Valid Cliente cliente,
            BindingResult bindingResult, RedirectAttributes redirAttr) {
        cliente.setAtivo(true);

        if (repositorio.count() != 0) {
            if (repositorio.findByEmail(cliente.getEmail()) != null) {
                bindingResult.reject("email", "E-mail já cadastrado");
            }

            if (repositorio.findByCpf(cliente.getCpf()) != null) {
                bindingResult.reject("cpf", "CPF já cadastrado");
            }
        }

        if (bindingResult.hasErrors()) { 
            return new ModelAndView("clientes/incluir-cliente");
        } else {
        	cliente.setSenhaEncriptada(cliente.getSenha());
            repositorio.save(cliente);
        }
        

        ModelAndView view = new ModelAndView("redirect:/");
        redirAttr.addFlashAttribute("cliente", cliente);
        return view;
    }

    @GetMapping("/alterar/{id}")
    public ModelAndView alterar(@PathVariable("id") Integer id) {
        ModelAndView view = new ModelAndView("clientes/alterar-cliente");
        view.addObject("cliente", repositorio.findById(id));
        return view;
    }

    @PostMapping("/alterar")
    public ModelAndView alterar(@ModelAttribute("cliente") @Valid Cliente cliente,
            BindingResult bindingResult, RedirectAttributes redirAttr) {
        if (bindingResult.hasErrors()) 
            return new ModelAndView("clientes/alterar-cliente");
        else 
            repositorio.save(cliente);       

        ModelAndView view = new ModelAndView("redirect:/cliente/perfil/" + cliente.getId());
        redirAttr.addFlashAttribute("cliente", cliente);
        return view;
    }
    
    @GetMapping("/perfil/{id}")
    public ModelAndView perfil(@PathVariable("id") Integer id) {
        ModelAndView view = new ModelAndView("clientes/meuPerfil");
        view.addObject("cliente", repositorio.findById(id));
        view.addObject("enderecosCliente", enderecoRepositorio.findByIdCliente(id));
        return view;
    }
}
