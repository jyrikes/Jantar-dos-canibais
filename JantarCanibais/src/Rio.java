import java.util.ArrayList;

public class Rio {

    public ArrayList<Pessoa> margemEsquerda;
    public ArrayList<Pessoa> margemDireita;
    public boolean modificando = false;

    public Rio() {
        this.margemDireita = new ArrayList<>();
        this.margemEsquerda = new ArrayList<>();
    }

    public synchronized void adicionarPessoa(ArrayList<Pessoa> margem, Pessoa pessoa) {
        try {
            modificando = true;
            margem.add(pessoa);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            modificando = false;
            notifyAll();
        }
    }

    public synchronized void removerPessoa(ArrayList<Pessoa> margem, Pessoa pessoa) {
        try {
            modificando = true;
            margem.remove(pessoa);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            modificando = false;
            notifyAll();
        }
    }

    private synchronized void waitIfModifying() throws InterruptedException {
        while (modificando) {
            wait();
        }
    }

    public synchronized int contarCanibais(ArrayList<Pessoa> margem) throws InterruptedException {
        waitIfModifying();
        int contadorCanibais = 0;
        for (Pessoa pessoa : margem) {
            if ("Canibal".equals(pessoa.getTipo())) {
                contadorCanibais++;
            }
        }
        return contadorCanibais;
    }

    public synchronized int contarMissionarios(ArrayList<Pessoa> margem) throws InterruptedException {
        waitIfModifying();
        int contadorMissionarios = 0;
        for (Pessoa pessoa : margem) {
            if ("Missionario".equals(pessoa.getTipo())) {
                contadorMissionarios++;
            }
        }
        return contadorMissionarios;
    }

    public synchronized boolean condicaoSeguranca() throws InterruptedException {
        return contarCanibais(margemEsquerda) <= contarMissionarios(margemEsquerda)
                && (contarCanibais(margemDireita) <= contarMissionarios(margemDireita));
    }

    public synchronized int contarPessoasMargen(ArrayList<Pessoa> margem) throws InterruptedException {
        waitIfModifying();
        int contadorCanibais = contarCanibais(margem);
        int contadorMissionarios = contarMissionarios(margem);
        notifyAll();
        return contadorCanibais + contadorMissionarios;
    }

    public boolean condicaoFutura(ArrayList<Pessoa> margemEsquerda, ArrayList<Pessoa> margemDireita, Pessoa pessoa, int op) throws InterruptedException {
        waitIfModifying();

        int canibaisEsquerda = this.contarCanibais(margemEsquerda);
        int missionariosEsquerda = this.contarMissionarios(margemEsquerda);
        int canibaisDireita = this.contarCanibais(margemDireita);
        int missionariosDireita = this.contarMissionarios(margemDireita);

        switch (op) {
            case 1: // caso de subida
                if (pessoa.getTipo().equals("Canibal")) {
                    canibaisEsquerda--;
                }
                if (pessoa.getTipo().equals("Missionario")) {
                    missionariosEsquerda--;
                }
                return (canibaisEsquerda <= missionariosEsquerda);

            case 2: // caso de descida
                if (pessoa.getTipo().equals("Canibal")) {
                    canibaisDireita++;
                }
                if (pessoa.getTipo().equals("Missionario")) {
                    missionariosEsquerda++;
                }

                    return (canibaisDireita <= missionariosDireita);
            

            case 3:
                int somaCanibal = canibaisDireita + canibaisEsquerda;
                int somaMissionario = missionariosDireita + missionariosEsquerda;
                int somaTotal = 6;
                int count = somaTotal - somaCanibal - somaMissionario;

                return (canibaisDireita + count < missionariosDireita);

            default:
                return false;
        }
    }
}
