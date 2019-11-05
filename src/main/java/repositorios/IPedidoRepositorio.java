/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositorios;

import entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Gi
 */
public interface IPedidoRepositorio extends JpaRepository<Pedido, Integer> {
    @Query(value = "SELECT id FROM pedido order by id desc limit 1", nativeQuery = true)
    public Integer findByLastId();
}
