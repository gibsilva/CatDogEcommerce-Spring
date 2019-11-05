package entidades.enums;

public enum EPorteAnimal {
    Filhote(1), Pequeno(2), Médio(3), Grande(4);

    private final int opcao;

    EPorteAnimal(int opcao) {
        this.opcao = opcao;
    }

    public int getOpcao() {
        return this.opcao;
    }

    public static EPorteAnimal fromInt(int x) {
        switch (x) {
            case 1:
                return Filhote;
            case 2:
                return Pequeno;
            case 3:
                return Médio;
            case 4:
                return Grande;
        }
        return null;
    }
}
