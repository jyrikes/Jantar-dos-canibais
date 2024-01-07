import java.util.concurrent.Semaphore;

public class Pessoa implements Runnable {

    public String nome;
    public String tipo;
    public Barco barco;
    public Rio rio;
    public Semaphore embarcou;
    public Semaphore atravessado;

    public Pessoa(String nome, String tipo, Barco barco, Rio rio) {
        this.nome = nome;
        this.tipo = tipo;
        this.barco = barco;
        this.rio = rio;
        this.embarcou = new Semaphore(1);
        this.atravessado = new Semaphore(0); 
    }

    public String getTipo() {
        return tipo;
    }

    public boolean subirBarco() throws InterruptedException {
        synchronized (barco) {
            if (barco.podeSubir(this)) {
                barco.subir(this);
                embarcou.acquire();
                barco.barco.acquire();
                return true;
            } else {
                synchronized(this){
                    this.notifyAll();
                }
                return false;
            }
        }
    }

    public boolean descerBarco() throws InterruptedException {
        synchronized (barco) {
            if (barco.podeDescer(this,2) ){
                System.out.println("Liberado para descer");
                barco.descer(this);
                embarcou.release();
                barco.barco.release();
     

                return true;
            } else if (rio.contarPessoasMargen(rio.margemEsquerda) == 0) {
                barco.descer(barco.pessoasEmbarcadas[0]);
                barco.descer(barco.pessoasEmbarcadas[1]);
       
                return true;
            } else {
                System.out.println("Não liberado para descer");
                return false;
            }
        }
      
    }

    @Override
    public void run() {
        System.out.println("Iniciando a thread " + this.nome);
        while (this.atravessado.availablePermits() == 0) {
        try {
            
                subirBarco();
                if (barco.atravessar(this)) {
                    this.atravessado.release(); 
                } else {
                    synchronized (this) {
                        System.out.println(this.nome + " Esperando para atravessar");
                        this.wait();
                        System.out.println(this.nome + " Parou de esperar");
                    }
                }
            }
         catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized (barco) {
                System.out.println(this.nome + " Atravessou");
                if (this.atravessado.availablePermits() == 0) {
                    barco.notifyAll();  // Mova a notificação para dentro do bloco synchronized apenas se a thread estiver esperando
                }
                System.out.println(this.nome + " Notificou todo mundo");
            }
        }
    }
}
}