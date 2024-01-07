import java.util.concurrent.Semaphore;

public class Missionario extends Pessoa{
    Semaphore atravessado;
    public Missionario(String nome, Barco barco, Rio rio) {
        super(nome, "Missionario", barco, rio);
        
       
    }
   
}
