package repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entidades.Cliente;
import java.util.List;


@Repository
public interface IClienteRepositorio extends JpaRepository<Cliente, Integer> {
	public Cliente findByEmail(String email);
        public Cliente findByCpf(String cpf);
        public List<Cliente> findByAtivo(boolean ativo);
}
