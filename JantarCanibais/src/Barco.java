import java.util.concurrent.Semaphore;

public class Barco {

    Pessoa[] pessoasEmbarcadas = new Pessoa[2];
    static int posicaoEmbarque = 0;
    public Semaphore barco;
    public Semaphore viajemIda;
    public Semaphore viajemVolta;
    Rio rio;
    int idas = 0;
    int voltas = 0;

    public Barco(Rio rio) {

        this.barco = new Semaphore(2);
        this.viajemIda = new Semaphore(1);
        this.viajemVolta = new Semaphore(1);
        this.rio = rio;
    }

    public synchronized boolean atravessar(Pessoa pessoa) throws InterruptedException {
    
        if(barco.availablePermits() == 0 ){
        
            viajemIda.acquire();
            idas ++;
    
            try {
                
                System.out.println("Travessia: " + pessoasEmbarcadas[0].nome + " e " + pessoasEmbarcadas[1].nome);
                System.out.println("Atravessando...");
    
                Thread.sleep(100);
                System.out.println("tentando descer");
                boolean descerPrimeiraVaga = pessoasEmbarcadas[0].descerBarco();
                boolean descerSegundaVaga = pessoasEmbarcadas[1].descerBarco();
                synchronized(pessoa){
                    pessoa.notifyAll();
                }
                if (descerPrimeiraVaga || descerSegundaVaga) {
                    System.out.println("descendo..");
                    this.voltar(pessoa);
                    voltas++;
    
                } else {
                    System.out.println(("Não pode descer"));
    
                    rio.margemEsquerda.add(pessoasEmbarcadas[1]);
                   
                    pessoasEmbarcadas[1].embarcou.release();
                    pessoasEmbarcadas[1] = null;
                    synchronized(pessoa){
                        pessoa.notifyAll();
                    }
                    return false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.viajemIda.release();  // Mova a liberação do semáforo para fora do bloco try-catch
            }
            
            System.out.println("Ainda pode embarcar ? :" + this.viajemIda.availablePermits());
            System.out.println("Já voltou ?: " + this.viajemVolta.availablePermits());
            System.out.println("idas :" + idas + " voltas:"+ voltas);
    
            // Adicione a notificação para todas as threads Pessoa
            synchronized (pessoa) {
                pessoa.notifyAll();
            }
    
            return true;
        } else {
           
            synchronized(pessoa){
                pessoa.notifyAll();
            }
             return false;
        }
    }
    

        
        

    public  synchronized void voltar(Pessoa pessoa) {
       // viajemVolta.release();
        if(pessoasEmbarcadas[0]!= null){
            System.out.println("Volta: " + pessoasEmbarcadas[0].nome);
            
        }else{
            System.out.println("Volta: " + pessoasEmbarcadas[1].nome);
            
        }
        synchronized(pessoa){
            pessoa.notifyAll();
        }
        
        
        

    }

    public synchronized void subir(Pessoa pessoa) throws InterruptedException {
        
        if (pessoasEmbarcadas[0] == null) {


            pessoasEmbarcadas[0] = pessoa;
            rio.removerPessoa(rio.margemEsquerda, pessoa);


        } else if (pessoasEmbarcadas[1] == null) {
// Initialize with 0 permits

            pessoasEmbarcadas[1] = pessoa;
            rio.removerPessoa(rio.margemEsquerda, pessoa);


        }

        
     
    }

    public synchronized void descer(Pessoa pessoa) {
        if (pessoasEmbarcadas[0] == pessoa) {
            pessoasEmbarcadas[0] = null;
            rio.adicionarPessoa(rio.margemDireita, pessoa);
        } else if (pessoasEmbarcadas[1] == pessoa) {
            pessoasEmbarcadas[1] = null;
            rio.adicionarPessoa(rio.margemDireita, pessoa);
        }
        else{
            System.out.println("Erro ao descer ");
        }
        System.out.println("desceu :" + pessoa.nome);
    }
    public  boolean podeDescer(Pessoa pessoa, int option) throws InterruptedException{
        
        boolean seguroAgora = rio.condicaoSeguranca();
        boolean barcoComVaga;
        if(option == 1){
            barcoComVaga = barco.availablePermits() > 0;
        }else{
            barcoComVaga = barco.availablePermits() ==0;
        }
        
        boolean condicaoFutura = rio.condicaoFutura(rio.margemEsquerda,rio.margemDireita,   pessoa, 2);
        
        if( seguroAgora  && barcoComVaga && condicaoFutura){
            return true;
        }else{
            return false;
        }

    }

    public boolean podeSubir(Pessoa pessoa) throws InterruptedException{
        
        boolean seguroAgora = rio.condicaoSeguranca();
        boolean barcoComVaga = barco.availablePermits() > 0;
        boolean aindaNaoSubiu = pessoa.embarcou.availablePermits() > 0;
        boolean barcoParado =  viajemIda.availablePermits() == 1 && viajemVolta.availablePermits() ==1;
        //boolean podeDescer = podeDescer(pessoa,1);
        
        if( seguroAgora  && barcoComVaga && barcoParado  && aindaNaoSubiu ){
            return true;
        }else{
          
            
            return false;
            
        }
    }
}
