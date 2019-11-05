package entidades.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gi
 */
public enum ETipoAnimal {
    Cachorro(1), Gato(2), Ave(3), Peixe(4), Roedor(5);

    private final int opcao;

    ETipoAnimal(int opcao) {
        this.opcao = opcao;
    }

    public int getOpcao() {
        return this.opcao;
    }

    public static ETipoAnimal fromInt(int x) {
        switch (x) {
            case 1:
                return Cachorro;
            case 2:
                return Gato;
            case 3:
                return Ave;
            case 4:
                return Peixe;
            case 5:
                return Roedor;
        }
        return null;
    }

    public static Map getValues() {
        Map<String, String> lista = new HashMap<>();
        for(ETipoAnimal i : ETipoAnimal.values()){
            lista.put(String.valueOf(i.getOpcao()), i.toString());
        }
        return lista;
    }
}
