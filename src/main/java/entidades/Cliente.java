/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Girlaine Silva
 */
@Data
@Entity
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    public Cliente() {
    }

    public Cliente(int id, String nome, String cpf, String email,
            LocalDate dataNasc, String senha, String telefone, String celular, String sexo, Boolean ativo) {

        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataNasc = dataNasc;
        this.senha = senha;
        this.telefone = telefone;
        this.celular = celular;
        this.sexo = sexo;
        this.ativo = ativo;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    @NotBlank(message = "Campo Obrigatório")
    private String nome;

    @Column(name = "cpf")
    @NotBlank(message = "Campo Obrigatório")
    @Size(min = 11, max = 11)
    private String cpf;

    @Column(name = "email")
    @NotBlank(message = "Campo Obrigatório")
    @Email
    private String email;

    @Column(name = "DataNasc")
    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNasc;

    @Column(name = "senha")
    @NotBlank(message = "Campo Obrigatório")
    @Min(5)
    private String senha;

    @Column(name = "telefone")
    @NotBlank(message = "Campo Obrigatório")
    @Size(min = 10, max = 10)
    private String telefone;

    @Column(name = "celular")
    @NotBlank(message = "Campo Obrigatório")
    @Size(min = 11, max = 11)
    private String celular;

    @Column(name = "sexo")
    private String sexo;

    @Column(name = "ativo")
    private Boolean ativo;
}
